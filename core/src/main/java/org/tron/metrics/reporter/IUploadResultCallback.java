package org.tron.metrics.reporter;

import org.tron.metrics.bean.StatDataResponse;

/**
 * date: 2026/1/23
 * desc:
 **/
public interface IUploadResultCallback {
    void onSuccess(StatDataResponse response);

    void onFail(Throwable throwable);
}
