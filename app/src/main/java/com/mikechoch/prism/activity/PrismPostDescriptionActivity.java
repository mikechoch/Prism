package com.mikechoch.prism.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.IntentHelper;

import java.io.FileInputStream;
import java.io.IOException;


public class PrismPostDescriptionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nextButton;
    private ImageView previewImageView;
    private TextInputLayout descriptionTextInputLayout;
    private EditText descriptionEditText;

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
        setContentView(R.layout.prism_post_description_activity_layout);

        toolbar = findViewById(R.id.toolbar);
        nextButton = findViewById(R.id.prism_post_description_next_button);
        previewImageView = findViewById(R.id.prism_post_preview_image_view);
        descriptionTextInputLayout = findViewById(R.id.prism_post_description_text_input_layout);
        descriptionEditText = findViewById(R.id.prism_post_description_edit_text);

        setupInterfaceElements();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     *
     * @param filename
     */
    private void setupPrismPostImagePreview(String filename) {
        try {
            FileInputStream fileInputStream = openFileInput(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);

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
     * Setup the next button, which will intent back to main activity with the PrismPost details
     * to begin PrismPost Firebase upload
     */
    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToMainActivityWithPrismUploadSuccess(
                        PrismPostDescriptionActivity.this,
                        imageUri.toString(),
                        descriptionEditText.getText().toString().trim());
            }
        });
    }

    /**
     * Setup elements of current activity
     */
    private void setupInterfaceElements() {
        descriptionEditText.setTypeface(Default.sourceSansProLight);
        nextButton.setTypeface(Default.sourceSansProBold);

        String filename = getIntent().getStringExtra(Default.UPLOAD_IMAGE_FILE_PATH_EXTRA);
        setupPrismPostImagePreview(filename);
        setupToolbar();
        setupNextButton();
    }
}
