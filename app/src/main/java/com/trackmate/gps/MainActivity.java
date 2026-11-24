package com.trackmate.gps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION = 100;

    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;

    private TextView phoneStatus, bikeStatus;
    private TextView latitude, longitude;
    private TextView accuracy, speed, updated;
    private Button gpsButton;

    private boolean tracking = false;

    private final int BG = Color.rgb(9, 16, 31);
    private final int CARD = Color.rgb(22, 34, 56);
    private final int BLUE = Color.rgb(38, 132, 255);
    private final int GREEN = Color.rgb(38, 190, 125);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(160, 177, 200);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationClient =
            LocationServices.getFusedLocationProviderClient(this);

        createDashboard();
        setupLocationCallback();

        gpsButton.setOnClickListener(v -> {
            if (tracking) {
                stopGPS();
            } else {
                startGPS();
            }
        });

        if (hasPermission()) {
            startGPS();
        } else {
            requestGPSPermission();
        }
    }

    private void createDashboard() {

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(dp(18), dp(24), dp(18), dp(28));

        scroll.addView(main);

        TextView title = text("TRACKMATE GPS", 25, WHITE, true);
        main.addView(title);

        main.addView(text(
            "Your vehicle. Your control.",
            13, MUTED, false
        ));

        space(main, 22);

        LinearLayout statusCard = card(main);

        statusCard.addView(text(
            "TRACKING STATUS", 12, MUTED, true
        ));

        phoneStatus = text(
            "● Phone GPS: Searching...",
            16, GREEN, true
        );
        phoneStatus.setPadding(0, dp(14), 0, dp(12));
        statusCard.addView(phoneStatus);

        bikeStatus = text(
            "● Motorcycle GPS: Not Connected",
            14, MUTED, true
        );
        statusCard.addView(bikeStatus);

        space(main, 22);

        main.addView(text(
            "LIVE GPS INFORMATION", 14, WHITE, true
        ));

        space(main, 10);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);

        speed = addStat(row1, "SPEED", "-- km/h");
        accuracy = addStat(row1, "ACCURACY", "-- m");

        main.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);

        latitude = addStat(row2, "LATITUDE", "--");
        longitude = addStat(row2, "LONGITUDE", "--");

        main.addView(row2);

        space(main, 20);

        LinearLayout mapCard = card(main);

        mapCard.addView(text(
            "LIVE TRACKING MAP", 14, WHITE, true
        ));

        TextView mapInfo = text(
            "Your live location will appear here.\n\nMap integration is the next development step.",
            14, MUTED, false
        );
        mapInfo.setGravity(Gravity.CENTER);
        mapInfo.setPadding(0, dp(35), 0, dp(35));
        mapCard.addView(mapInfo);

        space(main, 18);

        LinearLayout deviceCard = card(main);

        deviceCard.addView(text(
            "MOTORCYCLE DEVICE", 14, WHITE, true
        ));

        space(deviceCard, 12);

        deviceCard.addView(text(
            "Device: Not Connected",
            14, MUTED, false
        ));

        deviceCard.addView(text(
            "IMEI: Not Available",
            13, MUTED, false
        ));

        deviceCard.addView(text(
            "Battery: --",
            13, MUTED, false
        ));

        deviceCard.addView(text(
            "Network: --",
            13, MUTED, false
        ));

        space(main, 18);

        LinearLayout updateCard = card(main);

        updateCard.addView(text(
            "LAST PHONE GPS UPDATE", 12, MUTED, true
        ));

        updated = text("--", 15, WHITE, true);
        updated.setPadding(0, dp(10), 0, 0);
        updateCard.addView(updated);

        space(main, 20);

        gpsButton = new Button(this);
        gpsButton.setText("START PHONE GPS");
        gpsButton.setTextColor(WHITE);
        gpsButton.setTextSize(14);
        gpsButton.setAllCaps(false);
        gpsButton.setBackground(background(BLUE, 14));

        main.addView(gpsButton,
            new LinearLayout.LayoutParams(-1, dp(54))
        );

        space(main, 24);

        main.addView(text(
            "TRACKING FEATURES", 14, WHITE, true
        ));

        space(main, 12);

        addFeature(main, "📍  Live Tracking",
            "View real-time vehicle location");

        addFeature(main, "🕘  History & Trips",
            "View previous routes and trip details");

        addFeature(main, "🛡  Geofence",
            "Set safe zones and boundaries");

        addFeature(main, "🔔  Alerts & Notifications",
            "Movement, power and overspeed alerts");

        addFeature(main, "🚘  Device Management",
            "Add and manage GPS trackers");

        addFeature(main, "📊  Reports",
            "Review tracking and trip reports");

        space(main, 24);

        TextView footer = text(
            "TRACKMATE GPS  •  VERSION 1.1",
            11, MUTED, false
        );
        footer.setGravity(Gravity.CENTER);
        main.addView(footer);

        setContentView(scroll);
    }

    private void addFeature(
        LinearLayout parent,
        String title,
        String description
    ) {
        LinearLayout box = card(parent);

        TextView heading = text(title, 15, WHITE, true);
        box.addView(heading);

        TextView info = text(description, 12, MUTED, false);
        info.setPadding(0, dp(6), 0, 0);
        box.addView(info);

        box.setOnClickListener(v ->
            Toast.makeText(
                this,
                "This feature will be enabled in the next development step.",
                Toast.LENGTH_SHORT
            ).show()
        );

        space(parent, 9);
    }

    private TextView addStat(
        LinearLayout row,
        String title,
        String value
    ) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(13), dp(16), dp(8), dp(16));
        box.setBackground(background(CARD, 14));

        box.addView(text(title, 11, MUTED, true));

        TextView result = text(value, 15, WHITE, true);
        result.setPadding(0, dp(10), 0, 0);
        box.addView(result);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(0, -2, 1);

        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        row.addView(box, params);

        return result;
    }

    private LinearLayout card(LinearLayout parent) {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(16), dp(16), dp(16));
        box.setBackground(background(CARD, 16));

        parent.addView(box,
            new LinearLayout.LayoutParams(-1, -2)
        );

        return box;
    }

    private TextView text(
        String value,
        int size,
        int color,
        boolean bold
    ) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private GradientDrawable background(
        int color,
        int radius
    ) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private void space(LinearLayout layout, int height) {
        View view = new View(this);
        layout.addView(view,
            new LinearLayout.LayoutParams(-1, dp(height))
        );
    }

    private int dp(int value) {
        return (int) (value *
            getResources().getDisplayMetrics().density);
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGPSPermission() {
        ActivityCompat.requestPermissions(
            this,
            new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            },
            LOCATION_PERMISSION
        );
    }

    private void setupLocationCallback() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {

                if (result == null) return;

                Location loc = result.getLastLocation();
                if (loc == null) return;

                latitude.setText(String.format(
                    Locale.US, "%.6f", loc.getLatitude()
                ));

                longitude.setText(String.format(
                    Locale.US, "%.6f", loc.getLongitude()
                ));

                accuracy.setText(String.format(
                    Locale.US, "%.1f m", loc.getAccuracy()
                ));

                speed.setText(String.format(
                    Locale.US, "%.1f km/h",
                    loc.hasSpeed() ? loc.getSpeed() * 3.6f : 0
                ));

                updated.setText(
                    new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm:ss a",
                        Locale.getDefault()
                    ).format(new Date(loc.getTime()))
                );

                phoneStatus.setText("● Phone GPS: LIVE");
                phoneStatus.setTextColor(GREEN);
            }
        };
    }

    private void startGPS() {

        if (!hasPermission()) {
            requestGPSPermission();
            return;
        }

        LocationRequest request =
            new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000
            )
            .setMinUpdateIntervalMillis(2000)
            .build();

        try {
            locationClient.requestLocationUpdates(
                request,
                locationCallback,
                getMainLooper()
            );

            tracking = true;
            phoneStatus.setText("● Phone GPS: Searching...");
            gpsButton.setText("STOP PHONE GPS");

        } catch (SecurityException e) {
            phoneStatus.setText("Phone GPS: Permission Error");
        }
    }

    private void stopGPS() {

        if (tracking) {
            locationClient.removeLocationUpdates(locationCallback);
            tracking = false;

            phoneStatus.setText("● Phone GPS: STOPPED");
            gpsButton.setText("START PHONE GPS");
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        );

        if (requestCode == LOCATION_PERMISSION) {
            if (hasPermission()) {
                startGPS();
            } else {
                phoneStatus.setText("Phone GPS: Permission Denied");
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopGPS();
        super.onDestroy();
    }
}
