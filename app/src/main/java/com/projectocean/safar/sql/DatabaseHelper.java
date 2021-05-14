package com.projectocean.safar.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	    private final static String DATABASE_NAME = "Safar.db";
	    private final static String TABLE_NAME1 = "login_info_table";
	    private static final String COL_1a = "ID";
	    private static final String COL_2a = "TYPE";



	    public DatabaseHelper(Context context) {
        	        super(context, DATABASE_NAME, null, 1);
        	    }

        	    @Override
        public void onCreate(SQLiteDatabase db) {
					Log.d("sqlitetesting","oncreate is called");
					db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME1+" (ID TEXT PRIMARY KEY,TYPE TEXT)");
					}

        	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1);
					onCreate(db);
        	    }

        	    public boolean insertData(String type) {
        	        SQLiteDatabase db = this.getWritableDatabase();
        	        ContentValues cv = new ContentValues();
        	        cv.put(COL_1a, "1");
                    cv.put(COL_2a, type);

        	        long result = db.insert(TABLE_NAME1, null, cv);
					return result != -1;
        	    }

        	    public Cursor getData(){
        	        SQLiteDatabase db = this.getWritableDatabase();
        	        String query="SELECT * FROM "+TABLE_NAME1+" WHERE ID = 1";
        	        Cursor cursor = db.rawQuery(query,null);
        	        return cursor;
        	    }

        	    public boolean updateData(String type) {
        	        SQLiteDatabase db = this.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(COL_1a, "1");
                    cv.put(COL_2a, type);
        	        long result = db.update(TABLE_NAME1, cv, "ID=?", new String[]{"1"});
                    if (result == -1) return false;
                    else return true;
            }

        	    public boolean deleteData() {
        	        SQLiteDatabase db = this.getWritableDatabase();
        	        long result = db.delete(TABLE_NAME1, "ID = ?", new String[]{"1"});
        	        return result !=-1;
        	    }

        	    public Cursor getAllData() {
        	        SQLiteDatabase db = this.getWritableDatabase();
        	        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME1, null);
        	        return res;
        	    }

	}
