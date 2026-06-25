package org.tron.common.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.tron.common.crypto.datatypes.generated.Uint256;

import java.math.BigInteger;

public class TypeDecoderTest {

    /** A full 32-byte (64 hex char) ABI word encoding the value 1. */
    private static final String WORD_ONE =
            "0000000000000000000000000000000000000000000000000000000000000001";

    @Test(expected = TypeMappingException.class)
    public void decodeNumeric_truncatedInput_throwsTypeMappingException() {
        // "1234" is 2 bytes — far short of the 32-byte ABI word width.
        TypeDecoder.decodeNumeric("1234", Uint256.class);
    }

    @Test
    public void decodeNumeric_fullWord_decodesValue() {
        Uint256 value = TypeDecoder.decodeNumeric(WORD_ONE, Uint256.class);
        Assert.assertEquals(BigInteger.ONE, value.getValue());
    }
}
