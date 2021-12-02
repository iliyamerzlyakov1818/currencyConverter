package com.example.currencyconverter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Запускает окно выбора обменной валюты.
 *
 * @author Илья
 */
public class ExchangeCurrencyChoice extends DialogFragment {
    private int choice = 0;

    /**
     * Прослушивает выбор пользователя.
     *
     * @author Илья
     */
    interface ChoiceListener {
        void onClickPositiveButton(String currency, String sign, int choice);
        void onClickNegativeButton();
    }

    ChoiceListener choiceListener;

    /**
     * Получает и присваивает фрагменту окна выбора обменной валюты окно вызова данного фрагмента.
     *
     * @param context информация об окне вызова.
     */
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            choiceListener = (ChoiceListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Создаёт фрагмент окна выбора обменной валюты.
     *
     * @param savedInstanceState сохранённые данные.
     * @return созданный фрагмент окна выбора обменной валюты.
     */
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] currencies = getResources().getStringArray(R.array.currencies);
        String[] signs = getResources().getStringArray(R.array.currencies_signs);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle("Выберите обменную валюту").setSingleChoiceItems(currencies, choice, (dialog, item) ->
                choice = item
        ).setPositiveButton("Ок", (dialog, item) ->
                choiceListener.onClickPositiveButton(currencies[choice], signs[choice], choice)
        ).setNegativeButton("Отмена", (dialog, item) ->
                choiceListener.onClickNegativeButton()
        );

        return dialogBuilder.create();
    }
}
