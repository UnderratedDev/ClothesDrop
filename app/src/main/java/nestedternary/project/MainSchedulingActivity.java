package nestedternary.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MainSchedulingActivity extends AppCompatActivity {

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

            startActivity(intent);
        }


    }

    public void jsonRequest(final View view)
    {
        final ArrayList<String> ListRegions = new ArrayList<String>();
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
                                }
                                schedulingDetails(view, ListRegions);
                            }
                        });

    }
}
