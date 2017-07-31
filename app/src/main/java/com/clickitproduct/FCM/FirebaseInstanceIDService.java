package com.clickitproduct.FCM;

import android.util.Log;

import com.clickitproduct.SQLite.DataBaseConnection;
import com.clickitproduct.activities.common_variable;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService
{
    private static final String TAG = "FirebaseIIDService";
    DataBaseConnection objDb;

    @Override
    public void onTokenRefresh()
    {
        objDb = new DataBaseConnection(getApplicationContext());
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        common_variable.FCM_Token = refreshedToken;

        objDb.open();
        objDb.updateFCM_ID(refreshedToken);
        objDb.close();

        Log.i(TAG, "Refreshed token: " + refreshedToken+"////"+common_variable.FCM_Token);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token)
    {
        objDb.open();
        String user_id = objDb.getUSER_ID();
        objDb.close();

        if (user_id != "0")
        {
            JsonObject jsonParamLogin = new JsonObject();
            jsonParamLogin.addProperty("user_id", user_id);
            jsonParamLogin.addProperty("fcmid", token);
            jsonParamLogin.addProperty("platform", "1");

            Log.e("123", "paramssss :" + jsonParamLogin);

            Ion.with(getApplicationContext())
            .load(common_variable.main_web_url + "/user/user_update")
            .setJsonObjectBody(jsonParamLogin)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {  }
            });
        }

    }
}
