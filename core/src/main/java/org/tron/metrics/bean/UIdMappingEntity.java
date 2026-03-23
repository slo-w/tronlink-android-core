package org.tron.metrics.bean;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "uid_mapping")
public class UIdMappingEntity {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "uid")
    private String uId;
}
