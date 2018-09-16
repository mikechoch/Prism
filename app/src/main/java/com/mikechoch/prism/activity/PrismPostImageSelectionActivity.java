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
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView;

import java.io.File;
import java.util.HashMap;
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
    private ImageView resetRotateButton;
    private ImageView rotate90Button;
    private LinearLayout aspectRatioControllerLinearLayout;
    private LinearLayout aspectRatioButtonsLinearLayout;
    private ImageView cropActionButton;
    private ImageView rotateActionButton;
    private ImageView aspectRatioActionButton;
    private UCropView uCropView;

    private HashMap<AspectRatio, TextView> aspectRatioTextViews;
    private TransformImage currentAction = TransformImage.ASPECT_RATIO_MODE;

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
        resetRotateButton = findViewById(R.id.prism_post_upload_image_selection_reset_rotation_button);
        rotate90Button = findViewById(R.id.prism_post_upload_image_selection_90_rotation_button);
        aspectRatioControllerLinearLayout = findViewById(R.id.prism_post_upload_image_selection_aspect_ratio_controller);
        aspectRatioButtonsLinearLayout = findViewById(R.id.prism_post_upload_image_selection_aspect_ratio_buttons);
        cropActionButton = findViewById(R.id.prism_post_upload_image_selection_crop_button);
        rotateActionButton = findViewById(R.id.prism_post_upload_image_selection_rotate_button);
        aspectRatioActionButton = findViewById(R.id.prism_post_upload_image_selection_aspect_ratio_button);

        uploadedPostImageViewLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.68);
        actionButtonControlsLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.11);
        actionButtonsLinearLayout.getLayoutParams().height = (int) (Default.screenHeight * 0.11);

        setupUIElements();

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
     * Setup the toolbar and back button to return to previous activity
     * Setup gallery intent image selection
     * Setup camera intent image selection
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
    }

    /**
     * Handles cropping the current image with the specific crop overlay
     * On complete handles intent to image editing activity
     */
    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportInvalidateOptionsMenu();
//                Uri destUri = Uri.fromFile(new File(Environment.DIRECTORY_PICTURES + "IMG_" + System.currentTimeMillis()));
//                UCrop.of(imageUriExtra, destUri).start(PrismPostImageSelectionActivity.this);
                float scale = outputBitmap.getHeight() / (float) uCropView.getCropImageView().getViewBitmap().getHeight();
                int x = (int) (uCropView.getOverlayView().getCropViewRect().left - uCropView.getOverlayView().getPaddingLeft());
                int y = (int) (uCropView.getOverlayView().getCropViewRect().top - uCropView.getOverlayView().getPaddingTop());
                int height = (int) (uCropView.getOverlayView().getCropViewRect().height());
                int width = (int) (uCropView.getOverlayView().getCropViewRect().width());


                Bitmap newBitmap = Bitmap.createBitmap(outputBitmap, 0, 0, width, height);
                BitmapHelper.storeImage(PrismPostImageSelectionActivity.this, newBitmap);

//                uCropView.getCropImageView().cropAndSaveImage(Bitmap.CompressFormat.JPEG, 100, new BitmapCropCallback() {
//                    @Override
//                    public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
//                        IntentHelper.intentToUploadImageEditActivity(PrismPostImageSelectionActivity.this, resultUri);
//                    }
//
//                    @Override
//                    public void onCropFailure(@NonNull Throwable t) {
//                        t.printStackTrace();
//                    }
//                });
            }
        });
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        nextButton.setTypeface(Default.sourceSansProBold);

        setupToolbar();
        setupNextButton();
        newActionButtonPressed();
    }

    /**
     * When coming back from gallery or camera intents
     * Handles Uri and new bitmap creation and population
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Default.GALLERY_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    imageUriExtra = data.getData();
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "IMG_" + System.currentTimeMillis()));
                    UCrop.of(imageUriExtra, destinationUri);
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
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "IMG_" + System.currentTimeMillis()));
                    UCrop.of(imageUriExtra, destinationUri);
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
     * Setup all action buttons:
     * Zoom/Crop, Rotate, Aspect Ratio
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
     * Resets all action buttons and toggles the current state on with the appropriate button
     */
    private void newActionButtonPressed() {
        if (uCropView != null) {
            uCropView.getOverlayView().setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_DISABLE);
        }
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
                uCropView.getOverlayView().setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE);
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
     * Setup Zoom/Crop controller layout
     * Contains a text view showing percentage zoom/crop
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
     * Setup Rotate controller layout
     * Contains a text view showing degrees rotation
     * Also has a reset button and 90 degree crop button
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

        resetRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRotation();
            }
        });

        rotate90Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateByAngle();
            }
        });
    }

    /**
     * Setup aspect ratio controller layout
     * Contains 5 buttons:
     * 1:1
     * 4:3
     * ORIG
     * 9:16
     * 2:3
     */
    private void setupAspectRatioControllerLayout() {
        aspectRatioTextViews = new HashMap<>();
        aspectRatioButtonsLinearLayout.removeAllViews();
        for (AspectRatio aspectRatio : AspectRatio.values()) {
            TextView aspectRatioTextView = new TextView(this);
            aspectRatioTextView.setText(aspectRatio.getTitle());
            aspectRatioTextView.setTextSize(14.25f);
            aspectRatioTextView.setTextColor(Color.WHITE);
            aspectRatioTextView.setGravity(Gravity.CENTER);
            aspectRatioTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            aspectRatioTextView.setPadding((int) (2 * Default.scale), (int) (2 * Default.scale), (int) (2 * Default.scale), (int) (2 * Default.scale));
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
            aspectRatioTextViews.put(aspectRatio, aspectRatioTextView);
            aspectRatioButtonsLinearLayout.addView(aspectRatioTextView);
        }
    }

    /**
     * Iterate through all aspect ratio text views and toggle off
     * @param aspectRatioTextViews
     */
    private void toggleAspectRatioButtons(HashMap<AspectRatio, TextView> aspectRatioTextViews) {
        for (AspectRatio aspectRatio : aspectRatioTextViews.keySet()) {
            TextView aspectRatioTextView = aspectRatioTextViews.get(aspectRatio);
            aspectRatioTextView.setTextColor(deactivatedColor);
        }
    }

    /**
     * Sets the angle text for the rotate controller text view
     * @param angle
     */
    private void setAngleText(float angle) {
        if (rotateValueTextView != null) {
            rotateValueTextView.setText(String.format(Locale.getDefault(), "%.1f°", angle));
        }
    }

    /**
     * Sets the scale text for the zoom/crop controller text view
     * @param scale
     */
    private void setScaleText(float scale) {
        if (cropValueTextView != null) {
            cropValueTextView.setText(String.format(Locale.getDefault(), "%d%%", (int) (scale * 100)));
        }
    }

    /**
     * Reset the uCropView rotate
     */
    private void resetRotation() {
        uCropView.getCropImageView().postRotate(-uCropView.getCropImageView().getCurrentAngle());
        uCropView.getCropImageView().setImageToWrapCropBounds();
    }

    /**
     * Rotate uCropView by 90 degrees
     */
    private void rotateByAngle() {
        uCropView.getCropImageView().postRotate(90);
        uCropView.getCropImageView().setImageToWrapCropBounds();
    }

    /**
     * Setup the gesture uCropView for the activity
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
