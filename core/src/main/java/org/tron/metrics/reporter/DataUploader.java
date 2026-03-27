package org.tron.metrics.reporter;

import org.tron.common.utils.LogUtils;
import org.tron.metrics.bean.StatDataRequest;
import org.tron.metrics.repository.IBalanceRepository;
import org.tron.metrics.repository.ITransactionRepository;
import org.tron.metrics.utils.GsonUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Data upload flow manager
 */
public class DataUploader {
    private static final String TAG = "ReportLog";
    private IBalanceRepository balanceRepository;
    private ITransactionRepository transactionRepository;
    private boolean formatPlain = false;
    private OkHttpClient okHttpClient;
    private String baseUrl;
    private Disposable disposable;

    private DataUploader() {
    }

    public static DataUploader getInstance() {
        return Holder.INSTANCE;
    }

    public void init(IBalanceRepository balanceRepository,
                     ITransactionRepository transactionCache,
                     boolean formatPlain,
                     OkHttpClient okHttpClient,
                     String baseUrl) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionCache;
        this.formatPlain = formatPlain;
        this.okHttpClient = okHttpClient;
        this.baseUrl = baseUrl;
    }

    public boolean getFormatPlain() {
        return this.formatPlain;
    }

    public void upload(IUploadResultCallback iUploadResultCallback) {
        if (this.okHttpClient == null || this.baseUrl == null) {
            return;
        }
        long startTime = System.currentTimeMillis();
        try {
            DataPreparationManager.DataPreparationResult prepResult = DataPreparationManager.prepareUploadData(balanceRepository, transactionRepository);

            if (!prepResult.hasData()) {
                LogUtils.i(TAG, "No data needs to be uploaded");
                return;
            }

            executeNetworkRequest(prepResult, startTime, iUploadResultCallback);
        } catch (Exception e) {
            LogUtils.e(TAG, "Data upload flow failed: " + e.getMessage());
        }
    }

    private void executeNetworkRequest(DataPreparationManager.DataPreparationResult prepResult, long startTime, IUploadResultCallback iUploadResultCallback) {
        try {
            StatDataRequest statRequest = prepResult.getRequest();

            okhttp3.RequestBody requestBody = createRequestBody(statRequest);

            ReporterHttpApi api = createStatDataAPI();

            disposable = api.uploadStatData(requestBody).subscribeOn(Schedulers.io()).subscribe(statDataResponse -> {
                if (statDataResponse != null && statDataResponse.getData() != null) {
                    deleteCachedData(statDataResponse.getData().isTxt(), prepResult);
                }

                if (iUploadResultCallback != null) {
                    iUploadResultCallback.onSuccess(statDataResponse);
                }
            }, throwable -> {
                if (iUploadResultCallback != null) {
                    iUploadResultCallback.onFail(throwable);
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, "Network request exception: " + e.getMessage());
        }
    }

    private okhttp3.RequestBody createRequestBody(StatDataRequest statRequest) {
        String jsonString = GsonUtils.toGsonString(statRequest);
        return okhttp3.RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonString
        );
    }

    private ReporterHttpApi createStatDataAPI() {
        okhttp3.OkHttpClient httpClient = this.okHttpClient.newBuilder()
                .addInterceptor(new DataFormatInterceptor())
                .build();

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .client(httpClient)
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(ReporterHttpApi.class);
    }

    private void deleteCachedData(boolean result, DataPreparationManager.DataPreparationResult prepResult) {
        try {
            if (result) {
                // Delete balance data using uploaded data
                if (prepResult.getBalanceList() != null && !prepResult.getBalanceList().isEmpty()) {
                    balanceRepository.updateAndDelete(prepResult.getBalanceList());
                }
                // Delete transaction data using uploaded data
                if (prepResult.getTransactionList() != null && !prepResult.getTransactionList().isEmpty()) {
                    this.transactionRepository.updateAndDeleteData(prepResult.getTransactionList());
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "Cache deletion failed: " + e.getMessage());
        }
    }

    private static class Holder {
        private static final DataUploader INSTANCE = new DataUploader();
    }

}
