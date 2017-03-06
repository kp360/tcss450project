package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Pages.db";

    private CourseDBHelper mCourseDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public PageDB(Context context) {
        mCourseDBHelper = new CourseDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCourseDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the course into the local sqlite table. Returns true if successful, false otherwise.
     * @param username
     * @param pageNum
     * @return true or false
     */
    public boolean insertCourse(String username, int pageNum) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("pageNum", pageNum);

        long rowId = mSQLiteDatabase.insert("Course", null, contentValues);
        return rowId != -1;
    }

    public boolean updateCourses(String username, int pageNum) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("pageNum", pageNum);
        long rowId = mSQLiteDatabase.update("Course", contentValues, "username = ?", new String[] {username});

        return rowId != -1;
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    private static final String COURSE_TABLE = "Course";

    /**
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public int getPage(String username) {

        String[] columns = {
                "pageNum"
        };

        String whereClause = "username =?";
        String[] whereArgs = new String[]{username};
        Cursor c = mSQLiteDatabase.query(
                COURSE_TABLE,  // The table to query
                columns,                               // The columns to return
                whereClause,                                // The columns for the WHERE clause
                whereArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        if (c.getCount() == 0)
            return -1;
        else
            return c.getInt(0);
    }

    class CourseDBHelper extends SQLiteOpenHelper {

        private final String CREATE_COURSE_SQL;

        private final String DROP_COURSE_SQL;

        public CourseDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_COURSE_SQL = context.getString(R.string.CREATE_COURSE_SQL);
            DROP_COURSE_SQL = context.getString(R.string.DROP_COURSE_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_COURSE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_COURSE_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}
