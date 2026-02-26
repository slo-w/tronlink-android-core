package org.tron.metrics.bean;

import lombok.Data;


@Data
public class StatYData {
    /**
     * Encrypted account address, for frontend deduplication only
     */
    private String uId;
    
    /**
     * Address type enum: eg1, watch wallet data not uploaded
     */
    private String idType;
    
    /**
     * Business transaction type enum: eg2 (if not in enum, use fullNode native transaction type)
     */
    private String actionType;
    
    /**
     * Related transaction count
     */
    private String count;
    
    /**
     * Related token address (trx=_, trc10=100****, trc20=Taaa)
     */
    private String tokenAddress;
    
    /**
     * Related transaction total amount
     */
    private String tokenAmount;
    
    /**
     * Related transaction energy consumption
     */
    private String energy;
    
    /**
     * Related transaction bandwidth consumption
     */
    private String bandwidth;
    
    /**
     * Related transaction TRX burn amount (with precision, e.g. 10.5trx)
     */
    private String burn;
    
    /**
     * Date in YYYY-MM-DD format (UTC time, date only)
     */
    private String day;
    
    /**
     * Transaction amount distribution, TRX/USDT enum: eg3
     * Format: A1:1,A2:3
     */
    private String amountDistribution;
}
