package com.TechQamar.dapattendenc;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.airbnb.lottie.LottieAnimationView;
import com.TechQamar.dapattendenc.Modules.SingleProductModel;
import com.TechQamar.dapattendenc.usersession.UserSession;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Attendence_list extends AppCompatActivity {

    //to get user session data
    private UserSession session;
    private HashMap<String, String> user;
    private String name, date, time, Address;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;


    private LottieAnimationView tv_no_item;
    private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;

    private ArrayList<SingleProductModel> cartcollect;
    private float totalcost = 0;
    private int totalproducts = 0;

    //firebase
    public String uid;
    private FirebaseAuth auth;
    //firebase objects
    private StorageReference storageReference;
    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();

    ImageView profile;


    String imageURL;
    String DATE;
    String user_logout = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar1 = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar1);
        toolbar1.setTitle("Today's Attendance");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());

        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateAsISOString = df.format(date);
        System.out.println("dateAsISOString: " + dateAsISOString);

        DATE = dateAsISOString;


        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //retrieve session values and display on listviews
//        getValues();

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.activity_cart_list);
        emptycart = findViewById(R.id.empty_cart);
        cartcollect = new ArrayList<>();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        populateRecyclerView();
//        if(session.getCartValue()>0) {
//            populateRecyclerView();
//        }else if(session.getCartValue() == 0)  {
//            tv_no_item.setVisibility(View.GONE);
//            activitycartlist.setVisibility(View.GONE);
//            emptycart.setVisibility(View.VISIBLE);
//        }

        //FIREBASE STORAGE

        profile = findViewById(R.id.view_profile);

        uid = auth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("users").child(uid);

//        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                imageURL = uri.toString();
//                Glide.with(getApplicationContext()).load(imageURL).into(profile);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
    }

    private void populateRecyclerView() {

        //Say Hello to our new FirebaseUI android Element, i.e., FirebaseRecyclerAdapter
        final FirebaseRecyclerAdapter<SingleProductModel, MovieViewHolder> adapter = new FirebaseRecyclerAdapter<SingleProductModel, MovieViewHolder>(
                SingleProductModel.class,
                R.layout.cart_item_layout,
                MovieViewHolder.class,
                //referencing the node where we want the database to store the data from our Object
//                mDatabaseReference.child("users").getRef())
                mDatabaseReference.child(DATE).child(user_logout).getRef()) {
            @Override
            protected void populateViewHolder(final MovieViewHolder viewHolder, final SingleProductModel model, final int position) {
                if (tv_no_item.getVisibility() == View.VISIBLE) {
                    tv_no_item.setVisibility(View.GONE);
                }
                viewHolder.item_name.setText(model.getName());
                System.out.println(model.getName());
                viewHolder.item_date.setText(model.getDate());
                System.out.println(model.getDate());
                viewHolder.item_address.setText(model.getAddress());
                viewHolder.item_time.setText(model.getTime());

                Picasso.get().load(model.getmImageUrl()).into(viewHolder.picture);

                System.out.println("getmImageUrl" + model.getmImageUrl());


                cartcollect.add(model);


            }
        };
        mRecyclerView.setAdapter(adapter);
    }


    //viewHolder for our Firebase UI
    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView item_name;
        TextView item_date;
        TextView item_address;
        TextView item_time;
        ImageView picture;

        View mView;

        public MovieViewHolder(View v) {
            super(v);
            mView = v;
            item_name = v.findViewById(R.id.idName);
            item_date = v.findViewById(R.id.idDATE);
            item_address = v.findViewById(R.id.idADDRESS);
            item_time = v.findViewById(R.id.idTime);
            picture = v.findViewById(R.id.image_cartlist);

        }
    }


//    private void getValues() {
//
//        //create new session object by passing application context
//        session = new UserSession(getApplicationContext());
//
//        //validating session
//        session.isLoggedIn();
//
//        //get User details if logged in
//        user = session.getUserDetails();
//
//        name = user.get(UserSession.KEY_NAME);
//        email = user.get(UserSession.KEY_EMAIL);
//        mobile = user.get(UserSession.KEY_MOBiLE);
//        photo = user.get(UserSession.KEY_PHOTO);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    public void viewProfile(View view) {
//        startActivity(new Intent(Orders.this,Profile.class));
//        finish();
//    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }

//    public void Notifications(View view) {
//
//        startActivity(new Intent(Orders.this,NotificationActivity.class));
//        finish();
//    }
}
