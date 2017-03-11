package tcss450team3.uw.tacoma.edu.justjokes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import tcss450team3.uw.tacoma.edu.justjokes.R;

/**
 * This class is used to store the page number of the last page the user browsed. This stored page
 * number is then retrieved on the user's next login, and we automatically go to the page they were
 * last browsing. Page numbers are stored with usernames, so multiple users can use our app on the
 * same device, without the fear of their last browsed page number being overwritten.
 *
 * @author Vlad 3/6/2017
 */
public class PageDB {

    /** The name of our table */
    private static final String PAGE_TABLE = "PageNumbers";

    /** Our database version number. */
    private static final int DB_VERSION = 1;

    /** The name of our database. */
    private static final String DB_NAME = "PageNumbers.db";

    /** The SQLite database we will be using to manager our data. */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Constructor to initialize all of our fields.
     * @param context The context our objects will exist in.
     */
    public PageDB(Context context) {
        PageDBHelper aPageDBHelper = new PageDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = aPageDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the page number into the local sqlite table. Returns true if successful, false otherwise.
     * @param username The username of the person currently using the app.
     * @param pageNum The page of jokes that they were last visiting.
     * @return true or false
     */
    public boolean insertRow(String username, int pageNum) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("pageNum", pageNum);

        long rowId = mSQLiteDatabase.insert(PAGE_TABLE, null, contentValues);
        return rowId != -1;
    }

    /**
     * Updates the page number in the local sqlite table. Returns true if successful, false otherwise.
     * @param username The username of the person currently using the app.
     * @param pageNum The page of jokes that they were last visiting.
     * @return true or false
     */
    public boolean updatePageNum(String username, int pageNum) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("pageNum", pageNum);
        long rowId = mSQLiteDatabase.update(PAGE_TABLE, contentValues, "username = ?", new String[] {username});

        return rowId != -1;
    }


    /**
     * Returns the page number that the user was last on, from the local Page table.
     * @return -1 if the user's last page wasn't stored, or the user's last visited page number.
     */
    public int getPage(String username) {

        String[] columns = {
                "pageNum"
        };

        String whereClause = "username =?";
        String[] whereArgs = new String[]{username};
        Cursor c = mSQLiteDatabase.query(
                PAGE_TABLE,  // The table to query
                columns,                               // The columns to return
                whereClause,                                // The columns for the WHERE clause
                whereArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        int pageNumber = -1; //-1 means that our query didn't return anything.
        if (c.getCount() > 0)
            pageNumber = c.getInt(0);
        c.close(); //Closing this to avoid warning messages about memory leaks.
        return pageNumber;
    }

    /** A class that helps us manage our PageDB table. */
    private class PageDBHelper extends SQLiteOpenHelper {

        /** SQL command to create our SQLite table. */
        private final String CREATE_PAGE_SQL;

        /** SQL command to drop/delete our SQLite table. */
        private final String DROP_PAGE_SQL;

        /**
         * Constructor to initialize our two fields.
         * @param context The context our objects will exist in.
         * @param name Database name.
         * @param factory Unknown usage.
         * @param version Database version.
         */
        public PageDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_PAGE_SQL = context.getString(R.string.CREATE_PAGE_SQL);
            DROP_PAGE_SQL = context.getString(R.string.DROP_PAGE_SQL);

        }

        /**
         * Creates the SQLite table when created.
         * @param sqLiteDatabase The SQLite database to store the table in.
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_PAGE_SQL);
        }

        /**
         * Drops our Page table, and immediately recreates it.
         * @param sqLiteDatabase The SQLite database to store the table in.
         * @param i Variable necessary to override superclass, unknown usage.
         * @param i1 Variable necessary to override superclass, unknown usage.
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_PAGE_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}
