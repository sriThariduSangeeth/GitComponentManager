package com.sangeeth.gitbot.retrofitDrive;

import com.google.gson.GsonBuilder;
import com.sangeeth.gitbot.configurations.Properties;
import com.sangeeth.gitbot.retrofitEndPoints.GitAPI;
import okhttp3.Credentials;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

/**
 * @author dtsangeeth
 * @created 28 / 03 / 2020
 * @project GitComponentManager
 */
public class GitRetrofitDrive {

    private String auth = "";
    private String baseUrl;
    private Class<?> className;

    public GitRetrofitDrive(String authToken , String baseUrl) {

        this.auth = authToken;
        this.baseUrl = baseUrl;
        this.className = GitAPI.class;
        

    }

    public Object invoke() throws NullPointerException{
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder().addHeader("Authorization",
                    "Bearer "+this.auth);

            Request newRequest = builder.build();
            return chain.proceed(newRequest);

        }).connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS).cookieJar(new JavaNetCookieJar(new CookieManager()
                )).build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(okHttpClient)
                .build().create(className);
    }

}