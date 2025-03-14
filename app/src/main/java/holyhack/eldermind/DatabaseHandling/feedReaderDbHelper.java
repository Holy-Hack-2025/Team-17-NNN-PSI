package holyhack.eldermind.DatabaseHandling;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class feedReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "UserInfoFeedReader.db";


    public feedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Userdetails(usernameCreate TEXT primary key, emailCreate TEXT , passwordCreate TEXT, dateCreate TEXT, countryCreate TEXT)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop Table if exists Userdetails");
        onCreate(db);
    }

    public boolean insertUserData(String usernameCreate, String emailCreate, int passwordCreate, String dateCreate, String countryCreate){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("usernameCreate", usernameCreate);
        contentValues.put("emailCreate", emailCreate);
        contentValues.put("passwordCreate", passwordCreate);
        contentValues.put("dateCreate", dateCreate);
        contentValues.put("countryCreate", countryCreate);
        long result = db.insert("Userdetails",null,contentValues);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }
    public boolean updateUserData(String usernameCreate, String emailCreate, String passwordCreate, String dateCreate, String countryCreate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("usernameCreate", usernameCreate);
        contentValues.put("emailCreate", emailCreate);
        contentValues.put("passwordCreate", passwordCreate);
        contentValues.put("dateCreate", dateCreate);
        contentValues.put("countryCreate", countryCreate);
        Cursor cursor = db.rawQuery("Select* from Userdetails where usernameCreate =?", new String[]{usernameCreate});

        if (cursor.getCount() > 0) {
            long result = db.update("Userdetails", contentValues, "name=?", new String[]{usernameCreate});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
        else{
            return false;
        }
    }

    public boolean updateUserData(String usernameCreate, String emailCreate, String countryCreate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("emailCreate", emailCreate);
        contentValues.put("countryCreate", countryCreate);
        Cursor cursor = db.rawQuery("Select* from Userdetails where usernameCreate =?", new String[]{usernameCreate});

        if (cursor.getCount() > 0) {
            long result = db.update("Userdetails", contentValues, "name=?", new String[]{usernameCreate});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
        else{
            return false;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Userdetails", null);
        return cursor;
    }

    public boolean checkIdentity(String username, String email, String birthday, String address) {

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor cursor;
            // Use parameterized query to prevent SQL injection
            String selection = "usernameCreate = ?";
            String[] selectionArgs = {username};
            cursor = db.query("Userdetails", null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve values from cursor
                @SuppressLint("Range") String emailCheck = cursor.getString(cursor.getColumnIndex("emailCreate"));
                @SuppressLint("Range") String birthdayCheck = cursor.getString(cursor.getColumnIndex("dateCreate"));
                @SuppressLint("Range") String addressCheck = cursor.getString(cursor.getColumnIndex("countryCreate"));

                // Compare values
                if (emailCheck.equals(email) && birthdayCheck.equals(birthday) && addressCheck.equals(address)) {
                    return true;
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            // Handle SQLite exception
            Log.e("checkIdentity", "SQLite exception occurred: " + e.getMessage());
        }
        // Close the database connection
        return false;
    }

    public void changePassword(String username, int passCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("passwordCreate", passCode);
        String selection = "usernameCreate = ?";
        String[] selectionArgs = { username };

        try {
            int rowsAffected = db.update("Userdetails", values, selection, selectionArgs);
            if (rowsAffected == 0) {
                // Handle case where username doesn't exist
                Log.e("changePassword", "No rows affected. Username not found: " + username);
            } else {
                // Password changed successfully
                Log.d("changePassword", "Password changed successfully for username: " + username);
            }
        } catch (SQLException e) {
            // Handle SQL exception
            Log.e("changePassword", "SQL exception occurred: " + e.getMessage());
        } finally {
            // Close the database connection
            db.close();
        }
    }


    public boolean checkUser(String password, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Use parameterized query to prevent SQL injection
            String selection = "emailCreate = ?";
            String[] selectionArgs = { email };
            cursor = db.query("Userdetails", null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve password from cursor
                @SuppressLint("Range") String passwordCheck = cursor.getString(cursor.getColumnIndex("passwordCreate"));

                // Compare passwords
                if (passwordCheck.equals(password)) {
                    return true;
                }
            }
        } catch (SQLiteException e) {
            // Handle SQLite exception
            Log.e("checkUser", "SQLite exception occurred: " + e.getMessage());
        } finally {
            // Close the cursor
            if (cursor != null) {
                cursor.close();
            }
            // Close the database connection
            db.close();
        }
        return false;
    }

    public Cursor getDataEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { email };
        Cursor cursor = db.rawQuery("Select * from Userdetails where emailCreate = ?" , selectionArgs , null);
        return cursor;
    }

    @SuppressLint("Range")
    public String getUsername(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { email };
        String name = null;

        Cursor cursor = db.rawQuery("SELECT usernameCreate FROM Userdetails WHERE emailCreate = ?", selectionArgs);
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("usernameCreate"));
        }
        cursor.close();
        db.close();
        return name;
    }

    public Cursor getDataUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { username };
        Cursor cursor = db.rawQuery("SELECT * FROM Userdetails WHERE usernameCreate = ?" , selectionArgs , null);
        return cursor;
    }


}


