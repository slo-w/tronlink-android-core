package org.tron.metrics.reporter;

import org.tron.metrics.bean.StatDataRequest;
import org.tron.metrics.utils.GsonUtils;
import org.tron.metrics.utils.StatDataConverter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Interceptor for data encryption using ts and signature from signed requests
 */
public class DataFormatInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        RequestBody originalBody = request.body();
        if (originalBody == null || !isStatDataRequest(request)) {
            return chain.proceed(request);
        }

        // Check if encryption is needed based on plain status
        if (DataUploader.getInstance().getFormatPlain()) {
            // Plain mode - no encryption needed
            return chain.proceed(request);
        }

        String ts = request.header("ts");
        if (ts == null || ts.isEmpty()) {
            return chain.proceed(request);
        }

        String signature = request.url().queryParameter("signature");
        if (signature == null || signature.isEmpty()) {
            return chain.proceed(request);
        }

        Buffer buffer = new Buffer();
        originalBody.writeTo(buffer);
        String originalJson = buffer.readString(StandardCharsets.UTF_8);

        StatDataRequest originalStatRequest = GsonUtils.gsonToBean(originalJson, StatDataRequest.class);
        if (originalStatRequest == null) {
            return chain.proceed(request);
        }

        StatDataRequest encryptedStatRequest = new StatDataRequest();

        try {
            if (originalStatRequest.getX() != null && !originalStatRequest.getX().isEmpty()) {
                String encryptedXData = StatDataConverter.encryptDataWithTs(originalStatRequest.getX(), ts, signature);
                encryptedStatRequest.setX(encryptedXData);
            }

            if (originalStatRequest.getY() != null && !originalStatRequest.getY().isEmpty()) {
                String encryptedYData = StatDataConverter.encryptDataWithTs(originalStatRequest.getY(), ts, signature);
                encryptedStatRequest.setY(encryptedYData);
            }
        } catch (Exception e) {
            return chain.proceed(request);
        }

        String encryptedJson = GsonUtils.toGsonString(encryptedStatRequest);

        RequestBody encryptedBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                encryptedJson
        );

        Request finalRequest = request.newBuilder()
                .method(request.method(), encryptedBody)
                .build();

        return chain.proceed(finalRequest);
    }

    private boolean isStatDataRequest(Request request) {
        return request.url().toString().contains("api/stat/some");
    }
}