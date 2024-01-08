package com.mesbahi.crudapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    User loggedInUser = SessionManager.getInstance().getCurrentUser();

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);

        // Find the UI elements by their IDs
        Spinner spinnerMonth = findViewById(R.id.spinnerMonth);
        EditText etYear = findViewById(R.id.etYear);
        EditText etConsumptionValue = findViewById(R.id.etConsumptionValue);
        Button btnAddConsumption = findViewById(R.id.btnAddConsumption);

        // Set up the spinner for months
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.months_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Set the year field to the current year and make it non-editable
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        etYear.setText(String.valueOf(currentYear));
        etYear.setEnabled(false); // Make it non-editable

        // Set default selected month
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        spinnerMonth.setSelection(currentMonth);

        // Set an OnClickListener for the Add Consumption button
        btnAddConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values from the input fields
                try {
                    int month = spinnerMonth.getSelectedItemPosition() + 1; // Add 1 because months are 1-indexed
                    int year = Integer.parseInt(etYear.getText().toString());
                    double consumptionValue = Double.parseDouble(etConsumptionValue.getText().toString());

                    // Check if the consumption already exists for the specified month and year
                    if (dbHelper.isWaterConsumptionExists(month, year)) {
                        // Display an error message
                        Toast.makeText(AddActivity.this, "Water consumption already exists for the selected month and year", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the water consumption to the database
                        long id = dbHelper.addWaterConsumption(loggedInUser.getId(),month, year, consumptionValue);

                        if (id != -1) {
                            // Successful insertion
                            Toast.makeText(AddActivity.this, "Water consumption added successfully", Toast.LENGTH_SHORT).show();
                            //finish(); // Close the activity after adding consumption
                            Intent intent = new Intent(AddActivity.this, WaterList.class);
                            startActivity(intent);
                        } else {
                            // Failed insertion
                            Toast.makeText(AddActivity.this, "Failed to add water consumption", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AddActivity.this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
