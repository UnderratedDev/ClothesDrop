package nestedternary.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduledRequestDetailsActivity extends AppCompatActivity {

    private Pickup p;
    // private HashMap<Region, ArrayList<Integer>> regionsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_request_details);
        Button region  = (Button) findViewById (R.id.details_region), location = (Button) findViewById (R.id.details_location), date = (Button) findViewById (R.id.details_date), bagQty = (Button) findViewById (R.id.details_bagQty);
        TextView notes = (TextView) findViewById(R.id.details_notes);
        Intent intent  = getIntent();
        p              = (Pickup) intent.getSerializableExtra ("selectedPickup");
        // regionsMap     = (HashMap<Region, ArrayList<Integer>>)intent.getSerializableExtra ("hMap");
        region.setText (p.getRegion().getName ());
        location.setText (p.address);
        date.setText (p.date);
        bagQty.setText ("" + p.getbagQty());
        notes.setText (p.notes);
    }

    public void edit (final View view) {
        Intent intent = new Intent (ScheduledRequestDetailsActivity.this, EditRequestActivity.class);
        intent.putExtra ("editPickup", p);
        // intent.putExtra ("hMap", regionsMap);
        startActivity (intent);
        finish ();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();
        // startActivity (new Intent(ScheduledRequestDetailsActivity.this, MainSchedulingActivity.class));
    }
}
