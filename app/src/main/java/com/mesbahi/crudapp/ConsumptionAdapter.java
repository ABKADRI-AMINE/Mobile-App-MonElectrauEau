
package com.mesbahi.crudapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ConsumptionAdapter extends CursorAdapter {

    private ConsumptionItemClickListener itemClickListener;

    public ConsumptionAdapter(Context context, Cursor c, ConsumptionItemClickListener listener) {
        super(context, c, 0);
        this.itemClickListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_consumption, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewMonthYear = view.findViewById(R.id.textViewMonthYear);
        TextView textViewConsumptionValue = view.findViewById(R.id.textViewConsumptionValue);
        TextView textViewCalculatedAmount = view.findViewById(R.id.textViewCalculatedAmount);

        @SuppressLint("Range")int consumptionId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
        @SuppressLint("Range")int month = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_MONTH));
        @SuppressLint("Range")int year = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_YEAR));
        @SuppressLint("Range")double consumptionValue = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_CONSUMPTION_VALUE));
        @SuppressLint("Range")double calculatedAmount = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_CALCULATED_AMOUNT));

        String monthName = getMonthName(month);
        textViewMonthYear.setText(String.format("%s, %s", monthName, year));
        textViewConsumptionValue.setText(String.format("Consumption Value: %.2f kWh", consumptionValue));
        textViewCalculatedAmount.setText(String.format("Calculated Amount: %.2f MAD", calculatedAmount));

        // Set click listener for the whole item
        view.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onConsumptionClick(consumptionId);
            }
        });
    }

    private String getMonthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (month >= 1 && month <= 12) {
            return monthNames[month - 1];
        } else {
            return "Invalid Month";
        }
    }

    // Interface to handle item clicks
    public interface ConsumptionItemClickListener {
        void onConsumptionClick(int consumptionId);
    }
}
