package org.tron.metrics.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
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

    @Query("UPDATE transaction_cache SET updated = 0 WHERE id = :id AND count = :count")
    void clearUpdatedIfUnchanged(long id, int count);

    @Query("DELETE FROM transaction_cache WHERE day != :dayNow AND updated = 0")
    void deleteStaleUploaded(String dayNow);

    /**
     * Confirms a successful upload of the given snapshot. The flag is cleared
     * in place and only where `count` still equals the uploaded value: rows
     * that accumulated more transactions during the network round-trip keep
     * updated=1 and are re-uploaded next time, instead of being overwritten
     * by the stale snapshot.
     */
    @Transaction
    default void confirmUploaded(List<TransactionCacheEntity> uploaded, String dayNow) {
        for (TransactionCacheEntity snapshot : uploaded) {
            if (snapshot.getId() == null) {
                continue;
            }
            clearUpdatedIfUnchanged(snapshot.getId(), snapshot.getCount());
        }
        deleteStaleUploaded(dayNow);
    }
}
