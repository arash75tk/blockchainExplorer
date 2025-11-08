package org.crypto.blockchain.blockchainexplorer.repository;

import org.crypto.blockchain.blockchainexplorer.model.Block;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockRepository extends MongoRepository<Block, String> {}
