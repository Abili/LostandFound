package com.raisac.lostandfound;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostPage extends AppCompatActivity implements View.OnClickListener {

    public static final int CAMERA_REQUEST_CODE = 5467;
    public static final int PICKFILE_REQUEST_CODE = 8352;
    private static final String TAG = "PostPage";
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_CODE = 1234;
    private static final double MB_THRESHHOLD = 5.0;
    private static final double MB = 1000000.0;
    ImageLoader imageLoader;
    Dialog imageUploadDialog;
    FirebaseAuth mAuth;
    StorageReference storageReference;

    @BindView(R.id.post_Btn)
    TextView mSave;
    @BindView(R.id.cropImageView)
    ImageView mLostImageView;
    @BindView(R.id.lostDescription)
    EditText lostDescription;

    @BindView(R.id.lostTitle)
    EditText lost_title;
    boolean isEnabled = false;
    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    //vars
    private boolean mStoragePermissions;
    private Uri mSelectedImageUri;
    private Bitmap mSelectedImageBitmap;
    private byte[] mBytes;
    private double progress;

    Context context;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseStorage mStorage;
    DatabaseReference mDatabaseReference, mDatabaseUsers;
    StorageReference mStorageReference;
    FirebaseUser mCurrentUser;
    ProgressDialog progressDialog;
    Uri FilepathUri;
    int ImageRequest_code = 7;
    String mName;
    String mPicUrl;

    ProgressDialog progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static final double MB_THRESHOLD = 5.0;
    private AlertDialog.Builder mProgressDialog;
    private Bitmap mBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_model);
        ButterKnife.bind(this);
//           Toolbar profileToolbar = findViewById(R.id.profiletoolbar);
        //setActionBar(profileToolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //progressBar = findViewById(R.id.progressBarCreateAcc);
        progressBar = new ProgressDialog(PostPage.this);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        // String user_id = mAuth.getCurrentUser().getUid();
        //mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("User");
        //mDatabaseUsers.setValue(true);

        mLostImageView.setOnClickListener(this);
        mSave.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post_Btn:

                UploadImage();

                break;

            case R.id.cropImageView:
                chooseImage();

                break;
        }
    }

    private void UploadImage() {

        if (mSelectedImageUri != null) {
            loadDialog("loading...");

            // String uid = mAuth.getUid();
            final String lostTitle = lost_title.getText().toString();
            final String lostDecription = lostDescription.getText().toString();
            // User user = new User(username1, null,null);
            //mDatabaseUsers.child(mCurrentUser.getUid()).push().setValue(user);
            if (!TextUtils.isEmpty(lostTitle)) {
                final String userId = mAuth.getCurrentUser().getUid();
                StorageReference ref = mStorageReference.child(userId).child("itemsImages").child(mSelectedImageUri.getLastPathSegment());
                ref.putFile(mSelectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        String uid = mAuth.getUid();
                        Item item = new Item(lostTitle, lostDecription, downloadUrl.toString());
                        mDatabaseReference.child(uid)
                                .child("image")
                                .push()
                                .setValue(item);
//                        Intent intent = new Intent(PostPage.this, HomeSlider.class);
////                        intent.putExtra("username", username1);
////                        intent.putExtra("profilepic", downloadUrl.toString());
//                        startActivity(intent);
                        loadDialog("Posted.");
                        finish();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        loadDialog("Initialising " + (int) progress + "%...");
                    }
                });
            } else {
                lost_title.setError("Title required");
                lostDescription.setError("Descrition required required");
                //users.child("profile").setValue(userUploadInfo);
                mSave.setTextColor(Color.GRAY);
                mSave.setEnabled(false);
            }
        }
    }

    private void loadDialog(String message) {
        mProgressDialog = new AlertDialog.Builder(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }


    private void chooseImage() {
        imageUploadDialog = new Dialog(PostPage.this);
        imageUploadDialog.setContentView(R.layout.activity_change_photo_dialog);
        imageUploadDialog.show();

        //TODO initialise the textview for choosing an image from memory
        TextView selectPhoto = imageUploadDialog.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phone memory,");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        //TODO initialise the textview for choosing an image from camera
        TextView takePhoto = imageUploadDialog.findViewById(R.id.dialogOpenCamera);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starting camera,");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO rsults after selecting image from phone memory
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mSelectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image: " + mSelectedImageUri);

            //TODO send the bitmap and frrgment to the interface
            mLostImageView.setImageURI(mSelectedImageUri);
            imageUploadDialog.dismiss();
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: done taking a photo.");

            mBitmap = (Bitmap) data.getExtras().get("data");

            mLostImageView.setImageBitmap(mBitmap);
            imageUploadDialog.dismiss();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();

                    mLostImageView.setImageURI(resultUri);
// or (prefer using uri for performance and better user experience)
                    mLostImageView.setImageBitmap(mBitmap);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }

}

