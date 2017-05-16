package nestedternary.project;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void forgotPassword (final View view) {
        String url = URL();
        Log.e (":)", url);
        if (url != null) {
            Ion.with(getApplicationContext()).load(URL())
                    .asString()
                .setCallback(
                    new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e != null) {
                                Toast.makeText (getApplicationContext (), e.toString (), Toast.LENGTH_SHORT).show ();
                            } else {
                                // Toast.makeText (getApplicationContext (), result, Toast.LENGTH_LONG).show();
                                Log.e (":)", result);
                            }
                        }
                    });
            Toast.makeText(getApplicationContext(), "An email has been sent to the email provided", Toast.LENGTH_LONG).show();
        }
    }

    public String URL(){
        String email = ((EditText) findViewById (R.id.txt_forgot_username)).getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No email provided", Toast.LENGTH_SHORT).show();
            return null;
        } else
            return ("http://mail.posabilities.ca:8000/api/forgotPassword.php?email=" + encode(email)).replaceAll ("\n", "");
    }

    public String encode(String word) {

        try {
            return Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception ex){
            Toast.makeText(ForgotPasswordActivity.this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
