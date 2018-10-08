package com.mikechoch.prism.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.NotificationRecyclerViewAdapter;
import com.mikechoch.prism.attribute.Notification;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.CurrentUser;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    private DatabaseReference databaseReferenceAllPosts;
    private DatabaseReference usersReference;

    private RelativeLayout noNotificationRelativeLayout;
    private TextView noNotificationTextView;
    private RecyclerView notificationRecyclerView;
    public static NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    private ProgressBar notificationProgressBar;

    private int[] swipeRefreshLayoutColors = {R.color.colorAccent};
    private SwipeRefreshLayout notificationSwipeRefreshLayout;

    public static ArrayList<Notification> notificationArrayList;

    public static final NotificationFragment newInstance() {
        NotificationFragment notificationFragment = new NotificationFragment();
        return notificationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_fragment_layout, container, false);

        notificationProgressBar = view.findViewById(R.id.notification_progress_bar);
        noNotificationRelativeLayout = view.findViewById(R.id.no_notification_relative_layout);
        noNotificationTextView = view.findViewById(R.id.no_notification_text_view);
        noNotificationTextView.setTypeface(Default.sourceSansProLight);

         /*
         * The main purpose of this NotificationFragment is to hold all notifications for the user
         * The RecyclerView being created below will show all of the most recent notifications
         * The notifications shown will be for likes, reposts, and following related actions
         */
        notificationRecyclerView = view.findViewById(R.id.notification_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.recycler_view_divider));
        notificationRecyclerView.setLayoutManager(linearLayoutManager);
        notificationRecyclerView.setItemAnimator(defaultItemAnimator);
        notificationRecyclerView.addItemDecoration(dividerItemDecoration);
        notificationRecyclerView.setItemViewCacheSize(20);

        notificationArrayList = CurrentUser.getNotifications();

        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(getContext(), notificationArrayList);
        notificationRecyclerView.setAdapter(notificationRecyclerViewAdapter);

        notificationSwipeRefreshLayout = view.findViewById(R.id.notification_swipe_refresh_layout);
        notificationSwipeRefreshLayout.setColorSchemeResources(swipeRefreshLayoutColors);
        notificationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationRecyclerViewAdapter.notifyDataSetChanged();
                notificationSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    /**
     *
     */
    public static void clearAllNotifications() {
        for (Notification notification : NotificationFragment.notificationArrayList) {
            notification.setViewed(true);
        }
    }
}
