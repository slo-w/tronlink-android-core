package org.tron.metrics.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Upsert;

import org.tron.metrics.bean.BalanceCacheEntity;
import org.tron.metrics.bean.TransactionCacheEntity;

import java.util.List;

@Dao
public interface TransactionCacheDao {

    @Upsert
    void insertAll(List<TransactionCacheEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TransactionCacheEntity entity);

    @Query("SELECT * FROM transaction_cache WHERE updated = 1")
    List<TransactionCacheEntity> getUpdatedTransactionCaches();

    @Query("SELECT * FROM transaction_cache WHERE uid = :uid AND action_type = :actionType AND token_address = :tokenAddress AND day = :day LIMIT 1")
    TransactionCacheEntity getByUniqueKey(String uid, int actionType, String tokenAddress, String day);

    @Delete
    void delete(List<TransactionCacheEntity> entities);


    @Query("SELECT * FROM transaction_cache")
    List<TransactionCacheEntity> getAll();
}
