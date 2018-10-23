package com.mikechoch.prism.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.BitmapCompat;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;


public class PrismPostImageSelectionActivity extends AppCompatActivity {

    private int activatedColor;
    private int deactivatedColor;

    private Toolbar toolbar;
    private ImageView gallerySelectionButton;
    private ImageView cameraSelectionButton;
    private TextView nextButton;
    private LinearLayout cropImageViewLinearLayout;
    private CropImageView cropImageView;

    private Uri imageUriExtra;
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
        setContentView(R.layout.prism_post_image_selection_activity_layout);

        activatedColor = this.getResources().getColor(R.color.colorAccent);
        deactivatedColor = Color.WHITE;

        toolbar = findViewById(R.id.prism_post_upload_image_selection_toolbar);
        gallerySelectionButton = findViewById(R.id.prism_post_upload_image_selection_toolbar_gallery_button);
        cameraSelectionButton = findViewById(R.id.prism_post_upload_image_selection_toolbar_camera_button);
        nextButton = findViewById(R.id.prism_post_upload_image_selection_toolbar_next_button);
        cropImageViewLinearLayout = findViewById(R.id.prism_post_upload_image_selection_crop_image_view_limiter);
        cropImageView = findViewById(R.id.prism_post_upload_image_selection_crop_image_view);

        setupInterfaceElements();

        boolean isAllowed = Helper.permissionRequest(PrismPostImageSelectionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isAllowed) {
            IntentHelper.selectImageFromGallery(PrismPostImageSelectionActivity.this);
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
                    IntentHelper.selectImageFromGallery(PrismPostImageSelectionActivity.this);
                } else {
                    super.onBackPressed();
                }
                break;
            case Default.MY_PERMISSIONS_CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageUriExtra = IntentHelper.takePictureFromCamera(PrismPostImageSelectionActivity.this);
                } else {
                    super.onBackPressed();
                }
                break;
        }
    }

    /**
     * Setup the toolbar and back button to return to previous activity
     * Setup gallery intent image selection
     * Setup camera intent image selection
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     *
     */
    private void setupGalleryButton() {
        gallerySelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(PrismPostImageSelectionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllowed) {
                    IntentHelper.selectImageFromGallery(PrismPostImageSelectionActivity.this);
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
                boolean isAllowed = Helper.permissionRequest(PrismPostImageSelectionActivity.this, Manifest.permission.CAMERA);
                if (isAllowed) {
                    imageUriExtra = IntentHelper.takePictureFromCamera(PrismPostImageSelectionActivity.this);
                }
            }
        });
    }

    /**
     * Handles cropping the current image with the specific crop overlay
     * On complete handles intent to image editing activity
     */
    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double height = cropImageView.getCropRect().height();
                double width = cropImageView.getCropRect().width();
                int byteCount = BitmapCompat.getAllocationByteCount(cropImageView.getCroppedImage());
                int cropValidation = BitmapHelper.isValidCrop(height, width, byteCount);
                switch (cropValidation) {
                    case Default.CROP_VALID:
                        Uri croppedUri = BitmapHelper.getImageUri(PrismPostImageSelectionActivity.this, cropImageView.getCroppedImage());
                        IntentHelper.intentToUploadImageEditActivity(PrismPostImageSelectionActivity.this, croppedUri.toString(), PictureUpload.PRISM_POST);
                        break;
                    case Default.CROP_ASPECT_RATIO_INVALID:
                        Helper.toast(PrismPostImageSelectionActivity.this, "Invalid crop aspect ratio");
                        break;
                    case Default.CROP_RES_INVALID:
                        Helper.toast(PrismPostImageSelectionActivity.this, "Resolution of the image too low");
                        break;
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
     * When coming back from gallery or camera intents
     * Handles Uri and new bitmap creation and population
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Default.GALLERY_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    imageUriExtra = data.getData();
                    outputBitmap = BitmapHelper.updateOutputBitmap(PrismPostImageSelectionActivity.this, imageUriExtra);
                    setupCropImageView(outputBitmap);

                } else {
                    if (cropImageView == null || cropImageView.getCroppedImage() == null) {
                        onBackPressed();
                    }
                }
                break;
            case Default.CAMERA_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    outputBitmap = BitmapHelper.updateOutputBitmap(PrismPostImageSelectionActivity.this, imageUriExtra);
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
