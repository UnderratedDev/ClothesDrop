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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class EditRequestActivity extends AppCompatActivity {

    private Pickup p;
    // private HashMap<Region, ArrayList<Integer>> regionsMap;
    private Spinner regions, date_picker;
    private ArrayAdapter<String> adapter, dateAdapter;
    private int regionId;
    private String address;
    private HashMap<Region, ArrayList<Integer>> regionsMap;
    private EditText location, bagQty, notes;
    private EditRequestActivity.PickupServiceReciever pickupServiceReciever;
    private EditRequestActivity.AddressServiceReciever addresServiceReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);
        p = (Pickup)getIntent ().getSerializableExtra ("editPickup");
        location = (EditText) findViewById (R.id.edit_location);
        bagQty = (EditText) findViewById (R.id.edit_bagQty);
        notes = (EditText) findViewById (R.id.edit_notes);
        location.setText (p.address);
        bagQty.setText ("" + p.getbagQty());
        notes.setText (p.notes);
        Intent intent = getIntent ();
        // regionsMap = (HashMap<Region, ArrayList<Integer>>)intent.getSerializableExtra ("hMap");
        regions     = (Spinner)findViewById(R.id.edit_spinner);
        date_picker = (Spinner) findViewById (R.id.edit_date_picker);

        ArrayList<String> regionNames = new ArrayList<>();

        regionsMap = MainSchedulingActivity.regions;

        Region dateAdd = null;
        boolean add    = true;

        for (Region r : regionsMap.keySet()) {
            regionNames.add(r.getName());
            if (r.getId () == p.getRegion().getId()) {
                ArrayList<Integer> numericDates = regionsMap.get(r);
                dateAdd = r;
                for (Integer numericDate : numericDates) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-7"));
                    Date date = new Date (numericDate * 1000L);
                    String formattedDate = simpleDateFormat.format (date);
                    if (add && p.date.equalsIgnoreCase (formattedDate)) {
                        add = false;
                        Log.e (":)", p.date + " " + formattedDate);
                    }
                        // regionsMap.get (r).add (p.getUnixTimestampDate());
                }
            }
        }

        if (add)
            regionsMap.get (dateAdd).add (p.getUnixTimestampDate());

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, regionNames);

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
    }

    public void cancel (final View view) {
        finish ();
    }

    public void save (final View view) {
        final String location_inputted = location.getText ().toString ();

        address = location_inputted;
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + Uri.encode(location_inputted);

        Intent mServiceIntent = new Intent (EditRequestActivity.this, AddressLatLngService.class);
        mServiceIntent.setData (Uri.parse (url));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION);

        addresServiceReciever = new EditRequestActivity.AddressServiceReciever ();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(addresServiceReciever, intentFilter);

        startService (mServiceIntent);
    }

    public String encode(String word) {

        try {
            return Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception ex){
            Toast.makeText(EditRequestActivity.this,
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
                finish();
            } else if (status == Constants.STATE_ACTION_FAILED) {
                Toast.makeText(getApplicationContext(), "Could not edit request", Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(pickupServiceReciever);
            }
        }
    }

    private class AddressServiceReciever extends BroadcastReceiver {

        private AddressServiceReciever() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(Constants.EXTENDED_DATA_STATUS, Constants.STATE_ACTION_CONNECTING);
            if (status == Constants.STATE_ACTION_COMPLETE) {
                final String  selected_date = date_picker.getSelectedItem ().toString (), bagQtyInputted = bagQty.getText().toString();
                final String notes_inputted = notes.getText() != null ? notes.getText().toString() : "";
                String url = ("http://mail.posabilities.ca:8000/api/modifypickupforuser.php?pickupid=" + encode("" + p.getId ()) + "&userid=" + encode(LoginActivity.userId) + "&regionid=" + encode(Integer.toString (regionId)) + "&bagqty=" + encode (bagQtyInputted)
                        + "&address=" + encode (address) + "&lat=" + encode (AddressLatLngService.lat) + "&lng=" + encode (AddressLatLngService.lng) + "&date=" + encode (selected_date) +  "&notes=" + encode (notes_inputted)).replaceAll ("\n", "");
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(addresServiceReciever);

                Intent mServiceIntent = new Intent (EditRequestActivity.this, PickupService.class);
                mServiceIntent.setData (Uri.parse (url));

                startService (mServiceIntent);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Constants.BROADCAST_ACTION);

                pickupServiceReciever = new EditRequestActivity.PickupServiceReciever();

                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(pickupServiceReciever, intentFilter);
            } else if (status == Constants.STATE_ACTION_FAILED) {
                Toast.makeText(getApplicationContext(), "Could not recieve address", Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(addresServiceReciever);
            }
        }
    }
}
