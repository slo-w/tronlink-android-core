package org.tron.metrics.bean;

import lombok.Data;

@Data
public class StatDataRequest {
    /**
     * X: Asset data (encrypted)
     */
    private String X;
    
    /**
     * Y: Transaction data (encrypted)
     */
    private String Y;
}
