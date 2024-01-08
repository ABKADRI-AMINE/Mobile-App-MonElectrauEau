package com.mesbahi.crudapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Toast;

public class EditConsumptionActivity extends AppCompatActivity {
    private ConsumptionAdapter consumptionAdapter;

    private EditText editTextConsumptionValue;
    private Button buttonSave;
    private DBHelper dbHelper;
    private int consumptionId; // Declare the variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consumption);

        dbHelper = new DBHelper(this);

        editTextConsumptionValue = findViewById(R.id.editTextConsumptionValue);
        buttonSave = findViewById(R.id.buttonSave);

        // Get the consumption ID from the intent
        consumptionId = getIntent().getIntExtra("consumptionId", -1);

        // Load the existing consumption details using the ID and populate the form
        loadConsumptionDetails(consumptionId);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the save action
                saveEditedConsumption(consumptionId);
            }
        });
    }

    private void loadConsumptionDetails(int consumptionId) {
        // TODO: Implement this method to load details from the database and populate the form
        // Example: Consumption consumption = dbHelper.getConsumptionDetails(consumptionId);
        //          editTextConsumptionValue.setText(String.valueOf(consumption.getConsumptionValue()));
    }

    private void saveEditedConsumption(int consumptionId) {
        // Retrieve edited details from the form
        double editedConsumptionValue = Double.parseDouble(editTextConsumptionValue.getText().toString());

        // Convert the double value to int if required
        int editedConsumptionValueInt = (int) editedConsumptionValue;

        // TODO: Implement this method to update the consumption in the database
        boolean updated = dbHelper.updateConsumption(consumptionId, editedConsumptionValueInt);

        if (updated) {
            Toast.makeText(this, "Consumption updated", Toast.LENGTH_SHORT).show();

            // Reload the page to reflect the changes
            loadConsumptionsForUser(1);

            // Close the activity after saving
            finish();
        } else {
            Toast.makeText(this, "Failed to update consumption", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadConsumptionsForUser(int userId) {
        Cursor cursor = dbHelper.getAllElectricityConsumptionsForUser(userId);
        consumptionAdapter.swapCursor(cursor);
    }
}
