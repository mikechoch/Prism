package com.mikechoch.prism.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PrismPostDescriptionActivity extends AppCompatActivity {

    /*
     * Global variables
     */
    private Toolbar toolbar;
    private ImageView previewImageView;
    private TextInputLayout descriptionTextInputLayout;
    private EditText descriptionEditText;
    private Button uploadButton;

    private Uri imageUri;


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
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prism_post_description_activity_layout);

        // Initialize all toolbar elements
        toolbar = findViewById(R.id.toolbar);

        previewImageView = findViewById(R.id.prism_post_preview_image_view);
        descriptionTextInputLayout = findViewById(R.id.prism_post_description_text_input_layout);
        descriptionEditText = findViewById(R.id.prism_post_description_edit_text);
        uploadButton = findViewById(R.id.description_upload_post_button);

        String filename = getIntent().getStringExtra("EditedPrismPostFilePath");

        Bitmap bitmap = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fileInputStream);

            imageUri = BitmapHelper.getImageUri(this, bitmap);

            fileInputStream.close();

            Glide.with(this)
                    .asBitmap()
                    .thumbnail(0.05f)
                    .load(bitmap)
                    .apply(new RequestOptions().fitCenter())
                    .into(previewImageView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupUIElements();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBackToMainActivitySuccess();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void intentBackToMainActivitySuccess() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra(Default.IMAGE_URI_EXTRA, imageUri.toString());
        mainActivityIntent.putExtra(Default.IMAGE_DESCRIPTION_EXTRA, descriptionEditText.getText().toString().trim());
        setResult(RESULT_OK, mainActivityIntent);
        startActivity(mainActivityIntent);
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

        // Setup Typefaces for all text based UI elements
        descriptionEditText.setTypeface(Default.sourceSansProLight);
        uploadButton.setTypeface(Default.sourceSansProLight);

    }

}
