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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class RequestDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Intent intent = getIntent ();
        ArrayList<String> regionList = intent.getStringArrayListExtra("regionList");
        String address = intent.getStringExtra("location");

        Spinner regions = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, regionList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TextView location = (TextView)findViewById(R.id.location);
        regions.setAdapter(adapter);
        if(location != null)
            location.setText(address);
    }

    public void cancel(final View view) {
        Toast.makeText (getApplicationContext (), "HERE", Toast.LENGTH_LONG).show ();
        finish();
    }

    public void callUs(final View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:7782584377"));
        startActivity(intent);
    }


}
