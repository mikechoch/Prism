package com.mikechoch.prism.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.user_interface.BitmapEditingControllerLayout;

import java.io.File;

import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class PrismPostImageEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RelativeLayout uploadedPostImageViewRelativeLayout;
    private PhotoEditorView photoEditorView;
    private BitmapEditingControllerLayout bitmapEditingControllerLayout;
    private TabLayout bitmapEditingControllerTabLayout;

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

        imageUriExtra = Uri.parse(getIntent().getStringExtra(Default.UPLOAD_IMAGE_SELECTION_URI_EXTRA));
        outputBitmap = BitmapHelper.createBitmapFromImageUri(this, imageUriExtra);

        toolbar = findViewById(R.id.prism_post_image_edit_toolbar);
        uploadedPostImageViewRelativeLayout = findViewById(R.id.prism_post_image_edit_photo_editor_view_limiter);
        photoEditorView = findViewById(R.id.prism_post_image_edit_photo_editor_view);
        bitmapEditingControllerLayout = findViewById(R.id.prism_post_image_edit_bitmap_editing_controller_layout);
        bitmapEditingControllerTabLayout = findViewById(R.id.prism_post_image_edit_tab_layout);

        bitmapEditingControllerLayout.attachTabLayout(bitmapEditingControllerTabLayout);
        bitmapEditingControllerLayout.attachPhotoEditorView(photoEditorView);

        setupUIElements();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

}
