package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // retrofit : 안드로이드 및 자바용 Type-Safe HTTP 클라이언트

    private static Retrofit rcRetrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (rcRetrofit == null) {
            rcRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    // addConverterFactory : JSON -> POJO class 형식으로 자동 변환
                    // POJO : '화관'의 Cosmetics 클래스 같은 거
                    .build();
        }
        return rcRetrofit;
    }
}
