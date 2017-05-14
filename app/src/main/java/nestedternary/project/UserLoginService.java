package nestedternary.project;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import nestedternary.project.database.schema.BinLocations;

/**
 * Created by Yudhvir on 14/05/2017.
 */

public class UserLoginService extends IntentService {

    private BroadcastNotifier broadcaster = new BroadcastNotifier(this);

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public UserLoginService () {
        super("UserLoginService");
    }

    @Override
    protected void onHandleIntent (Intent workIntent) {
        final String dataString = workIntent.getDataString ();
        Log.e ("WOOF", dataString);
        Ion.with(getApplicationContext()).
                load(dataString).
                asJsonArray()
                .setCallback(
                        new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray array) {
                                if (e != null) {
                                    // error handling goes here
                                } else {
                                    for (final JsonElement el : array) {
                                        final JsonObject json;
                                        final JsonElement statusElement, useridElement;
                                        final String      status, userid;

                                        json          = el.getAsJsonObject();
                                        statusElement = json.get("status");
                                        useridElement = json.get("address");
                                        status        = statusElement.getAsString();
                                        userid        = useridElement.getAsString();

                                        Log.e ("WOOF", userid + " " + status);

                                        if (dataString.contains ("reqister.php")) {
                                            if (status.equalsIgnoreCase("success")) {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                                                // cache user id, advance to login page with intent
                                                Log.e ("WOOF", userid + " " + status);
                                            } else {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                            }
                                        } else if (dataString.contains ("login.php")) {
                                            if (status.equalsIgnoreCase("success")) {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                                                Log.e ("WOOF", userid + " " + status);
                                                // cache user id, advance to login page with intent
                                            } else {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                            }
                                        }
                                        broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                    }
                                }
                            }
                        }
                );
    }

}