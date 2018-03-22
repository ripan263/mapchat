package com.example.ripan.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by paddyxvy on 16/03/2018.
 */

public class Register extends AppCompatActivity {

    EditText first;
    EditText sur;
    EditText user;
    EditText dateofbirth;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        first = (EditText) findViewById(R.id.firstname);
        sur = (EditText) findViewById(R.id.surname);
        user = (EditText) findViewById(R.id.username);
        dateofbirth = (EditText) findViewById(R.id.dateofbirth);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        final Button email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMainIntent = new Intent(Register.this, MapsActivity.class);
                saveDetails();
                Register.this.startActivity(goToMainIntent);
                finish();
            }
        });
    }

    public void saveDetails(){
        InternalFile saveName = new InternalFile();
        Log.w("Register", user.getText().toString());
        User newUsers = new User(first.getText().toString(),
                                sur.getText().toString(),
                                dateofbirth.getText().toString(),
                                email.getText().toString(),
                                password.getText().toString(),
                                user.getText().toString());
        saveName.saveUserInfo(newUsers, this); 
        //saveName.saveUsername(user.getText().toString(), this);

    }
}