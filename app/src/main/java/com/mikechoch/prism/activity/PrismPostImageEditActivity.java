package com.mikechoch.prism.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.Message;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.Edit;
import com.mikechoch.prism.type.PictureUpload;
import com.mikechoch.prism.user_interface.BitmapEditingControllerLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class PrismPostImageEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nextButton;
    private RelativeLayout uploadedPostImageViewRelativeLayout;
    private PhotoEditorView photoEditorView;
    private BitmapEditingControllerLayout bitmapEditingControllerLayout;
    private TabLayout bitmapEditingControllerTabLayout;

    private Uri imageUriExtra;
    private File output;
    private Bitmap outputBitmap;

    private PictureUpload pictureUpload;
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

        imageUriExtra = Uri.parse(getIntent().getStringExtra(Default.UPLOAD_IMAGE_SELECTION_URI_EXTRA));
        outputBitmap = BitmapHelper.createBitmapFromImageUri(this, imageUriExtra);
        pictureUpload = (PictureUpload) getIntent().getSerializableExtra(Default.UPLOAD_IMAGE_SELECTION_TYPE_EXTRA);

        toolbar = findViewById(R.id.prism_post_image_edit_toolbar);
        nextButton = findViewById(R.id.prism_post_image_edit_next_button);
        uploadedPostImageViewRelativeLayout = findViewById(R.id.prism_post_image_edit_photo_editor_view_limiter);
        photoEditorView = findViewById(R.id.prism_post_image_edit_photo_editor_view);
        bitmapEditingControllerLayout = findViewById(R.id.prism_post_image_edit_bitmap_editing_controller_layout);
        bitmapEditingControllerTabLayout = findViewById(R.id.prism_post_image_edit_tab_layout);

        photoEditorView.getLayoutParams().height = (int) (Default.screenHeight * 0.59);

        bitmapEditingControllerLayout.attachTabLayout(bitmapEditingControllerTabLayout);
        bitmapEditingControllerLayout.attachPhotoEditorView(photoEditorView, pictureUpload == PictureUpload.PROFILE_PICTURE);

        setupUIElements();
    }

    @Override
    public void onBackPressed() {
        if (!isSavingImage) {
            if (bitmapEditingControllerLayout.isAdjusting) {
                bitmapEditingControllerLayout.isAdjusting = false;
                bitmapEditingControllerLayout.filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
            } else {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
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
        nextButton.setTypeface(Default.sourceSansProBold);

        setupToolbar();
        setupNextButton();
        populatePreviewImageView(outputBitmap);
    }

    /**
     * @param bitmap
     */
    private void populatePreviewImageView(Bitmap bitmap) {
        float maxHeight = Default.screenHeight * 0.5f;
        Bitmap tempBitmap = BitmapHelper.scaleBitmap(bitmap, true, maxHeight);

        switch (pictureUpload) {
            case PRISM_POST:
                bitmapEditingControllerLayout.modifiedBitmap = tempBitmap.copy(Bitmap.Config.RGB_565, true);
                break;
            case PROFILE_PICTURE:
                bitmapEditingControllerLayout.modifiedBitmap = BitmapHelper.getCircledBitmap(tempBitmap.copy(Bitmap.Config.RGB_565, true));
                break;
        }

        photoEditorView.getSource().setImageBitmap(bitmapEditingControllerLayout.modifiedBitmap);
        bitmapEditingControllerLayout.bitmapPreview = tempBitmap.copy(Bitmap.Config.RGB_565, true);

        maxHeight = 56 * Default.scale;
        Bitmap tinyTempBitmap = BitmapHelper.scaleBitmap(bitmap, true, maxHeight);
        bitmapEditingControllerLayout.setupFilterController(tinyTempBitmap.copy(tinyTempBitmap.getConfig(), true));

        bitmapEditingControllerLayout.brightness = Edit.BRIGHTNESS.getDef();
        bitmapEditingControllerLayout.contrast = Edit.CONTRAST.getDef();
        bitmapEditingControllerLayout.saturation = Edit.SATURATION.getDef();

        bitmapEditingControllerLayout.isAdjusting = false;
        bitmapEditingControllerLayout.filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
    }

    /**
     *
     */
    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSavingImage) {
                    new PrismPostImageTask().execute();
                }
            }
        });
    }

    private class PrismPostImageTask extends AsyncTask<Object, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSavingImage = true;
        }

        @Override
        protected String doInBackground(Object... params) {
            String filename = String.valueOf(System.currentTimeMillis());
            try {
                Bitmap outputBitmapCopy = outputBitmap.copy(Bitmap.Config.RGB_565, true);

                Canvas canvas = new Canvas(outputBitmapCopy);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                Matrix matrix = new Matrix();

                cm.set(BitmapHelper.createEditMatrix(
                        bitmapEditingControllerLayout.brightness,
                        bitmapEditingControllerLayout.contrast,
                        bitmapEditingControllerLayout.saturation));
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
            Intent[] uploadIntents = new Intent[2];
            switch (pictureUpload) {
                case PRISM_POST:
                    uploadIntents[0] = new Intent(PrismPostImageEditActivity.this, PrismPostDescriptionActivity.class);
                    uploadIntents[0].putExtra("EditedPrismPostFilePath", filename);
                    startActivity(uploadIntents[0]);
                    break;
                case PROFILE_PICTURE:
                    uploadIntents[0] = new Intent(PrismPostImageEditActivity.this, MainActivity.class);
                    uploadIntents[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    uploadIntents[0].putExtra(Default.CROPPED_PROFILE_PICTURE_EXTRA, filename);
                    uploadIntents[1] = new Intent(PrismPostImageEditActivity.this, PrismUserProfileActivity.class);
                    uploadIntents[1].putExtra(Default.PRISM_USER_EXTRA, CurrentUser.prismUser);

                    try {
                        FileInputStream fileInputStream = openFileInput(uploadIntents[0].getStringExtra(Default.CROPPED_PROFILE_PICTURE_EXTRA));
                        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                        Uri profilePictureUri = BitmapHelper.getImageUri(PrismPostImageEditActivity.this, bitmap);
                        uploadProfilePictureToCloud(profilePictureUri);
                        startActivities(uploadIntents);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            isSavingImage = false;
        }

    }

    /**
     * Takes the profilePicUri and stores the image to cloud. Once the image file is
     * successfully uploaded to cloud successfully, it adds the profilePicUri to
     * the firebaseUser's profile details section
     */
    private void uploadProfilePictureToCloud(Uri profilePictureUri) {
        StorageReference profilePicRef = Default.STORAGE_REFERENCE.child(Key.STORAGE_USER_PROFILE_IMAGE_REF).child(profilePictureUri.getLastPathSegment());
        profilePicRef.putFile(profilePictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult().getDownloadUrl();
                    DatabaseReference userRef = Default.USERS_REFERENCE.child(CurrentUser.prismUser.getUid()).child(Key.USER_PROFILE_PIC);
                    userRef.setValue(downloadUrl.toString()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.wtf(Default.TAG_DB, Message.PROFILE_PIC_UPDATE_FAIL, task.getException());
                            Helper.toast(PrismPostImageEditActivity.this, "Unable to update profile picture");
                        }
                    });
                } else {
                    Log.e(Default.TAG_DB, Message.FILE_UPLOAD_FAIL, task.getException());
                    Helper.toast(PrismPostImageEditActivity.this, "Unable to update profile picture");
                }
            }
        });
    }

}
