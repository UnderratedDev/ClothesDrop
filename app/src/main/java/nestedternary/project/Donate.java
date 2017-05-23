package nestedternary.project;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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
        // Maybe check for max??
        final String qty_str = ((EditText)findViewById (R.id.donate_qty)).getText().toString();

        if (qty_str.isEmpty())
            return;

        final int qty        = Integer.parseInt (qty_str);

        editor.putInt ("qty", qty);
        editor.commit ();

        // Toast.makeText (getApplicationContext(), "QTY : " + qty, Toast.LENGTH_LONG).show ();

        final String url = ("http://mail.posabilities.ca:8000/api/bagsDonated.php?qtyDonated=" + encode ("" + qty)).replaceAll ("\n", "");;

        // Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();

        Ion.with(getApplicationContext()).
                load(url).
                asJsonObject()
                .setCallback(
                         new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject json) {
                                if (e != null) {
                                    Toast.makeText (getApplicationContext (), e.toString (), Toast.LENGTH_SHORT).show ();
                                } else {
                                    final JsonElement statusElement = json.get ("status");
                                    final String status             = statusElement.getAsString ();
                                    Log.e (":)", status);
                                    finish ();
                                }
                            }
                        }
                );
    }

    public String encode(String word) {
        try {
            return Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception ex){
            Toast.makeText(Donate.this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void sendToServer () {
        // prefs.getAll ()
        // edit.clear ()....
        // future implemntation, comments may help :)
    }
}
