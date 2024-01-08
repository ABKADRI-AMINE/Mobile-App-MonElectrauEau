package com.mesbahi.crudapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Move this line here

        // Find the buttons by their IDs
        Button btnViewConsumptionWater = findViewById(R.id.btnViewConsumptionWater);
        Button btnViewConsumptions = findViewById(R.id.btnViewConsumptions);

        // Set an OnClickListener for the "View Consumption List" button
        btnViewConsumptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ConsumptionListActivity when the button is clicked
                Intent intent = new Intent(MainActivity.this, ConsumptionListActivity.class);
                startActivity(intent);
            }
        });

        // Set an OnClickListener for the "Add New Consumption" button
        btnViewConsumptionWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the NewConsumptionActivity when the button is clicked
                Intent intent = new Intent(MainActivity.this, WaterList.class);
                startActivity(intent);
            }
        });

        // Initialize the DBHelper and insert sample data
        dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase(); // Ensure the database is created
        //dbHelper.insertSampleData();
    }
}
