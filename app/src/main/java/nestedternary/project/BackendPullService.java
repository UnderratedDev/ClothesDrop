package nestedternary.project;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by Yudhvir on 10/05/2017.
 */

public class BackendPullService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public BackendPullService() {
        super("BackendPullService");
    }

    @Override
    protected void onHandleIntent (Intent workIntent) {
        String dataString = workIntent.getDataString ();
        Log.d ("Background Service", dataString);
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
                                        final JsonElement nameElement;
                                        final JsonElement pictureUrlElement;
                                        final JsonElement latitudeElement, longtitudeElement;
                                        final String      name;
                                        final String      pictureURL;
                                        final double latitude, longtitude;

                                        json              = el.getAsJsonObject();
                                        nameElement       = json.get("name");
                                        pictureUrlElement = json.get("address");
                                        latitudeElement   = json.get("lat");
                                        longtitudeElement = json.get("long");
                                        name              = nameElement.getAsString();
                                        pictureURL        = pictureUrlElement.getAsString();
                                        latitude          = latitudeElement.getAsDouble ();
                                        longtitude        = longtitudeElement.getAsDouble ();
                                        Log.d("X",
                                                name + " -> " + pictureURL + " " + latitude + " " + longtitude);
                                    }
                                }
                            }
                        }
                );
    }
}
