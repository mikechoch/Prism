package com.mikechoch.prism.activity;

import android.app.Activity;
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
import android.widget.ProgressBar;
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
import java.io.Serializable;

import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class ImageEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nextButton;
    private ProgressBar nextButtonProgressBar;
    private RelativeLayout uploadedPostImageViewRelativeLayout;
    private PhotoEditorView photoEditorView;
    private BitmapEditingControllerLayout bitmapEditingControllerLayout;
    private TabLayout bitmapEditingControllerTabLayout;

    private Uri imageUriExtra;
    private File output;
    private Bitmap outputBitmap;

    private PictureUpload pictureUpload;
    private static boolean isSavingImage = false;


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
        setContentView(R.layout.prism_post_image_edit_activity_layout);

        imageUriExtra = Uri.parse(getIntent().getStringExtra(Default.UPLOAD_IMAGE_SELECTION_URI_EXTRA));
        outputBitmap = BitmapHelper.updateOutputBitmap(ImageEditActivity.this, imageUriExtra);
        pictureUpload = (PictureUpload) getIntent().getSerializableExtra(Default.UPLOAD_IMAGE_SELECTION_TYPE_EXTRA);

        toolbar = findViewById(R.id.prism_post_image_edit_toolbar);
        nextButton = findViewById(R.id.prism_post_image_edit_next_button);
        nextButtonProgressBar = findViewById(R.id.prism_post_image_edit_next_button_progress_bar);
        uploadedPostImageViewRelativeLayout = findViewById(R.id.prism_post_image_edit_photo_editor_view_limiter);
        photoEditorView = findViewById(R.id.prism_post_image_edit_photo_editor_view);
        bitmapEditingControllerLayout = findViewById(R.id.prism_post_image_edit_bitmap_editing_controller_layout);
        bitmapEditingControllerTabLayout = findViewById(R.id.prism_post_image_edit_tab_layout);

        photoEditorView.getLayoutParams().height = (int) (Default.screenHeight * 0.60);

        bitmapEditingControllerLayout.attachTabLayout(bitmapEditingControllerTabLayout);
        bitmapEditingControllerLayout.attachPhotoEditorView(photoEditorView, pictureUpload == PictureUpload.PROFILE_PICTURE);

        setupInterfaceElements();
    }

    @Override
    public void onBackPressed() {
        if (!isSavingImage) {
            if (bitmapEditingControllerLayout.isIsAdjusting()) {
                bitmapEditingControllerLayout.setIsAdjusting(false);
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Setup elements of current activity
     */
    private void setupInterfaceElements() {
        nextButton.setTypeface(Default.sourceSansProBold);

        setupToolbar();
        setupNextButton();
        populatePreviewImageView(outputBitmap);
    }

    /**
     * @param bitmap
     */
    private void populatePreviewImageView(Bitmap bitmap) {
        float maxHeight = Default.screenHeight * 0.3f;
        Bitmap tempBitmap = BitmapHelper.scaleBitmap(bitmap, true, maxHeight);

        switch (pictureUpload) {
            case PRISM_POST:
                bitmapEditingControllerLayout.setModifiedBitmap(tempBitmap.copy(Bitmap.Config.RGB_565, true));
                break;
            case PROFILE_PICTURE:
                bitmapEditingControllerLayout.setModifiedBitmap(BitmapHelper.getCircledBitmap(tempBitmap.copy(Bitmap.Config.RGB_565, true)));
                break;
        }

        photoEditorView.getSource().setImageBitmap(bitmapEditingControllerLayout.getModifiedBitmap());
        bitmapEditingControllerLayout.setBitmapPreview(tempBitmap.copy(Bitmap.Config.RGB_565, true));

        maxHeight = 56 * Default.scale;
        Bitmap tinyTempBitmap = BitmapHelper.scaleBitmap(bitmap, true, maxHeight);
        bitmapEditingControllerLayout.setupFilterController(tinyTempBitmap.copy(tinyTempBitmap.getConfig(), true));

        bitmapEditingControllerLayout.setBrightness(Edit.BRIGHTNESS.getDef());
        bitmapEditingControllerLayout.setContrast(Edit.CONTRAST.getDef());
        bitmapEditingControllerLayout.setSaturation(Edit.SATURATION.getDef());

        bitmapEditingControllerLayout.setIsAdjusting(false);
        bitmapEditingControllerLayout.filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
    }

    /**
     * Setup next button in ImageEditActivity so that it executes an AsyncTask
     * This will grab the current edits and save the current image
     * Then pass the filename through to PrismPostDescriptionActivity
     */
    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSavingImage) {
                    try {
                        isSavingImage = true;
                        nextButton.setVisibility(View.GONE);
                        nextButtonProgressBar.setVisibility(View.VISIBLE);

                        String filename = "PrismPostEdit_" + String.valueOf(System.currentTimeMillis());
                        FileOutputStream fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        Intent[] uploadIntents = new Intent[2];
                        FileInputStream fileInputStream = null;
                        switch (pictureUpload) {
                            case PRISM_POST:
                                uploadIntents[0] = new Intent(ImageEditActivity.this, PrismPostDescriptionActivity.class);
                                uploadIntents[0].putExtra(Default.UPLOAD_IMAGE_FILE_PATH_EXTRA, filename);
                                break;
                            case PROFILE_PICTURE:
                                uploadIntents[0] = new Intent(ImageEditActivity.this, MainActivity.class);
                                uploadIntents[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                uploadIntents[0].putExtra(Default.CROPPED_PROFILE_PICTURE_EXTRA, filename);
                                uploadIntents[1] = new Intent(ImageEditActivity.this, PrismUserProfileActivity.class);
                                uploadIntents[1].putExtra(Default.PRISM_USER_EXTRA, (Serializable) CurrentUser.prismUser);
                                fileInputStream = openFileInput(uploadIntents[0].getStringExtra(Default.CROPPED_PROFILE_PICTURE_EXTRA));
                                break;
                        }

                        new ImageTask().execute(ImageEditActivity.this,
                                outputBitmap,
                                bitmapEditingControllerLayout,
                                filename,
                                fileOutputStream,
                                pictureUpload,
                                uploadIntents,
                                fileInputStream,
                                nextButton,
                                nextButtonProgressBar);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static class ImageTask extends AsyncTask<Object, Object[], Object[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(Object... params) {
            Bitmap outputBitmap = (Bitmap) params[1];
            BitmapEditingControllerLayout bitmapEditingControllerLayout = (BitmapEditingControllerLayout) params[2];
            FileOutputStream fileOutputStream = (FileOutputStream) params[4];

            try {
                Bitmap outputBitmapCopy = outputBitmap.copy(Bitmap.Config.RGB_565, true);

                Canvas canvas = new Canvas(outputBitmapCopy);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                Matrix matrix = new Matrix();

                cm.set(BitmapHelper.createEditMatrix(
                        bitmapEditingControllerLayout.getBrightness(),
                        bitmapEditingControllerLayout.getContrast(),
                        bitmapEditingControllerLayout.getSaturation()));

                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                canvas.drawBitmap(outputBitmapCopy, matrix, paint);


                outputBitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(Object[] params) {
            super.onPostExecute(params);
            Context context = (Context) params[0];
            PictureUpload pictureUpload = (PictureUpload) params[5];
            Intent[] uploadIntents = (Intent[]) params[6];
            FileInputStream fileInputStream = (FileInputStream) params[7];
            TextView nextButton = (TextView) params[8];
            ProgressBar nextButtonProgressBar = (ProgressBar) params[9];

            switch (pictureUpload) {
                case PRISM_POST:
                    context.startActivity(uploadIntents[0]);
                    break;
                case PROFILE_PICTURE:
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                    Uri profilePictureUri = BitmapHelper.getImageUri(context, bitmap);
                    uploadProfilePictureToCloud(context, profilePictureUri);
                    context.startActivities(uploadIntents);
                    break;
            }
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            isSavingImage = false;
            nextButton.setVisibility(View.VISIBLE);
            nextButtonProgressBar.setVisibility(View.GONE);
        }

    }

    /**
     * Takes the profilePicUri and stores the image to cloud. Once the image file is
     * successfully uploaded to cloud successfully, it adds the profilePicUri to
     * the firebaseUser's profile details section
     * TODO put this in DatabaseAction
     * TODO call CurrentUser update here so that app has correct CurrentUser info populated
     */
    private static void uploadProfilePictureToCloud(Context context, Uri profilePictureUri) {
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
                            Helper.toast(context, "Unable to update profile picture");
                        }
                    });
                } else {
                    Log.e(Default.TAG_DB, Message.FILE_UPLOAD_FAIL, task.getException());
                    Helper.toast(context, "Unable to update profile picture");
                }
            }
        });
    }

}
