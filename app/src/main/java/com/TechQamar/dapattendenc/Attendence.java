package com.TechQamar.dapattendenc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.TechQamar.dapattendenc.Modules.LatLong;
import com.TechQamar.dapattendenc.Modules.Upload;
import com.TechQamar.dapattendenc.networkUtils.Constant;
import com.TechQamar.dapattendenc.networkUtils.GpsTracker;
import com.TechQamar.dapattendenc.networkUtils.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Attendence extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView selectedImage;
    CardView camera, galleryBtn, uploadBtn,logout_upload, attendence_list,attendence_logou_list,logout, map_marker;
//    Button  logout, map_marker;

    TextView selfi, textaddress;


    private StorageTask mUploadTask;

    //for geo location
    private String lat = "";
    private String lng = "";
    boolean settingsCheck = true;
    List<Address> address;
    String ADDRESS;
    String mImageUrl;
    String Latitude;
    String Longitude;
    public String uid;
    private FirebaseAuth auth;
    String DATE,user_logout="LOGOUT",user_login ="LOGIN";
    String Time;
    String UserName, NAME;
    String imageFileName;
    String name;
    String currentPhotoPath;

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE,
            Manifest.permission.RECORD_AUDIO,
    };


    //Firebase
    StorageReference fileReference;
    StorageReference storageReference;
    DatabaseReference mDatabaseReference;


    private ProgressBar mProgressBar;
    private Geocoder geocoder;
    AlertDialog alert;
    Uri contentUri;
    File f;

    LinearLayout login_logout_list,camera_gallery,upload_login_logout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        selectedImage = findViewById(R.id.displayImageView);
//        cameraBtn = findViewById(R.id.cameraBtn);
        camera = findViewById(R.id.camera);
        galleryBtn = findViewById(R.id.galleryBtn);
        uploadBtn = findViewById(R.id.login_upload);
        logout_upload = findViewById(R.id.logout_upload);
        logout = findViewById(R.id.logout);
        attendence_list = findViewById(R.id.attendence_list);
        attendence_logou_list = findViewById(R.id.attendence_logou_list);
        map_marker = findViewById(R.id.map_marker);
        mProgressBar = findViewById(R.id.progress_bar);
        selfi = findViewById(R.id.selfi);
        textaddress = findViewById(R.id.address);
        login_logout_list = findViewById(R.id.login_logout_list);
        camera_gallery = findViewById(R.id.camera_gallery);
        upload_login_logout = findViewById(R.id.upload_login_logout);

        startLocationService();


        auth = FirebaseAuth.getInstance();
        uid = auth.getInstance().getCurrentUser().getUid();
        System.out.println("UID"+uid);

        storageReference = FirebaseStorage.getInstance().getReference("pictures");

        //initializing database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        System.out.println("currentDateTimeString: " + currentDateTimeString);


        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateAsISOString = df.format(date);
        System.out.println("dateAsISOString: " + dateAsISOString);

        String currentTimeString = DateFormat.getTimeInstance().format(new Date());
        System.out.println("currentTimeString: " + currentTimeString);

        SharedPreferences sharedpreferences = Attendence.this.getSharedPreferences("profiledata", Context.MODE_PRIVATE);
        UserName = sharedpreferences.getString("user_email", "");
        NAME = sharedpreferences.getString("name", "");
        System.out.println("username" + UserName);
        System.out.println("NAME" + NAME);

        selfi.setText(NAME);


        DATE = dateAsISOString;
        Time = currentTimeString;
        name = UserName;


        final Handler handler = new Handler();
        final int delay = 30000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {


                queryLocation();
                if(uid==null){
                    System.out.println("UID ="+uid);
                }else {
//                    writeLatLong();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

        if (UserName.equals("admin@dap.com")) {

            login_logout_list.setVisibility(View.VISIBLE);
            map_marker.setVisibility(View.VISIBLE);


        } else {

            camera_gallery.setVisibility(View.VISIBLE);
            upload_login_logout.setVisibility(View.VISIBLE);


        }


        queryLocation();

        checkPermissions();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                askCameraPermissions();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadFile();

            }
        });
        logout_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadFile_logout();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//
                signOut();

            }
        });
        attendence_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//
                Intent intent = new Intent(Attendence.this, Attendence_list.class);
                startActivity(intent);
//                finish();

            }
        });
        attendence_logou_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//
                Intent intent = new Intent(Attendence.this, Attendance_logout_list.class);
                startActivity(intent);
//                finish();

            }
        });

        map_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//
                Intent intent = new Intent(Attendence.this, DashBoard.class);
                startActivity(intent);
//                finish();

            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        geocoder = new Geocoder(this, Locale.getDefault());

        geocoder = new Geocoder(Attendence.this, Locale.getDefault());
        LocationManager locationManager = (LocationManager) Attendence.this.getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                if (getLocationMode(getApplicationContext()) == 3) {

                } else {
                    showGPSDisabledAlertToUser("Please set Location service to HIGH ACCURACY Mode.");
                }

            } else {
                showGPSDisabledAlertToUser("GPS is disabled in your device. Would you like to enable it?");
            }
        }


    }


    ///Location Service starting
    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {

                if (LocationServices.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }

                }

            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationServices.class);
            intent.setAction(Constant.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationServices.class);
            intent.setAction(Constant.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }

    }

    private void showGPSDisabledAlertToUser(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Attendence.this);
        alertDialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Goto Settings To Enable",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Attendence.this.finish();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    public int getLocationMode(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return 0;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
                startLocationService();
            }
            return;
        }
    }


    private void uploadImageToFirebase() {
        final StorageReference image = storageReference.child("pictures/" + f.getName());
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                    }
                });

                Toast.makeText(Attendence.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Attendence.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "net.smallacademy.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selectedImage.setImageBitmap(imageBitmap);
        }


        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selectedImage.setImageBitmap(imageBitmap);
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri = Uri.fromFile(f);
                System.out.println("contentUri: " + contentUri);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

//                uploadImageToFirebase(f.getName(), contentUri);


            }

        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                selectedImage.setImageURI(contentUri);

//                uploadImageToFirebase(imageFileName, contentUri);


            }

        }


    }


    private void queryLocation() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {


            @Override
            public void run() {


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        GpsTracker gpsTracker = new GpsTracker(Attendence.this);

                        if (gpsTracker.canGetLocation()) {

                            lat = gpsTracker.getLatitude() + "";

                            System.out.println("latitude :::: " + lat);
                            lng = gpsTracker.getLongitude() + "";
                            System.out.println("longiitude :::: " + lng);

                            Latitude = lat;
                            Longitude = lng;


                            if (lat != null && lat.length() > 0
                                    && !lat.trim().equalsIgnoreCase("0.0")) {

                                gpsTracker.stopUsingGPS();
                                timer.cancel();

                                try {
                                    address = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);

                                    ADDRESS = address.get(0).getAddressLine(0);
                                    System.out.println("ADDRESS" + ADDRESS);
                                    textaddress.setText(ADDRESS);

//                                    Toast.makeText(Attendence.this, "Upload ADDRESS "+ADDRESS, Toast.LENGTH_LONG).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {

                            // show gps settings dialog only once..
                            settingsCheck = false;
                            timer.cancel();
                            // gpsTracker.showSettingsAlert();

                        }
                    }
                });
            }
        }, 0, 3000);
        lat = 0 + "";
        lng = 0 + "";
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {


        final KProgressHUD progressDialog = KProgressHUD.create(Attendence.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        if (contentUri != null) {

            if (imageFileName != null) {
                fileReference = storageReference.child("pictures" + imageFileName);
            } else {


                fileReference = storageReference.child("pictures" + f.getName());
            }


            mUploadTask = fileReference.putFile(contentUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            mImageUrl = downloadUrl.toString();
                            System.out.println("downloadUrl:" + downloadUrl);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(Attendence.this, "Upload successful", Toast.LENGTH_LONG).show();


                            Upload upload = new Upload(address, mImageUrl, DATE, NAME, Time);
                            String uploadId = mDatabaseReference.push().getKey();
                            writeNewUser(uploadId);
                            System.out.println("databaseuser:" + uploadId);

//                            mDatabaseReference.child(uploadId).setValue(upload);
                            Intent intent = new Intent(Attendence.this, Attendence.class);
                            startActivity(intent);

                            finish();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Attendence.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                            progressDialog.dismiss();
//                            startActivity(new Intent(Attendence.this, Cards.class));
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();
        }


    }
    private void uploadFile_logout() {


        final KProgressHUD progressDialog = KProgressHUD.create(Attendence.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        if (contentUri != null) {

            if (imageFileName != null) {
                fileReference = storageReference.child("pictures" + imageFileName);
            } else {


                fileReference = storageReference.child("pictures" + f.getName());
            }


            mUploadTask = fileReference.putFile(contentUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            mImageUrl = downloadUrl.toString();
                            System.out.println("downloadUrl:" + downloadUrl);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(Attendence.this, "Upload successful", Toast.LENGTH_LONG).show();


                            Upload upload = new Upload(address, mImageUrl, DATE, NAME, Time);
                            String uploadId = mDatabaseReference.push().getKey();
                            writeNewUser_logout(uploadId);
                            System.out.println("databaseuser:" + uploadId);

//                            mDatabaseReference.child(uploadId).setValue(upload);
                            Intent intent = new Intent(Attendence.this, Attendence.class);
                            startActivity(intent);

                            finish();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Attendence.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                            progressDialog.dismiss();
//                            startActivity(new Intent(Attendence.this, Cards.class));
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();
        }


    }


    private void writeNewUser(String uploadId) {
        System.out.println("writeNewUser:");
        Upload upload = new Upload(ADDRESS, mImageUrl, DATE, NAME, Time);

        uid = auth.getInstance().getCurrentUser().getUid();
//        user_login ="LOGIN";

        mDatabaseReference.child(DATE).child(user_login).child(uploadId).setValue(upload);
        System.out.println("databaseuser:");

    }
    private void writeNewUser_logout(String uploadId) {
        System.out.println("writeNewUser:");
        Upload upload = new Upload(ADDRESS, mImageUrl, DATE, NAME, Time);

        uid = auth.getInstance().getCurrentUser().getUid();
//        user_logout ="LOGOUT";

        mDatabaseReference.child(DATE).child(user_logout).child(uploadId).setValue(upload);
        System.out.println("databaseuser:");

    }

    private void writeLatLong() {


        System.out.println("writeLatLong:");
        System.out.println("Latitude:" + Latitude);
        System.out.println("Longitude:" + Longitude);
        System.out.println("UserName:" + NAME);
        LatLong latLong = new LatLong(Latitude, Longitude, NAME);



        String marker = "marker";

        System.out.println("UID writeLatLong"+uid);

        mDatabaseReference.child(marker).child(uid).setValue(latLong);
        System.out.println("databaseuser:");

    }

    //sign out method
    public void signOut() {
        stopLocationService();
        auth.signOut();

        Intent intent = new Intent(Attendence.this, LoginActivity.class);
        startActivity(intent);
        finish();



    }

}