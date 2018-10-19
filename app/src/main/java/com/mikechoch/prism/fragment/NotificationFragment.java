package com.mikechoch.prism.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    private RelativeLayout noNotificationRelativeLayout;
    private TextView noNotificationTextView;
    private ProgressBar notificationProgressBar;
    private RelativeLayout noNotificationsRelativeLayout;
    private TextView noNotificationsTextView;

    private SwipeRefreshLayout notificationSwipeRefreshLayout;
    private RecyclerView notificationRecyclerView;
    private NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;

    public static ArrayList<Notification> notificationArrayList;


    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_fragment_layout, container, false);

        notificationProgressBar = view.findViewById(R.id.notification_progress_bar);
        notificationSwipeRefreshLayout = view.findViewById(R.id.notification_swipe_refresh_layout);
        notificationRecyclerView = view.findViewById(R.id.notification_recycler_view);
        noNotificationsRelativeLayout = view.findViewById(R.id.no_notification_relative_layout);
        noNotificationsTextView = view.findViewById(R.id.no_notification_text_view);

        notificationArrayList = new ArrayList<>();
        notificationArrayList = CurrentUser.getNotifications();

        setupInterfaceElements();

        return view;
    }

    /**
     * Iterate over all notifications and set all of them to viewed as True
     */
    public static void clearAllNotifications() {
        for (Notification notification : NotificationFragment.notificationArrayList) {
            notification.setViewed(true);
        }
    }

    /**
     * Setup the notification swipe refresh layout,
     * which will call notifyDataSetChanged on the notification adapter
     */
    private void setupNotificationSwipeRefreshLayout() {
        notificationSwipeRefreshLayout.setColorSchemeResources(InterfaceAction.swipeRefreshLayoutColors);
        notificationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationRecyclerViewAdapter.notifyDataSetChanged();
                notificationSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * The main purpose of this NotificationFragment is to hold all notifications for the user
     * The RecyclerView being created below will show all of the most recent notifications
     * The notifications shown will be for likes, reposts, and following related actions
     */
    private void setupNotificationRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getActivity().getResources().getDrawable(R.drawable.recycler_view_divider));
        notificationRecyclerView.setLayoutManager(linearLayoutManager);
        notificationRecyclerView.setItemAnimator(defaultItemAnimator);
        notificationRecyclerView.addItemDecoration(dividerItemDecoration);
        notificationRecyclerView.setItemViewCacheSize(20);

        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(getContext(), notificationArrayList);
        notificationRecyclerView.setAdapter(notificationRecyclerViewAdapter);
    }

    /**
     * Setup elements in current fragment
     */
    private void setupInterfaceElements() {
        noNotificationsTextView.setTypeface(Default.sourceSansProLight);

        setupNotificationRecyclerView();
        setupNotificationSwipeRefreshLayout();
    }

}
