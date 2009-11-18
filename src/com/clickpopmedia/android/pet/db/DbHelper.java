package com.clickpopmedia.android.pet.db;

import static com.clickpopmedia.android.pet.db.DbAdapter.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Help open and close the DB.
 *
 */
class DbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "GPP DbHelper";
    
	private static final String DATABASE_NAME = "data";
	
    private static final int DATABASE_VERSION = 4;
	
    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, 
        	"Upgrading database from version " + oldVersion + " to "
             + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }
}