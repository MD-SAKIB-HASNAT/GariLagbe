package com.example.garilagbe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    private EditText editMessage;
    private Button btnSend;
    LinearLayout layoutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support); // Link with your XML layout

        editMessage = findViewById(R.id.edit_message);
        btnSend = findViewById(R.id.btn_send);
        layoutBack = findViewById(R.id.back_support);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSupportMessage();
            }
        });

        layoutBack.setOnClickListener(v -> finish());
    }

    private void sendSupportMessage() {
        String message = editMessage.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter your message.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"garibd2025@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request from GariBD App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app installed on your device.", Toast.LENGTH_LONG).show();
        }
    }


}
