package org.example.api;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

    private BlobServiceClient blobServiceClient;

    public StorageService() {
        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(System.getenv("AZURE_STORAGE_CONNECTION_STRING")).buildClient();
        LOGGER.debug("Connected to Storage Account: {}.", blobServiceClient.getAccountUrl());
    }

    private BlobContainerClient getBlobContainerClient(String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
            LOGGER.debug("Container not found: {}. Created new container.", containerClient.getBlobContainerUrl());
        }
        return containerClient;
    }

    public List<String> getListOfFilesInFolder(String folderName) {
        BlobContainerClient containerClient = getBlobContainerClient(folderName);
        return listBlobsInContainer(containerClient);
    }

    private List<String> listBlobsInContainer(BlobContainerClient containerClient) {
        List<String> blobNames = new ArrayList<>();
        for (BlobItem blob : containerClient.listBlobs()) {
            blobNames.add(blob.getName());
        }
        LOGGER.debug("Listed blobs in container {}.", containerClient.getBlobContainerUrl());
        return blobNames;
    }

    public void uploadFilesToFolder(String folderName) {

    }

    private void uploadFolderContentsToContainer(String folderPath, BlobContainerClient containerClient) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> uploadFileToContainer(path, containerClient));
        }
    }

    private void uploadFileToContainer(Path filePath, BlobContainerClient containerClient) {
        String fileName = filePath.getFileName().toString();
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.uploadFromFile(filePath.toString(), false);
        LOGGER.debug("Uploaded file {} to container: {}.", fileName, containerClient.getBlobContainerUrl());
    }

    public void downloadFolderContents(String folderName) {

    }

    private void downloadContainerContentsToFolder(BlobContainerClient containerClient, String downloadPath) {
        for (BlobItem blob : containerClient.listBlobs()) {
            BlobClient blobClient = containerClient.getBlobClient(blob.getName());
            downloadBlobAsFile(blobClient, downloadPath);
        }
    }

    private void downloadBlobAsFile(BlobClient blobClient, String downloadPath) {
        blobClient.downloadToFile(downloadPath);
        LOGGER.debug("Downloaded file {}.", blobClient.getBlobUrl());
    }
}
