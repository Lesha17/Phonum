package lmm.com.phonum.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP on 23.07.2015.
 */
public class MySQLHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "com.lmm.phonum.calls_database";
    public static final String TABLE_NAME = "call_list_table";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static final String NUMBER_COLUMN = "number";
    public static final String FORMATTED_NUMBER_COLUMN = "formatted_number";
    public static final String NAME_COLUMN = "name";
    public static final String CATEGORY_COLUMN = "category";
    public static final String HINT_COLUMN = "hint";
    public static final String ASSIGNED_COLUMN = "assigned_column";
    public static final String LAST_COLUMN = "last";
    public static final String CALLS_COLUMN = "calls";


    public MySQLHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                " (" + "_id INTEGER PRIMARY KEY,"
                + NUMBER_COLUMN + TEXT_TYPE + COMMA_SEP
                + FORMATTED_NUMBER_COLUMN + TEXT_TYPE + COMMA_SEP
                + NAME_COLUMN + TEXT_TYPE + COMMA_SEP
                + CATEGORY_COLUMN + TEXT_TYPE + COMMA_SEP
                + HINT_COLUMN + TEXT_TYPE + COMMA_SEP
                + ASSIGNED_COLUMN + INT_TYPE + COMMA_SEP
                + LAST_COLUMN + INT_TYPE + COMMA_SEP
                + CALLS_COLUMN + TEXT_TYPE
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
