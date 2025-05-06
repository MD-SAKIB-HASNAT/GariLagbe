package com.example.garilagbe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class spsc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spsc);
        Thread th = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                    Intent i = new Intent(spsc.this, GetStartPage.class);
                    startActivity(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        th.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}