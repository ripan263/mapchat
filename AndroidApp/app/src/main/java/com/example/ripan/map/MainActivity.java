package com.example.ripan.map;

import android.support.v7.app.*;
import android.os.*;

import java.util.concurrent.*;

public class MainActivity extends AppCompatActivity {
    DataFetcher fetcher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetcher = new DataFetcher();

        fetcher.execute(new DataFetcher.DataRequest("10.0.2.2", 5002));
        try{
            String result = fetcher.get(100, TimeUnit.SECONDS);
            System.out.println(result);
        }catch (Exception e) {
            System.out.println("Failed");
        }
    }
}
