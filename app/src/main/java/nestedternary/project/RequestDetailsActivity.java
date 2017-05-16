package nestedternary.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class RequestDetailsActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter, dateAdapter;
    // ArrayList<String> regionList = new ArrayList<>();
    HashMap<Region, ArrayList<Integer>> regionsMap = new HashMap<>();
    int regionId;
    Spinner regions, date_picker;
    TextView location, bagQty;

    private RequestDetailsActivity.PickupServiceReciever pickupServiceReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Intent intent = getIntent ();
        regionsMap = (HashMap<Region, ArrayList<Integer>>)intent.getSerializableExtra ("hMap");
        // regionList = intent.getStringArrayListExtra("regionList");
        // what happens if location is never set, this can happen if GSP is not enabled
        String address = intent.getStringExtra("location");

        regions     = (Spinner)findViewById(R.id.spinner);
        date_picker = (Spinner) findViewById (R.id.date_picker);

        ArrayList<String> regionNames = new ArrayList<>();

        for (Region r : regionsMap.keySet())
            regionNames.add(r.getName());

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, regionNames);

        // adapter = new HashMapRegionAdapter (regionsMap);

        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location    = (TextView) findViewById (R.id.location);
        bagQty      = (TextView) findViewById (R.id.bagQty);

        regions.setAdapter(adapter);

        regions.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Region obj = (Region)(regionsMap.keySet()).toArray ()[position];
                regionId = obj.getId ();
                ArrayList<Integer> numericDates  = regionsMap.get (obj);
                ArrayList<String> formattedDates = new ArrayList<>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                simpleDateFormat.setTimeZone (TimeZone.getTimeZone ("GMT-7"));
                for (Integer numericDate : numericDates) {
                    Date date = new Date(numericDate * 1000L);
                    formattedDates.add(simpleDateFormat.format(date));
                }
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
        Intent mServiceIntent = new Intent (RequestDetailsActivity.this, PickupService.class);
        mServiceIntent.setData (Uri.parse (URL()));
        // mServiceIntent.setData (Uri.parse ("http://mail.posabilities.ca:8000/api/login.php?email=YWJjQGdtYWlsLmNvbQ&password=cHc"));
        startService (mServiceIntent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION);
        pickupServiceReciever = new RequestDetailsActivity.PickupServiceReciever();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(pickupServiceReciever, intentFilter);
        // Background serivce to complete create pickup
    }

    public String URL() {
        String selected_region = regions.getSelectedItem ().toString (), selected_date = date_picker.getSelectedItem ().toString (), location_inputted = location.getText ().toString (), bagQtyInputted = bagQty.getText().toString();

        // Toast.makeText (getApplicationContext (), regionId + " " + selected_region + " " + selected_date + " " + location_inputted + " " + bagQtyInputted, Toast.LENGTH_LONG).show ();


        // TextView emailTextView    = ((TextView) findViewById(R.id.txt_username)), passwordTextView = ((TextView) findViewById(R.id.txt_password));
        // String email = emailTextView.getText().toString (), password = passwordTextView.getText ().toString ();

        // ADD NOTES LATERRRR

        String address = "JOANNE USE BEFORE API CALL TO GET ADDRESS AND LAT AND LONG, VALIDATE ADDRESS", lat = "0", lng = "2";

        return ("http://mail.posabilities.ca:8000/api/createpickupforuser.php?userid=" + encode(LoginActivity.userId) + "&regionid=" + encode(Integer.toString (regionId)) + "&bagqty=" + encode (bagQtyInputted)
                + "&address=" + address + "&lat=" + lat + "&lng=" + lng + "&date=" + selected_date +  "&notes=").replaceAll ("\n", "");
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
            }
        }
    }

}