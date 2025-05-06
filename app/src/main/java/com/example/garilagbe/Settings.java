package com.example.garilagbe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    Switch switchNotification, switchDarkMode;
    LinearLayout layoutRateApp, layoutShareApp, layoutPrivacy, layoutTerms, layoutCookies,
            layoutFeedback, layoutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Switches
        switchNotification = findViewById(R.id.switch_notification);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        // Layouts
        layoutBack = findViewById(R.id.back_setting);
        layoutRateApp = findViewById(R.id.layout_rate_app);
        layoutShareApp = findViewById(R.id.layout_share_app);
        layoutPrivacy = findViewById(R.id.layout_privacy);
        layoutTerms = findViewById(R.id.layout_terms);
        layoutCookies = findViewById(R.id.layout_cookies);
        layoutFeedback = findViewById(R.id.layout_feedback);


        // Switch Listeners
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show()
        );

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Dark mode " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show()
        );

        // Click listeners
        layoutBack.setOnClickListener(v -> finish());

        layoutRateApp.setOnClickListener(v -> {
            // Example: open Play Store page
            Toast.makeText(this, "Rate App Clicked", Toast.LENGTH_SHORT).show();
        });

        layoutShareApp.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out GariBD");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Best app to buy/sell used bikes and cars: https://play.google.com/store/apps/details?id=com.example.garibd");
            startActivity(Intent.createChooser(shareIntent, "Share App via"));
        });

        layoutPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            // Open privacy policy URL or activity
        });

        layoutTerms.setOnClickListener(v -> {
            Toast.makeText(this, "Terms and Conditions", Toast.LENGTH_SHORT).show();
            // Open terms URL or activity
        });

        layoutCookies.setOnClickListener(v -> {
            Toast.makeText(this, "Cookies Policy", Toast.LENGTH_SHORT).show();
            // Open cookies info
        });

        layoutFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:your_email@example.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for GariBD");
            startActivity(intent);
        });
    }
}
