package com.example.paolosalvati.demo.sqlLite;

/**
 * Created by Paolo on 09/04/2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DbAdapter {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = DbAdapter.class.getSimpleName();

    private Context context;
    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValues(String provider, Integer idTrack, Integer iLike, Integer iUnLike) {

        ContentValues cv = new ContentValues();
        cv.put(DatabaseStrings.FIELD_PROVIDER, provider);
        cv.put(DatabaseStrings.FIELD_ID_TRACK, idTrack);
        cv.put(DatabaseStrings.FIELD_LIKE, iLike);
        cv.put(DatabaseStrings.FIELD_UN_LIKE, iUnLike);
        return cv;
    }

    private ContentValues createContentValues(Integer iLike, Integer iUnLike) {

        ContentValues cv = new ContentValues();
        cv.put(DatabaseStrings.FIELD_LIKE, iLike);
        cv.put(DatabaseStrings.FIELD_UN_LIKE, iUnLike);
        return cv;
    }

    //add a preference
    public long createPreference(String provider, Integer idTrack, Integer iLike, Integer iUnLike) {
        ContentValues initialValues = createContentValues(provider, idTrack, iLike, iUnLike);
        return database.insertOrThrow(DatabaseStrings.TBL_NAME, null, initialValues);
    }

    //update a preference
    public boolean updatePreference(Integer idTrack, Integer iLike, Integer iUnLike) {
        ContentValues updateValues = createContentValues(iLike, iUnLike);
        return database.update(DatabaseStrings.TBL_NAME, updateValues, DatabaseStrings.FIELD_ID + "=" + idTrack, null) > 0;
    }

    //delete a preference
    public boolean deleteContact(Integer idTrack) {
        return database.delete(DatabaseStrings.TBL_NAME, DatabaseStrings.FIELD_ID + "=" + idTrack, null) > 0;
    }

    //fetch all a preferences
    public Cursor fetchAllPreferences() {
        return database.query(DatabaseStrings.TBL_NAME, new String[]{ DatabaseStrings.FIELD_ID,  DatabaseStrings.FIELD_ID_TRACK,  DatabaseStrings.FIELD_PROVIDER,  DatabaseStrings.FIELD_LIKE, DatabaseStrings.FIELD_UN_LIKE}, null, null, null, null, null);
    }

    //fetch preferences filter by a track
    public Cursor fetchPreferencesByFilter(Integer filter) {
        Cursor mCursor = database.query(true, DatabaseStrings.TBL_NAME, new String[]{
                        DatabaseStrings.FIELD_LIKE, DatabaseStrings.FIELD_UN_LIKE,DatabaseStrings.FIELD_ID_TRACK},
                //DatabaseStrings.FIELD_ID_TRACK + " like '%" + filter + "%'", null, null, null, null, null);
                DatabaseStrings.FIELD_ID_TRACK + " = " + filter , null, null, null, null, null);
        return mCursor;
    }
}