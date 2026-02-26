package org.tron.metrics.reporter;

import org.tron.metrics.bean.StatDataResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * date: 2026/1/23
 * desc:
 **/
public interface ReporterHttpApi {
    @Headers({"Content-Type: application/json", "Accept: application/json", "Signature: true"})
    @POST("api/stat/some")
    Observable<StatDataResponse> uploadStatData(@Body RequestBody statData);

}
