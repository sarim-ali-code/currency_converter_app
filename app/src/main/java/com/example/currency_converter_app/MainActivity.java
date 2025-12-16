// src/main/java/com/example/currency_converter_app/MainActivity.java

package com.example.currency_converter_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "ac543f3b0a169963ddb14586";

    private EditText etAmount;
    private AutoCompleteTextView actvFromCurrency;
    private AutoCompleteTextView actvToCurrency;
    private Button btnConvert;
    private TextView tvResult;

    // *** REPLACED THE HARDCODED ARRAY WITH A DYNAMIC LIST ***
    private List<String> currencyList = new ArrayList<>();

    // Fallback list to use if the API connection fails at startup
    private final String[] DEFAULT_CURRENCIES = new String[]{
            "USD - United States Dollar", "EUR - Euro", "GBP - British Pound"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views
        etAmount = findViewById(R.id.et_amount);
        actvFromCurrency = findViewById(R.id.actv_from_currency);
        actvToCurrency = findViewById(R.id.actv_to_currency);
        btnConvert = findViewById(R.id.btn_convert);
        tvResult = findViewById(R.id.tv_result);

        // 2. *** NEW: Call the API to fetch the full currency list ***
        fetchCurrencyCodes();

        // 3. Set up Button Click Listener
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });
    }

    // *** NEW METHOD TO FETCH CURRENCY CODES ***
    private void fetchCurrencyCodes() {
        CurrencyApi api = RetrofitClient.getClient();
        Call<CurrencyCodeResponse> call = api.getCurrencyCodes(API_KEY);

        // Show a loading state until the currencies are ready
        actvFromCurrency.setHint("Loading Currencies...");
        actvToCurrency.setHint("Loading Currencies...");


        call.enqueue(new Callback<CurrencyCodeResponse>() {
            @Override
            public void onResponse(Call<CurrencyCodeResponse> call, Response<CurrencyCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CurrencyCodeResponse codeResponse = response.body();

                    if ("success".equals(codeResponse.getResult())) {
                        currencyList.clear();

                        // Process the list of [Code, Name] arrays from the API
                        for (List<String> codePair : codeResponse.getSupportedCodes()) {
                            // Format: "USD - United States Dollar"
                            currencyList.add(codePair.get(0) + " - " + codePair.get(1));
                        }

                        // Populate dropdowns with the full API list
                        setupCurrencyDropdowns(currencyList.toArray(new String[0]));

                    } else {
                        Toast.makeText(MainActivity.this, "API Error: Could not get code list.", Toast.LENGTH_LONG).show();
                        setupCurrencyDropdowns(DEFAULT_CURRENCIES); // Use fallback list
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Network Error: Failed to connect for currency list.", Toast.LENGTH_LONG).show();
                    setupCurrencyDropdowns(DEFAULT_CURRENCIES); // Use fallback list
                }
            }

            @Override
            public void onFailure(Call<CurrencyCodeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Connection failed. Using default currencies.", Toast.LENGTH_LONG).show();
                setupCurrencyDropdowns(DEFAULT_CURRENCIES); // Use fallback list
            }
        });
    }

    // *** UPDATED METHOD TO ACCEPT A DYNAMIC LIST ***
    private void setupCurrencyDropdowns(String[] currencies) {

        // Restore hints
        actvFromCurrency.setHint("From");
        actvToCurrency.setHint("To");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                currencies // Use the fully fetched list!
        );

        actvFromCurrency.setAdapter(adapter);
        actvToCurrency.setAdapter(adapter);

        // Set defaults from the loaded list
        if (currencies.length > 0) {
            actvFromCurrency.setText(currencies[0], false);
        }
        if (currencies.length > 1) {
            actvToCurrency.setText(currencies[1], false);
        }
    }

    // The convertCurrency method remains the same and is fine for conversion.
    private void convertCurrency() {
        String amountText = etAmount.getText().toString();
        String fromCurrencyFull = actvFromCurrency.getText().toString();
        String toCurrencyFull = actvToCurrency.getText().toString();

        if (TextUtils.isEmpty(amountText) || TextUtils.isEmpty(fromCurrencyFull) || TextUtils.isEmpty(toCurrencyFull)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract currency codes (e.g., "USD" from "USD - United States Dollar")
        String fromCode = fromCurrencyFull.substring(0, 3).trim();
        String toCode = toCurrencyFull.substring(0, 3).trim();

        // 4. API Call
        CurrencyApi api = RetrofitClient.getClient();
        Call<ConversionResponse> call = api.getConversionRate(API_KEY, fromCode, toCode);

        // Show a temporary message
        tvResult.setText("Loading...");

        call.enqueue(new Callback<ConversionResponse>() {
            @Override
            public void onResponse(Call<ConversionResponse> call, Response<ConversionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ConversionResponse conversionResponse = response.body();

                    if ("success".equals(conversionResponse.getResult()) && conversionResponse.getConversionRate() != null) {
                        double rate = conversionResponse.getConversionRate();
                        double convertedAmount = amount * rate;

                        // Format the result to two decimal places
                        DecimalFormat df = new DecimalFormat("#,##0.00");
                        String resultText = df.format(convertedAmount) + " " + toCode;

                        tvResult.setText(resultText);

                        // Optional: Show the exchange rate in a toast
                        Toast.makeText(MainActivity.this,
                                "Rate: 1 " + fromCode + " = " + df.format(rate) + " " + toCode,
                                Toast.LENGTH_LONG).show();

                    } else {
                        // Handle API specific error (e.g., invalid currency codes)
                        tvResult.setText("Error fetching rate.");
                        Toast.makeText(MainActivity.this, "API Error: Check currency codes.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle HTTP error (e.g., 404, 500)
                    tvResult.setText("Conversion Failed");
                    Toast.makeText(MainActivity.this, "Network Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConversionResponse> call, Throwable t) {
                // Handle total failure (e.g., no internet, connection timeout)
                tvResult.setText("Conversion Failed");
                Toast.makeText(MainActivity.this, "Connection Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}