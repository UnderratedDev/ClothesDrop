package nestedternary.project;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
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
 * Created by Yudhvir on 16/05/2017.
 */

public class PickupService extends IntentService {

    private DatabaseHelper helper;
    private ArrayList<BinLocations> binLocations;
    // private int status = 0;

    private BroadcastNotifier broadcaster = new BroadcastNotifier(this);

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public PickupService() {
        super("PickupService");
    }

    @Override
    protected void onHandleIntent (Intent workIntent) {

        binLocations = new ArrayList<>();
        final String dataString = workIntent.getDataString ();
        Log.d ("Background Service", dataString);
        if (dataString.contains ("createpickupforuser.php")) {
            Ion.with(getApplicationContext()).
                    load(dataString).
                    asJsonObject()
                    .setCallback(
                            new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject json) {
                                    if (e != null) {
                                        Toast.makeText (getApplicationContext (), e.toString (), Toast.LENGTH_SHORT).show ();
                                        // error handling goes here
                                    } else {
                                        final JsonElement statusElement = json.get ("status");
                                        final String      status        = statusElement.getAsString ();

                                        if (status.equalsIgnoreCase ("success"))
                                            broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                                        else
                                            broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                        }
                                    }
                            }
                    );
        } else if (dataString.contains ("getpickupsforuser.php")) {
            // FINISH IMPLEMENTING AFTER BACKEND IS UP
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

                                            final JsonObject  json;
                                            final JsonElement regionidElement;
                                            final JsonElement addressElement;
                                            final JsonElement notesElement;
                                            final JsonElement dateElement;
                                            final JsonElement bagQtyElement;
                                            final String      name;
                                            final String      address;

                                            json              = el.getAsJsonObject();
                                        }
                                    }
                                }
                            }
                    );
        }

        broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
    }
}
