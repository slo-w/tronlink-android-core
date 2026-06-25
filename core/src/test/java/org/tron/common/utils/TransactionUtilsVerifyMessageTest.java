package org.tron.common.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Regression guard for S-03: verifyMessage must never validate a too-short
 * signature as true and must never let an exception escape. Behavior is the
 * same before and after the fix (the catch block already returns false); this
 * locks the contract so a future refactor that removes the catch stays safe.
 */
public class TransactionUtilsVerifyMessageTest {

    private static final String ANY_ADDRESS = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";

    @Test
    public void shortHexSignature_returnsFalse() {
        // "0x1234" decodes to 2 bytes (< 65); must return false, not throw.
        boolean result = TransactionUtils.verifyMessage("deadbeef", "0x1234", ANY_ADDRESS);
        Assert.assertFalse(result);
    }

    @Test
    public void emptySignature_returnsFalse() {
        boolean result = TransactionUtils.verifyMessage("deadbeef", "", ANY_ADDRESS);
        Assert.assertFalse(result);
    }
}
