package org.tron.metrics.repository;


import org.tron.metrics.bean.BalanceCacheEntity;
import org.tron.metrics.bean.EqualStatus;
import org.tron.metrics.dao.BalanceCacheDao;
import org.tron.metrics.dao.MetricsDatabase;
import org.tron.metrics.utils.DayBucketPartitioner;

import java.util.List;
import java.util.Objects;

public class BalanceRepository implements IBalanceRepository {
    private BalanceCacheDao balanceCacheDao;

    public BalanceRepository() {
        balanceCacheDao = MetricsDatabase.getInstance().balanceCacheDao();
    }

    @Override
    public boolean insert(BalanceCacheEntity balanceCacheEntity) {
        return balanceCacheDao.insert(balanceCacheEntity) != -1;
    }

    /**
     * must run on ThreeThread
     */
    @Override
    public List<BalanceCacheEntity> queryData() {
        return balanceCacheDao.getUpdatedBalanceCaches();
    }

    /**
     * must run on ThreeThread
     */
    @Override
    public void updateAndDelete(List<BalanceCacheEntity> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        // Confirm in a single transaction with in-place conditional updates.
        // Upserting the pre-upload snapshot here would overwrite balances that
        // changed during the network round-trip and re-upload stale values.
        balanceCacheDao.confirmUploaded(list, DayBucketPartitioner.todayUtc());
    }

    /**
     * must run on ThreeThread
     */
    @Override
    public void deleteData(List<BalanceCacheEntity> list) {
        balanceCacheDao.delete(list);
    }

    public BalanceCacheEntity getCurrentDayData(String day, String uId) {
        return balanceCacheDao.getBalanceCachesByDay(uId, day);
    }


    public EqualStatus equalData(BalanceCacheEntity dbData, String trxBalance, String usdtBalance) {
        if (dbData == null) return EqualStatus.Null;
        String dbTrxBalance = dbData.getTrxBalance();
        String dbUsdtBalance = dbData.getUsdtBalance();
        if (Objects.equals(trxBalance, dbTrxBalance) && Objects.equals(usdtBalance, dbUsdtBalance)) {
            return EqualStatus.HasSameData;
        }
        return EqualStatus.DifferentData;
    }
}

