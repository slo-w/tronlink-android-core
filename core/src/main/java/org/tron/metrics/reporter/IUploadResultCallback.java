package org.tron.metrics.reporter;

import org.tron.metrics.bean.StatDataResponse;

/**
 * date: 2026/1/23
 * desc:
 **/
public interface IUploadResultCallback {
    void onSuccess(StatDataResponse response);

    void onFail(Throwable throwable);

    /**
     * Invoked when the upload is skipped without a network attempt (uploader not
     * initialized or nothing to upload). Default no-op keeps existing hosts working.
     */
    default void onSkipped(String reason) {
    }
}
