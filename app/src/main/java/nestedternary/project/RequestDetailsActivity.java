package nestedternary.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class RequestDetailsActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter, dateAdapter;
    // ArrayList<String> regionList = new ArrayList<>();
    // HashMap<Region, ArrayList<Integer>> regionsMap = new HashMap<>();
    int regionId = -1;
    Spinner regions, date_picker;
    EditText location, bagQty, notes;

    private String lat, lng;

    private RequestDetailsActivity.PickupServiceReciever pickupServiceReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Intent intent = getIntent ();
        // regionsMap = (HashMap<Region, ArrayList<Integer>>)intent.getSerializableExtra ("hMap");
        // regionList = intent.getStringArrayListExtra("regionList");
        // what happens if location is never set, this can happen if GPS is not enabled
        String address = intent.getStringExtra("location");

        regions     = (Spinner)findViewById(R.id.spinner);
        date_picker = (Spinner) findViewById (R.id.date_picker);

        ArrayList<String> regionNames = new ArrayList<>();

        for (Region r : MainSchedulingActivity.regions.keySet()) {
            regionNames.add(r.getName());
        }

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, regionNames);

        // adapter = new HashMapRegionAdapter (regionsMap);

        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location    = (EditText) findViewById (R.id.location);
        bagQty      = (EditText) findViewById (R.id.bagQty);
        notes       = (EditText) findViewById (R.id.notes);

        regions.setAdapter(adapter);

        regions.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Region obj = (Region)(MainSchedulingActivity.regions.keySet()).toArray ()[position];
                regionId = obj.getId ();
                ArrayList<Integer> numericDates  = MainSchedulingActivity.regions.get (obj);
                ArrayList<String> formattedDates = new ArrayList<>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                simpleDateFormat.setTimeZone (TimeZone.getTimeZone ("GMT-7"));
                for (Integer numericDate : numericDates) {
                    Date date = new Date(numericDate * 1000L);
                    formattedDates.add(simpleDateFormat.format(date));
                }
                // Toast.makeText(RequestDetailsActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                Log.e (":|", "" + formattedDates.size());
                if (formattedDates.isEmpty ())
                    formattedDates.add ("No Available days");
                dateAdapter = new ArrayAdapter<>(getApplicationContext (), android.R.layout.simple_spinner_item, formattedDates);
                date_picker.setAdapter (dateAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(location != null && address != null)
            location.setText(address);
    }

    public void cancel(final View view) {
        finish();
    }

    public void callUs(final View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:7782584377"));
        startActivity(intent);
    }

    // get lat and long from address
    public void requestPickup (final View view) {
        final Intent mServiceIntent = new Intent (RequestDetailsActivity.this, PickupService.class);

        final Object selected_region_obj = regions.getSelectedItem (), selected_date_obj = date_picker.getSelectedItem ();
        final Editable location_inputted_obj = location.getText (), bagQtyInputted_obj = bagQty.getText();
        if (selected_region_obj == null || selected_date_obj == null || location_inputted_obj == null || bagQtyInputted_obj == null)
            return;
        final String selected_region = selected_region_obj.toString (), selected_date = selected_date_obj.toString (), location_inputted = location_inputted_obj.toString (), bagQtyInputted = bagQtyInputted_obj.toString();
        final String address = location_inputted;
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + Uri.encode(location_inputted);

        Ion.with(getApplicationContext())
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            // error handling goes here
                        } else {
                            try {
                                JSONArray results;
                                JSONObject obj = new JSONObject(result);
                                if (!obj.getString("status").equalsIgnoreCase("OK")) {
                                    return;
                                }
                                results = (JSONArray) obj.get("results");
                                JSONObject temp = (JSONObject) results.get(0);
                                JSONObject geometry = (JSONObject) temp.get("geometry");
                                JSONObject location = (JSONObject) geometry.get("location");

                                lat = location.get("lat").toString();
                                lng = location.get("lng").toString();
                            } catch (Exception ex) {

                            }

                            //String url = (("http://mail.posabilities.ca:8000/api/createpickupforuser.php?userid=" + encode(LoginActivity.userId) + "&regionid=" + encode(Integer.toString (regionId)) + "&bagqty=" + encode (bagQtyInputted)
                                   // + "&address=" + encode (address) + "&lat=" + encode (lat) + "&lng=" + encode (lng) + "&date=" + encode (selected_date) +  "&notes=" + encode (notes_inputted)).replaceAll ("\n", "")).replaceAll (" ", "%20");
                            String url = (("http://mail.posabilities.ca:8000/api/createpickupforuser.php?userid=" + encode(LoginActivity.userId) + "&regionid=" + encode(Integer.toString (regionId)) + "&bagqty=" + encode (bagQtyInputted)
                                    + "&address=" + encode (address) + "&lat=" + encode (lat) + "&lng=" + encode (lng) + "&date=" + encode (selected_date) +  "&notes=").replaceAll ("\n", "")).replaceAll (" ", "%20");
                            if (url != null && !url.isEmpty()) {
                                mServiceIntent.setData(Uri.parse(url));
                                // mServiceIntent.setData (Uri.parse ("http://mail.posabilities.ca:8000/api/login.php?email=YWJjQGdtYWlsLmNvbQ&password=cHc"));
                                startService(mServiceIntent);

                                IntentFilter intentFilter = new IntentFilter();
                                intentFilter.addAction(Constants.BROADCAST_ACTION);
                                pickupServiceReciever = new RequestDetailsActivity.PickupServiceReciever();

                                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(pickupServiceReciever, intentFilter);
                                // Background serivce to complete create pickup
                            }// else
                               // Toast.makeText(getApplicationContext(), "Unable to proccess request", Toast.LENGTH_LONG).show ();
                        }
                    }
                });

    }

    public String URL() {

        final Object selected_region_obj = regions.getSelectedItem(), selected_date_obj = date_picker.getSelectedItem();
        final Editable location_inputted_obj = location.getText(), bagQtyInputted_obj = bagQty.getText();
        if (selected_region_obj == null || selected_date_obj == null || location_inputted_obj == null || bagQtyInputted_obj == null)
            return null;
        final String selected_region = selected_region_obj.toString(), selected_date = selected_date_obj.toString(), location_inputted = location_inputted_obj.toString(), bagQtyInputted = bagQtyInputted_obj.toString();

        final String notes_inputted = notes.getText() != null ? notes.getText().toString() : "";

        boolean complete = false;
        // Toast.makeText (getApplicationContext (), regionId + " " + selected_region + " " + selected_date + " " + location_inputted + " " + bagQtyInputted, Toast.LENGTH_LONG).show ();


        // TextView emailTextView    = ((TextView) findViewById(R.id.txt_username)), passwordTextView = ((TextView) findViewById(R.id.txt_password));
        // String email = emailTextView.getText().toString (), password = passwordTextView.getText ().toString ();

        // ADD NOTES LATERRRR

        String address = location_inputted;
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + Uri.encode(location_inputted);

        Ion.with(getApplicationContext())
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.d("whats happening", location_inputted);
                        if (e != null) {
                            Log.d("theres", e.getMessage());
                            // error handling goes here
                        } else {
                            try {
                                Log.d("locationjson", result);
                                JSONArray results;
                                JSONObject obj = new JSONObject(result);
                                if (!obj.getString("status").equalsIgnoreCase("OK")) {
                                    return;
                                }
                                results = (JSONArray) obj.get("results");
                                JSONObject temp = (JSONObject) results.get(0);
                                JSONObject geometry = (JSONObject) temp.get("geometry");
                                JSONObject location = (JSONObject) geometry.get("location");

                                lat = location.get("lat").toString();
                                lng = location.get("lng").toString();
                            } catch (Exception ex) {

                            }
                            Log.d("latlng", lat + " " + lng);
                        }
                    }
                });

        while (lat == null || lng == null) ;

        // if (lat == null || lng == null)
        // return null;

        if (LoginActivity.userId == null || regionId == -1 || bagQtyInputted.isEmpty() || address.isEmpty() || selected_date == null || selected_date.equalsIgnoreCase("No Available days")) {
            Toast.makeText(getApplicationContext(), "Unable to proccess request", Toast.LENGTH_LONG).show ();
            return null;
        }

        if (Integer.parseInt (bagQtyInputted) < 20) {
            Toast.makeText (getApplicationContext (), "Bag Qty must be equal to or greater than 20", Toast.LENGTH_LONG).show ();
            return null;
        }

        return ("http://mail.posabilities.ca:8000/api/createpickupforuser.php?userid=" + encode(LoginActivity.userId) + "&regionid=" + encode(Integer.toString (regionId)) + "&bagqty=" + encode (bagQtyInputted)
                + "&address=" + encode (address) + "&lat=" + encode (lat) + "&lng=" + encode (lng) + "&date=" + encode (selected_date) +  "&notes=" + encode (notes_inputted)).replaceAll ("\n", "");
    }

    public String encode(String word) {

        try {
            return Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception ex){
            Toast.makeText(RequestDetailsActivity.this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private class PickupServiceReciever extends BroadcastReceiver {

        private PickupServiceReciever() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(Constants.EXTENDED_DATA_STATUS, Constants.STATE_ACTION_CONNECTING);
            if (status == Constants.STATE_ACTION_COMPLETE) {
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(pickupServiceReciever);
                /// May need to call oncreate for previous screen
                finish();
            } else if (status == Constants.STATE_ACTION_FAILED) {
                Toast.makeText(getApplicationContext(), "Could not create request", Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(pickupServiceReciever);
            } else if (status == Constants.STATE_ACTION_LIMIT_REACHED) {
                Toast.makeText(getApplicationContext(), "User Limit Reached", Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(pickupServiceReciever);
            }
        }
    }
}