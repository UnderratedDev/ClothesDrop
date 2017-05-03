package nestedternary.project;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Donate extends AppCompatActivity {

    SharedPreferences        prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        prefs  = PreferenceManager.getDefaultSharedPreferences (this);
        editor = prefs.edit ();

        String binName = getIntent ().getStringExtra ("binName");

        TextView binNameText = (TextView) findViewById (R.id.bin_name);
        binNameText.setText (binName);
    }

    public void submtiQty (final View view) {
        final String qty_str = ((EditText)findViewById (R.id.donate_qty)).getText().toString();
        final int qty        = Integer.parseInt (qty_str);

        editor.putInt ("qty", qty);
        editor.commit ();

        Toast.makeText (getApplicationContext(), "QTY : " + qty, Toast.LENGTH_LONG).show ();
    }

    public void sendToServer () {
        // prefs.getAll ()
        // edit.clear ()....
        // future implemntation, comments may help :)
    }
}
