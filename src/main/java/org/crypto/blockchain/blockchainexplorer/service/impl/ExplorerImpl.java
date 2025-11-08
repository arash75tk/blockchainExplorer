package org.crypto.blockchain.blockchainexplorer.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crypto.blockchain.blockchainexplorer.model.Block;
import org.crypto.blockchain.blockchainexplorer.model.Transactions;
import org.crypto.blockchain.blockchainexplorer.repository.BlockRepository;
import org.crypto.blockchain.blockchainexplorer.repository.TransactionRepository;
import org.crypto.blockchain.blockchainexplorer.service.Explorer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerImpl implements Explorer {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BlockRepository blockRepo;
    private final TransactionRepository txRepo;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_URL = "https://api.blockcypher.com/v1/btc/main";
    @Value("${block_cypher.token}")
    private String token;

    @Override
    public void fetchLatestBlocks(int count){
        String latestUrl = BASE_URL + "?token=" + token;
        String json = restTemplate.getForObject(latestUrl, String.class);
        JsonNode latestNode;
        String currentHash;
        try {
            latestNode = mapper.readTree(json);
            currentHash = latestNode.path("hash").asText();
        }catch (JsonProcessingException e){
            log.debug("json Processing exception");
            return;
        }


        for (int i = 0; i < count; i++) {
            json = restTemplate.getForObject(BASE_URL + "/blocks/" + currentHash, String.class);
            Map<String, Object> block = restTemplate.getForObject(BASE_URL + "/blocks/" + currentHash, Map.class);
            saveBlock(block);
            currentHash = (String) block.get("prev_block");
            if (currentHash == null) break;
        }
    }


    @Override
    public void saveBlock(Map<String, Object> blockData) {
        Block block = new Block();
        block.setHash((String) blockData.get("hash"));
        block.setHeight(((Number) blockData.get("height")).longValue());
        block.setPrevBlock((String) blockData.get("prev_block"));
        block.setTxIds((List<String>) blockData.get("txids"));
        String timeStr=(String) blockData.get("time");
        if (timeStr != null && !timeStr.isEmpty()) {
            Instant instant = Instant.parse(timeStr); // parses 2025-10-26T06:36:52Z
            block.setTime(Date.from(instant));        // convert to java.util.Date for MongoDB
        }
        blockRepo.save(block);
        saveTxs((List<String>) blockData.get("txids"));
    }

    @Override
    public void saveTxs(List<String> txs){
        txs.forEach(tx->{
            Map<String, Object> transaction = restTemplate.getForObject(BASE_URL + "/txs/" + tx, Map.class);
            Transactions btcTransaction = new Transactions();
            btcTransaction.setHash(tx);
            btcTransaction.setBlockHeight(((Number) transaction.get("block_height")).longValue());
            btcTransaction.setTotal(((Number) transaction.get("total")).longValue());
            btcTransaction.setFee(((Number) transaction.get("fees")).longValue());
            String timeStr=(String) transaction.get("confirmed");
            if (timeStr != null && !timeStr.isEmpty()) {
                Instant instant = Instant.parse(timeStr); // parses 2025-10-26T06:36:52Z
                btcTransaction.setConfirmed(Date.from(instant));
            }
            timeStr=(String) transaction.get("received");
            if (timeStr != null && !timeStr.isEmpty()) {
                Instant instant = Instant.parse(timeStr); // parses 2025-10-26T06:36:52Z
                btcTransaction.setReceived(Date.from(instant));
            }
            btcTransaction.setAddresses((List<String>) transaction.get("addresses"));
            txRepo.save(btcTransaction);
        });
    }


}
