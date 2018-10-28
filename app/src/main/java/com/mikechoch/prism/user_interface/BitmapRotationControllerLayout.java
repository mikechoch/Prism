package com.mikechoch.prism.user_interface;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.type.Rotation;
import com.theartofdev.edmodo.cropper.CropImageView;


public class BitmapRotationControllerLayout extends RelativeLayout {

    private Context context;
    private LayoutInflater layoutInflater;

    private RelativeLayout bitmapRotationRelativeLayout;
    private HorizontalScrollView bitmapRotationHorizontalScrollView;
    private LinearLayout bitmapRotationLinearLayout;

    private CropImageView cropImageView;


    public BitmapRotationControllerLayout(Context context) {
        super(context);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    public BitmapRotationControllerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    public BitmapRotationControllerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    public BitmapRotationControllerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    /**
     * Inflate the rotation controller layout and initialize all the interface elements
     */
    public void initBitmapEdit() {
        View view = layoutInflater.inflate(R.layout.bitmap_rotation_controller_layout, this, true);

        bitmapRotationRelativeLayout = view.findViewById(R.id.bitmap_rotation_controller_relative_layout);
        bitmapRotationHorizontalScrollView = view.findViewById(R.id.bitmap_rotation_controller_editing_horizontal_scroll_view);
        bitmapRotationLinearLayout = view.findViewById(R.id.bitmap_rotation_controller_editing_linear_layout);

        setupRotationController();
    }


    /**
     * Setup the rotation button controls using the Rotation enum
     * Creates the FAB and also sets up the on click for each button to handle the CropImageView
     */
    private void setupRotationController() {
        for (Rotation rotation : Rotation.values()) {
            FloatingActionButton rotationFab = new FloatingActionButton(context);
            LinearLayout.LayoutParams fabParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int fabMargin = (int) (12 * Default.scale);
            fabParams.setMargins(fabMargin, 0, fabMargin, 0);
            rotationFab.setLayoutParams(fabParams);

            rotationFab.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
            rotationFab.setRippleColor(Color.WHITE);
            Drawable rotationFabIcon = context.getResources().getDrawable(rotation.getIcon());
            rotationFabIcon.setTint(Color.WHITE);
            rotationFab.setImageDrawable(rotationFabIcon);

            rotationFab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (rotation) {
                        case CLOCKWISE_90:
                            cropImageView.rotateImage(90);
                            break;
                        case FLIP_VERTICAL:
                            cropImageView.flipImageVertically();
                            break;
                        case FLIP_HORIZONTAL:
                            cropImageView.flipImageHorizontally();
                            break;
                    }
                }
            });

            bitmapRotationLinearLayout.addView(rotationFab);
        }
    }

    /**
     * Attach the CropImageView from activity requiring rotation
     * @param cropImageView - CropImageView library being controlled via rotation
     */
    public void attachCropImageView(CropImageView cropImageView) {
        this.cropImageView = cropImageView;
    }

}
