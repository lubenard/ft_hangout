package com.lubenard.ft_hangouts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String contactsTableParsedPhoneNumber = "contactParsedPhoneNumber";
    private static final String contactsTableEmail = "contactEmail";
    private static final String contactsTableAddress = "contactAddress";
    private static final String contactsTableBirthdate = "contactBirthdate";
    private static final String contactsTableIconPath = "contactsIconPath";

    private static final String messagesTable = "messages";
    private static final String messageTableId = "id";
    private static final String messageTableContactId = "contactId";
    private static final String messageTableContent = "content";
    private static final String messageTableDirection = "direction";

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
                + contactsTablePhoneNumber + " TEXT, " + contactsTableParsedPhoneNumber + " INTEGER, " + contactsTableEmail + " TEXT, "  + contactsTableAddress + " TEXT, "
                + contactsTableBirthdate + " TEXT, "  + contactsTableIconPath+ " TEXT)");

        db.execSQL("CREATE TABLE " + messagesTable + " (" + messageTableId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + messageTableContactId + " INTEGER, "
                + messageTableContent + " TEXT, "  + messageTableDirection + " TEXT)");

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

    public ArrayList<String> getContactDetail(int id) {
        ArrayList<String> contactDatas = new ArrayList<>();

        String[] columns = new String[]{contactsTableName, contactsTablePhoneNumber, contactsTableEmail, contactsTableAddress, contactsTableBirthdate, contactsTableIconPath};
        Cursor cursor = readableDB.query(contactsTable, columns,contactsTableId + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        //Log.d(TAG, "cursor = " + cursor.getString(cursor.getColumnIndex(contactsTableName)));
        contactDatas.add(cursor.getString(cursor.getColumnIndex(contactsTableName)));
        contactDatas.add(cursor.getString(cursor.getColumnIndex(contactsTablePhoneNumber)));
        contactDatas.add(cursor.getString(cursor.getColumnIndex(contactsTableEmail)));
        contactDatas.add(cursor.getString(cursor.getColumnIndex(contactsTableAddress)));
        contactDatas.add(cursor.getString(cursor.getColumnIndex(contactsTableBirthdate)));
        contactDatas.add(cursor.getString(cursor.getColumnIndex(contactsTableIconPath)));
        Log.d(TAG, "getStatApp adding " + cursor.getString(cursor.getColumnIndex(contactsTableName)) + ", " + cursor.getString(cursor.getColumnIndex(contactsTablePhoneNumber)));
        Log.d(TAG, "Columns array is = " + Arrays.toString(cursor.getColumnNames()));
        cursor.close();
        return contactDatas;
    }

    /**
     * Delete a contact
     * @param contactId the id of the contact we want to delete
     */
    public void deleteContact(int contactId)
    {
        if (contactId > 0)
            writableDB.delete(contactsTable,contactsTableId + "=?", new String[]{String.valueOf(contactId)});
    }

    /**
     * Get the contact list for a the main List
     * @return The datas fetched from the DB as a LinkedHashMap
     */
    public LinkedHashMap<Integer, ContactModel> getAllContactsForMainList() {
        LinkedHashMap<Integer, ContactModel> contactDatas = new LinkedHashMap<>();

        String[] columns = new String[]{contactsTableId, contactsTableName, contactsTablePhoneNumber, contactsTableEmail, contactsTableIconPath};
        Cursor cursor = readableDB.query(contactsTable,  columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            contactDatas.put(cursor.getInt(cursor.getColumnIndex(contactsTableId)), new ContactModel(cursor.getInt(cursor.getColumnIndex(contactsTableId)),
                    cursor.getString(cursor.getColumnIndex(contactsTableName)), cursor.getString(cursor.getColumnIndex(contactsTablePhoneNumber)),
                    cursor.getString(cursor.getColumnIndex(contactsTableEmail)),  cursor.getString(cursor.getColumnIndex(contactsTableIconPath))));
            //Log.d(TAG, "getStatApp adding " + cursor.getString(cursor.getColumnIndex(contactsTableName)) + " and value " + cursor.getString(cursor.getColumnIndex(contactsTablePhoneNumber)));
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
    public void createNewContact(String name, String phoneNumber, String email, String address, String birthday, String iconPath) {
        ContentValues cv = new ContentValues();
        cv.put(contactsTableName, name);
        cv.put(contactsTablePhoneNumber, phoneNumber);
        cv.put(contactsTableParsedPhoneNumber, phoneNumber.replaceAll( "[^0-9]",""));
        cv.put(contactsTableEmail, email);
        cv.put(contactsTableAddress, address);
        cv.put(contactsTableBirthdate, birthday);
        cv.put(contactsTableIconPath, iconPath);

        writableDB.insertWithOnConflict(contactsTable, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Update contact datas:
     * Example: The contact toto already exist, but the user did not gave the right email
     * @param name new contact name
     * @param phoneNumber new Phone number
     * @param email email contact address
     * @param address postal contact address
     * @param birthday contact birthday
     */
    public void updateContact(int id, String name, String phoneNumber, String email, String address, String birthday, String iconPath) {
        ContentValues cv = new ContentValues();
        cv.put(contactsTableName, name);
        cv.put(contactsTablePhoneNumber, phoneNumber);
        cv.put(contactsTableEmail, email);
        cv.put(contactsTableAddress, address);
        cv.put(contactsTableBirthdate, birthday);
        cv.put(contactsTableIconPath, iconPath);

        int u = writableDB.update(contactsTable, cv, contactsTableId + "=?", new String[]{String.valueOf(id)});
        if (u == 0) {
            Log.d(TAG, "updateContact: update does not seems to work, insert data: (for id=" + id);
            cv.put(contactsTableName, name);
            cv.put(contactsTablePhoneNumber, phoneNumber);
            cv.put(contactsTableEmail, email);
            cv.put(contactsTableAddress, address);
            cv.put(contactsTableBirthdate, birthday);
            cv.put(contactsTableIconPath, iconPath);
            writableDB.insertWithOnConflict(contactsTable, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public long saveNewMessage(int contactId, String messageContent, String direction) {
        ContentValues cv = new ContentValues();
        cv.put(messageTableContactId, contactId);
        cv.put(messageTableContent, messageContent);
        cv.put(messageTableDirection, direction);

        return writableDB.insertWithOnConflict(messagesTable, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Close the db when finished using it.
     */
    public void closeDb() {
        if (writableDB != null) { writableDB.close();}
    }

    public int getContactIdFromPhoneNumber(String phoneNumber) {
        int contactId = -1;
        String parsedPhoneNumber = phoneNumber.replaceAll( "[^0-9]","");
        String[] columns = new String[]{contactsTableId};
        Cursor cursor = readableDB.query(contactsTable, columns,contactsTableParsedPhoneNumber + "=?",
                new String[]{parsedPhoneNumber}, null, null, null);

        if (cursor.moveToFirst()) {
            contactId = cursor.getInt(cursor.getColumnIndex(contactsTableId));
            cursor.close();
        }
        return contactId;
    }

    public String getContactNameFromPhoneNumber(String phoneNumber) {
        String contactName = null;
        String parsedPhoneNumber = phoneNumber.replaceAll( "[^0-9]","");
        String[] columns = new String[]{contactsTableName};
        Cursor cursor = readableDB.query(contactsTable, columns,contactsTableParsedPhoneNumber + "=?",
                new String[]{parsedPhoneNumber}, null, null, null);

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(contactsTableName));
            cursor.close();
        }
        return contactName;
    }

    public LinkedHashMap<Integer, MessageModel> getAllMessageFromContactId(int contactId) {
        LinkedHashMap<Integer, MessageModel> messageDatas = new LinkedHashMap<>();

        Log.d(TAG, "function launched");
        String[] columns = new String[]{messageTableId, messageTableContactId, messageTableContent, messageTableDirection};
        Cursor cursor = readableDB.query(messagesTable,  columns, messageTableContactId + "=?",
                new String[]{String.valueOf(contactId)}, null, null, null);

        while (cursor.moveToNext()) {
            messageDatas.put(cursor.getInt(cursor.getColumnIndex(messageTableId)), new MessageModel(cursor.getInt(cursor.getColumnIndex(messageTableId)),
                    cursor.getInt(cursor.getColumnIndex(messageTableContactId)), cursor.getString(cursor.getColumnIndex(messageTableContent)),
                    cursor.getString(cursor.getColumnIndex(messageTableDirection))));
            Log.d(TAG, "getMessage adding " + cursor.getString(cursor.getColumnIndex(messageTableContactId)) + " and value " + cursor.getString(cursor.getColumnIndex(messageTableContent)));
        }
        cursor.close();
        return messageDatas;
    }
}

