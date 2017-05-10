package nestedternary.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainSchedulingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scheduling);
    }

    public void schedulingDetails (final View view) {
        Intent intent = new Intent (MainSchedulingActivity.this, RequestDetailsActivity.class);
        startActivity (intent);

    }
}
