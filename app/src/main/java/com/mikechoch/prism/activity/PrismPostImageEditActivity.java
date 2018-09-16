package com.mikechoch.prism.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.user_interface.BitmapEditingControllerLayout;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;

import java.io.File;


/**
 * Created by mikechoch on 1/21/18.
 */

public class PrismPostImageEditActivity extends AppCompatActivity {

    /*
     * Global variables
     */
    private Toolbar toolbar;
    private ImageView toolbarGalleryButton;
    private ImageView toolbarCameraButton;
    private RelativeLayout uploadedPostImageViewRelativeLayout;
    private GestureCropImageView cropView;
    private BitmapEditingControllerLayout uploadedPostBitmapEditingControllerLayout;
    private TabLayout uploadedPostBitmapEditingControllerTabLayout;

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
                super.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prism_post_image_edit_activity_layout);

        // Initialize all toolbar elements
        toolbar = findViewById(R.id.toolbar);

        // Initialize all UI elements
        toolbarGalleryButton = findViewById(R.id.upload_image_toolbar_gallery_button);
        toolbarCameraButton = findViewById(R.id.upload_image_toolbar_camera_button);
        uploadedPostImageViewRelativeLayout = findViewById(R.id.uploaded_post_crop_image_view_limiter);

        uploadedPostBitmapEditingControllerLayout.attachTabLayout(uploadedPostBitmapEditingControllerTabLayout);

        setupUIElements();

        toolbarGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(PrismPostImageEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllowed) {
                    IntentHelper.selectImageFromGallery(PrismPostImageEditActivity.this);
                }
            }
        });

        toolbarCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(PrismPostImageEditActivity.this, Manifest.permission.CAMERA);
                if (isAllowed) {
                    imageUriExtra = IntentHelper.takePictureFromCamera(PrismPostImageEditActivity.this);
                }
            }
        });

//        IntentHelper.selectImageFromGallery(this);
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
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupToolbar();
    }

    /**
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Default.GALLERY_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    imageUriExtra = data.getData();
                    outputBitmap = BitmapHelper.createBitmapFromImageUri(this, imageUriExtra);
                    initPhotoEditorWithBitmap(outputBitmap);

                } else {
                    if (cropView.getDrawable() == null) {
                        super.onBackPressed();
                    }
                }
                break;
            case Default.CAMERA_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    outputBitmap = BitmapHelper.createBitmapFromImageUri(this, imageUriExtra);
                    initPhotoEditorWithBitmap(outputBitmap);

                } else {
                    if (cropView.getDrawable() == null) {
                        super.onBackPressed();
                    }
                }
                break;
        }
    }

    /**
     * @param bitmap
     */
    private void initPhotoEditorWithBitmap(Bitmap bitmap) {
        uploadedPostImageViewRelativeLayout.removeAllViews();

        LinearLayout.LayoutParams cropViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Default.screenHeight * 0.6));

        cropView = new GestureCropImageView(this);
        cropView.setLayoutParams(cropViewLayoutParams);
        cropView.setRotateEnabled(false);

        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        cropView.setImageBitmap(tempBitmap);

        OverlayView cropOverlayView = new OverlayView(this);
        cropOverlayView.setLayoutParams(cropViewLayoutParams);
        cropOverlayView.setShowCropFrame(true);
        cropOverlayView.setShowCropGrid(true);
        cropOverlayView.setCropFrameColor(Color.WHITE);
        cropOverlayView.setCropGridColor(Color.WHITE);
        cropOverlayView.setupCropBounds();

        uploadedPostImageViewRelativeLayout.addView(cropView);
        uploadedPostImageViewRelativeLayout.addView(cropOverlayView);
        uploadedPostBitmapEditingControllerLayout.attachCropImageView(cropView);
    }

}
