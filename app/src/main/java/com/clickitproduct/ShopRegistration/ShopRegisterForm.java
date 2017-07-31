package com.clickitproduct.ShopRegistration;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.SQLite.DataBaseConnection;
import com.clickitproduct.User_Profile.Profile_new;
import com.clickitproduct.activities.MapsActivity;
import com.clickitproduct.activities.common_variable;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShopRegisterForm extends AppCompatActivity
{
    int editShop = 0;
    String Shop_Id;
    DataBaseConnection objDb;
    EditText etShopName, etMob, etEmail, etMyTags, etLat, etLon;
    Button btnRegister;
    public static String shopname, mobile, email, category, latitude, longitude;
    ListView dialog_ListView;
    long[] stdrno;// used for taking roll numbers
    private Calendar myCalendar;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private int hour;
    private int minute;
    private TimePicker timePicker1;
    static final int TIME_DIALOG_ID = 999;
    int time_flag;
    public static String lat = "", lon = "";
    private GoogleApiClient client;
    View focusView = null;
    boolean cancel = false;
    int home_delivery = 0;
    String shop_cats[] = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_register_form);
        objDb = new DataBaseConnection(ShopRegisterForm.this);
        etShopName = (EditText) findViewById(R.id.etShopName);
        etMob = (EditText) findViewById(R.id.etMob);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMyTags = (EditText) findViewById(R.id.etMyTags);
        etLat = (EditText) findViewById(R.id.etLat);
        etLon = (EditText) findViewById(R.id.etLon);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        ImageView btnMap = (ImageView) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ShopRegisterForm.this, MapsActivity.class);
                startActivity(i);
            }
        });

        etMyTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    final ProgressDialog dialog = new ProgressDialog(ShopRegisterForm.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    JsonObject jsonParam = new JsonObject();
                    Ion.with(ShopRegisterForm.this)
                    .load(common_variable.main_web_url+"/admin/getshopcategory")
                    .progressDialog(dialog)
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                dialog.dismiss();
                                JsonArray Shop_Category = result.getAsJsonArray("data");
                                shop_cats = new String[Shop_Category.size()];
                                for (int c=0; c<Shop_Category.size(); c++)
                                {
                                    JsonObject objCat = (JsonObject) Shop_Category.get(c);
                                    shop_cats[c] = objCat.get("category_name").toString().replaceAll("^\"|\"$", "");
                                }

                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ShopRegisterForm.this);
                                LayoutInflater inflater = ShopRegisterForm.this.getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.list_view, null);
                                dialogBuilder.setView(dialogView);

                                dialogBuilder.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                stdrno = dialog_ListView.getCheckItemIds();
                                                String cats = "";
                                                for (int i = 0; i < stdrno.length; i++) {
                                                    cats = cats + dialog_ListView.getItemAtPosition((int) stdrno[i]);
                                                    if (i != stdrno.length - 1)
                                                        cats = cats + ", ";
                                                }
                                                etMyTags.setText(cats);
                                                dialog.dismiss();
                                            }
                                        });

                                dialogBuilder.setNegativeButton("cancel",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.show();

                                dialog_ListView = (ListView) alertDialog.findViewById(R.id.listView);
                                dialog_ListView.setChoiceMode(dialog_ListView.CHOICE_MODE_MULTIPLE);
                                dialog_ListView.setAdapter(new ArrayAdapter<String>(ShopRegisterForm.this, android.R.layout.simple_list_item_checked, shop_cats));
                            }
                            catch (Exception e2){}
                        }
                    });
                }
                catch (Exception e)
                {}
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shopname = etShopName.getText().toString();
                mobile = etMob.getText().toString();
                email = etEmail.getText().toString();
                category = etMyTags.getText().toString();
                latitude = etLat.getText().toString();
                longitude = etLon.getText().toString();

               validation();
            }
        });
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        editShop = getIntent().getIntExtra("editShop", 0);
        if(editShop == 1)
        {
            btnRegister.setText("Update");
            JsonObject jShopInfo = Profile_new.shopInfo;

            Log.e("sssssssss", Profile_new.shopInfo+"");

            JsonObject jShopAddress = (JsonObject) jShopInfo.get("address");

            Shop_Id = jShopInfo.get("_id").toString().replaceAll("^\"|\"$", "");
            etShopName.setText(jShopInfo.get("shop_name").toString().replaceAll("^\"|\"$", ""));
            etMob.setText(jShopInfo.get("mobile").toString().replaceAll("^\"|\"$",""));
            etEmail.setText(jShopInfo.get("email").toString().replaceAll("^\"|\"$",""));

            String location = jShopAddress.get("location").toString().replaceAll("^\"|\"$","");
            location = location.substring(1,location.length()-1);
            String []loc = location.split(",");

            lat = loc[0];
            lon = loc[1];

            etLat.setText(lat);
            etLon.setText(lon);

            String[] temp_cate = jShopInfo.get("category").toString().split(",");
            String cate="";

            for (int p=0; p<temp_cate.length; p++){
                cate = cate + temp_cate[p].replaceAll("^\"|\"$","");
                if (p != temp_cate.length - 1)
                    cate = cate + ",";
            }

            etMyTags.setText(cate.substring(1,cate.length()-1).replaceAll("^\"|\"$",""));
        }
    }

    public void validation()
    {
        if (shopname.equals(""))
        {
            etShopName.requestFocus();
            etShopName.setError("Enter shop name");
            focusView = etShopName;
            cancel = true;
        }
        else if (isMobileValid(mobile))
        {
            etMob.requestFocus();
            etMob.setError("Enter valid mobile no.");
            focusView = etMob;
            cancel = true;
        }
        else if (!isEmailValid(email))
        {
            etEmail.requestFocus();
            etEmail.setError("Enter valid email id");
            focusView = etEmail;
            cancel = true;
        }
        else if (category.equals(""))
        {
            etMyTags.setError("Enter category name");
            etMyTags.requestFocus();
            focusView = etMyTags;
            cancel = true;
        }
        else if (latitude.equals("") && longitude.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Select Your Location", Toast.LENGTH_SHORT).show();
        }
        else
        {
            JsonObject jsonParam = new JsonObject();

            JSONArray jCategory = new JSONArray();

            String[] cats = category.split(",");

            for (int j = 0; j < cats.length; j++)
            {
                jCategory.put(cats[j]);
            }

            final ProgressDialog dialog = new ProgressDialog(ShopRegisterForm.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();

            if(editShop == 1)
            {
                jsonParam.addProperty("_id", Shop_Id);
            }
            jsonParam.addProperty("user_id", common_variable.User_id);
            jsonParam.addProperty("ownername", common_variable.User_Name);
            jsonParam.addProperty("shopname", shopname.trim());
            jsonParam.addProperty("mobile", mobile);
            jsonParam.addProperty("email", email);
            jsonParam.addProperty("category", category);
            jsonParam.addProperty("latitude", latitude);
            jsonParam.addProperty("longitude", longitude);
            jsonParam.addProperty("home_delivery", home_delivery);
            jsonParam.addProperty("platform", "1");

            String shop_url = "";

            if(editShop == 0)
            {
                shop_url = "/shop/shop_registration";
            }
            else
            {
                shop_url = "/shop/updateshop";
            }
            Log.e("123456789","ShopEdit : "+jsonParam.toString());

            Ion.with(getApplicationContext())
            .load(common_variable.main_web_url+shop_url)
            .progressDialog(dialog)
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    dialog.dismiss();
                    if(editShop == 0)
                    {
                        common_variable.IsShopAvailable = 1;
                        Toast.makeText(ShopRegisterForm.this, "Your shop regitered successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ShopRegisterForm.this, "Your shop updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    common_variable.Refresh_Shop = 1;
                    finish();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isMobileValid(String mobile) {
        //TODO: Replace this with your own logic
        return mobile.length() < 10 ;
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        etLat.setText(lat + "");
        etLon.setText(lon + "");
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction()
    {
        Thing object = new Thing.Builder()
                .setName("ShopRegisterForm Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
