package org.crypto.blockchain.blockchainexplorer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "blocks")
@Data
public class Block {
    @Id
    private String hash;
    private long height;
    private String prevBlock;
    private List<String> txIds;
    private Date time;
}
