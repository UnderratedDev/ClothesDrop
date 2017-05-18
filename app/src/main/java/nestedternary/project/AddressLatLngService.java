package nestedternary.project;

/**
 * Created by Yudhvir on 18/05/2017.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddressLatLngService extends IntentService {
    private BroadcastNotifier broadcaster = new BroadcastNotifier(this);
    // not a good idea, change to local to class later, edge case
    static String lat, lng;
    public AddressLatLngService () {
        super("AddressLatLngService");
    }

    @Override
    protected void onHandleIntent (Intent workIntent) {
        final String dataString = workIntent.getDataString ();
        Log.e ("Background Service", dataString);
        Ion.with(getApplicationContext())
                .load(dataString)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.d("theres", e.getMessage());
                            // error handling goes here
                        } else {
                            try {
                                Log.d("locationjson", result);
                                JSONArray results;
                                JSONObject obj = new JSONObject(result);
                                if (!obj.getString("status").equalsIgnoreCase("OK"))
                                    broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);

                                results = (JSONArray) obj.get("results");
                                JSONObject temp = (JSONObject) results.get(0);
                                JSONObject geometry = (JSONObject) temp.get("geometry");
                                JSONObject location = (JSONObject) geometry.get("location");

                                lat = location.get("lat").toString();
                                lng = location.get("lng").toString();
                                broadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
                            } catch (Exception ex) {

                            }
                            Log.d("latlng", lat + " " + lng);
                        }
                    }
                });
    }
}
