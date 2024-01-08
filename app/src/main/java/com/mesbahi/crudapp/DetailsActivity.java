package com.mesbahi.crudapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    String consumptionId, month, year, volume, amount;
    TextView tvMonth, tvYear, tvVolume, tvAmount;  // Declare TextViews at the class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get the data passed from the previous activity
        Intent intent = getIntent();
        consumptionId = intent.getStringExtra("CONSUMPTION_ID");
        month = intent.getStringExtra("CONSUMPTION_MONTH");
        year = intent.getStringExtra("CONSUMPTION_YEAR");
        volume = intent.getStringExtra("CONSUMPTION_VOLUME");
        amount = intent.getStringExtra("CONSUMPTION_AMOUNT");

        // Initialize TextViews
        tvMonth = findViewById(R.id.tvMonth);
        tvYear = findViewById(R.id.tvYear);
        tvVolume = findViewById(R.id.tvVolume);
        tvAmount = findViewById(R.id.tvAmount);

        // Display the details in TextViews
        tvMonth.setText("Month: " + month);
        tvYear.setText("Year: " + year);
        tvVolume.setText("Volume: " + volume);
        tvAmount.setText("Amount: " + amount);

        // Set up buttons for update and delete
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);

        // Set onClickListener for update button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch a new activity for updating the consumption data
                Intent updateIntent = new Intent(DetailsActivity.this, UpdateActivity.class);
                updateIntent.putExtra("CONSUMPTION_ID", consumptionId);
                updateIntent.putExtra("CONSUMPTION_MONTH", month);
                updateIntent.putExtra("CONSUMPTION_YEAR", year);
                updateIntent.putExtra("CONSUMPTION_VOLUME", volume);
                updateIntent.putExtra("CONSUMPTION_AMOUNT", amount);
                startActivityForResult(updateIntent, 1); // Use a unique request code
            }
        });

        // Set onClickListener for delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the logic for deleting the consumption data
                showDeleteConfirmationDialog();
            }
        });
    }

    // Handle the result from the UpdateActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Use the same request code that you used while starting the UpdateActivity
            if (resultCode == RESULT_OK) {
                // Update the displayed data in the TextViews or perform any other necessary actions
                updateDisplayedData();
            } else {
                Toast.makeText(this, "Update was not successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to update the displayed data after a successful update
    // Method to update the displayed data after a successful update
    private void updateDisplayedData() {
        // Update the displayed data in TextViews
        // Retrieve the updated data from the database based on consumptionId
        DBHelper dbHelper = new DBHelper(this);
        Cursor updatedData = dbHelper.getWaterConsumptionById(1,Long.parseLong(consumptionId));

        if (updatedData != null && updatedData.moveToFirst()) {
            // Update TextViews with the new data
            int monthIndex = updatedData.getColumnIndex(DBHelper.COLUMN_WATER_MONTH);
            int yearIndex = updatedData.getColumnIndex(DBHelper.COLUMN_WATER_YEAR);
            int volumeIndex = updatedData.getColumnIndex(DBHelper.COLUMN_WATER_VOLUME);
            int amountIndex = updatedData.getColumnIndex(DBHelper.COLUMN_WATER_AMOUNT);

            // Ensure the indices are valid before retrieving data
            if (monthIndex != -1 && yearIndex != -1 && volumeIndex != -1 && amountIndex != -1) {
                month = updatedData.getString(monthIndex);
                year = updatedData.getString(yearIndex);
                volume = updatedData.getString(volumeIndex);
                amount = updatedData.getString(amountIndex);

                tvMonth.setText("Month: " + month);
                tvYear.setText("Year: " + year);
                tvVolume.setText("Volume: " + volume);
                tvAmount.setText("Amount: " + amount);
            }

            updatedData.close();
        }
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform the deletion and finish the activity
                        deleteRecord();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the deletion
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteRecord() {
        DBHelper dbHelper = new DBHelper(this);
        long result = dbHelper.deleteWaterConsumption(Long.parseLong(consumptionId));

        if (result > 0) {
            Toast.makeText(this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after deletion
        } else {
            Toast.makeText(this, "Failed to delete record", Toast.LENGTH_SHORT).show();
        }
    }
}