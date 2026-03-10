package org.tron.metrics.repository;

import org.tron.metrics.bean.TransactionCacheEntity;

import java.util.List;

/**
 * date: 2026/1/23
 * desc:
 **/
public interface ITransactionRepository {

    List<TransactionCacheEntity> queryData();

    void updateAndDeleteData(List<TransactionCacheEntity> list);

    void insertData(TransactionCacheEntity transactionCacheEntity);
}
