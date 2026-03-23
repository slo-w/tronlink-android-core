package org.tron.metrics.bean;

import lombok.Data;

@Data
public class StatDataResponse {
    private int code;
    private String message;
    private DataInfo data;
    
    /**
     * Data field in response body
     */
    @Data
    public static class DataInfo {
        /**
         * Text format flag
         */
        private boolean txt;
        
        /**
         * Plain text format flag
         */
        private boolean plain;
    }
}
