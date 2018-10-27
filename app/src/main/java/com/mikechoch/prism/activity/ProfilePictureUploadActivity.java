package com.mikechoch.prism.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.PictureUpload;
import com.mikechoch.prism.user_interface.BitmapRotationControllerLayout;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

public class ProfilePictureUploadActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView gallerySelectionButton;
    private ImageView cameraSelectionButton;
    private TextView nextButton;
    private LinearLayout cropImageViewLinearLayout;
    private CropImageView cropImageView;
    private BitmapRotationControllerLayout bitmapRotationControllerLayout;

    private Uri imageUriExtra;
    private File output;
    private Bitmap outputBitmap;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.expense_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_picture_upload_activity_layout);

        toolbar = findViewById(R.id.toolbar);
        gallerySelectionButton = findViewById(R.id.profile_picture_upload_image_selection_toolbar_gallery_button);
        cameraSelectionButton = findViewById(R.id.profile_picture_upload_image_selection_toolbar_camera_button);
        nextButton = findViewById(R.id.profile_picture_upload_image_selection_toolbar_next_button);
        cropImageViewLinearLayout = findViewById(R.id.profile_picture_upload_image_selection_crop_image_view_limiter);
        cropImageView = findViewById(R.id.profile_picture_upload_image_selection_crop_image_view);
        bitmapRotationControllerLayout = findViewById(R.id.profile_picture_upload_image_selection_bitmap_rotation_controller_layout);

        bitmapRotationControllerLayout.attachCropImageView(cropImageView);

        cropImageViewLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.75);

        setupInterfaceElements();

        int intentCode = getIntent().getIntExtra(Default.PROFILE_PICTURE_TYPE_EXTRA, -1);
        switch (intentCode) {
            case Default.PROFILE_PICTURE_GALLERY:
                boolean isGalleryAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isGalleryAllowed) {
                    IntentHelper.selectImageFromGallery(ProfilePictureUploadActivity.this);
                }
                break;
            case Default.PROFILE_PICTURE_SELFIE:
                boolean isCameraAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.CAMERA);
                if (isCameraAllowed) {
                    imageUriExtra = IntentHelper.takePictureFromCamera(ProfilePictureUploadActivity.this);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Default.MY_PERMISSIONS_WRITE_MEDIA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IntentHelper.selectImageFromGallery(this);
                } else {
                    super.onBackPressed();
                }
                break;
            case Default.MY_PERMISSIONS_CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageUriExtra = IntentHelper.takePictureFromCamera(this);
                } else {
                    super.onBackPressed();
                }
                break;
        }
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     *
     */
    private void setupGalleryButton() {
        gallerySelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllowed) {
                    IntentHelper.selectImageFromGallery(ProfilePictureUploadActivity.this);
                }
            }
        });
    }

    /**
     *
     */
    private void setupCameraButton() {
        cameraSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.CAMERA);
                if (isAllowed) {
                    imageUriExtra = IntentHelper.takePictureFromCamera(ProfilePictureUploadActivity.this);
                }
            }
        });
    }

    /**
     * Setup all interface elements
     */
    private void setupInterfaceElements() {
        nextButton.setTypeface(Default.sourceSansProBold);

        setupToolbar();
        setupNextButton();
        setupGalleryButton();
        setupCameraButton();
    }

    /**
     * Handles cropping the current image with the specific crop overlay
     * On complete handles intent to image editing activity
     */
    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri croppedUri = BitmapHelper.getImageUri(ProfilePictureUploadActivity.this, cropImageView.getCroppedImage());
                IntentHelper.intentToUploadImageEditActivity(ProfilePictureUploadActivity.this, croppedUri.toString(), PictureUpload.PROFILE_PICTURE);
            }
        });
    }

    /**
     * When coming back from gallery or camera intents
     * Handles Uri and new bitmap creation and population
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Default.GALLERY_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    imageUriExtra = data.getData();
                    outputBitmap = BitmapHelper.updateOutputBitmap(ProfilePictureUploadActivity.this, imageUriExtra);
                    setupCropImageView(outputBitmap);

                } else {
                    if (cropImageView == null || cropImageView.getCroppedImage() == null) {
                        onBackPressed();
                    }
                }
                break;
            case Default.CAMERA_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    outputBitmap = BitmapHelper.updateOutputBitmap(ProfilePictureUploadActivity.this, imageUriExtra);
                    setupCropImageView(outputBitmap);

                } else {
                    if (cropImageView == null || cropImageView.getCroppedImage() == null) {
                        onBackPressed();
                    }
                }
                break;
        }
    }

    /**
     * Setup the gesture cropImageView for the activity
     * @param bitmap
     */
    private void setupCropImageView(Bitmap bitmap) {
        cropImageViewLinearLayout.removeAllViews();

        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        cropImageView.setImageBitmap(tempBitmap);

        cropImageViewLinearLayout.addView(cropImageView);
    }
}
