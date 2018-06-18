package com.mikechoch.prism.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

public class PrismPostDescriptionActivity extends AppCompatActivity {

    /*
     * Global variables
     */
    private Toolbar toolbar;
    private ImageView previewImageView;
    private TextInputLayout descriptionTextInputLayout;
    private EditText descriptionEditText;
    private Button uploadButton;


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

        byte[] byteArray = getIntent().getByteArrayExtra("EditedPrismPostImage");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        Glide.with(this)
                .asBitmap()
                .thumbnail(0.05f)
                .load(bitmap)
                .apply(new RequestOptions().fitCenter())
                .into(previewImageView);

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

        // Setup Typefaces for all text based UI elements
        descriptionEditText.setTypeface(Default.sourceSansProLight);
        uploadButton.setTypeface(Default.sourceSansProLight);

    }









}
