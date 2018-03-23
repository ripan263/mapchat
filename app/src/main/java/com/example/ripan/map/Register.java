package com.example.ripan.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by paddyxvy on 16/03/2018.
 */

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText firstname = (EditText) findViewById(R.id.firstname);
        final EditText surname = (EditText) findViewById(R.id.surname);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText dateofbirth = (EditText) findViewById(R.id.dateofbirth);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);

        final Button email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMainIntent = new Intent(Register.this, NavigationDrawer.class);
                Register.this.startActivity(goToMainIntent);
                finish();
            }
        });

    }
}