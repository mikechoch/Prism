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
import com.theartofdev.edmodo.cropper.CropImageView;

public class BitmapEditingControllerLayout extends RelativeLayout {

    private Context context;
    private LayoutInflater layoutInflater;

    private RelativeLayout bitmapEditingControllerRelativeLayout;
    private HorizontalScrollView bitmapEditingControllerHorizontalScrollView;
    public LinearLayout bitmapEditingControllerFilterLinearLayout;
    private LinearLayout bitmapEditingControllerEditingLinearLayout;
    public static LinearLayout filterEditingSeekBarLinearLayout;
    private CropImageView cropImageView;
    private SeekBar filterEditingSeekBar;
    private TextView filterEditingTextView;
    private TabLayout bitmapEditingControllerTabLayout;

    public static boolean isAdjusting = false;
    private boolean isFilter = true;
    private Filter currentFilter = Filter.NORMAL;
    public Edit currentEdit = null;
    private ImageView currentFilterImageView;
    public static Bitmap bitmapPreview;
    public static Bitmap alteredBitmap;
    public static Bitmap modifiedBitmap;

    public static float brightness;
    public static float contrast;
    public static float saturation;


    public BitmapEditingControllerLayout(Context context) {
        super(context);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        init();

    }

    public BitmapEditingControllerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        init();
    }

    public BitmapEditingControllerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {
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

                    if (isFilter) {
                        // If you ever want to add filter adjustment it goes here
                        // Add an OnClick for second filter click
                    } else {
                        switch (currentEdit) {
                            case BRIGHTNESS:
                                brightness = getEditSeekBarValue(progress, currentEdit.getMin(), currentEdit.getMax());
                                break;
                            case CONTRAST:
                                contrast = getEditSeekBarValue(progress, currentEdit.getMin(), currentEdit.getMax());
                                break;
                            case SATURATION:
                                saturation = getEditSeekBarValue(progress, currentEdit.getMin(), currentEdit.getMax());
                                break;
                        }
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
     * @param progress
     * @return
     */
    private float getEditSeekBarValue(int progress, float min, float max) {
        return (((progress / 200.0f) * (max - min)) + min);
    }

    /**
     *
     */
    private void
    applyEffectsToBitmap() {
        modifiedBitmap = bitmapPreview.copy(bitmapPreview.getConfig(), true);

        Canvas canvas = new Canvas(modifiedBitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        Matrix matrix = new Matrix();

        cm.set(BitmapHelper.createEditMatrix(brightness, contrast, saturation));
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(modifiedBitmap, matrix, paint);

        cropImageView.setImageBitmap(modifiedBitmap);
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
            filterPreviewImageView.setImageBitmap(bitmap);
            filterPreviewImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentFilter.equals(filter)) {
                        CardView.LayoutParams filterPreviewLayoutParams = (CardView.LayoutParams) currentFilterImageView.getLayoutParams();
                        filterPreviewLayoutParams.setMargins((int) (0 * Default.scale), (int) (0 * Default.scale), (int) (0 * Default.scale), (int) (0 * Default.scale));
                        currentFilterImageView.setLayoutParams(filterPreviewLayoutParams);

                        currentFilter = filter;
                        currentFilterImageView = filterPreviewImageView;
                        filterPreviewLayoutParams = (CardView.LayoutParams) currentFilterImageView.getLayoutParams();
                        // TODO: Highlight ImageView mayne add padding with white background?
                        filterPreviewLayoutParams.setMargins((int) (2 * Default.scale), (int) (2 * Default.scale), (int) (2 * Default.scale), (int) (2 * Default.scale));
                        currentFilterImageView.setLayoutParams(filterPreviewLayoutParams);

                        // TODO: apply filter to the bitmapPreview
                        brightness = getEditSeekBarValue(currentFilter.getBrightness(), Edit.BRIGHTNESS.getMin(), Edit.BRIGHTNESS.getMax());
                        contrast = getEditSeekBarValue(currentFilter.getContrast(), Edit.CONTRAST.getMin(), Edit.CONTRAST.getMax());
                        saturation = getEditSeekBarValue(currentFilter.getSaturation(), Edit.SATURATION.getMin(), Edit.SATURATION.getMax());

                        applyEffectsToBitmap();
                    }
                }
            });
            if (filter.equals(Filter.NORMAL)) {
                currentFilterImageView = filterPreviewImageView;
                CardView.LayoutParams filterPreviewLayoutParams = (CardView.LayoutParams) filterPreviewImageView.getLayoutParams();
                filterPreviewLayoutParams.setMargins((int) (2 * Default.scale), (int) (2 * Default.scale), (int) (2 * Default.scale), (int) (2 * Default.scale));
                currentFilterImageView.setLayoutParams(filterPreviewLayoutParams);
            }
            TextView filterPreviewTextView = filterPreview.findViewById(R.id.filter_preview_text_view);
            filterPreviewTextView.setText(filter.getTitle());
            filterPreviewTextView.setTypeface(Default.sourceSansProLight);

            bitmapEditingControllerFilterLinearLayout.addView(filterPreviewLinearLayout);
        }

    }

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
                            progress = (int) (((brightness - currentEdit.getMin()) / (currentEdit.getMax() - currentEdit.getMin()) * 200));
                            break;
                        case CONTRAST:
                            progress = (int) (((contrast - currentEdit.getMin()) / (currentEdit.getMax() - currentEdit.getMin()) * 200));
                            break;
                        case SATURATION:
                            progress = (int) (((saturation - currentEdit.getMin()) / (currentEdit.getMax() - currentEdit.getMin()) * 200));
                            break;
                    }
                    String progressText = String.valueOf(progress - 100);
                    filterEditingSeekBar.setProgress(progress);
                    filterEditingTextView.setText(progressText);

                    modifiedBitmap = bitmapPreview.copy(bitmapPreview.getConfig(), true);
                    Canvas canvas = new Canvas(modifiedBitmap);
                    Paint paint = new Paint();
                    ColorMatrix cm = new ColorMatrix();
                    Matrix matrix = new Matrix();

                    cm.set(BitmapHelper.createEditMatrix(brightness, contrast, saturation));
                    paint.setColorFilter(new ColorMatrixColorFilter(cm));
                    canvas.drawBitmap(modifiedBitmap, matrix, paint);

                    cropImageView.setImageBitmap(modifiedBitmap);
                }
            });
            editFabTextView.setText(edit.getTitle());
            editFabTextView.setTypeface(Default.sourceSansProLight);

            bitmapEditingControllerEditingLinearLayout.addView(editFabLinearLayout);
        }
    }

    /**
     *
     * @param bitmapEditingControllerTabLayout
     */
    public void attachTabLayout(TabLayout bitmapEditingControllerTabLayout) {
        TabLayout.Tab filterTab = bitmapEditingControllerTabLayout.newTab();
        TextView filterTextView = Helper.createTabTextView(context, "FILTER");
        filterTextView.setScaleY(-1);
        filterTab.setCustomView(filterTextView);

        TabLayout.Tab editingTab = bitmapEditingControllerTabLayout.newTab();
        TextView editingTextView = Helper.createTabTextView(context, "EDIT");
        editingTextView.setScaleY(-1);
        editingTab.setCustomView(editingTextView);

        bitmapEditingControllerTabLayout.addTab(filterTab);
        bitmapEditingControllerTabLayout.addTab(editingTab);

        int selectedTabColor = getResources().getColor(R.color.colorAccent);
        int unselectedTabColor = Color.WHITE;
        ((TextView) bitmapEditingControllerTabLayout.getTabAt(bitmapEditingControllerTabLayout.getSelectedTabPosition()).getCustomView())
                .setTextColor(selectedTabColor);

        bitmapEditingControllerTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(selectedTabColor);
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        isAdjusting = false;
                        isFilter = true;
                        filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
                        bitmapEditingControllerFilterLinearLayout.setVisibility(VISIBLE);
                        break;
                    case 1:
                        isAdjusting = false;
                        isFilter = false;
                        filterEditingSeekBarLinearLayout.setVisibility(View.GONE);
                        bitmapEditingControllerEditingLinearLayout.setVisibility(VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(unselectedTabColor);

                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        bitmapEditingControllerFilterLinearLayout.setVisibility(GONE);
                        break;
                    case 1:
                        bitmapEditingControllerEditingLinearLayout.setVisibility(GONE);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void attachCropImageView(CropImageView cropImageView) {
        this.cropImageView = cropImageView;

    }


}