package org.tron.metrics.utils;

import java.math.BigDecimal;

public class TokenAmountClassifier {

    public static String classifyTokenAmount(String tokenAmount) {
        BigDecimal amount;
        try {
            amount = (tokenAmount == null || tokenAmount.isEmpty())
                    ? BigDecimal.ZERO
                    : new BigDecimal(tokenAmount.replace(",", ""));
        } catch (NumberFormatException e) {
            amount = BigDecimal.ZERO;
        }

        if (amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(BigDecimal.ONE) <= 0) {
            return "A1";
        } else if (amount.compareTo(BigDecimal.ONE) > 0 && amount.compareTo(BigDecimal.TEN) <= 0) {
            return "A2";
        } else if (amount.compareTo(BigDecimal.TEN) > 0 && amount.compareTo(new BigDecimal("100")) <= 0) {
            return "A3";
        } else if (amount.compareTo(new BigDecimal("100")) > 0 && amount.compareTo(new BigDecimal("1000")) <= 0) {
            return "A4";
        } else if (amount.compareTo(new BigDecimal("1000")) > 0 && amount.compareTo(new BigDecimal("10000")) <= 0) {
            return "A5";
        } else if (amount.compareTo(new BigDecimal("10000")) > 0 && amount.compareTo(new BigDecimal("100000")) <= 0) {
            return "A6";
        } else if (amount.compareTo(new BigDecimal("100000")) > 0 && amount.compareTo(new BigDecimal("1000000")) <= 0) {
            return "A7";
        } else if (amount.compareTo(new BigDecimal("1000000")) > 0 && amount.compareTo(new BigDecimal("10000000")) <= 0) {
            return "A8";
        } else if (amount.compareTo(new BigDecimal("10000000")) > 0) {
            return "A9";
        } else {
            return "A0"; // amount <= 0
        }
    }
}

