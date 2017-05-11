package nestedternary.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class RequestDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Intent intent = getIntent ();
        ArrayList<String> regionList = intent.getStringArrayListExtra("regionList");


        Spinner regions = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, regionList);



        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Log.e ("MEOW", "" + regionList.size ());

        regions.setAdapter(adapter);
    }


}
