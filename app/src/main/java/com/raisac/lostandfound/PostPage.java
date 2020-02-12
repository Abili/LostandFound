package com.raisac.lostandfound;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostPage extends AppCompatActivity implements
        ChangePhotoDialog.OnPhotoReceivedListener {

    public static final int CAMERA_REQUEST_CODE = 5467;
    public static final int PICKFILE_REQUEST_CODE = 8352;
    private static final String TAG = "PostPage";
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final String DOMAIN_NAME = "tabian.ca";
    private static final int REQUEST_CODE = 1234;
    private static final double MB_THRESHHOLD = 5.0;
    private static final double MB = 1000000.0;
    ImageLoader imageLoader;
    Dialog imageUploadDialog;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.post_Btn)
    TextView mSave;
    @BindView(R.id.cropImageView)
    CropImageView mLostImageView;
    @BindView(R.id.lostDescription)
    EditText lostDescription;
    @BindView(R.id.lostTitle)
    EditText lost_title;
    boolean isEnabled = false;
    private FirebaseUser mFirebaseUser;
    private Uri mFilePath;
    private ProgressDialog mProgressDialog;
    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    //vars
    private boolean mStoragePermissions;
    private Uri mSelectedImageUri;
    private Bitmap mSelectedImageBitmap;
    private byte[] mBytes;
    private double progress;

    @Override
    public void getImagePath(Uri imagePath) {
        if (!imagePath.toString().equals("")) {
            mSelectedImageBitmap = null;
            mSelectedImageUri = imagePath;
            Log.d(TAG, "getImagePath: got the image uri: " + mSelectedImageUri);
            mLostImageView.setImageUriAsync(imagePath);
        }

    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mSelectedImageUri = null;
            mSelectedImageBitmap = bitmap;
            Log.d(TAG, "getImageBitmap: got the image bitmap: " + mSelectedImageBitmap);
            mLostImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_model);
        ButterKnife.bind(this);
        mSave.setEnabled(isEnabled);

        Log.d(TAG, "onCreate: started.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        mLostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loadImage = new Intent();
                loadImage.setType("image/*");
                loadImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(loadImage, "plaes pick image"), REQUEST_CODE);
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = lost_title.getText().toString();
                String desc = lostDescription.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    lost_title.setError("Title required");
                } else if (TextUtils.isEmpty(desc)) {
                    lostDescription.setError("Description required");
                } else {
                    mSave.setEnabled(true);
                    postData(title, desc);
                }

            }
        });
    }

    private void postData(String title, String desc) {
        uploadImage();
        Item item = new Item(title, desc, mFilePath);
    }

    private void uploadImage() {

        if (mFilePath != null) {

            // Code for showing progressDialog while uploading
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Uploading...");
            mProgressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(mFilePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    mProgressDialog.dismiss();
                                    Toast.makeText(PostPage.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            mProgressDialog.dismiss();
                            Toast.makeText(PostPage.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    mProgressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            // Get the Uri of data
            mFilePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), mFilePath);
                mLostImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

    }
}