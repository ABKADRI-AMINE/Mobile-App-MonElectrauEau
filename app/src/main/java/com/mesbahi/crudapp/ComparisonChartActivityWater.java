package com.mesbahi.crudapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComparisonChartActivityWater extends AppCompatActivity {
    User loggedInUser = SessionManager.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison_chart_water);

        // Assuming you have a BarChart view in your layout with the id 'barChart'
        BarChart barChart = findViewById(R.id.barChart);

        // Fetch consumption data for the actual month and the previous month
        List<Float> consumptionValues = getConsumptionDataForComparisonWater();

        // Get the current month and year
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Months are 0-indexed
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Calculate the previous month dynamically
        int[] previousMonthYear = getPreviousMonthAndYear(currentMonth, currentYear);
        int previousMonth = previousMonthYear[0];
        int previousYear = previousMonthYear[1];

        // Create entries for the BarChart
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, consumptionValues.get(0))); // Actual month
        entries.add(new BarEntry(1, consumptionValues.get(1))); // Previous month

        BarDataSet barDataSet = new BarDataSet(entries, "Monthly Consumption");
        BarData barData = new BarData(barDataSet);

        // Customize the chart appearance if needed
        barChart.setData(barData);

        // Set labels for each bar
        ArrayList<String> labels = new ArrayList<>();
        labels.add(getMonthYearLabel(currentMonth, currentYear)); // Actual month
        labels.add(getMonthYearLabel(previousMonth, previousYear)); // Previous month

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1);

        barChart.invalidate(); // Refresh the chart


    }



    private List<Float> getConsumptionDataForComparisonWater() {
        List<Float> consumptionValues = new ArrayList<>();

        // Replace 1 with the actual user ID
        long userId = loggedInUser.getId();

        // Get the current month and year
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Months are 0-indexed
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Fetch consumption for the actual month
        float actualMonthConsumption = getConsumptionForMonth(userId, currentMonth, currentYear);
        consumptionValues.add(actualMonthConsumption);

        // Calculate the previous month dynamically
        int[] previousMonthYear = getPreviousMonthAndYear(currentMonth, currentYear);
        int previousMonth = previousMonthYear[0];
        int previousYear = previousMonthYear[1];

        // Fetch consumption for the previous month
        float previousMonthConsumption = getConsumptionForMonth(userId, previousMonth, previousYear);
        consumptionValues.add(previousMonthConsumption);

        return consumptionValues;
    }

    private int[] getPreviousMonthAndYear(int currentMonth, int currentYear) {
        int[] result = new int[2];

        int previousMonth = currentMonth - 1;
        int previousYear = currentYear;

        if (previousMonth == 0) {
            // If the previous month is January, set it to December and adjust the year
            previousMonth = 12;
            previousYear--;
        }

        result[0] = previousMonth;
        result[1] = previousYear;

        return result;
    }

    private float getConsumptionForMonth(long userId, int month, int year) {
        // Replace with your actual method to fetch consumption data from the database
        float consumptionValue = 0.0f;

        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getWaterConsumptionForMonth(userId, month, year);

        if (cursor.moveToFirst()) {
            consumptionValue = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMN_WATER_VOLUME));
        }

        cursor.close();
        dbHelper.close();

        return consumptionValue;
    }
    private String getMonthYearLabel(int month, int year) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String monthName = monthNames[month - 1];
        return monthName + " " + year;
    }

}