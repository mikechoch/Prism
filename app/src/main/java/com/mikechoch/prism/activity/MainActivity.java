package com.mikechoch.prism.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import java.util.Calendar;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikechoch.prism.CurrentUser;
import com.mikechoch.prism.Default;
import com.mikechoch.prism.Key;
import com.mikechoch.prism.R;
import com.mikechoch.prism.ViewPagerAdapter;
import com.mikechoch.prism.Wallpaper;

public class MainActivity extends FragmentActivity {

    /*
     * Global variables
     */
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private DatabaseReference userReference;

    private Uri uploadedImageUri;
    private String uploadedImageDescription;

    private CoordinatorLayout mainCoordinateLayout;
    private TabLayout prismTabLayout;
    private ViewPager prismViewPager;
    private FloatingActionButton uploadImageFab;
    private NumberProgressBar imageUploadProgressBar;

    private Animation hideFabAnimation;
    private Animation showFabAnimation;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        // Generates current user's details
        new CurrentUser();
        auth = FirebaseAuth.getInstance();
        storageReference = Default.STORAGE_REFERENCE;
        databaseReference = Default.ALL_POSTS_REFERENCE;
        userReference = Default.USERS_REFERENCE.child(auth.getCurrentUser().getUid());

        // Create uploadImageFab showing and hiding animations
        showFabAnimation = createFabShowAnimation(false);
        hideFabAnimation = createFabShowAnimation(true);

        // Initialize the mainCoordinateLayout for the MainActivity layout
        mainCoordinateLayout = findViewById(R.id.main_coordinate_layout);

        /*
         * Initialize ViewPager and TabLayout
         * Give PageChangeListener control to TabLayout
         * Create ViewPagerAdapter and set it for the ViewPager
         */
        prismTabLayout = findViewById(R.id.prism_tab_layout);
        prismViewPager = findViewById(R.id.prism_view_pager);
        prismViewPager.setOffscreenPageLimit(Default.VIEW_PAGER_SIZE);
        prismViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(prismTabLayout));
        ViewPagerAdapter prismViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        prismViewPager.setAdapter(prismViewPagerAdapter);
        prismTabLayout.setupWithViewPager(prismViewPager);

        /*
         * Setup for the TabLayout
         * Give each tab an icon and set the listener for selecting, reselecting, and unselecting
         * Selected tabs will be a ColorAccent and unselected tabs White
         */
        prismTabLayout.getTabAt(Default.VIEW_PAGER_HOME).setIcon(R.drawable.ic_image_filter_hdr_white_36dp);
        prismTabLayout.getTabAt(Default.VIEW_PAGER_TRENDING).setIcon(R.drawable.ic_flash_white_36dp);
        prismTabLayout.getTabAt(Default.VIEW_PAGER_SEARCH).setIcon(R.drawable.ic_magnify_white_36dp);
        prismTabLayout.getTabAt(Default.VIEW_PAGER_NOTIFICATIONS).setIcon(R.drawable.ic_bell_white_36dp);
        prismTabLayout.getTabAt(Default.VIEW_PAGER_PROFILE).setIcon(R.drawable.ic_account_white_36dp);
        int tabUnselectedColor = Color.WHITE;
        int tabSelectedColor = getResources().getColor(R.color.colorAccent);
        prismTabLayout.getTabAt(Default.VIEW_PAGER_HOME).getIcon().setColorFilter(
                tabSelectedColor, PorterDuff.Mode.SRC_IN);
        prismTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(tabSelectedColor, PorterDuff.Mode.SRC_IN);
                prismViewPager.setCurrentItem(tab.getPosition(), true);
                if (tab.getPosition() <= Default.VIEW_PAGER_TRENDING && !uploadImageFab.isShown()) {
                    uploadImageFab.startAnimation(showFabAnimation);
                } else if (tab.getPosition() > Default.VIEW_PAGER_TRENDING && uploadImageFab.isShown()) {
                    uploadImageFab.startAnimation(hideFabAnimation);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(tabUnselectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        RecyclerView mainContentRecyclerView = MainActivity.this.findViewById(R.id.main_content_recycler_view);
                        if (mainContentRecyclerView != null) {
                            mainContentRecyclerView.smoothScrollToPosition(0);
                        }
                        break;
                    case 1:
                        RecyclerView trendingContentRecyclerView = MainActivity.this.findViewById(R.id.trending_content_recycler_view);
                        if (trendingContentRecyclerView != null) {
                            trendingContentRecyclerView.smoothScrollToPosition(0);
                        }
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        break;
                }
            }
        });

        // Initialize uploadImageFab and OnClickListener to take you to ImageUploadActivity
        imageUploadProgressBar = findViewById(R.id.image_upload_progress_bar);
        uploadImageFab = findViewById(R.id.upload_image_fab);
        uploadImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageUploadIntent = new Intent( MainActivity.this, ImageUploadActivity.class);
                startActivityForResult(imageUploadIntent, Default.IMAGE_UPLOAD_INTENT_REQUEST_CODE);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        // Ask user for write permissions to external storage
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Default.MY_PERMISSIONS_REQUEST_READ_MEDIA);
        }
    }


    /**
     * Takes in a boolean shouldHide and will create a hiding and showing animation
     */
    private Animation createFabShowAnimation(boolean shouldHide) {
        float scaleFromXY = shouldHide ? 1f : 0f;
        float scaleToXY = shouldHide ? 0f : 1f;
        float pivotXY = 0.5f;
        Animation scaleAnimation  = new ScaleAnimation(scaleFromXY, scaleToXY, scaleFromXY, scaleToXY,
                Animation.RELATIVE_TO_SELF, pivotXY,
                Animation.RELATIVE_TO_SELF, pivotXY);
        scaleAnimation.setDuration(200);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                uploadImageFab.setVisibility(shouldHide ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return scaleAnimation;
    }


    @SuppressLint("SimpleDateFormat")
    private void uploadImageToCloud() {
        StorageReference filePath = storageReference.child(Key.STORAGE_IMAGE_REF).child(uploadedImageUri.getLastPathSegment());
        filePath.putFile(uploadedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                DatabaseReference reference = databaseReference.push();

                String imageUri = downloadUrl.toString();
                String description = uploadedImageDescription;
                String username = auth.getCurrentUser().getDisplayName();
                String userId = auth.getCurrentUser().getUid();
                Long timestamp = -1 * Calendar.getInstance().getTimeInMillis();
                String postId = reference.getKey();

                DatabaseReference userPostRef = userReference.child(Key.DB_REF_USER_UPLOADS).child(postId);
                userPostRef.setValue(timestamp);

                Wallpaper wallpaper = new Wallpaper(imageUri, description, username, userId, timestamp, postId);

                reference.setValue(wallpaper).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        snackTime("Successfully uploaded image");
                        imageUploadProgressBar.setVisibility(View.GONE);
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                        imageUploadProgressBar.setProgress(progress);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                snackTime("Failed to upload image");
                imageUploadProgressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        });
    }

    /**
     *
     */
    private class ImageUploadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            uploadImageToCloud();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

    /**
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Default.IMAGE_UPLOAD_INTENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    imageUploadProgressBar.setProgress(0);
                    imageUploadProgressBar.setVisibility(View.VISIBLE);
                    uploadedImageUri = Uri.parse(data.getStringExtra("ImageUri"));
                    uploadedImageDescription = data.getStringExtra("ImageDescription");
                    new ImageUploadTask().execute();
                }
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Default.MY_PERMISSIONS_REQUEST_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Code here for allowing write permission
                }
                break;
            default:
                break;
        }
    }

    /**
     * Shortcut for displaying a Toast message
     */
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shortcut for displaying a Snackbar message
     */
    private void snackTime(String message) {
        Snackbar.make(mainCoordinateLayout, message, Toast.LENGTH_SHORT).show();
    }
}
