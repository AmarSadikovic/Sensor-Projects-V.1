package se.mah.af6851.sensorproject4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityLogin extends AppCompatActivity {

    private EditText etName, etPassword;
    private Button btnLogin;
    private MyDBHandler dbHandler;
    private String name, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHandler = new MyDBHandler(this, null, null, 1);
        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                password = etPassword.getText().toString();
                if(name.length()<3){
                    Toast.makeText(getApplicationContext(), "Your name is too short, needs to contain atleast 3 characters." , Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    Toast.makeText(getApplicationContext(), "Your password is too short, needs to contain atleast 6 characters." , Toast.LENGTH_SHORT).show();
                }else {
                    if(dbHandler.databaseNameAll(name)){ //Database contains that name, if password = databasePassword, swap activity
                        String dbPassword = dbHandler.databasePassword(name);
                        if(password.equals(dbPassword)){
                            swapActivity();
                        }else{
                            Toast.makeText(getApplicationContext(), "Your name and password doesn't match!" , Toast.LENGTH_SHORT).show();
                        }
                    }else{ //Register new user since database doesn't contain that name
                        registerNewUser();
                    }
                }
            }
        });
    }
    public void swapActivity(){
        Intent i = new Intent(ActivityLogin.this, MainActivity.class);
        i.putExtra("LoginName", name);
        i.putExtra("LoginPassword", password);
        startActivity(i);
    }
    public void registerNewUser(){
        UserInfo userInfo = new UserInfo(name, password, 0);
        dbHandler.addUserInfo(userInfo);
        Toast.makeText(getApplicationContext(), "New user registered!" , Toast.LENGTH_SHORT).show();
        swapActivity();
    }
}
