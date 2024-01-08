package com.mesbahi.crudapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {

    EditText etUpdatedVolume;
    Button btnSaveUpdate;

    DBHelper dbHelper;

    String consumptionId, month, year, volume, amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        etUpdatedVolume = findViewById(R.id.etUpdatedVolume);
        btnSaveUpdate = findViewById(R.id.btnSaveUpdate);

        dbHelper = new DBHelper(this);

        // Get data from the intent
        Intent intent = getIntent();
        consumptionId = intent.getStringExtra("CONSUMPTION_ID");
        month = intent.getStringExtra("CONSUMPTION_MONTH");
        year = intent.getStringExtra("CONSUMPTION_YEAR");
        volume = intent.getStringExtra("CONSUMPTION_VOLUME");
        amount = intent.getStringExtra("CONSUMPTION_AMOUNT");

        // Display current volume in the EditText
        etUpdatedVolume.setText(volume);

        // Set click listener for update button
        btnSaveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the water consumption with the new volume
                updateWaterConsumption();
            }
        });
    }


    private void updateWaterConsumption() {
        try {
            double updatedVolume = Double.parseDouble(etUpdatedVolume.getText().toString());

            // Calculate the amount based on the updated volume
            double updatedAmount = dbHelper.calculateWaterAmount(updatedVolume);

            // Update the water consumption in the database
            int numRowsAffected = dbHelper.updateWaterConsumption(
                    Long.parseLong(consumptionId),
                    Integer.parseInt(month),
                    Integer.parseInt(year),
                    updatedVolume
            );

            if (numRowsAffected > 0) {
                Toast.makeText(this, "Record updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateActivity.this, WaterList.class);
                startActivity(intent);

                setResult(RESULT_OK); // Set the result to indicate success
                finish(); // Close the activity after updating
            } else {
                Toast.makeText(this, "Failed to update record", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid numeric value for volume", Toast.LENGTH_SHORT).show();
        }

    }


}