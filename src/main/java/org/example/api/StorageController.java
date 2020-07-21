package org.example.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping(value = "/listOfFiles", produces = "application/json")
    public ResponseEntity<List<String>> getListOfFilesInFolder(@RequestParam String folderName) {
        return new ResponseEntity<>(storageService.getListOfFilesInFolder(folderName), HttpStatus.OK);
    }
}
