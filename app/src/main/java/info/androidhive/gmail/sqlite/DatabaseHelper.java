package info.androidhive.gmail.sqlite;

/**
 * Created by slim on 3/12/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "HistoricServers";
    public static final String TABLE_NAME = "Servers";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IP_ADDRESS = "parameter";
    public static final String COLUMN_FRIENDLY_NAME = "value";
    public static final String COLUMN_MODEL = "Model";
    public static final String COLUMN_TEST = "test";
    public static final String COLUMN_IS_IMPORTANT = "isImportant";
    public static final String COLUMN_TEST2 = "test2";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_NAME = "name";

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(DB_NAME,"Created");
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID + " INTEGER PRIMARY KEY , " +
                COLUMN_IP_ADDRESS+" VARCHAR, "+
                COLUMN_FRIENDLY_NAME+" VARCHAR, "+
                COLUMN_MODEL+" VARCHAR, " +
                COLUMN_TEST +" VARCHAR, "+
                COLUMN_NAME+" VARCHAR, "+
                COLUMN_IS_IMPORTANT +" INTEGER DEFAULT 0, "+
                COLUMN_TEST2+" INTEGER DEFAULT 0, "+
                COLUMN_COLOR+" INTEGER);";
        db.execSQL(sql);

    }




    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Persons";
        db.execSQL(sql);
        onCreate(db);
    }


    public boolean addServer(int id,String ipAddress, String friendlyName, String model,String isImportant,String name,int test1,int test2,int color) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_IP_ADDRESS, ipAddress);
        contentValues.put(COLUMN_FRIENDLY_NAME,friendlyName);
        contentValues.put(COLUMN_MODEL, model);
        contentValues.put(COLUMN_TEST,isImportant);
        contentValues.put(COLUMN_NAME,name);
        contentValues.put(COLUMN_IS_IMPORTANT,test1);
        contentValues.put(COLUMN_TEST2,test2);
        contentValues.put(COLUMN_COLOR,color);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public  void  deleteServer(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME+" where " +COLUMN_ID+"='"+id+"'");

    }

    /*
    * This method taking two arguments
    * first one is the id of the name for which
    * we have to update the sync status
    * and the second one is the status that will be changed
    * */

    public boolean updateIsImportant(int id, int isImportant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IS_IMPORTANT, isImportant);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        return true;
    }

    public int maxID() {

        SQLiteDatabase db = this.getReadableDatabase();
        int id = 0;
        final String MY_QUERY = "SELECT MAX("+COLUMN_ID+") AS "+COLUMN_ID+" FROM "+TABLE_NAME;
        Cursor mCursor = db.rawQuery(MY_QUERY, null);
        try {
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                id = mCursor.getInt(mCursor.getColumnIndex("id"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            db.close();
        }
        return id;
    }

    /*
    * this method will give us all the servers stored in sqlite
    * */
    public Cursor getServers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


}