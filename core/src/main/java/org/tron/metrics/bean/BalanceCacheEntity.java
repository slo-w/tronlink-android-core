package org.tron.metrics.bean;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "balance_cache", indices = {
        @Index(value = {"uid", "day"}, unique = true)}
)
public class BalanceCacheEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "uid")
    private String uId;
    @ColumnInfo(name = "id_type")
    private int idType;
    @ColumnInfo(name = "trx_balance")
    private String trxBalance;
    @ColumnInfo(name = "usdt_balance")
    private String usdtBalance;
    @ColumnInfo(name = "usd_balance")
    private String usdBalance;
    @ColumnInfo(name = "day")
    private String day;
    @ColumnInfo(name = "updated")
    private boolean updated;
    @Ignore
    private EqualStatus equalStatus;
}
