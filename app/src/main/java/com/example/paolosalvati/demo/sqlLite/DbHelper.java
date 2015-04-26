package com.example.paolosalvati.demo.sqlLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Paolo on 09/04/2015.
 */
public class DbHelper extends SQLiteOpenHelper
{
    public static final String DBNAME="PLAYJUKE";

    public DbHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String q="CREATE TABLE "+DatabaseStrings.TBL_NAME+
                " ( _id Integer PRIMARY KEY AUTOINCREMENT," +
                DatabaseStrings.FIELD_PROVIDER+" TEXT," +
                DatabaseStrings.FIELD_ID_TRACK+" INTEGER," +
                DatabaseStrings.FIELD_LIKE+" INTEGER," +
                DatabaseStrings.FIELD_UN_LIKE+" INTEGER)";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {  }

}
