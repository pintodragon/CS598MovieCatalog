package edu.sunyit.chryslj.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * This class defines the category associations table. It contains methods and
 * fields for creating and upgrading the table.
 * 
 * @author Justin Chrysler
 * 
 */
public class CategoryMovieAssociationTable implements DatabaseTable
{
    public static final String TABLE_ASSOCIATIONS = "category_associations";
    public static final String TABLE_ASSOCIATIONS_BACKUP = TABLE_ASSOCIATIONS +
            "_backup";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOVIEID = "movie_id";
    public static final String COLUMN_CATEGORYID = "category_id";

    private static final String TABLE_CREATE = "CREATE TABLE " +
            TABLE_ASSOCIATIONS + "(" + COLUMN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MOVIEID +
            " INTEGER NOT NULL, " + COLUMN_CATEGORYID + " INTEGER NOT NULL," +
            " FOREIGN KEY(" + COLUMN_MOVIEID + ") REFERENCES " +
            MovieTable.TABLE_MOVIES + "(" + MovieTable.COLUMN_ID + ")" +
            " FOREIGN KEY(" + COLUMN_CATEGORYID + ") REFERENCES " +
            MovieCategoryTable.TABLE_CATEGORY + "(" +
            MovieCategoryTable.COLUMN_ID + "));";

    // Used for upgrading the table.
    private static final String CREATE_BACKUP = "CREATE TEMPORARY TABLE " +
            TABLE_ASSOCIATIONS_BACKUP + " AS SELECT * FROM " +
            TABLE_ASSOCIATIONS + ";";

    private static final String DROP_TABLE = "DROP TABLE " +
            TABLE_ASSOCIATIONS + ";";

    private static final String COPY_TABLE_BACK = "INSERT INTO " +
            TABLE_ASSOCIATIONS + " SELECT * FROM " + TABLE_ASSOCIATIONS_BACKUP +
            ";";

    @Override
    public String[] getColumnNames()
    {
        return new String[] { COLUMN_ID, COLUMN_MOVIEID, COLUMN_CATEGORYID };
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(TABLE_CREATE);
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
}
