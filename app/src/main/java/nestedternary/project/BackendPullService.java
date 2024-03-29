package nestedternary.project;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import nestedternary.project.database.DatabaseHelper;
import nestedternary.project.database.schema.BinLocations;

/**
 * Created by Yudhvir on 10/05/2017.
 */

public class BackendPullService extends IntentService {

    private DatabaseHelper helper;
    private ArrayList<BinLocations> binLocations;

    private BroadcastNotifier broadcaster = new BroadcastNotifier(this);

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public BackendPullService() {
        super("BackendPullService");
    }

    private void insertIntoDatabase () {
        helper = DatabaseHelper.getInstance (this);
        helper.openDatabaseForWriting (this);
        helper.deleteAll ();
        BinLocations[] binLocationsArr = new BinLocations[binLocations.size()];
        binLocations.toArray(binLocationsArr);
        helper.createBinLocationsFromArray (binLocationsArr);
    }

    private void getAll () {
        Cursor binCursor = helper.getBinLocationsCursor();
        for (binCursor.moveToFirst(); !binCursor.isAfterLast (); binCursor.moveToNext ())
            Log.e ("SERVICE WOOF" , helper.getBinLocationFromCursor (binCursor).toString ());
    }

    @Override
    protected void onHandleIntent (Intent workIntent) {
        binLocations = new ArrayList<>();
        String dataString = workIntent.getDataString ();
        Log.e ("Background Service", dataString);
        Ion.with(getApplicationContext()).
                load(dataString).
                asJsonArray()
                .setCallback(
                        new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray array) {
                                if (e != null) {
                                    Toast.makeText (getApplicationContext (), e.toString (), Toast.LENGTH_SHORT).show ();
                                    // error handling goes here
                                } else {
                                    for (final JsonElement el : array) {
                                        final JsonObject json;
                                        final JsonElement nameElement;
                                        final JsonElement pictureUrlElement;
                                        final JsonElement latitudeElement, longtitudeElement;
                                        final String      name;
                                        final String      address;
                                        final double latitude, longtitude;

                                        json              = el.getAsJsonObject();
                                        nameElement       = json.get("name");
                                        pictureUrlElement = json.get("address");
                                        latitudeElement   = json.get("latitude");
                                        longtitudeElement = json.get("longtitude");
                                        name              = nameElement.getAsString();
                                        address           = pictureUrlElement.getAsString();
                                        latitude          = latitudeElement.getAsDouble ();
                                        longtitude        = longtitudeElement.getAsDouble ();
                                        binLocations.add (new BinLocations (null, name, address, latitude, longtitude));
                                    }
                                    insertIntoDatabase ();
                                    getAll ();
                                    helper.close ();
                                }
                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                            }
                        }
                );

    }
}
