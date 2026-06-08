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
        List<BalanceCacheEntity> dbAllData = balanceCacheDao.getAll();
        if (dbAllData == null || dbAllData.isEmpty()) {
            return;
        }
        DayBucketPartitioner.DayPartition<BalanceCacheEntity> partition =
                DayBucketPartitioner.splitByDay(dbAllData, list, BalanceCacheEntity::getDay);
        List<BalanceCacheEntity> beforeDayList = partition.getStaleEntries();
        List<BalanceCacheEntity> nowDayList = partition.getCurrentDayEntries();

        if (!nowDayList.isEmpty()) {
            String dayNow = partition.getDayNow();
            for (int i = 0; i < nowDayList.size(); i++) {
                BalanceCacheEntity entity = nowDayList.get(i);
                BalanceCacheEntity dbData = getCurrentDayData(dayNow, entity.getUId());
                EqualStatus equalStatus = equalData(dbData, entity.getTrxBalance(), entity.getUsdtBalance());
                if (equalStatus == EqualStatus.HasSameData) {
                    entity.setUpdated(false);
                }
            }
        }
        balanceCacheDao.insertAll(nowDayList);
        if (!beforeDayList.isEmpty()) {
            deleteData(beforeDayList);
        }
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

