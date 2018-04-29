package accountsguy.net.taskmanager.database;

import android.provider.BaseColumns;

/**
 * Created by AccountsGuy.Net on 26/03/2018.
 * This Class is a Scheme for yor Database Such as Table Name, Column Names
 */

public class DatabaseScheme {
    public static final String DATABASE_NAME = "AccountsGuy.db";
    public static final int DATABASE_VERSION = 1;

    private DatabaseScheme(){}

    public static class ToDoList implements BaseColumns{
        public static final String TABLE_NAME = "ToDoList";
        public static final String CN_ID = "_ID";
        public static final String CN_TITLE = "TITLE";
        public static final String CN_DESCRIPTION = "DESCRIPTION";
        public static final String CN_DATE = "TASKDATE";
        public static final String CN_STATUS="STATUS";

        public static final String TABLE_CREATE_STATEMENT =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        CN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        CN_TITLE + " TEXT," +
                        CN_DESCRIPTION + " TEXT," +
                        CN_DATE + " TEXT," +
                        CN_STATUS + " BOOLEAN)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
