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
        List<TransactionCacheEntity> dbAllData = transactionCacheDao.getAll();
        if (dbAllData == null || dbAllData.isEmpty()) return;
        DayBucketPartitioner.DayPartition<TransactionCacheEntity> partition =
                DayBucketPartitioner.splitByDay(dbAllData, list, TransactionCacheEntity::getDay);
        List<TransactionCacheEntity> beforeDayList = partition.getStaleEntries();
        List<TransactionCacheEntity> nowDayList = partition.getCurrentDayEntries();

        nowDayList.forEach(entity -> entity.setUpdated(false));
        LogUtils.i(TAG, "updateAndDeleteData:update:" + nowDayList.size());
        LogUtils.i(TAG, "updateAndDeleteData:delete:" + beforeDayList.size());
        transactionCacheDao.insertAll(nowDayList);
        if (!beforeDayList.isEmpty()) {
            transactionCacheDao.delete(beforeDayList);
        }
    }

    @Override
    public void insertData(TransactionCacheEntity transactionCacheEntity) {
        transactionCacheDao.insert(transactionCacheEntity);
    }

    public TransactionCacheEntity queryExistingData(String uId, int actionType, String tokenAddress, String day) {
        return transactionCacheDao.getByUniqueKey(uId, actionType, tokenAddress, day);
    }
}