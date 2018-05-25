package com.mikechoch.prism.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.FileChooser;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.user_interface.BitmapEditingControllerLayout;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by mikechoch on 2/1/18.
 */

public class ProfilePictureUploadActivity extends AppCompatActivity {

    /*
     * Global variables
     */

    private Toolbar toolbar;
    private TextView toolbarTextView;
    private ImageView toolbarGalleryButton;
    private ImageView toolbarCameraButton;
    private LinearLayout uploadedProfileImageViewLinearLayout;
    private CropImageView uploadedProfileImageView;
    private BitmapEditingControllerLayout bitmapEditingControllerLayout;
    private TabLayout bitmapEditingControllerTabLayout;
    private Button saveButton;
    private ProgressBar uploadProfilePictureProgressBar;

    private Uri imageUriExtra;
    private File output;


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
        setContentView(R.layout.profile_picture_upload_activity_layout);
        
        // Initialize all UI elements
        toolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarGalleryButton = findViewById(R.id.upload_image_toolbar_gallery_button);
        toolbarCameraButton = findViewById(R.id.upload_image_toolbar_camera_button);
        uploadedProfileImageViewLinearLayout = findViewById(R.id.uploaded_profile_crop_image_view_limiter);
        uploadedProfileImageView = findViewById(R.id.uploaded_profile_crop_image_view);
        bitmapEditingControllerLayout = findViewById(R.id.uploaded_profile_picture_bitmap_editing_controller_layout);
        bitmapEditingControllerTabLayout = findViewById(R.id.uploaded_profile_picture_bitmap_editing_controller_tab_layout);
        saveButton = findViewById(R.id.save_profile_button_card_view);
        uploadProfilePictureProgressBar = findViewById(R.id.upload_profile_picture_progress_bar);

        bitmapEditingControllerLayout.attachTabLayout(bitmapEditingControllerTabLayout);

        setupUIElements();

        int imageType = getIntent().getIntExtra(Default.PROFILE_PICTURE_TYPE_EXTRA, -1);
        handleProfilePictureType(imageType);

        toolbarGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllowed) {
                    selectImageFromGallery();
                }
            }
        });

        toolbarCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.CAMERA);
                if (isAllowed) {
                    takePictureFromCamera();
                }
            }
        });

        uploadedProfileImageViewLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.5);
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
            case Default.MY_PERMISSIONS_REQUEST_WRITE_MEDIA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageFromGallery();
                } else {
                    super.onBackPressed();
                }
                break;
            case Default.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePictureFromCamera();
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
     * When the ImageView holding the cropped image is clicked, Gallery is opened
     * Select a new image to crop for profile picture
     */
    private void setupUploadedProfileImageView() {
        uploadedProfileImageView.setForeground(getResources().getDrawable(R.drawable.image_upload_selector));
        uploadedProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });
    }

    /**
     * Setup saveButton, so once an image is selected and cropped it will return the Profile
     * The cropped image will be stored in the cloud under the user and replaced in the app UIs
     */
    private void setupSaveButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            saveButton.setForeground(getResources().getDrawable(R.drawable.image_upload_selector));
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * When the saveButton is clicked, a new Intent is created
                 * This passes the uploaded image data (image and description) back to ProfileFragment
                 * Then ProfilePictureUploadActivity is finished
                 */
                new ImageUploadTask().execute();
            }
        });
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupToolbar();

        // Setup Typefaces for all text based UI elements
        toolbarTextView.setTypeface(Default.sourceSansProBold);
        saveButton.setTypeface(Default.sourceSansProLight);

        setupUploadedProfileImageView();
        setupSaveButton();
    }

    /**
     *
     * @param imageType
     */
    private void handleProfilePictureType(int imageType) {
        boolean isAllowed;
        switch (imageType) {
            case Default.PROFILE_PICTURE_GALLERY:
                isAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllowed) {
                    selectImageFromGallery();
                }
                break;
            case Default.PROFILE_PICTURE_CAMERA:
                isAllowed = Helper.permissionRequest(ProfilePictureUploadActivity.this, Manifest.permission.CAMERA);
                if (isAllowed) {
                    takePictureFromCamera();
                }
                break;
        }
    }

    /**
     * Create an Intent to ask user to select a image they would like to upload
     */
    private void selectImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "Select a picture"), Default.GALLERY_INTENT_REQUEST);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Create an Intent to ask user to take a picture with the phone's camera
     * Also prepares a file to save the image
     */
    private void takePictureFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File output = new File(dir, "image" + new Date().getTime() + ".jpeg");
        imageUriExtra = Uri.fromFile(output);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        startActivityForResult(cameraIntent, Default.CAMERA_INTENT_REQUEST);
    }

    /**
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Default.GALLERY_INTENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageUriExtra = data.getData();
                    Bitmap bitmap = createBitmapFromImageUri(imageUriExtra);
                    uploadedProfileImageView.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                    bitmapEditingControllerLayout.setupFilterController(bitmap);
                } else {
                    if (uploadedProfileImageView.getCroppedImage() == null) {
                        super.onBackPressed();
                    }
                }
                break;
            case Default.CAMERA_INTENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = createBitmapFromImageUri(imageUriExtra);
                    uploadedProfileImageView.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                    bitmapEditingControllerLayout.setupFilterController(bitmap);
                } else {
                    if (uploadedProfileImageView.getCroppedImage() == null) {
                        super.onBackPressed();
                    }
                }
                break;
        }
    }

    /**
     *
     * @param imageUri
     * @return
     */
    private Bitmap createBitmapFromImageUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String imagePath = FileChooser.getPath(this, imageUri);
        bitmap = BitmapHelper.rotateBitmap(imagePath, bitmap);
        imageUriExtra = getImageUri(bitmap);

        return bitmap;
    }

    /**
     *
     */
    private Uri getImageUri(Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        String path = BitmapHelper.insertImage(this.getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }

    /**
     *
     */
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            System.out.println("Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

        } catch (IOException e) {
            //System.out.println("File not found: " + e.getMessage());
            //System.out.println("Error accessing file: " + e.getMessage());

            saveButton.setVisibility(View.VISIBLE);
            uploadProfilePictureProgressBar.setVisibility(View.INVISIBLE);

            saveButton.setVisibility(View.VISIBLE);
            uploadProfilePictureProgressBar.setVisibility(View.INVISIBLE);
        }

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, pictureFile.getPath());

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     *
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/PrismProfilePictures");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        long timeStamp = new Date().getTime();
        String imagePath = mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg";
        return new File(imagePath);
    }

    /**
     *
     */
    private class ImageUploadTask extends AsyncTask<Void, Void, Void> {

        Bitmap profilePicture = uploadedProfileImageView.getCroppedImage();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            saveButton.setVisibility(View.INVISIBLE);
            uploadProfilePictureProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            storeImage(profilePicture);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            Uri uri = getImageUri(uploadedProfileImageView.getCroppedImage());

            Intent data = new Intent();
            data.putExtra(Default.CROPPED_PROFILE_PICTURE_EXTRA, uri.toString());
            setResult(RESULT_OK, data);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }
}
