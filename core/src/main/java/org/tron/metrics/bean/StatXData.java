package org.tron.metrics.bean;

import lombok.Data;

@Data
public class StatXData {
    /**
     * Encrypted account address, for frontend deduplication only
     */
    private String uId;
    
    /**
     * Address type enum: eg1, watch wallet data not uploaded
     */
    private String idType;
    
    /**
     * TRX asset amount (with precision, e.g. 10.5trx)
     */
    private String trxBalance;
    
    /**
     * USDT asset amount (with precision, e.g. 10.5usdt)
     */
    private String usdtBalance;
    
    /**
     * USD asset amount (with precision, e.g. 10.5usd)
     */
    private String usdBalance;
    
    /**
     * Date in YYYY-MM-DD format (UTC time, date only)
     */
    private String day;
}
