package org.tron.metrics.repository;

import org.tron.common.utils.LogUtils;
import org.tron.metrics.bean.TransactionCacheEntity;
import org.tron.metrics.dao.MetricsDatabase;
import org.tron.metrics.dao.TransactionCacheDao;
import org.tron.metrics.utils.DayBucketPartitioner;

import java.util.List;

public class TransactionRepository implements ITransactionRepository {
    private static final String TAG = "TransactionCacheController";
    private TransactionCacheDao transactionCacheDao;

    public TransactionRepository() {
        transactionCacheDao = MetricsDatabase.getInstance().transactionCacheDao();
    }

    @Override
    public List<TransactionCacheEntity> queryData() {
        return transactionCacheDao.getUpdatedTransactionCaches();
    }

    /**
     * must run on ThreeThread
     */
    @Override
    public void updateAndDeleteData(List<TransactionCacheEntity> list) {
        if (list == null || list.isEmpty()) return;
        // Confirm in a single transaction with in-place conditional updates.
        // Upserting the pre-upload snapshot here would overwrite counters that
        // insertData() accumulated during the network round-trip and lose them.
        LogUtils.i(TAG, "updateAndDeleteData:confirm:" + list.size());
        transactionCacheDao.confirmUploaded(list, DayBucketPartitioner.todayUtc());
    }

    @Override
    public void insertData(TransactionCacheEntity transactionCacheEntity) {
        transactionCacheDao.insert(transactionCacheEntity);
    }

    public TransactionCacheEntity queryExistingData(String uId, int actionType, String tokenAddress, String day) {
        return transactionCacheDao.getByUniqueKey(uId, actionType, tokenAddress, day);
    }
}