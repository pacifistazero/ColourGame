package com.example.colourgame.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.colourgame.model.ScoreDataContract;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "scoreManager";

    // Scores table name
    private static final String TABLE_SCORE = "scores";

    // Scores Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SCORE = "score";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SCORE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_SCORE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public void addScore(ScoreDataContract data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.getName()); // Score Name
        values.put(KEY_SCORE, data.getScore()); // Score score

        // Inserting Row
        db.insert(TABLE_SCORE, null, values);
        db.close(); // Closing database connection
    }

    public ScoreDataContract getScore(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORE, new String[]{
                        KEY_ID, KEY_NAME, KEY_SCORE
                }, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        ScoreDataContract scoreData = null;

        try {
            if (cursor != null) {
                cursor.moveToFirst();

                scoreData = new ScoreDataContract(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), Double.parseDouble(cursor.getString(2)));
            }
            // return contact
            return scoreData;
        }
        catch (Exception e){
            Log.e("COLOURGAME", e.getLocalizedMessage());
        }
        finally {
            db.close();
            cursor.close();
        }

        return scoreData;
    }

    // Getting All Scores
    public List<ScoreDataContract> getAllScores() {
        List<ScoreDataContract> scoreList = new ArrayList<ScoreDataContract>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCORE;

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    ScoreDataContract score = new ScoreDataContract();
                    score.setId(Integer.parseInt(cursor.getString(0)));
                    score.setName(cursor.getString(1));
                    score.setScore(Double.parseDouble(cursor.getString(2)));
                    // Adding contact to list
                    scoreList.add(score);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.e("COLOURGAME", e.getLocalizedMessage());
        }finally {
            db.close();
            cursor.close();
        }

        // return contact list
        return scoreList;
    }

    // Updating single contact
    public int updateScore(ScoreDataContract score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, score.getName());
        values.put(KEY_SCORE, score.getScore());

        // updating row
        return db.update(TABLE_SCORE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(score.getId())});
    }

    // Deleting single contact
    public void deleteScore(ScoreDataContract score) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCORE, KEY_ID + " = ?",
                new String[]{String.valueOf(score.getId())});
        db.close();
    }

    public int getScoreCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SCORE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}

