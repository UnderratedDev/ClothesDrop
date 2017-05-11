package nestedternary.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.List;

public class MainSchedulingActivity extends AppCompatActivity {

    String address = null;
    boolean location = false, region = false;
    ArrayList<String> ListRegions = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scheduling);
    }

    public void schedulingDetails (final View view, ArrayList<String> regionInfo) {


        Intent intent = new Intent (MainSchedulingActivity.this, RequestDetailsActivity.class);

        if(regionInfo.size() == 0)
        {
            Toast.makeText(MainSchedulingActivity.this,
                    "Error with connection please try again",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            intent.putStringArrayListExtra("regionList", regionInfo);
            intent.putExtra("location", address);
            startActivity(intent);
        }


    }

    public void jsonRequest(final View view)
    {
        Log.e("MEOW", "button");
        locationRequest(view);
        regionRequest(view);

    }

    public void locationRequest(final View view){
        Log.e("MEOW", "Inside");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            location = true;
            return;
        }
        else
        {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Get the name of the best provider

            String provider = locationManager.getBestProvider(criteria, true);
            // Get Current Location
            Location cur_location = locationManager.getLastKnownLocation(provider);


            Log.e("MEOW", "first else");
            Ion.with(this).
                    load("http://maps.googleapis.com/maps/api/geocode/json?latlng="+ cur_location.getLatitude() + "," + cur_location.getLongitude() +"&sensor=true").
                    asString().
                    setCallback(
                            new FutureCallback<String>()
                            {

                                @Override
                                public void onCompleted(final Exception ex,
                                                        String result)
                                {
                                    String      name = null;
                                    if(ex != null)
                                    {
                                        Toast.makeText(MainSchedulingActivity.this,
                                                "Error: " + ex.getMessage(),
                                                Toast.LENGTH_LONG).show();

                                    }
                                    else
                                    {
                                        Log.e("MEOW", "ion else");

                                        try
                                        {
                                            final JsonElement nameElement;
                                            JSONArray results;
                                            JSONObject obj = new JSONObject(result);
                                            results         = (JSONArray)obj.get("results");
                                            JSONObject temp  =(JSONObject) results.get(0);

                                            address = temp.get("formatted_address").toString();
                                            location = true;
                                            if(region && location)
                                                schedulingDetails(view, ListRegions);
                                        }
                                        catch (Exception e)
                                        {
                                            Log.e("MEOW", "ion catch " + e.getMessage());
                                        }

                                    }


                                }
                            });

        }

    }


    public void regionRequest(final View view){

        Ion.with(this).
                load("http://mail.posabilities.ca:8000/api/getregions.php").
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
                                        final JsonElement nameElement;
                                        final String      name;

                                        json              = element.getAsJsonObject();
                                        nameElement       = json.get("regionname");
                                        name              = nameElement.getAsString();
                                        ListRegions.add(name);
                                    }
                                    region = true;
                                    if(region && location)
                                        schedulingDetails(view, ListRegions);
                                }

                            }
                        });
    }

}
