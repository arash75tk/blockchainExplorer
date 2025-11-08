package org.crypto.blockchain.blockchainexplorer.repository;

import org.crypto.blockchain.blockchainexplorer.model.Transactions;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transactions, String> {}
