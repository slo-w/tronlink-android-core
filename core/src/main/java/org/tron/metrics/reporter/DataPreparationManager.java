package org.tron.metrics.reporter;

import org.tron.metrics.bean.BalanceCacheEntity;
import org.tron.metrics.bean.StatDataRequest;
import org.tron.metrics.bean.StatXData;
import org.tron.metrics.bean.StatYData;
import org.tron.metrics.bean.TransactionCacheEntity;
import org.tron.metrics.repository.IBalanceRepository;
import org.tron.metrics.repository.ITransactionRepository;
import org.tron.metrics.utils.StatDataConverter;

import java.util.List;

/**
 * Data preparation manager
 * Responsibilities: data collection, data conversion, request building
 * Corresponding flow: Step 1 - Data preparation stage
 */
public class DataPreparationManager {

    private DataPreparationManager() {
    }

    /**
     * Prepare upload data
     * 1. Collect data from cache
     * 2. Convert to statistics data structure
     * 3. Build request object
     */
    public static DataPreparationResult prepareUploadData(IBalanceRepository balanceRepository, ITransactionRepository transactionCache) {
        try {
            List<BalanceCacheEntity> balanceList = balanceRepository.queryData();
            List<TransactionCacheEntity> transactionList = transactionCache.queryData();

            List<StatXData> xDataList = StatDataConverter.convertBalanceCacheToStatXData(balanceList);
            List<StatYData> yDataList = StatDataConverter.convertTransactionCacheToStatYData(transactionList);

            StatDataRequest request = buildStatDataRequest(xDataList, yDataList);
            boolean hasData = !isEmpty(balanceList) || !isEmpty(transactionList);

            return new DataPreparationResult(request, hasData, balanceList, transactionList);

        } catch (Exception e) {
            return new DataPreparationResult(new StatDataRequest(), false, null, null);
        }
    }

    /**
     * Build statistics data request object
     */
    private static StatDataRequest buildStatDataRequest(List<StatXData> xDataList, List<StatYData> yDataList) {
        StatDataRequest request = new StatDataRequest();

        if (!isEmpty(xDataList)) {
            String xDataString = StatDataConverter.buildXDataString(xDataList);
            request.setX(xDataString);
        }

        if (!isEmpty(yDataList)) {
            String yDataString = StatDataConverter.buildYDataString(yDataList);
            request.setY(yDataString);
        }

        return request;
    }

    private static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Data preparation result
     */
    public static class DataPreparationResult {
        private final StatDataRequest request;
        private final boolean hasData;
        private final List<BalanceCacheEntity> balanceList;
        private final List<TransactionCacheEntity> transactionList;

        public DataPreparationResult(StatDataRequest request, boolean hasData,
                                     List<BalanceCacheEntity> balanceList,
                                     List<TransactionCacheEntity> transactionList) {
            this.request = request;
            this.hasData = hasData;
            this.balanceList = balanceList;
            this.transactionList = transactionList;
        }

        public StatDataRequest getRequest() {
            return request;
        }

        public boolean hasData() {
            return hasData;
        }

        public List<BalanceCacheEntity> getBalanceList() {
            return balanceList;
        }

        public List<TransactionCacheEntity> getTransactionList() {
            return transactionList;
        }
    }
}
