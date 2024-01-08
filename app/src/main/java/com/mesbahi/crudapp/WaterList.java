package com.mesbahi.crudapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class WaterList extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    User loggedInUser = SessionManager.getInstance().getCurrentUser();

    DBHelper myDB;
    ArrayList<String> consumption_id, consumption_month, consumption_year, consumption_volume, consumption_amount;

    CustomAdapter customAdapter;

    Spinner spinnerYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_list);

        // Initialize other components and views...

        recyclerView = findViewById(R.id.recyclerview);
        add_button = findViewById(R.id.add_button);

        // Set an OnClickListener for the add_button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the AddActivity when the add_button is clicked
                Intent intent = new Intent(WaterList.this, AddActivity.class);
                startActivity(intent);
            }
        });

        myDB = new DBHelper(WaterList.this);
        consumption_id = new ArrayList<>();
        consumption_month = new ArrayList<>();
        consumption_year = new ArrayList<>();
        consumption_volume = new ArrayList<>();
        consumption_amount = new ArrayList<>();

        // Set up the RecyclerView with the initial CustomAdapter
        customAdapter = new CustomAdapter(WaterList.this, consumption_id,
                consumption_month, consumption_year, consumption_volume, consumption_amount);

        // Set the item click listener
        customAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String consumptionId, String month, String year, String volume, String amount) {
                // Launch DetailsActivity with the clicked item's data
                Intent intent = new Intent(WaterList.this, DetailsActivity.class);
                intent.putExtra("CONSUMPTION_ID", consumptionId);
                intent.putExtra("CONSUMPTION_MONTH", month);
                intent.putExtra("CONSUMPTION_YEAR", year);
                intent.putExtra("CONSUMPTION_VOLUME", volume);
                intent.putExtra("CONSUMPTION_AMOUNT", amount);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(WaterList.this));

        spinnerYear = findViewById(R.id.spinnerYear);

        // Set up the Spinner for years
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.years_array,
                android.R.layout.simple_spinner_item
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);


        // Set the default selection to the current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int position = getPositionOfItem(spinnerYear, String.valueOf(currentYear));
        spinnerYear.setSelection(position);

        // Manually call storeDataInArrays after setting the default selection
        storeDataInArrays(currentYear);

        // Set an item selected listener for the year Spinner
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Load data into arrays and update the adapter based on the selected year
                int selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
                storeDataInArrays(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        //addW();
        Button btnCompareChart = findViewById(R.id.btnCompareChart);
        btnCompareChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ComparisonChartActivity with necessary data
                Intent intent = new Intent(WaterList.this, ComparisonChartActivityWater.class);
                intent.putExtra("userId", loggedInUser.getId()); // Replace with actual user ID
                intent.putExtra("currentYear", Calendar.getInstance().get(Calendar.YEAR));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if spinnerYear.getSelectedItem() is not null before calling toString()
        if (spinnerYear.getSelectedItem() != null) {
            storeDataInArrays(Integer.parseInt(spinnerYear.getSelectedItem().toString()));
        }
    }


    private int getPositionOfItem(Spinner spinner, String item) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        return adapter.getPosition(item);
    }

    void storeDataInArrays(int selectedYear) {
        Cursor cursor = myDB.getAllWaterConsumptionDataByYear(loggedInUser.getId(), selectedYear);
        if (cursor.getCount() == 0) {
            // No data, show empty state
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.emptyStateView).setVisibility(View.VISIBLE);
        } else {
            // Data available, show RecyclerView
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.emptyStateView).setVisibility(View.GONE);

            consumption_id.clear();
            consumption_month.clear();
            consumption_year.clear();
            consumption_volume.clear();
            consumption_amount.clear();

            while (cursor.moveToNext()) {
                consumption_id.add(cursor.getString(0));
                consumption_month.add(cursor.getString(1));
                consumption_year.add(cursor.getString(3));

                // Convert the volume to m³
                double volumeLiters = Double.parseDouble(cursor.getString(4));
                double volumeCubicMeters = volumeLiters;
                consumption_volume.add( volumeCubicMeters + " m³");

                // Display the amount in MAD
                double amountDinars = Double.parseDouble(cursor.getString(5));
                consumption_amount.add( amountDinars + " MAD");
            }
        }
        cursor.close();

        customAdapter.notifyDataSetChanged();
    }
    private void addW(){
        for (int year = 2020; year <= 2023; year++) {
            for (int month = 1; month <= 12; month++) {
                // Generate random values for volume between 150 and 450
                double randomVolume = generateRandomVolume();

                // Call the method to add water consumption with the generated random volume
                myDB.addWaterConsumption(1,month, year, randomVolume);
       }
        }
    }
    // Method to generate random volume values between 150 and 450
    private double generateRandomVolume() {
        Random random = new Random();
        return (random.nextDouble() * 300) + 150;
}
}