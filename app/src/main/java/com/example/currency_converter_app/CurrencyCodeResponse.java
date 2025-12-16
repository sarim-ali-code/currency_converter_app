// src/main/java/com/example/currency_converter_app/CurrencyCodeResponse.java

package com.example.currency_converter_app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrencyCodeResponse {

    @SerializedName("result")
    private String result;

    // This field will hold the list of lists: e.g., [ ["USD", "United States Dollar"], ["EUR", "Euro"] ]
    @SerializedName("supported_codes")
    private List<List<String>> supportedCodes;

    public String getResult() {
        return result;
    }

    public List<List<String>> getSupportedCodes() {
        return supportedCodes;
    }
}