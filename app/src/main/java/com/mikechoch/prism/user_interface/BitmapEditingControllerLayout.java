package com.mikechoch.prism.user_interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.BitmapHelper;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.Edit;
import com.mikechoch.prism.type.Filter;
import com.mikechoch.prism.type.ImageEdit;

import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class BitmapEditingControllerLayout extends RelativeLayout {

    private Context context;
    private LayoutInflater layoutInflater;

    private RelativeLayout bitmapEditingControllerRelativeLayout;
    private HorizontalScrollView bitmapEditingControllerHorizontalScrollView;
    private LinearLayout bitmapEditingControllerFilterLinearLayout;
    private LinearLayout bitmapEditingControllerEditingLinearLayout;
    public LinearLayout filterEditingSeekBarLinearLayout;
    private PhotoEditorView photoEditorView;
    private SeekBar filterEditingSeekBar;
    private TextView filterEditingTextView;
    private TabLayout bitmapEditingControllerTabLayout;

    private ImageView currentFilterImageView;
    private Bitmap bitmapPreview;
    private Bitmap alteredBitmap;
    private Bitmap modifiedBitmap;

    private boolean isCircularBitmap = false;

    private boolean isAdjusting = false;
    private boolean isFilter = true;
    private Filter currentFilter = Filter.NORMAL;
    private Edit currentEdit = null;
    private float brightness;
    private float contrast;
    private float saturation;


    public BitmapEditingControllerLayout(Context context) {
        super(context);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    public BitmapEditingControllerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    public BitmapEditingControllerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    public BitmapEditingControllerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        initBitmapEdit();
    }

    public boolean isIsAdjusting() {
        return isAdjusting;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public Bitmap getBitmapPreview() {
        return bitmapPreview;
    }

    public Bitmap getAlteredBitmap() {
        return alteredBitmap;
    }

    public Bitmap getModifiedBitmap() {
        return modifiedBitmap;
    }

    public float getBrightness() {
        return brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setIsAdjusting(boolean isAdjusting) {
        this.isAdjusting = isAdjusting;
    }

    public void setFilter(boolean filter) {
        this.isFilter = filter;
    }

    public void setBitmapPreview(Bitmap bitmapPreview) {
        this.bitmapPreview = bitmapPreview;
    }

    public void setAlteredBitmap(Bitmap alteredBitmap) {
        this.alteredBitmap = alteredBitmap;
    }

    public void setModifiedBitmap(Bitmap modifiedBitmap) {
        this.modifiedBitmap = modifiedBitmap;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    /**
     *
     */
    public void initBitmapEdit() {
        View view = layoutInflater.inflate(R.layout.bitmap_editing_controller_layout, this, true);

        bitmapEditingControllerRelativeLayout = view.findViewById(R.id.bitmap_editing_controller_relative_layout);
        bitmapEditingControllerHorizontalScrollView = view.findViewById(R.id.bitmap_editing_controller_editing_horizontal_scroll_view);
        bitmapEditingControllerFilterLinearLayout = view.findViewById(R.id.bitmap_editing_controller_filter_linear_layout);
        bitmapEditingControllerEditingLinearLayout = view.findViewById(R.id.bitmap_editing_controller_editing_linear_layout);
        filterEditingSeekBarLinearLayout = view.findViewById(R.id.filter_editing_seek_bar_linear_layout);
        filterEditingSeekBar = view.findViewById(R.id.filter_editing_seek_bar);
        filterEditingTextView = view.findViewById(R.id.filter_editing_percentage_text_view);

        filterEditingTextView.setTypeface(Default.sourceSansProLight);

        // The SeekBar will be represented on a range of 200
        // The TextView min -100, max 100, and a default of 0
        filterEditingSeekBar.setMax(200);
        filterEditingSeekBar.setProgress(0);
        filterEditingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    filterEditingTextView.setText(String.valueOf(progress - 100));
                    switch (currentEdit) {
                        case BRIGHTNESS:
                            brightness = Helper.getEditSeekBarValue(progress, currentEdit.getMin(), currentEdit.getMax());
                            break;
                        case CONTRAST:
                            contrast = Helper.getEditSeekBarValue(progress, currentEdit.getMin(), currentEdit.getMax());
                            break;
                        case SATURATION:
                            saturation = Helper.getEditSeekBarValue(progress, currentEdit.getMin(), currentEdit.getMax());
                            break;
                    }
                    applyEffectsToBitmap();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setupEditingController();
    }

    /**
     *
     * @param bitmap
     */
    public void setupFilterController(Bitmap bitmap) {
        bitmapEditingControllerFilterLinearLayout.removeAllViews();
        for (Filter filter : Filter.values()) {
            View filterPreview = layoutInflater.inflate(R.layout.filter_preview_layout, null, true);
            LinearLayout filterPreviewLinearLayout = filterPreview.findViewById(R.id.filter_preview_linear_layout);
            ImageView filterPreviewImageView = filterPreview.findViewById(R.id.filter_preview_image_view);

            Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), true);

            Canvas canvas = new Canvas(tempBitmap);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            Matrix matrix = new Matrix();

            float b = Helper.getEditSeekBarValue(filter.getBrightness(),
                    Edit.BRIGHTNESS.getMin(), Edit.BRIGHTNESS.getMax());
            float c = Helper.getEditSeekBarValue(filter.getContrast(),
                    Edit.CONTRAST.getMin(), Edit.CONTRAST.getMax());
            float s = Helper.getEditSeekBarValue(filter.getSaturation(),
                    Edit.SATURATION.getMin(), Edit.SATURATION.getMax());

            cm.set(BitmapHelper.createEditMatrix(b, c, s));
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(tempBitmap, matrix, paint);

            filterPreviewImageView.setImageBitmap(tempBitmap);
            filterPreviewImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentFilter.equals(filter)) {
                        handleFilterClick(filter, filterPreviewImageView);
                    }
                }
            });

            if (filter.equals(Filter.NORMAL)) {
                currentFilterImageView = filterPreviewImageView;
                CardView.LayoutParams filterPreviewLayoutParams = (CardView.LayoutParams) filterPreviewImageView.getLayoutParams();
                int outlineMargin = (int) (2 * Default.scale);
                filterPreviewLayoutParams.setMargins(outlineMargin, outlineMargin, outlineMargin, outlineMargin);
                currentFilterImageView.setLayoutParams(filterPreviewLayoutParams);
            }

            TextView filterPreviewTextView = filterPreview.findViewById(R.id.filter_preview_text_view);
            filterPreviewTextView.setText(filter.getTitle());
            filterPreviewTextView.setTypeface(Default.sourceSansProLight);

            bitmapEditingControllerFilterLinearLayout.addView(filterPreviewLinearLayout);
        }
    }

    /**
     *
     */
    private void setupEditingController() {
        brightness = Edit.BRIGHTNESS.getDef();
        contrast = Edit.CONTRAST.getDef();
        saturation = Edit.SATURATION.getDef();

        for (Edit edit : Edit.values()) {
            View editFabView = layoutInflater.inflate(R.layout.edit_fab_layout, null, true);
            LinearLayout editFabLinearLayout = editFabView.findViewById(R.id.edit_fab_linear_layout);
            FloatingActionButton editFab = editFabView.findViewById(R.id.edit_fab);
            TextView editFabTextView = editFabView.findViewById(R.id.edit_fab_text_view);
            Drawable editFabIcon = context.getResources().getDrawable(edit.getIcon());
            editFabIcon.setTint(Color.WHITE);
            editFab.setImageDrawable(editFabIcon);
            editFab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAdjusting = true;
                    currentEdit = edit;
                    filterEditingSeekBarLinearLayout.setVisibility(View.VISIBLE);

                    int progress = 100;
                    switch (currentEdit) {
                        case BRIGHTNESS:
                            progress = (int) (((brightness - currentEdit.getMin()) /
                                    (currentEdit.getMax() - currentEdit.getMin()) * 200));
                            break;
                        case CONTRAST:
                            progress = (int) (((contrast - currentEdit.getMin()) /
                                    (currentEdit.getMax() - currentEdit.getMin()) * 200));
                            break;
                        case SATURATION:
                            progress = (int) (((saturation - currentEdit.getMin()) /
                                    (currentEdit.getMax() - currentEdit.getMin()) * 200));
                            break;
                    }
                    String progressText = String.valueOf(progress - 100);
                    filterEditingSeekBar.setProgress(progress);
                    filterEditingTextView.setText(progressText);

                    applyEffectsToBitmap();
                }
            });
            editFabTextView.setText(edit.getTitle());
            editFabTextView.setTypeface(Default.sourceSansProLight);

            bitmapEditingControllerEditingLinearLayout.addView(editFabLinearLayout);
        }
    }

    /**
     *
     * @param photoEditorView
     * @param isCircularBitmap
     */
    public void attachPhotoEditorView(PhotoEditorView photoEditorView, boolean isCircularBitmap) {
        this.photoEditorView = photoEditorView;
        this.isCircularBitmap = isCircularBitmap;
    }

    /**
     *
     * @param bitmapEditingControllerTabLayout
     */
    public void attachTabLayout(TabLayout bitmapEditingControllerTabLayout) {
        int selectedTabColor = getResources().getColor(R.color.colorAccent);
        int unselectedTabColor = Color.WHITE;

        for (ImageEdit imageEdit : ImageEdit.values()) {
            TabLayout.Tab imageEditTab = bitmapEditingControllerTabLayout.newTab();
            TextView imageEditTextView = Helper.createTabTextView(context, imageEdit.getTitle());
            imageEditTextView.setScaleY(-1);

            if (imageEdit.getId() == Default.IMAGE_EDIT_TYPE_VIEW_PAGER_FILTER) {
                imageEditTextView.setTextColor(selectedTabColor);
            }

            imageEditTab.setCustomView(imageEditTextView);
            bitmapEditingControllerTabLayout.addTab(imageEditTab);
        }

        bitmapEditingControllerTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTextView = (TextView) tab.getCustomView();
                if (tabTextView != null) {
                    tabTextView.setTextColor(selectedTabColor);
                }
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case Default.IMAGE_EDIT_TYPE_VIEW_PAGER_FILTER:
                        handleTabClick(true);
                        break;
                    case Default.IMAGE_EDIT_TYPE_VIEW_PAGER_EDIT:
                        handleTabClick(false);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTextView = (TextView) tab.getCustomView();
                if (tabTextView != null) {
                    tabTextView.setTextColor(unselectedTabColor);
                }
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case Default.IMAGE_EDIT_TYPE_VIEW_PAGER_FILTER:
                        bitmapEditingControllerFilterLinearLayout.setVisibility(GONE);
                        break;
                    case Default.IMAGE_EDIT_TYPE_VIEW_PAGER_EDIT:
                        bitmapEditingControllerEditingLinearLayout.setVisibility(GONE);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                TextView tabTextView = (TextView) tab.getCustomView();
                if (tabTextView != null) {
                    tabTextView.setTextColor(selectedTabColor);
                }
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case Default.IMAGE_EDIT_TYPE_VIEW_PAGER_FILTER:
                        handleTabClick(true);
                        break;
                    case Default.IMAGE_EDIT_TYPE_VIEW_PAGER_EDIT:
                        handleTabClick(false);
                        break;
                }
            }
        });
    }

    /**
     *
     * @param isFilter
     */
    private void handleTabClick(boolean isFilter) {
        isAdjusting = false;
        this.isFilter = isFilter;
        filterEditingSeekBarLinearLayout.setVisibility(View.GONE);

        int filterLayoutVisibility = isFilter ? View.VISIBLE : View.GONE;
        int editLayoutVisibility = isFilter ? View.GONE : View.VISIBLE;
        bitmapEditingControllerFilterLinearLayout.setVisibility(filterLayoutVisibility);
        bitmapEditingControllerEditingLinearLayout.setVisibility(editLayoutVisibility);
    }

    /**
     *
     * @param filter
     * @param filterPreviewImageView
     */
    private void handleFilterClick(Filter filter, ImageView filterPreviewImageView) {
        CardView.LayoutParams filterPreviewLayoutParams = (CardView.LayoutParams) currentFilterImageView.getLayoutParams();
        filterPreviewLayoutParams.setMargins(0, 0, 0, 0);
        currentFilterImageView.setLayoutParams(filterPreviewLayoutParams);

        currentFilter = filter;
        currentFilterImageView = filterPreviewImageView;
        filterPreviewLayoutParams = (CardView.LayoutParams) currentFilterImageView.getLayoutParams();

        int outlineMargin = (int) (2 * Default.scale);
        filterPreviewLayoutParams.setMargins(outlineMargin, outlineMargin, outlineMargin, outlineMargin);
        currentFilterImageView.setLayoutParams(filterPreviewLayoutParams);

        brightness = Helper.getEditSeekBarValue(currentFilter.getBrightness(),
                Edit.BRIGHTNESS.getMin(), Edit.BRIGHTNESS.getMax());
        contrast = Helper.getEditSeekBarValue(currentFilter.getContrast(),
                Edit.CONTRAST.getMin(), Edit.CONTRAST.getMax());
        saturation = Helper.getEditSeekBarValue(currentFilter.getSaturation(),
                Edit.SATURATION.getMin(), Edit.SATURATION.getMax());

        applyEffectsToBitmap();
    }

    /**
     *
     */
    private void applyEffectsToBitmap() {
        alteredBitmap = modifiedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(alteredBitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        Matrix matrix = new Matrix();

        cm.reset();
        paint.reset();
        matrix.reset();

        cm.set(BitmapHelper.createEditMatrix(brightness, contrast, saturation));
        paint.setColorFilter(new ColorMatrixColorFilter(cm));

        canvas.drawBitmap(alteredBitmap, matrix, paint);

        photoEditorView.getSource().setImageBitmap(alteredBitmap);
    }

}