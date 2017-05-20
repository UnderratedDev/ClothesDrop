package nestedternary.project;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by Yudhvir on 16/05/2017.
 */

public class PickupService extends IntentService {

    public static ArrayList<Pickup> pickups = new ArrayList<>();
    // private int status = 0;

    private BroadcastNotifier broadcaster   = new BroadcastNotifier(this);

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
        final String dataString = workIntent.getDataString ();
        Log.e ("Background Service", dataString);
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
                                        else if (status.equalsIgnoreCase ("User limit reached"))
                                            broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_LIMIT_REACHED);
                                        else
                                            broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                        }
                                    }
                            }
                    );
        } else if (dataString.contains ("getpickupsforuser.php")) {
            // FINISH IMPLEMENTING AFTER BACKEND IS UP
            pickups.clear();
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

                                            final JsonObject  json              = el.getAsJsonObject();
                                            final JsonElement pickupIdElement   = json.get ("pickupid");
                                            final JsonElement regionIdElement   = json.get ("regionid");
                                            final JsonElement regionNameElement = json.get ("regionname");
                                            final JsonElement bagQtyElement     = json.get ("bagqty");
                                            final JsonElement addressElement    = json.get ("address");
                                            final JsonElement notesElement      = json.get ("notes");
                                            final JsonElement dateElement       = json.get ("date");
                                            final JsonElement latElement        = json.get ("lat");
                                            final JsonElement lngElement        = json.get ("lng");
                                            final int         pickupid          = pickupIdElement.getAsInt ();
                                            final int         regionid          = regionIdElement.getAsInt ();
                                            final String      regionname        = regionNameElement.getAsString ();
                                            final int         bagQty            = bagQtyElement.getAsInt ();
                                            final String      address           = addressElement.getAsString ();
                                            final String      notes             = !notesElement.isJsonNull() ? notesElement.getAsString () : null;
                                            final String      date              = dateElement.getAsString();
                                            final float       lat               = latElement.getAsFloat ();
                                            final float       lng               = lngElement.getAsFloat ();


                                            pickups.add (new Pickup (pickupid, bagQty, date, address, notes, lat, lng, regionname, regionid));

                                            Log.e (":)", "" + pickups.size ());

                                            // public Pickup (int pickupid, int bagQty, int date, String address, String notes, float lat, float lng, String name, int regionid) {
                                        }
                                        broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                                    }
                                }
                            }
                    );
        } else if (dataString.contains ("modifypickupforuser.php")) {
            Ion.with(getApplicationContext()).
                    load(dataString).
                    asJsonObject()
                    .setCallback(
                            new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject json) {
                                    if (e != null) {
                                        Toast.makeText (getApplicationContext (), e.toString (), Toast.LENGTH_SHORT).show ();
                                    } else {
                                            final JsonElement statusElement = json.get ("status");
                                            final String status             = statusElement.getAsString ();

                                            if (!status.equalsIgnoreCase ("success"))
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);

                                        broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                                    }
                                }
                            }
                    );
        }
    }
}
