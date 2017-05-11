package nestedternary.project.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import nestedternary.project.database.schema.BinLocations;
import nestedternary.project.database.schema.BinLocationsDao;
import nestedternary.project.database.schema.DaoMaster;
import nestedternary.project.database.schema.DaoSession;

/**
 * Created by Yudhvir on 11/05/2017.
 */

public class DatabaseHelper {
    private static DatabaseHelper   instance;
    private SQLiteDatabase          db;
    private DaoMaster               daoMaster;
    private DaoSession              daoSession;
    private BinLocationsDao         binLocationsDao;
    private DaoMaster.DevOpenHelper helper;

    private DatabaseHelper(final Context context) {
        openDatabaseForWriting(context);
    }

    public synchronized static DatabaseHelper getInstance(final Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);

        return (instance);
    }

    public static DatabaseHelper getInstance() {
        if (instance == null)
            throw new Error();

        return (instance);
    }

    private void openDatabase() {
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        binLocationsDao = daoSession.getBinLocationsDao();
    }

    public void openDatabaseForWriting(final Context context) {
        helper = new DaoMaster.DevOpenHelper(context, "Bins.db", null);
        db = helper.getWritableDatabase();
        openDatabase();
    }

    public void openDatabaseForReading(final Context context) {
        final DaoMaster.DevOpenHelper helper;

        helper = new DaoMaster.DevOpenHelper(context, "Bins.db", null);
        db = helper.getReadableDatabase();
        openDatabase();
    }

    public void close() { helper.close(); }

    public BinLocations createBinLocation(final String name, final String address, final Double latitude, final Double longitutde) {
        final BinLocations data;

        data = new BinLocations(null, name, address, latitude, longitutde);

        binLocationsDao.insertOrReplace(data);

        return data;
    }

    public BinLocations getBinLocationFromCursor(final Cursor cursor) {
        final BinLocations binLocation;

        binLocation = binLocationsDao.readEntity(cursor, 0);

        return binLocation;
    }

    public List<BinLocations> getBinLocations() {
        return (binLocationsDao.loadAll());
    }

    public Cursor getBinLocationsCursor() {
        final Cursor cursor;

        cursor = db.query(binLocationsDao.getTablename(),
                binLocationsDao.getAllColumns(),
                null,
                null,
                null,
                null,
                null);

        return cursor;
    }

    public void deleteAll() {
        db.execSQL("delete from " + "BinLocations");
    }
}
