package com.mikechoch.prism.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.helper.IntentHelper;
import com.mikechoch.prism.type.AspectRatio;
import com.mikechoch.prism.type.TransformImage;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView;

import java.util.ArrayList;
import java.util.Locale;

public class PrismPostImageSelectionActivity extends AppCompatActivity {

    private int activatedColor;
    private int deactivatedColor;

    private Toolbar toolbar;
    private ImageView gallerySelectionButton;
    private ImageView cameraSelectionButton;
    private TextView nextButton;
    private LinearLayout uploadedPostImageViewLinearLayout;
    private LinearLayout actionButtonControlsLinearLayout;
    private LinearLayout actionButtonsLinearLayout;
    private LinearLayout cropControllerLinearLayout;
    private TextView cropValueTextView;
    private HorizontalProgressWheelView cropHorizontalProgressWheelView;
    private LinearLayout rotateControllerLinearLayout;
    private TextView rotateValueTextView;
    private HorizontalProgressWheelView rotateHorizontalProgressWheelView;
    private LinearLayout aspectRatioControllerLinearLayout;
    private LinearLayout aspectRatioButtonsLinearLayout;
    private ImageView cropActionButton;
    private ImageView rotateActionButton;
    private ImageView aspectRatioActionButton;
    private UCropView uCropView;

    private TransformImage currentAction = TransformImage.ASPECT_RATIO_MODE;
    private AspectRatio currentAspectRatio = AspectRatio.ASPECT_RATIO_ORIGINAL;

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
        setContentView(R.layout.prism_post_image_selection_activity_layout);

        activatedColor = this.getResources().getColor(R.color.colorAccent);
        deactivatedColor = Color.WHITE;

        toolbar = findViewById(R.id.prism_post_upload_image_selection_toolbar);
        gallerySelectionButton = findViewById(R.id.prism_post_upload_image_selection_toolbar_gallery_button);
        cameraSelectionButton = findViewById(R.id.prism_post_upload_image_selection_toolbar_camera_button);
        nextButton = findViewById(R.id.prism_post_upload_image_selection_toolbar_next_button);
        uploadedPostImageViewLinearLayout = findViewById(R.id.prism_post_upload_image_selection_crop_image_view_limiter);
        actionButtonControlsLinearLayout = findViewById(R.id.prism_post_upload_image_selection_action_button_controls);
        actionButtonsLinearLayout = findViewById(R.id.prism_post_upload_image_selection_action_buttons);
        cropControllerLinearLayout = findViewById(R.id.prism_post_upload_image_selection_crop_controller);
        cropValueTextView = findViewById(R.id.prism_post_upload_image_selection_crop_text_view);
        cropHorizontalProgressWheelView = findViewById(R.id.prism_post_upload_image_selection_crop_horizontal_progress_wheel_view);
        rotateControllerLinearLayout = findViewById(R.id.prism_post_upload_image_selection_rotate_controller);
        rotateValueTextView = findViewById(R.id.prism_post_upload_image_selection_rotate_text_view);
        rotateHorizontalProgressWheelView = findViewById(R.id.prism_post_upload_image_selection_rotate_horizontal_progress_wheel_view);
        aspectRatioControllerLinearLayout = findViewById(R.id.prism_post_upload_image_selection_aspect_ratio_controller);
        aspectRatioButtonsLinearLayout = findViewById(R.id.prism_post_upload_image_selection_aspect_ratio_buttons);
        cropActionButton = findViewById(R.id.prism_post_upload_image_selection_crop_button);
        rotateActionButton = findViewById(R.id.prism_post_upload_image_selection_rotate_button);
        aspectRatioActionButton = findViewById(R.id.prism_post_upload_image_selection_aspect_ratio_button);

        uploadedPostImageViewLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.68);
        actionButtonControlsLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.11);
        actionButtonsLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.11);

        setupUIElements();

        gallerySelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(PrismPostImageSelectionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllowed) {
                    IntentHelper.selectImageFromGallery(PrismPostImageSelectionActivity.this);
                }
            }
        });

        cameraSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllowed = Helper.permissionRequest(PrismPostImageSelectionActivity.this, Manifest.permission.CAMERA);
                if (isAllowed) {
                    imageUriExtra = IntentHelper.takePictureFromCamera(PrismPostImageSelectionActivity.this);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.intentToUploadImageEditActivity(PrismPostImageSelectionActivity.this);
            }
        });

        IntentHelper.selectImageFromGallery(PrismPostImageSelectionActivity.this);
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
        newActionButtonPressed();
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
                    setupGestureCropImageView(outputBitmap);

                } else {
                    if (uCropView != null && uCropView.getCropImageView().getDrawable() == null) {
                        super.onBackPressed();
                    }
                }
                break;
            case Default.CAMERA_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    outputBitmap = BitmapHelper.createBitmapFromImageUri(this, imageUriExtra);
                    setupGestureCropImageView(outputBitmap);

                } else {
                    if (uCropView != null && uCropView.getCropImageView().getDrawable() == null) {
                        super.onBackPressed();
                    }
                }
                break;
        }
    }

    /**
     *
     */
    private void setupActionButtons() {
        cropActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAction = TransformImage.CROP_MODE;
                newActionButtonPressed();
            }
        });

        rotateActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAction = TransformImage.ROTATE_MODE;
                newActionButtonPressed();
            }
        });

        aspectRatioActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAction = TransformImage.ASPECT_RATIO_MODE;
                newActionButtonPressed();
            }
        });
    }

    /**
     *
     */
    private void newActionButtonPressed() {
        cropActionButton.setColorFilter(deactivatedColor);
        rotateActionButton.setColorFilter(deactivatedColor);
        aspectRatioActionButton.setColorFilter(deactivatedColor);
        cropControllerLinearLayout.setVisibility(View.GONE);
        rotateControllerLinearLayout.setVisibility(View.GONE);
        aspectRatioControllerLinearLayout.setVisibility(View.GONE);
        switch (currentAction) {
            case CROP_MODE:
                cropActionButton.setColorFilter(activatedColor);
                cropControllerLinearLayout.setVisibility(View.VISIBLE);
                break;
            case ROTATE_MODE:
                rotateActionButton.setColorFilter(activatedColor);
                rotateControllerLinearLayout.setVisibility(View.VISIBLE);
                break;
            case ASPECT_RATIO_MODE:
                aspectRatioActionButton.setColorFilter(activatedColor);
                aspectRatioControllerLinearLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     *
     */
    private void setupCropControllerLayout() {
        cropHorizontalProgressWheelView.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                if (delta > 0) {
                    uCropView.getCropImageView().zoomInImage(uCropView.getCropImageView().getCurrentScale()
                            + delta * ((uCropView.getCropImageView().getMaxScale() - uCropView.getCropImageView().getMinScale()) / 15000));
                } else {
                    uCropView.getCropImageView().zoomOutImage(uCropView.getCropImageView().getCurrentScale()
                            + delta * ((uCropView.getCropImageView().getMaxScale() - uCropView.getCropImageView().getMinScale()) / 15000));
                }
            }

            @Override
            public void onScrollEnd() {
                uCropView.getCropImageView().setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                uCropView.getCropImageView().cancelAllAnimations();
            }
        });
    }

    /**
     *
     */
    private void setupRotateControllerLayout() {
        rotateHorizontalProgressWheelView.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                uCropView.getCropImageView().postRotate(delta / 42);
            }

            @Override
            public void onScrollEnd() {
                uCropView.getCropImageView().setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                uCropView.getCropImageView().cancelAllAnimations();
            }
        });
    }

    /**
     *
     */
    private void setupAspectRatioControllerLayout() {
        ArrayList<TextView> aspectRatioTextViews = new ArrayList<>();
        for (AspectRatio aspectRatio : AspectRatio.values()) {
            TextView aspectRatioTextView = new TextView(this);
            aspectRatioTextView.setText(aspectRatio.getTitle());
            aspectRatioTextView.setTextSize(14.25f);
            aspectRatioTextView.setTextColor(Color.WHITE);
            aspectRatioTextView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            aspectRatioTextView.setPadding((int) (4 * Default.scale), (int) (4 * Default.scale), (int) (4 * Default.scale), (int) (4 * Default.scale));
            layoutParams.weight = 1.0f;
            aspectRatioTextView.setLayoutParams(layoutParams);
            aspectRatioTextView.setTypeface(Default.sourceSansProBold);

            if (aspectRatio.equals(AspectRatio.ASPECT_RATIO_ORIGINAL)) {
                aspectRatioTextView.setTextColor(activatedColor);
                uCropView.getOverlayView().setTargetAspectRatio(outputBitmap.getWidth() / (float) outputBitmap.getHeight());
            }

            aspectRatioTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleAspectRatioButtons(aspectRatioTextViews);
                    aspectRatioTextView.setTextColor(activatedColor);
                    currentAspectRatio = aspectRatio;
                    switch (aspectRatio) {
                        case ASPECT_RATIO_ORIGINAL:
                            uCropView.getOverlayView().setTargetAspectRatio(outputBitmap.getWidth() / (float) outputBitmap.getHeight());
                            break;
                        default:
                            uCropView.getOverlayView().setTargetAspectRatio(aspectRatio.getX() / (float) aspectRatio.getY());
                            break;
                    }
                }
            });
            aspectRatioTextViews.add(aspectRatioTextView);
            aspectRatioButtonsLinearLayout.addView(aspectRatioTextView);
        }
    }

    private void toggleAspectRatioButtons(ArrayList<TextView> aspectRatioTextViews) {
        for (TextView aspectRatioTextView : aspectRatioTextViews) {
            aspectRatioTextView.setTextColor(deactivatedColor);
        }
    }

    private void setAngleText(float angle) {
        if (rotateValueTextView != null) {
            rotateValueTextView.setText(String.format(Locale.getDefault(), "%.1f°", angle));
        }
    }

    private void setScaleText(float scale) {
        if (cropValueTextView != null) {
            cropValueTextView.setText(String.format(Locale.getDefault(), "%d%%", (int) (scale * 100)));
        }
    }

    private void resetRotation() {
        uCropView.getCropImageView().postRotate(-uCropView.getCropImageView().getCurrentAngle());
        uCropView.getCropImageView().setImageToWrapCropBounds();
    }

    private void rotateByAngle(int angle) {
        uCropView.getCropImageView().postRotate(angle);
        uCropView.getCropImageView().setImageToWrapCropBounds();
    }

    /**
     * @param bitmap
     */
    private void setupGestureCropImageView(Bitmap bitmap) {
        uploadedPostImageViewLinearLayout.removeAllViews();

        uCropView = new UCropView(this, null);
        uCropView.getCropImageView().setRotateEnabled(false);

        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        uCropView.getCropImageView().setImageBitmap(tempBitmap);

        uCropView.getOverlayView().setShowCropFrame(true);
        uCropView.getOverlayView().setShowCropGrid(true);
        uCropView.getOverlayView().setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_DISABLE);

        rotateValueTextView.setTypeface(Default.sourceSansProBold);
        cropValueTextView.setTypeface(Default.sourceSansProBold);

        TransformImageView.TransformImageListener transformImageListener = new TransformImageView.TransformImageListener() {
            @Override
            public void onRotate(float currentAngle) {
                setAngleText(currentAngle);
            }

            @Override
            public void onScale(float currentScale) {
                setScaleText(currentScale);
            }

            @Override
            public void onLoadComplete() {
                uCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onLoadFailure(@NonNull Exception e) {
                finish();
            }

        };

        uCropView.getCropImageView().setTransformImageListener(transformImageListener);

        setupActionButtons();
        setupCropControllerLayout();
        setupRotateControllerLayout();
        setupAspectRatioControllerLayout();

        uploadedPostImageViewLinearLayout.addView(uCropView);
    }

}
