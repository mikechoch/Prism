package com.mikechoch.prism.notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.fire.DatabaseAction;

public class PrismFirebaseInstantIDService extends FirebaseInstanceIdService {

        private static final String TAG = "PrismFirebaseIIDService";

        @Override public void onTokenRefresh() {

            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + refreshedToken);

            // If you want to send messages to this application instance or
            // manage this apps subscriptions on the server side, send the
            // Instance ID token to your app server.
            if (CurrentUser.firebaseUser != null) {
                DatabaseAction.handleFirebaseTokenRefreshActivities(getApplicationContext());
            }

        }

}
