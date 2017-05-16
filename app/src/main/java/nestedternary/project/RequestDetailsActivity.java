package nestedternary.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.ArrayDeque;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class RequestDetailsActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter, dateAdapter;
    // ArrayList<String> regionList = new ArrayList<>();
    HashMap<Region, ArrayList<Integer>> regionsMap = new HashMap<>();
    Spinner regions, date_picker;



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

        for (Region r : regionsMap.keySet()) {
            regionNames.add(r.getName());
        }

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, regionNames);

        // adapter = new HashMapRegionAdapter (regionsMap);

        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TextView location = (TextView)findViewById(R.id.location);

        regions.setAdapter(adapter);

        regions.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener() {

            /*
                long unixSeconds = 1372339860;
                Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
                String formattedDate = sdf.format(date);
                System.out.println(formattedDate);
            */

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object obj = (regionsMap.keySet()).toArray ()[position];
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
        Toast.makeText (getApplicationContext (), "HERE", Toast.LENGTH_LONG).show ();
        finish();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();
    }

    public void callUs(final View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:7782584377"));
        startActivity(intent);
    }


}
