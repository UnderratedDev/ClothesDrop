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
                load(dataString)
                .asJsonObject ()
                .setCallback(
                        new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject json) {
                                if (e != null) {
                                    Toast.makeText (getApplicationContext (), e.toString (), Toast.LENGTH_SHORT).show ();
                                    // error handling goes here
                                } else {
                                        final  JsonElement statusElement, useridElement;
                                        final  String      status;
                                        String userid = "-1";

                                        statusElement = json.get("status");
                                        useridElement = json.get("userid");

                                        status        = statusElement.getAsString();
                                        if (status.equalsIgnoreCase ("success"))
                                            userid        = useridElement.getAsString();

                                        // Log.e ("WOOF", userid + " " + status);

                                        if (dataString.contains ("reqister.php")) {
                                            if (status.equalsIgnoreCase("success")) {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                                                // cache user id, advance to login page with intent
                                                // Log.e ("WOOF", userid + " " + status);
                                            } else {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                            }
                                        } else if (dataString.contains ("login.php")) {
                                            if (status.equalsIgnoreCase("success")) {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                                                LoginActivity.userId = userid;
                                                // Log.e ("WOOF", userid + " " + status);
                                                // cache user id, advance to login page with intent
                                            } else {
                                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                            }
                                        } else
                                            broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                                    }
                            }
                        }
                );
    }

}