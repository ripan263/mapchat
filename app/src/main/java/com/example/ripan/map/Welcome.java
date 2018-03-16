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
    }


    public void goToMap (View view){
        Intent intent = new Intent(this, MapsActivity.class);
        InternalFile user_file = new InternalFile();
        TextView userfill = (TextView)findViewById(R.id.username);

        //Use check for future features...
        int check = user_file.saveUsername(userfill.getText().toString(), this);

        startActivity(intent);
        finish();
    }
}
