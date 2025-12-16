// src/main/java/com/example/currency_converter_app/CurrencyApi.java

package com.example.currency_converter_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
// Removed unused import: import retrofit2.http.Query;

public interface CurrencyApi {

    // 1. Conversion Endpoint (Existing)
    @GET("v6/{apiKey}/pair/{from}/{to}")
    Call<ConversionResponse> getConversionRate(
            @Path("apiKey") String apiKey,
            @Path("from") String fromCurrencyCode,
            @Path("to") String toCurrencyCode
    );

    // 2. Currency List Endpoint (NEW: For fetching the full list)
    @GET("v6/{apiKey}/codes")
    Call<CurrencyCodeResponse> getCurrencyCodes(
            @Path("apiKey") String apiKey
    );
}