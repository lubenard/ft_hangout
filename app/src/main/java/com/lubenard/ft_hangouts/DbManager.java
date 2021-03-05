package com.lubenard.ft_hangouts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Handle the DB used to save datas.
 * Theses datas are sent to Musk in order to build Skynet.
 */
public class DbManager extends SQLiteOpenHelper {

    public static final String TAG = "DBManager";

    static final String dbName = "dataDB";

    // Contact table
    private static final String contactsTable = "contacts";
    private static final String contactsTableId = "id";
    private static final String contactsTableName = "contactName";
    private static final String contactsTablePhoneNumber = "contactPhoneNumber";
    private static final String contactsTableEmail = "contactEmail";
    private static final String contactsTableAddress = "contactAddress";
    private static final String contactsTableBirthdate = "contactBirthdate";

    private SQLiteDatabase writableDB;
    private SQLiteDatabase readableDB;

    public DbManager(Context context) {
        super(context, dbName , null,1);
        this.writableDB = this.getWritableDatabase();
        this.readableDB = this.getReadableDatabase();
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
     * Get the app datas for a specific date
     * @return The datas fetched from the DB
     */
    public LinkedHashMap<Integer, ContactModel> getAllAppsForMainList() {
        LinkedHashMap<Integer, ContactModel> contactDatas = new LinkedHashMap<>();

        //String [] columns = new String[]{contactsTableId, contactsTableName, contactsTablePhoneNumber, contactsTableEmail, contactsTableAddress, contactsTableBirthdate};
        Cursor cursor = readableDB.rawQuery("SELECT * from " + contactsTable, null);

        while (cursor.moveToNext()) {
            contactDatas.put(cursor.getInt(cursor.getColumnIndex(contactsTableId)), new ContactModel(cursor.getString(cursor.getColumnIndex(contactsTableName)), cursor.getString(cursor.getColumnIndex(contactsTablePhoneNumber)), null));
            Log.d(TAG, "getStatApp adding " + cursor.getString(cursor.getColumnIndex(contactsTableName)) + " and value " + cursor.getString(cursor.getColumnIndex(contactsTablePhoneNumber)));
        }
        cursor.close();
        return contactDatas;
    }

    /**
     * Create a new contact only if non existent:
     * Example: The contact named Toto does not exist, so let's create it
     * @param name new contact name
     * @param phoneNumber new Phone number
     * @param email email contact address
     * @param address postal contact address
     * @param birthday contact birthday
     */
    public void createNewContact(String name, String phoneNumber, String email, String address, String birthday) {
        ContentValues cv = new ContentValues();
        cv.put(contactsTableName, name);
        cv.put(contactsTablePhoneNumber, phoneNumber);
        cv.put(contactsTableEmail, email);
        cv.put(contactsTableAddress, address);
        cv.put(contactsTableBirthdate, birthday);

        //Log.d(TAG, String.format("update Contact: Create contact with new value (name = %d, PhoneNumber = %d, email = %d, address = %d, birtdate = %d",
        //        name, phoneNumber, email, address, birthday));
        writableDB.insertWithOnConflict(contactsTable, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Update contact datas:
     * Example: The contact toto already exist, but the user did not gave the right email
     * @param newScreenTime new screen time
     * @param date date to which you want to get the datas
     */
    public static void updateContact(int newScreenTime, String date) {
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
     * Close the db when finished using it.
     */
    public void closeDb() {
        if (writableDB != null) { writableDB.close();}
    }
}

