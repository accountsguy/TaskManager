package accountsguy.net.taskmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by advic on 26/03/2018.
 */

public class SQLHelper extends SQLiteOpenHelper {

    long insertedRecords = 0;
    SQLiteDatabase sqLiteDatabase = null;
    public Cursor cursor;

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DatabaseScheme.ToDoList.TABLE_CREATE_STATEMENT);
            Log.i("Test - ","SQLHelper Table Created");
        }
        catch (Exception e){
            Log.i("Test Exception - ", e.getLocalizedMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseScheme.ToDoList.SQL_DELETE_ENTRIES);
        Log.i("Test - ","SQLHelper Table Upgraded");
        onCreate(db);
    }

    public long recordTodolist(String Title, String Description, String stringDate, boolean
            status){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseScheme.ToDoList.CN_TITLE, Title);
        contentValues.put(DatabaseScheme.ToDoList.CN_DESCRIPTION, Description);
        contentValues.put(DatabaseScheme.ToDoList.CN_DATE, stringDate);
        contentValues.put(DatabaseScheme.ToDoList.CN_STATUS, status);

        try {
            sqLiteDatabase = this.getWritableDatabase();
            insertedRecords = sqLiteDatabase.insert(DatabaseScheme.ToDoList.TABLE_NAME, null,
                    contentValues);
        }
        catch (Exception e){
            Log.i("Test Exception- ", e.getLocalizedMessage());
        }
        finally {
            sqLiteDatabase.close();
        }
        return insertedRecords;
    }

    public Cursor fetchTodolist(boolean CompletedTasks){
        sqLiteDatabase = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
            "_ID",
            DatabaseScheme.ToDoList.CN_TITLE,
            DatabaseScheme.ToDoList.CN_DESCRIPTION,
            DatabaseScheme.ToDoList.CN_DATE,
            DatabaseScheme.ToDoList.CN_STATUS,
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = DatabaseScheme.ToDoList.CN_STATUS+ "=?";
        String[] selectionArgs;
        if(CompletedTasks) selectionArgs = new String[]{"1"};
        else selectionArgs = new String[]{"0"};

        String sortOrder = "date(strftime('%Y-%m-%d',REPLACE(SUBSTR(TASKDATE,-4,1) || SUBSTR(TASKDATE,-3,1) " +
                " || SUBSTR(TASKDATE,-2,1) || SUBSTR(TASKDATE,-1,1) || SUBSTR('-',-1,1) " +
                " || SUBSTR(TASKDATE,-7,1) || SUBSTR(TASKDATE,-6,1) || SUBSTR('-',-1,1) " +
                " || SUBSTR(TASKDATE,-10,1) || SUBSTR(TASKDATE,-9,1),'.','-')))";

        try {
            cursor = sqLiteDatabase.query(
                    DatabaseScheme.ToDoList.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );
            Log.i("Test SQLHelper - ", "");
        }catch (Exception e){
            Log.i("Test SQLHelper - ", e.getLocalizedMessage());
        }
        cursor.moveToFirst();
        return cursor;
    }

    public int updateTodoList(ContentValues updatableColumnvalues, String whereClause, String[] whereArgs){
        int updates;
        try{
            updates = sqLiteDatabase.update(DatabaseScheme.ToDoList.TABLE_NAME,
                    updatableColumnvalues,
                    whereClause,
                    whereArgs);
        }catch (Exception e){
            updates = 0;
            Log.i("Test - ", e.getMessage());
        }
        return updates;
    }

    public int deleteRecord(String whereClause, String[] whereArgs){
        int deletions;
        try{
            deletions = sqLiteDatabase.delete(DatabaseScheme.ToDoList.TABLE_NAME,
                    whereClause, whereArgs);
        } catch (Exception e){
            deletions = 0;
        }
        return deletions;
    }

    public int fetchRecords(boolean CompletedTasks){
        sqLiteDatabase = this.getReadableDatabase();
        int records = 0;
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {"_ID"};

        // Filter results WHERE "title" = 'My Title'
        String selection = DatabaseScheme.ToDoList.CN_STATUS+ "=?";
        String[] selectionArgs;
        if(CompletedTasks) selectionArgs = new String[]{"1"};
        else selectionArgs = new String[]{"0"};

        try {
            cursor = sqLiteDatabase.query(
                    DatabaseScheme.ToDoList.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );
            records = cursor.getCount();
        }catch (Exception e){
            Log.i("Error at SQLHelper - ", e.getLocalizedMessage());
        }
        cursor.moveToFirst();
        return records;
    }
}
