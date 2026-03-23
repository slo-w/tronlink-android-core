package org.tron.metrics.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Upsert;

import org.tron.metrics.bean.BalanceCacheEntity;

import java.util.List;

@Dao
public interface BalanceCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BalanceCacheEntity balanceCacheEntity);
    
    @Upsert
    void insertAll(List<BalanceCacheEntity> balanceCacheEntities);

    @Delete
    void delete(List<BalanceCacheEntity> balanceCacheEntities);

    @Query("SELECT * FROM balance_cache")
    List<BalanceCacheEntity> getAll();

    @Query("SELECT * FROM balance_cache WHERE updated = 1")
    List<BalanceCacheEntity> getUpdatedBalanceCaches();

    @Query("SELECT * FROM balance_cache WHERE uid = :uid and day = :day")
    BalanceCacheEntity getBalanceCachesByDay(String uid, String day);
}
