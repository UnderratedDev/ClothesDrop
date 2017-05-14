package nestedternary.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        final Button registerButton = (Button) findViewById(R.id.button_register_login);
        CheckBox agreeCheckbox = (CheckBox) findViewById(R.id.checkBox_agree);

        agreeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                registerButton.setEnabled(isChecked);
            }
        });
    }

    public void registerButton(final View view){

        Toast.makeText(RegisterActivity.this,
                URL(),
                Toast.LENGTH_LONG).show();

    }

    public String URL(){
        TextView email = (TextView) findViewById(R.id.txt_register_username);
        TextView phone = (TextView) findViewById(R.id.txt_register_phone);
        TextView pass = (TextView) findViewById(R.id.txt_register_password);
        TextView passComfirm = (TextView) findViewById(R.id.txt_register_password_confirm);


        String emailString = email.getText().toString();
        String phoneNumber = phone.getText().toString();

        String password = pass.getText().toString();
        String passwordComfirm = passComfirm.getText().toString();

        if (!password.equals(passwordComfirm)) {


            Toast.makeText(RegisterActivity.this,
                    "Password entered does not match",
                    Toast.LENGTH_LONG).show();
            return null;
        }
        return "userName=" + encode(emailString) + "&phone=" + encode(phoneNumber) + "&pass=" + encode(password);
    }

    public String encode(String word){

        try{
            String temp = Base64.encodeToString(word.getBytes("UTF-8"), Base64.DEFAULT);
            return temp;
        } catch (Exception ex){
            Toast.makeText(RegisterActivity.this,
                   ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            return null;
        }

    }

}
