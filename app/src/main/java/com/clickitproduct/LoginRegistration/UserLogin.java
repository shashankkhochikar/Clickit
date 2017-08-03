package com.clickitproduct.LoginRegistration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.SQLite.DataBaseConnection;
import com.clickitproduct.activities.Main_Activity;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class UserLogin extends AppCompatActivity
{
    EditText etUserName, etPass;
    String username, password;
    private TextView tvNewUser, bLogIn, forpass;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    DataBaseConnection objDb;
    public static EditText etOtp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        objDb = new DataBaseConnection(UserLogin.this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        common_variable.forgot_password = 1;

        objDb.open();
        common_variable.FCM_Token = objDb.getFCM_ID();
        objDb.close();

        etUserName = (EditText)findViewById(R.id.etUserName);
        etPass = (EditText)findViewById(R.id.etPass);
        tvNewUser = (TextView)findViewById(R.id.tvNewUser);
        bLogIn = (TextView) findViewById(R.id.bLogIn);
        forpass = (TextView) findViewById(R.id.forpass);

//////////////////////////////////////////////////////VERSION CHECK //////////////////////////////////////////////////////////////////////
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;
        Log.e("123","\n"+versionNumber+"\n"+versionName);

        JsonObject jsonParam11 = new JsonObject();
        Ion.with(getApplicationContext())
        .load(common_variable.main_web_url + "/user/checkversion")
        .setJsonObjectBody(jsonParam11)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result)
            {
                try {
                    if (versionNumber >= Integer.parseInt(result.get("version").toString())) {
                        Log.e("12345", "great");
                    } else {
                        Log.e("12345", "Less");
                        AlertDialog.Builder alert = new AlertDialog.Builder(UserLogin.this);
                        alert.setTitle("Update Application");
                        alert.setMessage("For further use of this application Please update application");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                finish();
                            }
                        });
                        alert.show();
                    }
                }catch (Exception x){}
            }
        });

//////////////////////////////////////////////////////VERSION CHECK //////////////////////////////////////////////////////////////////////


        tvNewUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
                Intent i = new Intent(UserLogin.this, UserRegister.class);
                startActivity(i);
            }
        });

        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        forpass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater li = LayoutInflater.from(UserLogin.this);
                final View prompt = li.inflate(R.layout.forget_password, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(UserLogin.this, R.style.MyAlertTheme));
                alertDialogBuilder.setView(prompt);
                alertDialogBuilder.setCancelable(true);
                final AlertDialog dialog = alertDialogBuilder.show();

                final EditText etMob = (EditText) prompt.findViewById(R.id.etMob);
                Button btnGetPass = (Button) prompt.findViewById(R.id.btnGetPass);
                etMob.setText(etUserName.getText().toString());

                btnGetPass.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(isNetworkConnected()) {
                            if (etMob.length() < 10) {
                                Toast.makeText(getApplicationContext(), "Enter valid mobile no.", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                final ProgressDialog dialog = new ProgressDialog(UserLogin.this);
                                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                dialog.setMessage("Loading...");
                                dialog.show();

                                JsonObject jsonParam = new JsonObject();
                                jsonParam.addProperty("mobile", etMob.getText().toString());
                                jsonParam.addProperty("platform", "" + 1);

                                Ion.with(getApplicationContext())
                                .load(common_variable.main_web_url + "/user/sendotp")
                                .progressDialog(dialog)
                                .setJsonObjectBody(jsonParam)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        dialog.dismiss();
                                        try {
                                            String success = result.get("success").toString();
                                            if (success.equals("false")) {
                                                Toast.makeText(getApplicationContext(), "Your mobile number is not registered", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "OTP has been sent to your mobile", Toast.LENGTH_SHORT).show();
                                                LayoutInflater li = LayoutInflater.from(UserLogin.this);
                                                final View prompt = li.inflate(R.layout.otp_confirm_pasword, null);

                                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(UserLogin.this, R.style.MyAlertTheme));
                                                alertDialogBuilder.setView(prompt);
                                                alertDialogBuilder.setCancelable(false);
                                                final AlertDialog dialog = alertDialogBuilder.show();

                                                etOtp = (EditText) prompt.findViewById(R.id.etOtp);
                                                final EditText etPass = (EditText) prompt.findViewById(R.id.etPass);
                                                final EditText etConPass = (EditText) prompt.findViewById(R.id.etConPass);
                                                TextView tvReset = (TextView) prompt.findViewById(R.id.tvReset);
                                                TextView tvCancel = (TextView) prompt.findViewById(R.id.tvCancel);

                                                tvCancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                tvReset.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (etOtp.getText().toString().equals("")) {
                                                            Toast.makeText(getApplicationContext(), "Enter OTP", Toast.LENGTH_SHORT).show();
                                                        } else if (etPass.getText().toString().equals("")) {
                                                            Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                                                        } else if (etConPass.getText().toString().equals("")) {
                                                            Toast.makeText(getApplicationContext(), "Enter confirm password", Toast.LENGTH_SHORT).show();
                                                        } else if (!etPass.getText().toString().equals(etConPass.getText().toString())) {
                                                            Toast.makeText(getApplicationContext(), "Enter same password", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            dialog.dismiss();

                                                            final ProgressDialog dialog1 = new ProgressDialog(UserLogin.this);
                                                            dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                            dialog1.setMessage("Loading...");
                                                            dialog1.show();

                                                            JsonObject jsonParam = new JsonObject();
                                                            jsonParam.addProperty("otp", etOtp.getText().toString());
                                                            jsonParam.addProperty("password", etPass.getText().toString());
                                                            jsonParam.addProperty("confirmpassword", etConPass.getText().toString());
                                                            jsonParam.addProperty("platform", "" + 1);

                                                            Ion.with(getApplicationContext())
                                                                    .load(common_variable.main_web_url + "/user/changepassword")
                                                                    .progressDialog(dialog1)
                                                                    .setJsonObjectBody(jsonParam)
                                                                    .asJsonObject()
                                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                                        @Override
                                                                        public void onCompleted(Exception e, JsonObject result) {
                                                                            String success = result.get("success").toString();
                                                                            Log.e("123", "response :" + result.toString());

                                                                            if (success.equals("true")) {
                                                                                dialog1.dismiss();
                                                                                Toast.makeText(getApplicationContext(), "your pasword changed successfully", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Toast.makeText(getApplicationContext(), "something is wrong", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }

                                                });
                                            }
                                        }
                                        catch (Exception ec){}
                                    }
                                });
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS} ,1);
            }
            return;
        }
    }

    private void attemptLogin()
    {
        // Reset errors.
        etUserName.setError(null);
        etPass.setError(null);

        // Store values at the time of the login attempt.
        username = etUserName.getText().toString();
        password = etPass.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
       if (TextUtils.isEmpty(username) || username.length() < 10)
       {
            etUserName.setError(getString(R.string.error_invalid_mobile));
           etUserName.requestFocus();
            focusView = etUserName;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password) || password.length() < 4)
        {
            etPass.setError(getString(R.string.error_invalid_password));
            etPass.requestFocus();
            focusView = etPass;
            cancel = true;
        }

        if (cancel) {
        }
        else
        {
            if(isNetworkConnected())
            {
                final ProgressDialog dialog = new ProgressDialog(UserLogin.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Loading...");
                dialog.show();

                JsonObject jsonParamLogin = new JsonObject();
                jsonParamLogin.addProperty("password", password.trim());
                jsonParamLogin.addProperty("username", username.trim());

                Log.e("12345 User Login", jsonParamLogin.toString() + "");

                Ion.with(getApplicationContext())
                .load(common_variable.main_web_url + "/user/login")
                .progressDialog(dialog)
                .setJsonObjectBody(jsonParamLogin)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            dialog.dismiss();
                            String success = result.get("success").toString();
                            Log.e("12345 User Login", result.toString() + "");
                            if (success.equals("true"))
                            {
                                common_variable.UserDetail = result.getAsJsonObject("data");

                                objDb.open();
                                objDb.updateUser_ID(common_variable.UserDetail.get("_id").toString().replaceAll("^\"|\"$", ""));
                                objDb.close();

                                editor = sharedpreferences.edit();
                                editor.putString("profile_pic_url", result.get("profilepicdir").toString().replaceAll("^\"|\"$", ""));
                                editor.putString("user_id", common_variable.UserDetail.get("_id").toString().replaceAll("^\"|\"$", ""));
                                editor.putString("email", common_variable.UserDetail.get("email").toString().replaceAll("^\"|\"$", ""));
                                editor.putString("mobile", common_variable.UserDetail.get("mobile").toString().replaceAll("^\"|\"$", ""));
                                editor.putString("first_name", common_variable.UserDetail.get("first_name").toString().replaceAll("^\"|\"$", ""));
                                editor.putString("last_name", common_variable.UserDetail.get("last_name").toString().replaceAll("^\"|\"$", ""));
                                try {
                                    editor.putString("fcm_id", common_variable.UserDetail.get("fcm_id").toString().replaceAll("^\"|\"$", ""));
                                } catch (Exception e1) {
                                    editor.putString("fcm_id", "");
                                }

                                try {
                                    if (!common_variable.UserDetail.get("profile_pic").toString().equals(""))
                                    {
                                        if(common_variable.UserDetail.get("profile_pic").toString().replaceAll("^\"|\"$", "").contains(".jpg")) {
                                            editor.putString("profile_pic", common_variable.UserDetail.get("profile_pic").toString().replaceAll("^\"|\"$", ""));
                                        }
                                        else{
                                            editor.putString("profile_pic", common_variable.UserDetail.get("profile_pic").toString().replaceAll("^\"|\"$", "")+".jpg");
                                        }
                                    }
                                } catch (Exception e2) {
                                    editor.putString("profile_pic", "");
                                }
                                editor.commit();

                                JsonObject jsonParamLogin = new JsonObject();
                                jsonParamLogin.addProperty("user_id", common_variable.UserDetail.get("_id").toString().replaceAll("^\"|\"$", ""));
                                jsonParamLogin.addProperty("fcm_id", common_variable.FCM_Token);
                                jsonParamLogin.addProperty("platform", "1");

                                Ion.with(getApplicationContext())
                                .load(common_variable.main_web_url + "/user/user_update")
                                .setJsonObjectBody(jsonParamLogin)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {       }
                                });

                                if (success.equals("true")) {
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), Main_Activity.class));
                                }
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "wrong username or password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();

                        }
                    }
                });
            }
            else
            {
                Toast.makeText(UserLogin.this, "Enable Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
