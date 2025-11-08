package org.crypto.blockchain.blockchainexplorer.service;

import java.util.List;
import java.util.Map;

public interface Explorer {
    public void fetchLatestBlocks(int count);

    public void saveBlock(Map<String, Object> blockData);

    public void saveTxs(List<String> txs);

}
