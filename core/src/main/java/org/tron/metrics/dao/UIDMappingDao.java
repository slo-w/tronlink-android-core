package org.tron.metrics.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.tron.metrics.bean.UIdMappingEntity;

@Dao
public interface UIDMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UIdMappingEntity entity);

    @Query("SELECT * FROM uid_mapping WHERE address = :address LIMIT 1")
    UIdMappingEntity getByAddress(String address);

    @Query("SELECT * FROM uid_mapping WHERE uid = :uid LIMIT 1")
    UIdMappingEntity getByUid(String uid);
}
