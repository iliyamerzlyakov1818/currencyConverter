package com.example.currencyconverter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Запускает главное окно приложения.
 *
 * @author Илья
 */
public class MainActivity extends AppCompatActivity implements ExchangeCurrencyChoice.ChoiceListener {
    private Intent converterActivity;
    private final double[] rates = new double[10];
    private int exchangeCurrencyCode = 0;
    private String exchangeCurrency = "RUB";
    private String exchangeCurrencySign = "₽";
    private String json = "";
    private String currentDate = "01.01.2000";
    private JSONObject jo;

    /**
     * Создает главное окно приложения.
     *
     * @param savedInstanceState сохранённые данные.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        converterActivity = new Intent(this, ConverterActivity.class);
        String day = ((day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))).length() == 1) ? "0" + day : day;
        String month = ((month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1)).length() == 1) ? "0" + month : month;
        currentDate = day + "." + month + "." + Calendar.getInstance().get(Calendar.YEAR);

        ((Button) findViewById(R.id.dateButton)).setText(currentDate);

        if (savedInstanceState == null) new AsyncRequest().execute("https://v6.exchangerate-api.com/v6/" + APIConfig.CODE + "/latest/" + exchangeCurrency);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.action_bar);
    }

    /**
     * Восстанавливает сохранённые данные.
     *
     * @param savedInstanceState сохранённые данные.
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        json = savedInstanceState.getString("JSON");
        currentDate = savedInstanceState.getString("CURRENT_DATE");
        exchangeCurrency = savedInstanceState.getString("EXCHANGE_CURRENCY");
        exchangeCurrencySign = savedInstanceState.getString("EXCHANGE_CURRENCY_SIGN");

        ((Button) findViewById(R.id.dateButton)).setText(currentDate);

        try {
            jo = new JSONObject(json);

            getRates();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Сохраняет данные.
     *
     * @param outState сохраняемые данные.
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("JSON", json);
        outState.putString("CURRENT_DATE", currentDate);
        outState.putString("EXCHANGE_CURRENCY", exchangeCurrency);
        outState.putString("EXCHANGE_CURRENCY_SIGN", exchangeCurrencySign);
        super.onSaveInstanceState(outState);
    }

    /**
     * Присваивает выбранную обменную валюту, знак выбранной обменной валюты и номер выбранной обменной валюты.
     *
     * @param currency выбранная обменная валюта.
     * @param sign знак выбранной обменной валюты.
     * @param choice номер выбранной обменной валюты.
     */
    public void onClickPositiveButton(String currency, String sign, int choice) {
        exchangeCurrency = currency;
        exchangeCurrencyCode = choice;
        exchangeCurrencySign = sign;

        getRates();
    }

    /**
     * Выполняет действия при закрытии окна выбора обменной валюты.
     */
    public void onClickNegativeButton() {}

    /**
     * Получает и присваивает курсы валют с API сервера курса валют.
     *
     * @author Илья
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    class AsyncRequest extends AsyncTask<String, String, String> {
        /**
         * Получает курсы валют с API сервера курса валют.
         *
         * @param urls ссылки.
         * @return ответ API сервера курса валют.
         */
        protected String doInBackground(String... urls) {
            try {
                URL URL = new URL(urls[0]);

                URLConnection uc = URL.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String line;

                while ((line = br.readLine()) != null) json = json.concat(line);

                br.close();

                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Присваивает курс валют с API сервера курса валют, если ответ был получен.
         * Если ответ не получен, то курс всех валют равняется 1.
         *
         * @param json полученный ответ API сервера курса валют.
         */
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            try {
                if (json != null) jo = new JSONObject(json);
                else jo = new JSONObject("{\"conversion_rates\":{\"RUB\":1,\"USD\":1,\"EUR\":1,\"JPY\":1,\"AUD\":1,\"AZN\":1,\"GBP\":1,\"AMD\":1,\"BYN\":1,\"BGN\":1}}");

                getRates();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Разделяет курсы валют.
     */
    private void getRates() {
        try {
            double choseRate = jo.getJSONObject("conversion_rates").getDouble(exchangeCurrency);

            for (int index = 0; index < 10; index++) {
                rates[index] = choseRate / jo.getJSONObject("conversion_rates").getDouble(getResources().getStringArray(R.array.currencies)[index]);
            }

            setRates();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Присваивает курсы валют ячейкам валют.
     */
    private void setRates() {
        int id;

        for (int index = 1; index < 10; index++) {
            id = getResources().getIdentifier("tile" + index, "id", "com.example.currencyconverter");

            ((Button) findViewById(id)).setText(getSpan((index == exchangeCurrencyCode) ? 0 : index), TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * Создаёт текст с международным кодом валюты и её курсом к обменной валюте для присваивания к ячейке валюты.
     *
     * @param index номер ячейки валюты.
     * @return созданный текст.
     */
    private SpannableString getSpan(int index) {
        SpannableString ss = new SpannableString(String.format(Locale.getDefault(), getResources().getStringArray(R.array.currencies)[index] + ((rates[index] < 0.1) ? "\n%.4f" : "\n%.2f") + exchangeCurrencySign, rates[index]));

        ss.setSpan(new AbsoluteSizeSpan(16, true), 4, ss.length(), 0);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#3A3B3A")), 4, ss.length(), 0);

        return ss;
    }

    /**
     * Открывает окно для выбора обменной валюты.
     *
     * @param v компонент пользовательского интерфейса.
     */
    public void onClickMenuButton(View v) {
        DialogFragment exchangeCurrencyChoice = new ExchangeCurrencyChoice();

        exchangeCurrencyChoice.setCancelable(false);
        exchangeCurrencyChoice.show(getSupportFragmentManager(), "Выбор обменной валюты");
    }

    /**
     * Передаёт данные валюты присвоенной нажатой ячейке окну конвертации и переходит на него.
     *
     * @param v компонент пользовательского интерфейса.
     */
    public void onClickCurrencyButton(View v) {
        converterActivity.putExtra("BUTTON_TEXT", String.valueOf(((Button) findViewById(v.getId())).getText()));
        converterActivity.putExtra("EXCHANGE_CURRENCY_SIGN", exchangeCurrencySign);

        startActivity(converterActivity);
    }
}