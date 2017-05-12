package nestedternary.project;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import nestedternary.project.database.DatabaseHelper;

/**
 * Created by Yudhvir on 11/05/2017.
 */

public class BinContentProvider extends ContentProvider{
    private static final UriMatcher uriMatcher;
    private static final int GET_BINS_URI_INT = 1;
    public static final Uri GET_BINS_URI;
    private DatabaseHelper helper;

    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("nestedternary.project.database.schema", "BinLocations", GET_BINS_URI_INT);
    }

    static
    {
        GET_BINS_URI = Uri.parse ("content://nestedternary.project.database.schema/BinLocations");
    }

    @Override
    public boolean onCreate()
    {
        helper = DatabaseHelper.getInstance(getContext());

        return true;
    }

    @Override
    public Cursor query(final Uri uri,
                        final String[] projection,
                        final String selection,
                        final String[] selectionArgs,
                        final String sortOrder)
    {
        final Cursor cursor;

        switch (uriMatcher.match(uri))
        {
            case GET_BINS_URI_INT:
            {
                final SQLiteDatabase db;

                helper.openDatabaseForReading(getContext());
                cursor = helper.getBinLocationsCursor();
                helper.close();
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        }

        return (cursor);
    }

    @Override
    public String getType(final Uri uri)
    {
        final String type;

        switch(uriMatcher.match(uri))
        {
            case GET_BINS_URI_INT:
                type = "vnd.android.cursor.dir/vnd.nestedternary.project.daodatabase.schema.BinLocations";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        return (type);
    }

    @Override
    public int delete(final Uri uri,
                      final String selection,
                      final String[] selectionArgs)
    {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(final Uri uri,
                      final ContentValues values)
    {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(final Uri uri,
                      final ContentValues values,
                      final String selection,
                      final String[]      selectionArgs)
    {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
