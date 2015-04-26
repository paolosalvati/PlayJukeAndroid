package com.example.paolosalvati.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.paolosalvati.demo.sqlLite.DatabaseStrings;
import com.example.paolosalvati.demo.sqlLite.DbHelper;

/**
 * Created by Paolo on 09/04/2015.
 */
public class DbManager
{
    private DbHelper dbhelper;

    public DbManager(Context ctx)
    {
        dbhelper=new DbHelper(ctx);
    }

    public void iLike(String provider, Integer idTrack, Integer iLike)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DatabaseStrings.FIELD_PROVIDER, provider);
        cv.put(DatabaseStrings.FIELD_ID_TRACK, idTrack);
        cv.put(DatabaseStrings.FIELD_LIKE, iLike);
        try
        {
            db.insert(DatabaseStrings.TBL_NAME, null,cv);
        }
        catch (SQLiteException sqle)
        {
            // Gestione delle eccezioni
        }
    }
    public void iUnLike(String provider, Integer idTrack, Integer iUnLike)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(DatabaseStrings.FIELD_PROVIDER, provider);
        cv.put(DatabaseStrings.FIELD_ID_TRACK, idTrack);
        cv.put(DatabaseStrings.FIELD_UN_LIKE, iUnLike);
        try
        {
            db.insert(DatabaseStrings.TBL_NAME, null,cv);
        }
        catch (SQLiteException sqle)
        {
            // Gestione delle eccezioni
        }
    }
    public boolean delete(long id)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        try
        {
            if (db.delete(DatabaseStrings.TBL_NAME, DatabaseStrings.FIELD_ID+"=?", new String[]{Long.toString(id)})>0)
                return true;
            return false;
        }
        catch (SQLiteException sqle)
        {
            return false;
        }

    }

    public Cursor query()
    {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DatabaseStrings.TBL_NAME, null, null, null, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return null;
        }
        return crs;
    }

}
