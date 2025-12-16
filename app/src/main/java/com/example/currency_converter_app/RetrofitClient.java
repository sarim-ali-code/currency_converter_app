// src/main/java/com/example/currency_converter_app/RetrofitClient.java

package com.example.currency_converter_app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // !!! IMPORTANT: Replace this with the BASE URL of your chosen API. 
    // Example: "https://v6.exchangerate-api.com/"
    private static final String BASE_URL = "https://v6.exchangerate-api.com/";

    public static CurrencyApi getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CurrencyApi.class);
    }
}