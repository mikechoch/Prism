package com.mikechoch.prism.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mikechoch.prism.R;
import com.mikechoch.prism.type.SettingType;
import com.mikechoch.prism.activity.LoginActivity;
import com.mikechoch.prism.constant.Default;

/**
 * Created by mikechoch on 2/7/18.
 */

public class SettingsOptionRecyclerViewAdapter extends RecyclerView.Adapter<SettingsOptionRecyclerViewAdapter.ViewHolder> {

    /*
     * Global variables
     */
    private Context context;

    private Typeface sourceSansProLight;
    private Typeface sourceSansProBold;

    private FirebaseAuth auth;


    public SettingsOptionRecyclerViewAdapter(Context context) {
        this.context = context;

        this.sourceSansProLight = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Light.ttf");
        this.sourceSansProBold = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Black.ttf");

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_recycler_view_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(SettingType.values()[position]);
    }

    @Override
    public int getItemCount() {
        return SettingType.values().length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout settingsOptionRelativeLayout;
        private TextView settingsOptionTextView;
        private ImageView settingsOptionImageView;

        private SettingType settingType;


        public ViewHolder(View itemView) {
            super(itemView);

            // SettingOptions UI initializations
            settingsOptionRelativeLayout = itemView.findViewById(R.id.settings_recycler_view_item_relative_layout);
            settingsOptionTextView = itemView.findViewById(R.id.settings_recycler_view_item_text_view);
            settingsOptionImageView = itemView.findViewById(R.id.settings_recycler_view_item_icon);
        }

        /**
         * Set data for the ViewHolder UI elements
         */
        public void setData(SettingType settingType) {
            this.settingType = settingType;
            populateUIElements();
        }

        /**
         * settingsOptionRelativeLayout
         * Set the onClickListener switch statement for each SettingType
         */
        private void setupSettingsOptionRelativeLayout() {
            settingsOptionRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int settingOptionId = settingType.getOptionId();
                    switch(settingOptionId) {
                        case Default.SETTINGS_OPTION_APP:
                            break;
                        case Default.SETTINGS_OPTION_NOTIFICATION:
                            break;
                        case Default.SETTINGS_OPTION_ACCOUNT:
                            break;
                        case Default.SETTINGS_OPTION_HELP:
                            break;
                        case Default.SETTINGS_OPTION_ABOUT:
                            break;
                        case Default.SETTINGS_OPTION_LOGOUT:
                            auth.signOut();
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        /**
         * settingsOptionTextView
         * Get the SettingType enum title and populate the TextView
         */
        private void setupSettingsOptionTextView() {
            settingsOptionTextView.setText(settingType.getOptionTitle());
        }

        /**
         * settingsOptionImageView
         * Get the SettingType enum icon and populate the ImageView
         */
        private void setupSettingsOptionImageView() {
            Drawable settingsIcon = context.getResources().getDrawable(settingType.getOptionIcon());
            settingsIcon.setTint(Color.WHITE);
            settingsOptionImageView.setImageDrawable(settingsIcon);
        }

        /**
         * Populate all UI elements with data
         */
        private void populateUIElements() {
            // Setup Typefaces for all text based UI elements
            settingsOptionTextView.setTypeface(sourceSansProLight);

            setupSettingsOptionRelativeLayout();
            setupSettingsOptionTextView();
            setupSettingsOptionImageView();
        }
    }
}