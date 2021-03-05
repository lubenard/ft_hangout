package com.lubenard.ft_hangouts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Handle the DB used to save datas.
 * Theses datas are sent to Musk in order to build Skynet.
 */
public class DbManager extends SQLiteOpenHelper {

    public static final String TAG = "DBManager";

    static final String dbName = "dataDB";

    // Contact table
    static final String contactsTable = "contacts";
    static final String contactsTableId = "id";
    static final String contactsTableName = "contactName";
    static final String contactsTablePhoneNumber = "contactPhoneNumber";
    static final String contactsTableEmail = "contactEmail";
    static final String contactsTableAddress = "contactAddress";
    static final String contactsTableBirthdate = "contactBirthdate";

    private Context context;

    private SQLiteDatabase readableDB;
    private SQLiteDatabase writableDB;

    public DbManager(Context context) {
        super(context, dbName , null,1);
        this.context = context;
        this.readableDB = this.getReadableDatabase();
        this.writableDB = this.getWritableDatabase();
    }

    /**
     * If the db does not exist, create it with thoses fields.
     * @param db The database Object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create apps table
        db.execSQL("CREATE TABLE " + contactsTable + " (" + contactsTableId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + contactsTableName + " TEXT, "
                + contactsTablePhoneNumber + " TEXT, " + contactsTableEmail + " TEXT, "  + contactsTableAddress + " TEXT, " + contactsTableBirthdate + " TEXT)");

        Log.d(TAG, "The db has been created, this message should only appear once.");
    }

    public static String getDBName() {
        return dbName;
    }

    /**
     * If you plan to improve the database, you might want to use this function as a automated
     * upgrade tool for db.
     * @param sqLiteDatabase
     * @param i Old DB version
     * @param i1 New DB version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Create a new contact only if non existent:
     * Example: The contact named Toto does not exist, so let's create it
     * @param name new contact name
     * @param phoneNumber new Phone number
     * @param email email contact address
     */
    public void createNewContact(String name, String phoneNumber, String email, String address, String birthdate) {
        /*ContentValues cv = new ContentValues();
        cv.put(screenTimeTableScreenTime, newScreenTime);

        Log.d(TAG, "updateScreenTime: update with new value (time = " + newScreenTime + ") for date = " + date);
        int u = writableDB.update(screenTimeTable, cv, screenTimeTableDate + "=?", new String []{date});
        if (u == 0) {
            Log.d(TAG, "updateScreenTime: update does not seems to work, insert data: (time = " + newScreenTime + ") for date = " + date);
            cv.put(screenTimeTableDate, date);
            writableDB.insertWithOnConflict(screenTimeTable, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }*/
    }

    /**
     * Update contact datas:
     * Example: The contact toto already exist, but the user did not gave the right email
     * @param newScreenTime new screen time
     * @param date date to which you want to get the datas
     */
    public void updateContact(int newScreenTime, String date) {
        /*ContentValues cv = new ContentValues();
        cv.put(screenTimeTableScreenTime, newScreenTime);

        Log.d(TAG, "updateScreenTime: update with new value (time = " + newScreenTime + ") for date = " + date);
        int u = writableDB.update(screenTimeTable, cv, screenTimeTableDate + "=?", new String []{date});
        if (u == 0) {
            Log.d(TAG, "updateScreenTime: update does not seems to work, insert data: (time = " + newScreenTime + ") for date = " + date);
            cv.put(screenTimeTableDate, date);
            writableDB.insertWithOnConflict(screenTimeTable, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }*/
    }

    /**
     * DEBUG FUNCTION
     * Use this function for testing. Print all the content of a given table
     * @param tableName Table name to print
     */
    public void getTableAsString(String tableName) {
        Log.d(TAG, "getTableAsString called for " + tableName);
        Log.d(TAG, String.format("Table %s:\n", tableName));
        Cursor allRows  = readableDB.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    Log.d(TAG, String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name))));
                }
                Log.d(TAG,"\n");

            } while (allRows.moveToNext());
        }
    }

    /**
     * Close the db when finished using it.
     */
    public void closeDb() {
        if (writableDB != null) { writableDB.close();}
        if (readableDB != null) { readableDB.close();}
    }
}

