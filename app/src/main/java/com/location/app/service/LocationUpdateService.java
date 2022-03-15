package com.location.app.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.location.app.R;
import com.location.app.activity.IntermittentActivity;
import com.location.app.model.LocationDTO;
import com.location.app.model.LocationUpdateEvent;
import com.location.app.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LocationUpdateService extends IntentService {

    private static final String IDENTIFIER = "GetAddressIntentService";
    private ResultReceiver addressResultReceiver;
    public LocationUpdateService() {
        super(IDENTIFIER);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg;
        addressResultReceiver = Objects.requireNonNull(intent).getParcelableExtra("add_receiver");
        if (addressResultReceiver == null) {

            return;
        }
        Location location = intent.getParcelableExtra("add_location");
        if (location == null) {
            msg = "No location, can't go further without location";
            sendResultsToReceiver(0, msg);
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch (Exception ioException) {
            Log.e("", "Error in getting address for the location");
        }
        if (addresses == null || addresses.size() == 0) {
            msg = "No address found for the location";
            sendResultsToReceiver(1, msg);
        }
        else {
            Address address = addresses.get(0);
            String addressDetails = address.getFeatureName() + "\n" + address.getThoroughfare() + "\n" +
                    "Locality: " + address.getLocality() + "\n" + "County: " + address.getSubAdminArea() + "\n" +
                    "State: " + address.getAdminArea() + "\n" + "Country: " + address.getCountryName() + "\n" +
                    "Postal Code: " + address.getPostalCode() + "\n";
            sendResultsToReceiver(2, addressDetails);
        }
    }
    private void sendResultsToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("address_result", message);
        addressResultReceiver.send(resultCode, bundle);
    }
}