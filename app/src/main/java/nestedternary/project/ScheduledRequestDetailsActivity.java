package nestedternary.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class ScheduledRequestDetailsActivity extends AppCompatActivity {

    private Pickup p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_request_details);
        Button region = (Button) findViewById (R.id.details_region), location = (Button) findViewById (R.id.details_location), date = (Button) findViewById (R.id.details_date), bagQty = (Button) findViewById (R.id.details_bagQty);
        Intent intent = getIntent();
        p             = (Pickup) intent.getSerializableExtra ("selectedPickup");
        region.setText (p.getRegion().getName ());
        location.setText (p.address);
        date.setText (p.date);
        bagQty.setText ("" + p.getbagQty());
    }

    public void edit () {
        Intent intent = new Intent (ScheduledRequestDetailsActivity.this, EditRequestActivity.class);
        intent.putExtra ("editPickup", p);
        startActivity (intent);
        finish ();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();
        startActivity (new Intent(ScheduledRequestDetailsActivity.this, MainSchedulingActivity.class));
    }
}
