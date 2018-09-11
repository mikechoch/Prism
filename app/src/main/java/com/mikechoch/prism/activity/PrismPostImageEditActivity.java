package com.mikechoch.prism.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.Edit;
import com.mikechoch.prism.user_interface.BitmapEditingControllerLayout;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


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
    private ImageView toolbarRestartButton;
    private LinearLayout uploadedPostImageViewLinearLayout;
    private CropImageView uploadedPostCropImageView;
    private BitmapEditingControllerLayout uploadedPostBitmapEditingControllerLayout;
    private TabLayout uploadedPostBitmapEditingControllerTabLayout;
    private Button nextButton;

    private Uri imageUriExtra;
    private File output;
    private Bitmap outputBitmap;

    private boolean isSavingImage = false;


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
        toolbarRestartButton = findViewById(R.id.upload_image_toolbar_restart_button);
        uploadedPostImageViewLinearLayout = findViewById(R.id.uploaded_post_crop_image_view_limiter);
        uploadedPostCropImageView = findViewById(R.id.uploaded_post_crop_image_view);
        uploadedPostBitmapEditingControllerLayout = findViewById(R.id.uploaded_post_bitmap_editing_controller_layout);
        uploadedPostBitmapEditingControllerTabLayout = findViewById(R.id.uploaded_post_bitmap_editing_controller_tab_layout);
        nextButton = findViewById(R.id.image_edit_next_upload_activity_button);

        uploadedPostBitmapEditingControllerLayout.attachTabLayout(uploadedPostBitmapEditingControllerTabLayout);
        uploadedPostBitmapEditingControllerLayout.attachCropImageView(uploadedPostCropImageView);

        setupUIElements();

        toolbarGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(PrismPostImageEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllowed) {
                    Helper.selectImageFromGallery(PrismPostImageEditActivity.this);
                }
            }
        });

        toolbarCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(PrismPostImageEditActivity.this, Manifest.permission.CAMERA);
                if (isAllowed) {
                    imageUriExtra = Helper.takePictureFromCamera(PrismPostImageEditActivity.this);
                }
            }
        });

        toolbarRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadedPostCropImageView.setImageBitmap(outputBitmap);

                uploadedPostBitmapEditingControllerLayout.brightness = Edit.BRIGHTNESS.getDef();
                uploadedPostBitmapEditingControllerLayout.contrast = Edit.CONTRAST.getDef();
                uploadedPostBitmapEditingControllerLayout.saturation = Edit.SATURATION.getDef();

                uploadedPostBitmapEditingControllerLayout.isAdjusting = false;
                uploadedPostBitmapEditingControllerLayout.filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
            }
        });

        uploadedPostImageViewLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.5);

        Helper.selectImageFromGallery(this);
    }

    @Override
    public void onBackPressed() {
        if (!isSavingImage) {
            if (uploadedPostBitmapEditingControllerLayout.isAdjusting) {
                uploadedPostBitmapEditingControllerLayout.isAdjusting = false;
                uploadedPostBitmapEditingControllerLayout.filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
            } else {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Default.MY_PERMISSIONS_WRITE_MEDIA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Helper.selectImageFromGallery(this);
                } else {
                    super.onBackPressed();
                }
                break;
            case Default.MY_PERMISSIONS_CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageUriExtra = Helper.takePictureFromCamera(this);
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

        // Setup Typefaces for all text based UI elements
        nextButton.setTypeface(Default.sourceSansProLight);

        setupNextButton();
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
                    populatePreviewImageView(outputBitmap);

                } else {
                    if (uploadedPostCropImageView.getCroppedImage() == null) {
                        super.onBackPressed();
                    }
                }
                break;
            case Default.CAMERA_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    outputBitmap = BitmapHelper.createBitmapFromImageUri(this, imageUriExtra);
                    populatePreviewImageView(outputBitmap);

                } else {
                    if (uploadedPostCropImageView.getCroppedImage() == null) {
                        super.onBackPressed();
                    }
                }
                break;
        }
    }

    /**
     * @param bitmap
     */
    private void populatePreviewImageView(Bitmap bitmap) {
        float maxHeight = Default.screenHeight * 0.5f;
        Bitmap tempBitmap = BitmapHelper.scaleBitmap(bitmap, true, maxHeight);
        uploadedPostBitmapEditingControllerLayout.alteredBitmap = tempBitmap.copy(tempBitmap.getConfig(), true);
        uploadedPostBitmapEditingControllerLayout.bitmapPreview = tempBitmap.copy(tempBitmap.getConfig(), true);
        uploadedPostCropImageView.setImageBitmap(uploadedPostBitmapEditingControllerLayout.alteredBitmap);

        maxHeight = 56 * Default.scale;
        Bitmap tinyTempBitmap = BitmapHelper.scaleBitmap(bitmap, true, maxHeight);
        uploadedPostBitmapEditingControllerLayout.setupFilterController(tinyTempBitmap.copy(tinyTempBitmap.getConfig(), true));

        uploadedPostBitmapEditingControllerLayout.brightness = Edit.BRIGHTNESS.getDef();
        uploadedPostBitmapEditingControllerLayout.contrast = Edit.CONTRAST.getDef();
        uploadedPostBitmapEditingControllerLayout.saturation = Edit.SATURATION.getDef();

        uploadedPostBitmapEditingControllerLayout.isAdjusting = false;
        uploadedPostBitmapEditingControllerLayout.filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
    }

    /**
     *
     */
    private void setupNextButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nextButton.setForeground(getResources().getDrawable(R.drawable.image_upload_selector));
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSavingImage) {
                    new UploadImageTask().execute();
                }
            }
        });
    }

    private class UploadImageTask extends AsyncTask<Object, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSavingImage = true;
        }

        @Override
        protected String doInBackground(Object... params) {
            String filename = String.valueOf(System.currentTimeMillis());
            try {
                float scale = outputBitmap.getHeight() / (float) uploadedPostBitmapEditingControllerLayout.alteredBitmap.getHeight();

                Bitmap outputBitmapCopy = Bitmap.createBitmap(outputBitmap,
                        (int) Math.abs(uploadedPostCropImageView.getCropRect().left * scale),
                        (int) Math.abs(uploadedPostCropImageView.getCropRect().top * scale),
                        (int) Math.abs(uploadedPostCropImageView.getCropRect().width() * scale),
                        (int) Math.abs(uploadedPostCropImageView.getCropRect().height() * scale));

                Canvas canvas = new Canvas(outputBitmapCopy);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                Matrix matrix = new Matrix();

                cm.set(BitmapHelper.createEditMatrix(
                        uploadedPostBitmapEditingControllerLayout.brightness,
                        uploadedPostBitmapEditingControllerLayout.contrast,
                        uploadedPostBitmapEditingControllerLayout.saturation));
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                canvas.drawBitmap(outputBitmapCopy, matrix, paint);

                FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputBitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return filename;
        }

        @Override
        protected void onPostExecute(String filename) {
            super.onPostExecute(filename);

            Intent prismPostDescriptionIntent = new Intent(PrismPostImageEditActivity.this, PrismPostDescriptionActivity.class);
            prismPostDescriptionIntent.putExtra("EditedPrismPostFilePath", filename);
            startActivity(prismPostDescriptionIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            isSavingImage = false;
        }

    }

}
