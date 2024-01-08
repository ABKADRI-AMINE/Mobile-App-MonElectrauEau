package com.mesbahi.crudapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> consumption_id, consumption_month, consumption_year, consumption_volume, consumption_amount;

    private OnItemClickListener listener;

    public CustomAdapter(Context context,
                         ArrayList<String> consumption_id,
                         ArrayList<String> consumption_month,
                         ArrayList<String> consumption_year,
                         ArrayList<String> consumption_volume,
                         ArrayList<String> consumption_amount) {
        this.context = context;
        this.consumption_id = consumption_id;
        this.consumption_month = consumption_month;
        this.consumption_year = consumption_year;
        this.consumption_volume = consumption_volume;
        this.consumption_amount = consumption_amount;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int monthValue = Integer.parseInt(consumption_month.get(position));
        String frenchMonth = getFrenchMonth(monthValue);
        holder.textViewMonth.setText("Month: " + frenchMonth);
        holder.textViewYear.setText("Year: " + consumption_year.get(position));

        // Display the volume with its unit (m³)
        holder.textViewVolume.setText("Volume: " + consumption_volume.get(position));

        // Display the amount with its unit (MAD)
        holder.textViewAmount.setText("Amount: " + consumption_amount.get(position));

        // Set the click listener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Pass the clicked item's data to the listener
                    listener.onItemClick(
                            consumption_id.get(position),
                            consumption_month.get(position),
                            consumption_year.get(position),
                            consumption_volume.get(position),
                            consumption_amount.get(position)
                    );
                }
            }
        });
    }

    private String getFrenchMonth(int month) {
        String[] frenchMonths = context.getResources().getStringArray(R.array.months_array);

        // Ensure the month value is within a valid range
        if (month >= 1 && month <= frenchMonths.length) {
            return frenchMonths[month - 1];
        } else {
            return "";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String consumptionId, String month, String year, String volume, String amount);
    }

    // Add a setter for the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return consumption_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMonth, textViewYear, textViewVolume, textViewAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textViewMonth);
            textViewYear = itemView.findViewById(R.id.textViewYear);
            textViewVolume = itemView.findViewById(R.id.textViewVolume);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }
}