package nestedternary.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public  static boolean loggedIn = false;
    public  static String userId;

    private LoginActivity.UserLoginServiceReceiver userLoginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void registerPage(final View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void forgotPasswordPage(final View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    public void login (final View view) {
        Intent mServiceIntent = new Intent (LoginActivity.this, UserLoginService.class);
        mServiceIntent.setData (Uri.parse (URL()));
        // mServiceIntent.setData (Uri.parse ("http://mail.posabilities.ca:8000/api/login.php?email=YWJjQGdtYWlsLmNvbQ&password=cHc"));
        startService (mServiceIntent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION);
        userLoginReceiver = new LoginActivity.UserLoginServiceReceiver();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(userLoginReceiver, intentFilter);
    }

    public String URL(){
        TextView emailTextView    = ((TextView) findViewById(R.id.txt_username)), passwordTextView = ((TextView) findViewById(R.id.txt_password));
        String email = emailTextView.getText().toString (), password = passwordTextView.getText ().toString ();

        return ("http://mail.posabilities.ca:8000/api/login.php?email=" + encode(email) + "&password=" + encode(password)).replaceAll ("\n", "");
    }

    public String encode(String word) {

        try {
            return Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception ex){
            Toast.makeText(LoginActivity.this,
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private class UserLoginServiceReceiver extends BroadcastReceiver {

        private UserLoginServiceReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra (Constants.EXTENDED_DATA_STATUS, Constants.STATE_ACTION_CONNECTING);
            if (status == Constants.STATE_ACTION_COMPLETE) {
                loggedIn = true;
                finish();
                startActivity (new Intent (LoginActivity.this, MainSchedulingActivity.class));
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver (userLoginReceiver);
                finish ();
            } else if (status == Constants.STATE_ACTION_FAILED) {
                Toast.makeText (getApplicationContext (), "Login Failed", Toast.LENGTH_SHORT).show ();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver (userLoginReceiver);
            }
        }
    }
}
