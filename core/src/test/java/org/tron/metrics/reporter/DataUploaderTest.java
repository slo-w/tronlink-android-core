package org.tron.metrics.reporter;

import org.junit.Assert;
import org.junit.Test;

public class DataUploaderTest {

    @Test
    public void plainFormatAllowed_onlyWhenDebugBuildRequestsIt() {
        Assert.assertTrue(DataUploader.isPlainFormatAllowed(true, true));
        Assert.assertFalse(DataUploader.isPlainFormatAllowed(false, true));
        Assert.assertFalse(DataUploader.isPlainFormatAllowed(true, false));
        Assert.assertFalse(DataUploader.isPlainFormatAllowed(false, false));
    }
}
