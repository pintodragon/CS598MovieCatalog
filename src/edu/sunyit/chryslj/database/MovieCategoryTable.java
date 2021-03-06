package edu.sunyit.chryslj.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class defines the category table. It contains methods and fields for
 * creating and upgrading the table.
 * 
 * @author Justin Chrysler
 * 
 */
public class MovieCategoryTable implements DatabaseTable
{
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_CATEGORY_BACKUP = TABLE_CATEGORY +
            "_backup";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";

    private static final String TABLE_CREATE = "CREATE TABLE " +
            TABLE_CATEGORY + "(" + COLUMN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE +
            " TEXT NOT NULL);";

    // Used for upgrading the table.
    private static final String CREATE_BACKUP = "CREATE TEMPORARY TABLE " +
            TABLE_CATEGORY_BACKUP + " AS SELECT * FROM " + TABLE_CATEGORY + ";";

    private static final String DROP_TABLE = "DROP TABLE " + TABLE_CATEGORY +
            ";";

    private static final String COPY_TABLE_BACK = "INSERT INTO " +
            TABLE_CATEGORY + " SELECT * FROM " + TABLE_CATEGORY_BACKUP + ";";

    private static final String[] defaultCategories = new String[] {
            "Unsorted", "Wishlist", "Loaned", "Borrowing" };

    @Override
    public String[] getColumnNames()
    {
        return new String[] { COLUMN_ID, COLUMN_TITLE };
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(TABLE_CREATE);

        insertDefaultLists(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion)
    {
        // Create a backup of the previous table.
        database.execSQL(CREATE_BACKUP);

        // Drop the original
        database.execSQL(DROP_TABLE);

        // Create the new version
        database.execSQL(TABLE_CREATE);

        // Copy the data back
        database.execSQL(COPY_TABLE_BACK);
    }

    private void insertDefaultLists(SQLiteDatabase database)
    {
        try
        {
            for (String categoryVals : defaultCategories)
            {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, categoryVals);
                database.insertOrThrow(TABLE_CATEGORY, null, values);
            }
        }
        catch (Exception e)
        {
            Log.e("Error in transaction", e.toString());
        }
    }

    /**
     * This method checks the given category name to see if it is in the list of
     * default categories.
     * 
     * @param categoryName
     *            category to check.
     * @return if the category is a default category.
     */
    public static boolean isDefaultCategory(String categoryName)
    {
        boolean isDefault = false;

        for (String categoryVals : defaultCategories)
        {
            if (categoryName.equals(categoryVals))
            {
                isDefault = true;
                break;
            }
        }

        return isDefault;
    }
}
