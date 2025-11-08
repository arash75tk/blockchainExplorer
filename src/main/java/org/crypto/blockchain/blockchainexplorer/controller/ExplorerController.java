package org.crypto.blockchain.blockchainexplorer.controller;

import lombok.RequiredArgsConstructor;
import org.crypto.blockchain.blockchainexplorer.service.Explorer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ExplorerController {
    private final Explorer service;

    @PostMapping("/fetch/{count}")
    public ResponseEntity<String> fetch(@PathVariable int count) {
        service.fetchLatestBlocks(count);
        return ResponseEntity.ok("Fetched " + count + " blocks");
    }
}
