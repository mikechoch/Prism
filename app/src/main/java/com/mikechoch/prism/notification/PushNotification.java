package com.mikechoch.prism.notification;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.mikechoch.prism.R;
import com.mikechoch.prism.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PushNotification {

    public PushNotification(String userToken) {

        JSONObject dataJson = new JSONObject();
        JSONObject jsonBody = new JSONObject();
        HashMap<String, String> params = new HashMap<>();
        try {
            dataJson.put("title", "Prism Notification");
            dataJson.put("message", "This is a Prism notification, make sense?");


            jsonBody.put("to", userToken);     // TODO programmatically populate this
            jsonBody.put("data", dataJson);

                    params.put("Content-Type", "application/json charset=utf-8");
                    params.put("Authorization", "key=" + "AAAAoddO4Nw:APA91bEjN_om3_iwSR0Yq5mix25YiYt1C47y9BbihCg4-8C94QoJg7OfEzuHCSM_uRZNP_L3HQf9dem1_1-KIglesoDUnjGLHXNlGgRkyH735dE08oDH6Z9ha2xW4nZzASTn14v8mv5y");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://fcm.googleapis.com/fcm/send")
                .addJSONObjectBody(jsonBody)
                .addHeaders(params)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("OKHTTPCLIENT", response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("OKHTTPCLIENT", error.getErrorDetail());
                    }
                });
    }

//    public PushNotification(String userToken) {
//        Context context = null;
//        try {
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            String URL = "https://fcm.googleapis.com/fcm/send";
//
//            JSONObject dataJson = new JSONObject();
//            dataJson.put("title", "Prism Notification");
//            dataJson.put("message", "This is a Prism notification, make sense?");
//
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("to", userToken);     // TODO programmatically populate this
//            jsonBody.put("data", dataJson);
//
//            final String requestBody = jsonBody.toString();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.i("VOLLEY", response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("VOLLEY", error.toString());
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    try {
//                        return requestBody == null ? null : requestBody.getBytes("utf-8");
//                    } catch (UnsupportedEncodingException uee) {
//                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                        return null;
//                    }
//                }
//
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("Content-Type", "application/json charset=utf-8");
//                    params.put("Authorization", "key=" + context.getResources().getString(R.string.firebase_cloud_messaging_server_key));
//                    return params;
//                }
//
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
//            };
//
//            requestQueue.add(stringRequest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
}
