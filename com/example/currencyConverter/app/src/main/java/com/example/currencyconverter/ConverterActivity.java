package com.example.currencyconverter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Locale;

/**
 * Запускает окно конвертации.
 *
 * @author Илья
 */
public class ConverterActivity extends AppCompatActivity {
    private double rate;

    /**
     * Создаёт окно конвертации.
     *
     * @param savedInstanceState сохранённые данные.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_converter);

        String full = getIntent().getExtras().getString("BUTTON_TEXT");
        String sign = getIntent().getExtras().getString("EXCHANGE_CURRENCY_SIGN");
        String currency = full.substring(0, 3);
        String rateString = full.substring(4);
        int code = getCurrencyCode(currency);
        EditText amount = findViewById(R.id.amount);
        TextView converted = findViewById(R.id.converted);
        rate = Double.parseDouble(rateString.replaceAll("[^.0-9]", ""));

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.action_bar_currency);

        TextView actionBarTitle = getSupportActionBar().getCustomView().findViewById(R.id.action_bar_title);

        actionBarTitle.setText(currency);
        converted.setText("0".concat(sign));
        ((TextView) findViewById(R.id.rateNumber)).setText(rateString);
        ((TextView) findViewById(R.id.currency)).setText(currency);
        ((TextView) findViewById(R.id.currencyName)).setText(getResources().getStringArray(R.array.currencies_names)[code]);
        amount.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 || s.toString().equals(".")) converted.setText("0".concat(sign));
                else if (s.length() > 3 && s.toString().contains(".") && !s.toString().substring(s.length() - 3, s.length()).contains(".")) {
                        amount.setText(s.subSequence(0, s.length() - 1));
                        amount.setSelection(s.length() - 1);
                } else if (s.length() > 16) {
                    amount.setText(s.subSequence(0, 16));
                    amount.setSelection(16);
                } else {
                    double resultNumber = Double.parseDouble(s.toString()) * rate;
                    String result;

                    if (resultNumber == Math.floor(resultNumber)) result = String.format(Locale.getDefault(), "%.0f", resultNumber);
                    else result = String.format(Locale.getDefault(), ((resultNumber < 0.1) ? "%.4f" : "%.2f"), resultNumber);

                    converted.setText(result.concat(sign));
                }

                if (s.length() > 10) converted.setTextSize(14);
                else converted.setTextSize(18);
            }
        });
    }

    /**
     * Определяет номер выбранной валюты.
     * @param currency международный код выбранной валюты.
     * @return номер выбранной валюты.
     */
    private int getCurrencyCode(String currency) {
        switch (currency) {
            case "RUB":
                return 0;
            case "USD":
                return 1;
            case "EUR":
                return 2;
            case "JPY":
                return 3;
            case "AUD":
                return 4;
            case "AZN":
                return 5;
            case "GBP":
                return 6;
            case "AMD":
                return 7;
            case "BYN":
                return 8;
            default:
                return 9;
        }
    }

    /**
     * Закрывает окно конвертации.
     *
     * @param v компонент пользовательского интерфейса.
     */
    public void onClickBackButton(View v) {
        finish();
    }
}