package org.crypto.blockchain.blockchainexplorer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "transactions")
@Data
public class Transactions {
    @Id
    private String hash;
    private long blockHeight;
    private List<String> addresses;
    private double total;
    private double fee;
    private Date confirmed;
    private Date received;
}