package com.mesbahi.crudapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConsumptionListActivity extends AppCompatActivity implements ConsumptionAdapter.ConsumptionItemClickListener{

    private ConsumptionAdapter consumptionAdapter;
    private DBHelper dbHelper;
    private Spinner spinnerYear;
    User loggedInUser = SessionManager.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_list);


        dbHelper = new DBHelper(this);
        //dbHelper.insertConsumptionsFor2023();
        dbHelper.getWritableDatabase(); // Ensure the database is created

        ListView listView = findViewById(R.id.listViewConsumptions);
        consumptionAdapter = new ConsumptionAdapter(this, null, this); // Pass the listener
        listView.setAdapter(consumptionAdapter);
        spinnerYear = findViewById(R.id.spinnerYear);
        setupYearSpinner();

        // Handle year selection
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Reload consumptions for the selected year
                loadConsumptionsForUser(loggedInUser.getId(), getSelectedYear());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        FloatingActionButton fabAddConsumption = findViewById(R.id.fabAddConsumption);
        fabAddConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsumptionListActivity.this, NewConsumptionActivity.class);
                startActivity(intent);

            }


        });
        Button btnCompareChart = findViewById(R.id.btnCompareChart);
        btnCompareChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ComparisonChartActivity with necessary data
                Intent intent = new Intent(ConsumptionListActivity.this, ComparisonChartActivity.class);
                intent.putExtra("userId", loggedInUser.getId()); // Replace with actual user ID
                intent.putExtra("currentYear", Calendar.getInstance().get(Calendar.YEAR));
                startActivity(intent);
            }
        });
    }
    @Override
    public void onConsumptionClick(int consumptionId) {
        showEditDeleteDialog(consumptionId);
    }
    private void loadConsumptionsForUser(long userId, int selectedYear) {
        Cursor cursor;

        if (selectedYear == 0) {
            // If "All" is selected, load all consumptions for the user
            cursor = dbHelper.getAllElectricityConsumptionsForUser(userId);
        } else {
            // If a specific year is selected, load consumptions for that year
            cursor = dbHelper.getElectricityConsumptionsForUserAndYear(userId, selectedYear);
        }

        consumptionAdapter.swapCursor(cursor);
    }


    private void showEditDeleteDialog(int consumptionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete Consumption");
        builder.setMessage("Choose an action for the selected consumption.");

        // Add buttons to the dialog
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the edit action
                // Start the EditConsumptionActivity with the consumption ID
                Intent editIntent = new Intent(ConsumptionListActivity.this, EditConsumptionActivity.class);
                editIntent.putExtra("consumptionId", consumptionId);
                startActivity(editIntent);
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the delete action
                // You can show a confirmation dialog before deleting or directly delete the consumption
                showDeleteConfirmationDialog(consumptionId);
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(int consumptionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this consumption?");

        // Add buttons to the confirmation dialog
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the deletion action
                boolean deleted = deleteConsumption(consumptionId);
                if (deleted) {
                    Toast.makeText(ConsumptionListActivity.this, "Consumption deleted", Toast.LENGTH_SHORT).show();
                    // Refresh the list after deletion
                    loadConsumptionsForUser(loggedInUser.getId(),0);
                } else {
                    Toast.makeText(ConsumptionListActivity.this, "Failed to delete consumption", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog if "No" is clicked
                dialog.dismiss();
            }
        });

        // Show the confirmation dialog
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }


    private boolean deleteConsumption(int consumptionId) {
        // Use your DBHelper to delete the consumption with the given ID
        return dbHelper.deleteConsumption(consumptionId);
    }


    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    private void setupYearSpinner() {
        // Get a list of unique years from the database
        List<Integer> years = dbHelper.getAllUniqueYears();

        // Convert the list of integers to a list of strings
        List<String> yearStrings = new ArrayList<>();
        yearStrings.add("All"); // Add "All" option at the beginning
        for (Integer year : years) {
            yearStrings.add(String.valueOf(year));
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearStrings);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerYear.setAdapter(adapter);
    }


    private int getSelectedYear() {
        // Get the selected year from the spinner
        String selectedYearString = (String) spinnerYear.getSelectedItem();

        // Convert the selected year string to an integer
        if ("All".equals(selectedYearString)) {
            return 0; // Return 0 for "All"
        } else {
            return Integer.parseInt(selectedYearString);
        }
    }





}
