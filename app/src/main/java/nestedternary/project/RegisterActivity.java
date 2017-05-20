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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private RegisterActivity.UserLoginServiceReceiver userLoginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        final ImageButton registerButton = (ImageButton) findViewById(R.id.button_register_login);
        CheckBox agreeCheckbox = (CheckBox) findViewById(R.id.checkBox_agree);

        agreeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    registerButton.setImageResource(R.drawable.button_register);
                    registerButton.setClickable(true);
                } else {
                    registerButton.setImageResource(R.drawable.button_register_disabled);
                    registerButton.setClickable(false);
                }
                registerButton.setEnabled(isChecked);
            }
        });
    }

    public void registerButton(final View view) {

        // Toast.makeText(RegisterActivity.this, URL(), Toast.LENGTH_LONG).show();

        String url = URL();

        if (url == null)
            return;

        Intent mServiceIntent = new Intent (RegisterActivity.this, UserLoginService.class);

        mServiceIntent.setData (Uri.parse (url));
        // Log.e ("HELL", " " + Uri.parse (URL()));
        startService (mServiceIntent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_ACTION);
        userLoginReceiver = new RegisterActivity.UserLoginServiceReceiver();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(userLoginReceiver, intentFilter);

        ImageButton registerButton = (ImageButton) findViewById(R.id.button_register_login);
        registerButton.setEnabled(false);
    }

    public String URL(){
        TextView email = (TextView) findViewById(R.id.txt_register_username);
        TextView pass = (TextView) findViewById(R.id.txt_register_password);
        TextView passComfirm = (TextView) findViewById(R.id.txt_register_password_confirm);


        String emailString = email.getText().toString();

        String password = pass.getText().toString();
        String passwordComfirm = passComfirm.getText().toString();

        if (!emailString.matches("^[a-z A-Z]+[a-z A-Z 0-9]*@[a-z A-Z]+[a-z A-Z 0-9]*\\.[a-z A-Z]+$")) {
            Toast.makeText(RegisterActivity.this,
                    "Invalid E-mail Was Entered",
                    Toast.LENGTH_LONG).show();
            return null;
        }

        if (!password.equals(passwordComfirm)) {


            Toast.makeText(RegisterActivity.this,
                    "Password entered does not match",
                    Toast.LENGTH_LONG).show();
            return null;
        }
        // return "mail.posabilities.ca:8000/api/login.php?email=" + encode(emailString) + "&phone=" + encode(phoneNumber) + "&password=" + encode(password);
        return ("http://mail.posabilities.ca:8000/api/reqister.php?email=" + encode(emailString) + "&password=" + encode(password) + "&tos=1").replaceAll ("\n", "");
    }

    public String encode(String word){

        try {
            return Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception ex){
            Toast.makeText(RegisterActivity.this,
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
            ImageButton registerButton = (ImageButton) findViewById(R.id.button_register_login);
            int status = intent.getIntExtra (Constants.EXTENDED_DATA_STATUS, Constants.STATE_ACTION_CONNECTING);
            if (status == Constants.STATE_ACTION_COMPLETE) {
                // startActivity (new Intent (RegisterActivity.this, LoginActivity.class));
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver (userLoginReceiver);
                finish();
            } else if (status == Constants.STATE_ACTION_FAILED) {
                Toast.makeText (getApplicationContext (), "Register Failed", Toast.LENGTH_SHORT).show ();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver (userLoginReceiver);
                registerButton.setEnabled(true);
            }

        }
    }


}
