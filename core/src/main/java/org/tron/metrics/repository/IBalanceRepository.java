package org.tron.metrics.repository;

import org.tron.metrics.bean.BalanceCacheEntity;

import java.util.List;

/**
 * date: 2026/1/23
 * desc:
 **/
public interface IBalanceRepository {
    boolean insert(BalanceCacheEntity balanceCacheEntity);

    List<BalanceCacheEntity> queryData();

    void updateAndDelete(List<BalanceCacheEntity> list);

    void deleteData(List<BalanceCacheEntity> list);
}
