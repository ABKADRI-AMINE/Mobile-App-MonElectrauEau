package com.mesbahi.crudapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBHelper extends SQLiteOpenHelper {
    // Define table and column names
    public static final String TABLE_ELECTRICITY_CONSUMPTION = "electricity_consumption";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_CONSUMPTION_VALUE = "consumption_value";
    public static final String COLUMN_CALCULATED_AMOUNT = "calculated_amount";

    // Database information
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "water_consumption";
    public static final String COLUMN_WATER_ID = "_id";  // Unique ID for water consumption
    public static final String COLUMN_WATER_MONTH = "month";
    public static final String COLUMN_WATER_YEAR = "year";
    public static final String COLUMN_WATER_VOLUME = "volume";  // Volume in m^2
    public static final String COLUMN_WATER_AMOUNT = "amount";  // Amount in DH



    // Create table query
    private static final String CREATE_TABLE_ELECTRICITY_CONSUMPTION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ELECTRICITY_CONSUMPTION + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_MONTH + " INTEGER, " +
                    COLUMN_YEAR + " INTEGER, " +
                    COLUMN_CONSUMPTION_VALUE + " REAL, " +
                    COLUMN_CALCULATED_AMOUNT + " REAL, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES users(id)" +
                    ");";


    public DBHelper(@Nullable Context context) {
        super(context, TABLE_ELECTRICITY_CONSUMPTION, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the electricity_consumption table
        sqLiteDatabase.execSQL(CREATE_TABLE_ELECTRICITY_CONSUMPTION);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, fname TEXT, lname TEXT,phone TEXT)");
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_WATER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WATER_MONTH + " INTEGER, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_WATER_YEAR + " INTEGER, " +
                COLUMN_WATER_VOLUME + " REAL, " +
                COLUMN_WATER_AMOUNT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES users(id)" +
                ");";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("DROP TABLE IF EXISTS users");
        MyDB.execSQL("DROP TABLE IF EXISTS electricity_consumption");
        onCreate(MyDB);
    }
    /*public void insertSampleData() {
        try {
            // Sample data for user ID 1
            addElectricityConsumption(1, 1, 2023, 100.0);
            addElectricityConsumption(1, 2, 2023, 150.0);
            addElectricityConsumption(1, 3, 2023, 120.0);

            // Sample data for user ID 2
            addElectricityConsumption(2, 1, 2023, 80.0);
            addElectricityConsumption(2, 2, 2023, 110.0);

            // Add more sample data as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public long addElectricityConsumption(long userId, int month, int year, double consumptionValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_CONSUMPTION_VALUE, consumptionValue);

        double calculatedAmount = calculateElectricityAmount(consumptionValue);
        values.put(COLUMN_CALCULATED_AMOUNT, calculatedAmount);

        long id = db.insert(TABLE_ELECTRICITY_CONSUMPTION, null, values);
        db.close();
        return id;
    }

    // Add water consumption to the database
    public long addWaterConsumption(long userId,int month, int year, double volume) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Calculate the amount based on the provided volume
        double amount = calculateWaterAmount(volume);

        ContentValues values = new ContentValues();
        values.put(COLUMN_WATER_MONTH, month);
        values.put(COLUMN_WATER_YEAR, year);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_WATER_VOLUME, volume);
        values.put(COLUMN_WATER_AMOUNT, amount);  // Add the calculated amount

        // Insert row
        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id;
    }
    private double calculateElectricityAmount(double consumptionValue) {
        double unitPrice;

        if (consumptionValue <= 100) {
            unitPrice = 0.9010;
        } else if (consumptionValue <= 150) {
            unitPrice = 1.0732;
        } else if (consumptionValue <= 200) {
            unitPrice = 1.0732;
        } else if (consumptionValue <= 300) {
            unitPrice = 1.1676;
        } else if (consumptionValue <= 500) {
            unitPrice = 1.3817;
        } else {
            unitPrice = 1.5958;
        }

        // Calculate the total amount
        // Including 14% VAT
        return consumptionValue * unitPrice * 1.14;
    }

    public double calculateWaterAmount(double volume) {
        double unitPrice;

        // Adjust these thresholds and prices based on your billing structure
        if (volume <= 50) {
            unitPrice = 0.5;  // Replace with your actual unit price
        } else if (volume <= 100) {
            unitPrice = 0.7;  // Replace with your actual unit price
        } else if (volume <= 150) {
            unitPrice = 0.75;  // Replace with your actual unit price
        } else {
            unitPrice = 0.9;  // Replace with your actual unit price
        }

        return volume * unitPrice;
    }
    public Cursor getAllElectricityConsumptionsForUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_MONTH,
                COLUMN_YEAR,
                COLUMN_CONSUMPTION_VALUE,
                COLUMN_CALCULATED_AMOUNT
        };

        String selection = COLUMN_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        return db.query(
                TABLE_ELECTRICITY_CONSUMPTION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
    // Check if water consumption already exists for the specified month and year
    public boolean isWaterConsumptionExists(int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_WATER_MONTH + " = " + month + " AND " +
                COLUMN_WATER_YEAR + " = " + year;

        Cursor cursor = db.rawQuery(query, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
    public Boolean insertData(String email, String password, String firstName, String lastName) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("first_name", firstName);
        contentValues.put("last_name", lastName);
        long result = MyDatabase.insert("users", null, contentValues);
        return result != -1;
    }
    // Method to get all water consumption data
    public Cursor getAllWaterConsumptionData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }
    // Update water consumption in the database
    // Update water consumption in the database
    public int updateWaterConsumption(long id, int month, int year, double volume) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Calculate the amount based on the provided volume
        double amount = calculateWaterAmount(volume);

        ContentValues values = new ContentValues();
        values.put(COLUMN_WATER_MONTH, month);
        values.put(COLUMN_WATER_YEAR, year);
        values.put(COLUMN_WATER_VOLUME, volume);
        values.put(COLUMN_WATER_AMOUNT, amount);  // Update the calculated amount

        // Update row
        int numRowsAffected = db.update(TABLE_NAME, values, COLUMN_WATER_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return numRowsAffected;
    }
    // Delete water consumption from the database
    public int deleteWaterConsumption(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete row
        int numRowsDeleted = db.delete(TABLE_NAME, COLUMN_WATER_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return numRowsDeleted;
    }

    // Assuming TABLE_NAME is a constant representing your table name
    public Cursor getWaterConsumptionById(int userId, long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_USER_ID + " = ? AND " +
                COLUMN_WATER_ID + " = ?";

        // Use selectionArgs to avoid SQL injection
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(id)};

        return db.rawQuery(query, selectionArgs);
    }

    public Cursor getAllWaterConsumptionDataByYear(long userId, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_USER_ID + " = ? AND " +
                COLUMN_WATER_YEAR + " = ?";

        // Use selectionArgs to avoid SQL injection
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(year)};

        return db.rawQuery(query, selectionArgs);
    }









    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        return cursor.getCount() > 0;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }
    public String getUserLastname(String userEmail) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        String[] columns = {"last_name"};
        String selection = "email=?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = MyDatabase.query("users", columns, selection, selectionArgs, null, null, null);

        String lastName = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("last_name");
            if (columnIndex >= 0) {
                lastName = cursor.getString(columnIndex);
            }
        }

        cursor.close();
        return lastName;
    }

    public boolean isConsumptionExists(long userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USER_ID + "=? AND " + COLUMN_MONTH + "=? AND " + COLUMN_YEAR + "=?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(month), String.valueOf(year)};

        Cursor cursor = db.query(
                TABLE_ELECTRICITY_CONSUMPTION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        return exists;
    }
    public boolean deleteConsumption(int consumptionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the WHERE clause to delete the consumption with the given ID
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(consumptionId)};

        // Perform the deletion
        int rowsDeleted = db.delete(TABLE_ELECTRICITY_CONSUMPTION, whereClause, whereArgs);

        // Close the database
        db.close();

        // Return true if at least one row is deleted, indicating successful deletion
        return rowsDeleted > 0;
    }
    public boolean updateConsumption(int consumptionId,  double editedConsumptionValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(COLUMN_CONSUMPTION_VALUE, editedConsumptionValue);

        double calculatedAmount = calculateElectricityAmount(editedConsumptionValue);
        values.put(COLUMN_CALCULATED_AMOUNT, calculatedAmount);

        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(consumptionId)};

        int rowsAffected = db.update(TABLE_ELECTRICITY_CONSUMPTION, values, whereClause, whereArgs);
        db.close();

        return rowsAffected > 0;
    }

    public List<Integer> getAllUniqueYears() {
        List<Integer> years = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_YEAR + " FROM " + TABLE_ELECTRICITY_CONSUMPTION, null);

        if (cursor.moveToFirst()) {
            do {
                int year = cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR));
                years.add(year);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return years;
    }
    public Cursor getElectricityConsumptionsForUserAndYear(long userId, int year) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_MONTH,
                COLUMN_YEAR,
                COLUMN_CONSUMPTION_VALUE,
                COLUMN_CALCULATED_AMOUNT
        };

        String selection = COLUMN_USER_ID + "=? AND " + COLUMN_YEAR + "=?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(year)};

        return db.query(
                TABLE_ELECTRICITY_CONSUMPTION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public void insertConsumptionsFor2023() {
        // Month-wise data for 2023
        int[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        double[] consumptionValues = {120.0, 130.0, 110.0, 140.0, 120.0, 130.0, 110.0, 140.0, 120.0, 130.0, 110.0, 140.0};

        // Get the current user ID (assuming it's 1 for this example)
        int userId = 1;

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < months.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_MONTH, months[i]);
            values.put(COLUMN_YEAR, 2023);
            values.put(COLUMN_CONSUMPTION_VALUE, consumptionValues[i]);

            double calculatedAmount = calculateElectricityAmount(consumptionValues[i]);
            values.put(COLUMN_CALCULATED_AMOUNT, calculatedAmount);

            // Insert the record
            db.insert(TABLE_ELECTRICITY_CONSUMPTION, null, values);
        }

        db.close();
    }
    // Add this method to DBHelper class
    public Cursor getElectricityConsumptionForMonth(long userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_CONSUMPTION_VALUE
        };

        String selection = COLUMN_USER_ID + "=? AND " + COLUMN_MONTH + "=? AND " + COLUMN_YEAR + "=?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(month), String.valueOf(year)};

        return db.query(
                TABLE_ELECTRICITY_CONSUMPTION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
    public Cursor getWaterConsumptionForMonth(long userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_WATER_VOLUME
        };

        String selection = COLUMN_USER_ID + "=? AND " + COLUMN_MONTH + "=? AND " + COLUMN_YEAR + "=?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(month), String.valueOf(year)};

        return db.query(
                TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
    public boolean save(User user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = getUserContentValues(user);

        try {
            long result = sqLiteDatabase.insert("users", null, contentValues);

            if (result != -1) {
                Log.d("DBHelper", "User saved successfully");
                return true;
            } else {
                Log.e("DBHelper", "Error while saving user: " + result);
                return false;
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while saving user", e);
            return false;
        } finally {
            sqLiteDatabase.close();
        }
    }

    private ContentValues getUserContentValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fname", user.getFirstName());
        contentValues.put("lname", user.getLastName());
        contentValues.put("password", user.getPassword());
        contentValues.put("email", user.getEmail());
        contentValues.put("phone", user.getPhone());
        return contentValues;
    }


    public boolean emailUnique(String email) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM users WHERE email = ?", new String[]{email});

        try {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count == 0;
            } else {
                // Handle unexpected cursor behavior
                return true; // Assuming true for safety, you may want to handle this differently
            }
        } finally {
            cursor.close();
            sqLiteDatabase.close();
        }
    }


    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = sqLiteDatabase.rawQuery("select * from users where email = ? and password = ?", new String[]{email, password});

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("id");
                int fnameIndex = cursor.getColumnIndex("fname");
                int lnameIndex = cursor.getColumnIndex("lname");
                int passwordIndex = cursor.getColumnIndex("password");
                int emailIndex = cursor.getColumnIndex("email");
                int phoneIndex = cursor.getColumnIndex("phone");

                if (fnameIndex != -1 && lnameIndex != -1 && passwordIndex != -1 && emailIndex != -1 && phoneIndex != -1) {
                    Long userId = cursor.getLong(idIndex);
                    User user = new User(
                            userId,
                            cursor.getString(fnameIndex),
                            cursor.getString(lnameIndex),
                            cursor.getString(passwordIndex),
                            cursor.getString(emailIndex),
                            cursor.getString(phoneIndex)
                    );
                    return Optional.of(user);
                } else {
                    Log.e("DBHelper", "One or more columns not found in the result set");
                    return Optional.empty();
                }
            } else {
                Log.d("DBHelper", "No user found with the given email and password");
                return Optional.empty();
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while retrieving user", e);
            return Optional.empty();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqLiteDatabase.close();
        }
    }



    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

        try {
            if (cursor.getCount()>0) {
                return true;
            } else {
                return false;
            }
        } finally {
            cursor.close();
        }
    }
}
