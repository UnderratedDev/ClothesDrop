package nestedternary.project;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainSchedulingActivity extends AppCompatActivity {

    private String address   = "";
    static HashMap<Region, ArrayList<Integer>> regions = new HashMap<>();
    ArrayList<String> addresses;
    ListView lv;
    ArrayAdapter<String> adapter;
    private MainSchedulingActivity.PickupServiceReciever pickupServiceReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scheduling);

        recieveData ();
    }

    public String URL() {
        return ("http://mail.posabilities.ca:8000/api/getpickupsforuser.php?userid=" + encode(LoginActivity.userId)).replaceAll ("\n", "");
    }

    public String encode(String word) {
        try {
            return Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception ex) {
            Toast.makeText(MainSchedulingActivity.this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void schedulingDetails (final View view) {

        Intent intent = new Intent (MainSchedulingActivity.this, RequestDetailsActivity.class);
        intent.putExtra("location", address);
        startActivity(intent);
    }

    private void recieveData () {
        lv = (ListView) findViewById (android.R.id.list);
        addresses = new ArrayList<>();

        Intent mServiceIntent = new Intent (MainSchedulingActivity.this, PickupService.class);
        mServiceIntent.setData (Uri.parse (URL()));

        startService (mServiceIntent);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION);

        pickupServiceReciever = new MainSchedulingActivity.PickupServiceReciever();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(pickupServiceReciever, intentFilter);
    }

    public void onResume () {
        super.onResume ();
        recieveData ();
    }

    public void jsonRequest(final View view) {
        Log.e("MEOW", "button");
        locationRequest(view);

    }

    public void locationRequest(final View view) {
        Log.e("MEOW", "Inside");
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Get the name of the best provider

            String provider = locationManager.getBestProvider(criteria, true);
            
            // Get Current Location
            Location cur_location = locationManager.getLastKnownLocation(provider);
            Log.e("MEOW", "first else");
            if(cur_location != null) {
                Log.e("MEOW", "In the if");
                Ion.with(this).
                        load("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + cur_location.getLatitude() + "," + cur_location.getLongitude() + "&sensor=true").
                        asString().
                        setCallback(
                                new FutureCallback<String>() {

                                    @Override
                                    public void onCompleted(final Exception ex,
                                                            String result) {
                                        String name = null;
                                        if (ex != null) {
                                            Toast.makeText(MainSchedulingActivity.this,
                                                    "Error: " + ex.getMessage(),
                                                    Toast.LENGTH_LONG).show();

                                        } else {
                                            Log.e("MEOW", "ion else");

                                            try {
                                                final JsonElement nameElement;
                                                JSONArray results;
                                                JSONObject obj = new JSONObject(result);
                                                results = (JSONArray) obj.get("results");
                                                JSONObject temp = (JSONObject) results.get(0);

                                                address = temp.get("formatted_address").toString();
                                                //if(region && location)
                                                // schedulingDetails(view, ListRegions);
                                                regionRequest(view);
                                            } catch (Exception e) {
                                                Log.e("MEOW", "ion catch " + e.getMessage());
                                            }

                                        }


                                    }
                                });
            } else {
                regionRequest (view);
            }
        }

    }

    public void regionRequest(final View view) {
        regions.clear ();
        // ListRegions.clear();
        Ion.with(this).
                load("http://mail.posabilities.ca:8000/api/schedulingjson.php").
                asJsonArray().
                setCallback(
                        new FutureCallback<JsonArray>()
                        {

                            @Override
                            public void onCompleted(final Exception ex,
                                                    final JsonArray array)
                            {
                                if(ex != null)
                                {
                                    Toast.makeText(MainSchedulingActivity.this,
                                            "Error: " + ex.getMessage(),
                                            Toast.LENGTH_LONG).show();

                                }
                                else
                                {
                                    for(final JsonElement element : array)
                                    {
                                        final JsonObject json;
                                        final JsonArray  datesJson;
                                        final JsonElement nameElement, idElement, dates;
                                        final String             name;
                                        final int                id;
                                        ArrayList<Integer> datesList = new ArrayList <>();

                                        json              = element.getAsJsonObject();
                                        nameElement       = json.get ("name");
                                        idElement         = json.get ("id");
                                        dates             = json.get ("regionDayPicker");

                                        name              = nameElement.getAsString();
                                        id                = idElement.getAsInt ();
                                        datesJson         = dates.getAsJsonArray();

                                        for (JsonElement el : datesJson)
                                            datesList.add (el.getAsInt ());

                                        regions.put (new Region (name, id, datesList), datesList);
                                        // ListRegions.add(name);
                                    }
                                    //region = true;
                                    //if(region && location)
                                    Log.e (":(", ":)");
                                    schedulingDetails(view); // ListRegions);
                                }

                            }
                        });
    }

    private class PickupServiceReciever extends BroadcastReceiver {

        private PickupServiceReciever() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(Constants.EXTENDED_DATA_STATUS, Constants.STATE_ACTION_CONNECTING);
            if (status == Constants.STATE_ACTION_COMPLETE) {
                addresses.clear ();
                for (Pickup p : PickupService.pickups)
                    addresses.add (p.address);
                Log.e (":)", "" + addresses.size ());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext (), android.R.layout.simple_list_item_1, addresses);
                lv.setAdapter (adapter);
                lv.setOnItemClickListener (new AdapterView.OnItemClickListener () {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        regions.clear ();
                        // ListRegions.clear();
                        Ion.with(getApplicationContext ()).
                                load("http://mail.posabilities.ca:8000/api/schedulingjson.php").
                                asJsonArray().
                                setCallback(
                                        new FutureCallback<JsonArray>()
                                        {

                                            @Override
                                            public void onCompleted(final Exception ex,
                                                                    final JsonArray array)
                                            {
                                                if(ex != null)
                                                {
                                                    Toast.makeText(MainSchedulingActivity.this,
                                                            "Error: " + ex.getMessage(),
                                                            Toast.LENGTH_LONG).show();

                                                }
                                                else
                                                {
                                                    for(final JsonElement element : array)
                                                    {
                                                        final JsonObject json;
                                                        final JsonArray  datesJson;
                                                        final JsonElement nameElement, idElement, dates;
                                                        final String             name;
                                                        final int                id;
                                                        ArrayList<Integer> datesList = new ArrayList <>();

                                                        json              = element.getAsJsonObject();
                                                        nameElement       = json.get ("name");
                                                        idElement         = json.get ("id");
                                                        dates             = json.get ("regionDayPicker");

                                                        name              = nameElement.getAsString();
                                                        id                = idElement.getAsInt ();
                                                        datesJson         = dates.getAsJsonArray();

                                                        for (JsonElement el : datesJson)
                                                            datesList.add (el.getAsInt ());

                                                        regions.put (new Region (name, id, datesList), datesList);
                                                        // ListRegions.add(name);
                                                    }
                                                    //region = true;
                                                    //if(region && location)
                                                     // ListRegions);
                                                    Pickup p = PickupService.pickups.get (position);
                                                    Intent intent = new Intent (MainSchedulingActivity.this, ScheduledRequestDetailsActivity.class);
                                                    // intent.putExtra ("hMap", regions);
                                                    intent.putExtra ("selectedPickup", p);
                                                    startActivity (intent);
                                                    // finish ();
                                                }

                                            }
                                        });
                    }
                });
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(pickupServiceReciever);
            } else if (status == Constants.STATE_ACTION_FAILED) {
                Toast.makeText(getApplicationContext(), "Could not get pickups", Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(pickupServiceReciever);
            }
        }
    }

}
