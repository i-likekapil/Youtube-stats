package com.example.kapil.youtubestats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    //https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UCQHLxxBFrbfdrk1jF0moTpw&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw
    //https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=love%20babber&type=channel&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}