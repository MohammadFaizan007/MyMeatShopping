package com.mymeatshop.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.mymeatshop.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Vivek on 1/8/18.
 */

public class ServiceGenerator {

    private static ServiceGenerator serviceGenerator;

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    // TODO SERVICE FOR FILE UPLOAD THROUGH MULTIPART
//    public static <S> S createServiceFile(Class<S> serviceClass) {
//        httpClient.readTimeout(60, TimeUnit.SECONDS);
//        httpClient.connectTimeout(120, TimeUnit.SECONDS);
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//                Request.Builder requestBuilder;
//                requestBuilder = original.newBuilder()
//                        .header("Content-Type", "multipart/form-data")
//                        .method(original.method(), original.body());
//                Request request = requestBuilder.build();
//                return chain.proceed(request);
//            }
//        });
//        OkHttpClient client = httpClient.build();
//        Retrofit retrofit = builderFile.client(client).build();
//        return retrofit.create(serviceClass);
//    }

    // TODO SERVICE FOR IIA SHOPPING
    public static <S> S createServiceShopping(Class<S> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_SHOPPING)
                .client(provideOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createGuestShopping(Class<S> serviceClass) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        /* if(BuildConfig.DEBUG){*/
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_GUEST_SHOPPING)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    // TODO SERVICE FOR UTILITY (LIKE RECHARGE,BILL PAYMENT)

    private static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).connectTimeout(2, TimeUnit.MINUTES)
                .build();
    }

    ////////////////////////////M2P V2 Starts///////////////////////////

    public static ServiceGenerator getInstance() {
        if (serviceGenerator == null) serviceGenerator = new ServiceGenerator();
        return serviceGenerator;
    }

    // TODO SERVICE FOR LOGIN


    static class AuthenticationInterceptor implements Interceptor {
        private String authToken;

        AuthenticationInterceptor(String token) {
            this.authToken = token;
        }

        @NotNull
        @Override
        public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + authToken);

            Request request = builder.build();
            return chain.proceed(request);
        }
    }


}