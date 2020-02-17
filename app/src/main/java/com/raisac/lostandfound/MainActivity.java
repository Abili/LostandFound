package com.raisac.lostandfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.addBtn)
    FloatingActionButton addBtn;

    @BindView(R.id.postRexyclerview)
    RecyclerView postListView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    ArrayList<Item> mItemArrayList = new ArrayList<>();
    LostItemAdapter mItemAdapter;
    private DatabaseReference mDatabaseLostImages;
    //
////    @BindView(R.id.postTitle)
////    TextView title;
//
//    @BindView(R.id.postDescription)
//    TextView description;
//
//    @BindView(R.id.user_name)
//    TextView userName;
//
//    @BindView(R.id.time_of_post)
//    TextView timePosted;
//
//    @BindView(R.id.postImageview)
//    ImageView lostItem;

//    @BindView(R.id.userImage)
//    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostPage.class));
            }
        });
        mDatabaseLostImages = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                if (mUser != null) {
                    Intent intent = new Intent(MainActivity.this, SignUp.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        getUserInfo();
    }


    private void getUserInfo() {
        mDatabaseLostImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    String titleLost = (String) dataSnapshot.child("uid").child("image").child("itemName").getValue();
                    String descLost = (String) dataSnapshot.child("uid").child("image").child("itemDescription").getValue();
                    Uri mPicUrl = (Uri) dataSnapshot.child("uid").child("image").child("itemImage").getValue();

                    mItemArrayList.add(new Item(titleLost, descLost, mPicUrl.toString()));

                    mItemAdapter = new LostItemAdapter(MainActivity.this, mItemArrayList);
                    LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
                    postListView.setLayoutManager(manager);
                    postListView.setAdapter(mItemAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error" + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

}