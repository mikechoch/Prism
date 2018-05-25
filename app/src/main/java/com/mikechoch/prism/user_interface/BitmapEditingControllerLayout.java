package com.mikechoch.prism.user_interface;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.Edit;

public class BitmapEditingControllerLayout extends RelativeLayout {

    private Context context;
    private LayoutInflater layoutInflater;

    private RelativeLayout bitmapEditingControllerRelativeLayout;
    private HorizontalScrollView bitmapEditingControllerHorizontalScrollView;
    private LinearLayout bitmapEditingControllerFilterLinearLayout;
    private LinearLayout bitmapEditingControllerEditingLinearLayout;

    private TabLayout bitmapEditingControllerTabLayout;

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

        for (Edit edit : Edit.values()) {
            View fabView = layoutInflater.inflate(R.layout.edit_fab_layout, null, true);
            FloatingActionButton fab = fabView.findViewById(R.id.edit_fab);
            TextView fabTextView = fabView.findViewById(R.id.edit_fab_text_view);
                    Drawable fabIcon = context.getResources().getDrawable(edit.getIcon());
            fabIcon.setTint(Color.WHITE);
            fab.setImageDrawable(fabIcon);
            fabTextView.setText(edit.getTitle());
            fabTextView.setTypeface(Default.sourceSansProLight);

            bitmapEditingControllerEditingLinearLayout.addView(fabView);
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
                        bitmapEditingControllerFilterLinearLayout.setVisibility(VISIBLE);
                        break;
                    case 1:
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
}