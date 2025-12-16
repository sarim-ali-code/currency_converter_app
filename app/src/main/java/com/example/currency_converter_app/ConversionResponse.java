// src/main/java/com/example/currency_converter_app/ConversionResponse.java

package com.example.currency_converter_app;

import com.google.gson.annotations.SerializedName;

public class ConversionResponse {

    @SerializedName("result")
    private String result;

    @SerializedName("conversion_rate")
    private Double conversionRate;

    // The API you choose might have a different key for the rate,
    // so you'll adjust this class accordingly.

    // You'll also need a model for fetching the list of all currencies, if your API provides one.

    public String getResult() {
        return result;
    }

    public Double getConversionRate() {
        return conversionRate;
    }
}