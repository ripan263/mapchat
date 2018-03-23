package com.example.ripan.map;

import android.content.Intent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.view.View;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        InternalFile user_file = new InternalFile();
        TextView userfill = (TextView)findViewById(R.id.username);

        //Use check for future features...
        String check = user_file.checkLogin(this);
        if (check == "1") {
            Intent intent = new Intent(this, NavigationDrawer.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        finish();
    }


    public void goToMap (View view){

    }
}
