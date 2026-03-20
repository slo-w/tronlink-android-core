package org.tron.metrics.bean;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;


@Data
@Entity(tableName = "transaction_cache", indices = {
        @Index(value = {"uid", "action_type", "token_address", "day"}, unique = true)}
)
public class TransactionCacheEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "uid")
    private String uId;
    @ColumnInfo(name = "id_type")
    private int idType;
    @ColumnInfo(name = "action_type")
    private int actionType;
    @ColumnInfo(name = "count")
    private int count;
    @ColumnInfo(name = "token_address")
    private String tokenAddress;
    @ColumnInfo(name = "token_amount")
    private String tokenAmount;
    @ColumnInfo(name = "energy")
    private String energy;
    @ColumnInfo(name = "bandwidth")
    private String bandwidth;
    @ColumnInfo(name = "burn")
    private String burn;
    @ColumnInfo(name = "day")
    private String day;
    @ColumnInfo(name = "updated")
    private boolean updated;
    @ColumnInfo(name = "distribution")
    private String distribution;
    @Ignore
    private String address;
    @Ignore
    private String hash;

    @Override
    public String toString() {
        return "YourEntity{" +
                "id=" + id +
                ", uId='" + uId + '\'' +
                ", idType=" + idType +
                ", actionType=" + actionType +
                ", count=" + count +
                ", tokenAddress='" + tokenAddress + '\'' +
                ", tokenAmount='" + tokenAmount + '\'' +
                ", energy='" + energy + '\'' +
                ", bandwidth='" + bandwidth + '\'' +
                ", burn='" + burn + '\'' +
                ", day='" + day + '\'' +
                ", updated=" + updated +
                ", distribution='" + distribution + '\'' +
                ", address='" + address + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }

}
