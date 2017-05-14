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
        startService (mServiceIntent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION);
        userLoginReceiver = new LoginActivity.UserLoginServiceReceiver();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(userLoginReceiver, intentFilter);
    }

    public String URL(){
        String email    = ((TextView) findViewById(R.id.txt_username)).toString (), password = ((TextView) findViewById(R.id.txt_password)).toString ();

        return "mail.posabilities.ca:8000/api/login.php?email=" + encode(email) + "&password=" + encode(password);
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
                startActivity (new Intent (LoginActivity.this, MainActivity.class));
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver (userLoginReceiver);
            } else if (status == Constants.STATE_ACTION_FAILED) {
                Toast.makeText (getApplicationContext (), "Login Failed", Toast.LENGTH_SHORT).show ();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver (userLoginReceiver);
            }

        }
    }
}
