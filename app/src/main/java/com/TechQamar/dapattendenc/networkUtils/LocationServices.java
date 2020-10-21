package com.TechQamar.dapattendenc.networkUtils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.TechQamar.dapattendenc.Modules.LatLong;
import com.TechQamar.dapattendenc.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationServices extends Service {
    private FirebaseAuth auth;
    public String uid;
    DatabaseReference mDatabaseReference;


    private LocationCallback locationcallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            SharedPreferences sharedpreferences = LocationServices.this.getSharedPreferences("profiledata", Context.MODE_PRIVATE);

            String NAME = sharedpreferences.getString("name", "");

            auth = FirebaseAuth.getInstance();
            uid = auth.getInstance().getCurrentUser().getUid();
            //initializing database reference
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.d("Location UPdate ", latitude + "," + longitude);

                String Latitude = String.valueOf(latitude);
                String Longitude = String.valueOf(longitude);
                LatLong latLong = new LatLong(Latitude, Longitude, NAME);
                String marker = "marker";
                if (Latitude.equals("0.0") && Longitude.equals("0.0")) {
                    Toast.makeText(LocationServices.this, "The value is zero", Toast.LENGTH_SHORT).show();
                } else {

                    mDatabaseReference.child(marker).child(uid).setValue(latLong);
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    private void startLocationService() {
        String channelId = "location notification channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This cChanal is used by Location Service");
                notificationManager.createNotificationChannel(notificationChannel);


            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationcallback, Looper.getMainLooper());

        startForeground(Constant.LOCATION_SERVICE_ID, builder.build());

    }

    private void stopLocationSrevice() {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationcallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (action != null) {


                if (action.equals(Constant.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constant.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationSrevice();

                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
