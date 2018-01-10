package com.jeppsson.japaneseverbs.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jeppsson.japaneseverbs.R;

public class CheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        if (savedInstanceState == null) {
            CheckFragment fragment = new CheckFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }
    }
}
