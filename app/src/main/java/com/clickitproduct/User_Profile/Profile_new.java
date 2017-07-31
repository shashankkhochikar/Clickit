package com.clickitproduct.User_Profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.Beans.ShopBean;
import com.clickitproduct.Coupens.AddCoupons;
import com.clickitproduct.Offer.Add_Offers;
import com.clickitproduct.Products.AddProducts;
import com.clickitproduct.R;
import com.clickitproduct.ShopRegistration.ShopRegisterForm;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;
import static com.clickitproduct.activities.common_variable.main_web_url;

public class Profile_new extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    ImageView user_profile_pic;
    TextView user_profile_name, user_profile_details;
    FloatingActionButton fabEditProfile;
    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static String user_profile_image, first_name, last_name, email_id, mobile_no;
    String image="";
    ImageView dialog_profile_img;
    SharedPreferences.Editor editor;
    int changed_image = 0;
    Dialog main_dialog;
//    TextView txtResetPassword;
    public static EditText etOtp;
    public static int CAMERA_PREVIEW_RESULT = 1;
    RecyclerView recycle_view_profile_menues;

    public static JsonObject shopInfo,jShopImgs;
    public String Shop_Name;
    public static String Shop_loc = "", Shop_cat = "";
    public ArrayList shopImagesArray;
    public JsonObject jsonParam;
    CollapsingToolbarLayout collapsing_layout;
    Toolbar toolbar;
    ImageView custom_toolbar_icon;
    TextView custom_toolbar_title;

    String []profile_menu = new String[]{
            "Add Product",
            "Add Offer",
            "Add Coupon",
            "My Classifieds",
            "My Services",
            "Grabbed Coupons",
            "Edit Shop"};
    Integer []profile_menuIcon = new Integer[]
            {
                R.drawable.ic_products_black,
                R.drawable.ic_offer_gray_new,
                R.drawable.ic_coupons,
                R.drawable.ic_classified_gray,
                R.drawable.ic_service_logo,
                R.drawable.ic_coupons,
                R.drawable.ic_edit  };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
//        setContentView(R.layout.activity_new_prof);

//        txtResetPassword = (TextView)findViewById(R.id.user_profile_resetPassword);
        user_profile_pic = (ImageView)findViewById(R.id.user_profile_pic);
        user_profile_name = (TextView)findViewById(R.id.user_profile_name);
        user_profile_details = (TextView)findViewById(R.id.user_profile_details);
        fabEditProfile = (FloatingActionButton)findViewById(R.id.fabEditProfile);
        recycle_view_profile_menues = (RecyclerView)findViewById(R.id.recycle_view_profile_menues);
        collapsing_layout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_layout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        custom_toolbar_icon = (ImageView)toolbar.findViewById(R.id.custom_toolbar_icon);
        custom_toolbar_title = (TextView)toolbar.findViewById(R.id.custom_toolbar_title);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        user_profile_image = ""+main_web_url+"/"+sharedpreferences.getString("profile_pic_url",null).replaceAll("^\"|\"$", "")+sharedpreferences.getString("profile_pic",null).replaceAll("^\"|\"$", "")+" ";
        first_name = sharedpreferences.getString("first_name",null).replaceAll("^\"|\"$","");
        last_name = sharedpreferences.getString("last_name",null).replaceAll("^\"|\"$","");
        email_id = sharedpreferences.getString("email",null).replaceAll("^\"|\"$","");
        mobile_no = sharedpreferences.getString("mobile",null).replaceAll("^\"|\"$","");

        /*Picasso.with(Profile_new.this).load(user_profile_image).transform(new CircleTransform()).into(user_profile_pic);
        Picasso.with(Profile_new.this).load(user_profile_image).transform(new CircleTransform()).into(custom_toolbar_icon);*/

        Ion.with(Profile_new.this)
                .load(user_profile_image)
                .withBitmap()
                .placeholder(R.raw.loading)
                .error(R.drawable.error)
                .animateLoad(R.anim.zoom_in)
                .animateIn(R.anim.fade_in)
                .intoImageView(user_profile_pic);
        Ion.with(Profile_new.this)
                .load(user_profile_image)
                .withBitmap()
                .placeholder(R.raw.loading)
                .error(R.drawable.error)
                .animateLoad(R.anim.zoom_in)
                .animateIn(R.anim.fade_in)
                .intoImageView(custom_toolbar_icon);

        custom_toolbar_title.setText(" "+first_name+" "+last_name);

        collapsing_layout.setTitle(" "+first_name+" "+last_name);

        user_profile_name.setText(" "+first_name+" "+last_name);
        user_profile_details.setText("Email : "+email_id+"\n Mobile : "+mobile_no);

        refreshShopData();

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(Profile_new.this);

        /*txtResetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });*/

        fabEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                main_dialog=new Dialog(Profile_new.this,android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                main_dialog.setContentView(R.layout.activity_user_profile_edit);

                dialog_profile_img = (ImageView) main_dialog.findViewById(R.id.profile_img);
                final EditText userFirstName = (EditText)main_dialog.findViewById(R.id.userFirstName);
                final EditText userLastName = (EditText)main_dialog.findViewById(R.id.userLastName);
                final EditText userMobile = (EditText)main_dialog.findViewById(R.id.userMobile);
                final EditText userEmail = (EditText)main_dialog.findViewById(R.id.userEmail);
                Button btnUpdate = (Button) main_dialog.findViewById(R.id.btnUpdate);

                final ImageView editUserProfile = (ImageView)main_dialog.findViewById(R.id.editUserProfile);

                editUserProfile.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        //cameraGalleryDialog();
                        SettingsList settingsList = new SettingsList();
                        settingsList
                                // Set custom camera export settings
                                .getSettingsModel(CameraSettings.class)
                                .setExportDir(Directory.PICTURES, ".clickit")
                                .setExportPrefix("camera_")
                                // Set custom editor export settings
                                .getSettingsModel(EditorSaveSettings.class)
                                .setExportDir(Directory.PICTURES, ".clickit")
                                .setExportPrefix("result_")
                                .setSavePolicy(EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT);

                        new CameraPreviewBuilder(Profile_new.this)
                                .setSettingsList(settingsList)
                                .startActivityForResult(Profile_new.this, CAMERA_PREVIEW_RESULT);

                    }
                });


                //Picasso.with(Profile_new.this).load(user_profile_image).transform(new CircleTransform()).into(dialog_profile_img);
                Ion.with(Profile_new.this)
                        .load(user_profile_image)
                        .withBitmap()
                        .placeholder(R.raw.loading)
                        .error(R.drawable.error)
                        .animateLoad(R.anim.zoom_in)
                        .animateIn(R.anim.fade_in)
                        .intoImageView(dialog_profile_img);

                userFirstName.setText(first_name);
                userLastName.setText(last_name);
                userMobile.setText(mobile_no);
                userEmail.setText(email_id);

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(userFirstName.getText().toString().equals(""))
                        {
                            userFirstName.requestFocus();
                            userFirstName.setError("Enter first name");
                        }
                        else if(userLastName.getText().toString().equals(""))
                        {
                            userLastName.requestFocus();
                            userLastName.setError("Enter last name");
                        }
                        else if(userMobile.getText().toString().equals("") && userMobile.getText().toString().length()<9)
                        {
                            userMobile.requestFocus();
                            userMobile.setError("Enter valid mobile no.");
                        }
                        else if(userEmail.getText().toString().equals("") && !userEmail.getText().toString().contains("@"))
                        {
                            userEmail.requestFocus();
                            userEmail.setError("Enter valid Email");
                        }
                        else
                        {
                            JsonObject jsonParamLogin = new JsonObject();
                            jsonParamLogin.addProperty("user_id", sharedpreferences.getString("user_id", "").toString().replaceAll("^\"|\"$",""));
                            jsonParamLogin.addProperty("firstname", userFirstName.getText().toString());
                            jsonParamLogin.addProperty("lastname", userLastName.getText().toString());
                            jsonParamLogin.addProperty("mobile", userMobile.getText().toString());
                            jsonParamLogin.addProperty("email", userEmail.getText().toString());
                            if(changed_image == 1) {
                                jsonParamLogin.addProperty("profilepic", image);
                            }
                            jsonParamLogin.addProperty("platform","1");

                            final ProgressDialog dialog = new ProgressDialog(Profile_new.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("Loading...");
                            dialog.show();

                            Ion.with(Profile_new.this)
                            .load(common_variable.main_web_url+"/user/user_update")
                            .progressDialog(dialog)
                            .setJsonObjectBody(jsonParamLogin)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>()
                            {
                                @Override
                                public void onCompleted(Exception e, JsonObject result)
                                {
                                    dialog.dismiss();
                                    if(result.get("success").toString().equals("true"))
                                    {
                                        dialog.dismiss();
                                        main_dialog.dismiss();
                                        changed_image = 0;

                                        try
                                        {
                                            JsonObject jsonParamLogin1 = new JsonObject();
                                            jsonParamLogin1.addProperty("id", sharedpreferences.getString("user_id", null).replaceAll("^\"|\"$",""));
                                            Ion.with(getApplicationContext())
                                                    .load(common_variable.main_web_url + "/user/logbyid")
                                                    .setJsonObjectBody(jsonParamLogin1)
                                                    .asJsonObject()
                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                        @Override
                                                        public void onCompleted(Exception e, JsonObject result)
                                                        {
                                                            common_variable.changed_profile_detail = 1;
                                                            JsonArray tmp = result.getAsJsonArray("user");
                                                            for(int i=0;i<tmp.size();i++)
                                                            {
                                                                JsonObject obj = (JsonObject) tmp.get(i);

                                                                editor = sharedpreferences.edit();
                                                                editor.putString("user_id", obj.get("_id").toString());
                                                                editor.putString("email", obj.get("email").toString());
                                                                editor.putString("mobile", obj.get("mobile").toString());
                                                                editor.putString("first_name", obj.get("first_name").toString());
                                                                editor.putString("last_name", obj.get("last_name").toString());
                                                                try
                                                                {   editor.putString("fcm_id", obj.get("fcm_id").toString());    }
                                                                catch(Exception e1)
                                                                {   editor.putString("fcm_id", "");     }

                                                                try
                                                                {
                                                                    if (!obj.get("profile_pic").toString().equals(""))
                                                                    {
                                                                        if(obj.get("profile_pic").toString().replaceAll("^\"|\"$","").contains(".jpg"))
                                                                        {
                                                                            editor.putString("profile_pic", obj.get("profile_pic").toString().replaceAll("^\"|\"$",""));
                                                                        }
                                                                        else
                                                                        {
                                                                            editor.putString("profile_pic", obj.get("profile_pic").toString().replaceAll("^\"|\"$","")+".jpg");
                                                                        }

                                                                    }
                                                                }
                                                                catch(Exception e2)
                                                                {   editor.putString("profile_pic", "");    }
                                                                editor.commit();

                                                                sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

                                                                user_profile_image = ""+main_web_url+"/"+sharedpreferences.getString("profile_pic_url",null).replaceAll("^\"|\"$", "")+sharedpreferences.getString("profile_pic",null).replaceAll("^\"|\"$", "")+" ";
                                                                first_name = sharedpreferences.getString("first_name",null).replaceAll("^\"|\"$","");
                                                                last_name = sharedpreferences.getString("last_name",null).replaceAll("^\"|\"$","");
                                                                email_id = sharedpreferences.getString("email",null).replaceAll("^\"|\"$","");
                                                                mobile_no = sharedpreferences.getString("mobile",null).replaceAll("^\"|\"$","");

                                                                //Picasso.with(Profile_new.this).load(user_profile_image).placeholder(R.drawable.ic_user).transform(new CircleTransform()).into(dialog_profile_img);
                                                                Ion.with(Profile_new.this)
                                                                        .load(user_profile_image)
                                                                        .withBitmap()
                                                                        .placeholder(R.raw.loading)
                                                                        .error(R.drawable.error)
                                                                        .animateLoad(R.anim.zoom_in)
                                                                        .animateIn(R.anim.fade_in)
                                                                        .intoImageView(dialog_profile_img);

                                                                user_profile_name.setText(" "+first_name+" "+last_name);
                                                                user_profile_details.setText("Email : "+email_id+"\n Mobile : "+mobile_no);
                                                            }
                                                        }
                                                    });
                                        }
                                        catch (Exception ee){}
//                                        Toast.makeText(Profile_new.this, "Change Will take Affect after restart app", Toast.LENGTH_LONG).show();
                                    }
                                }
                                });
                        }
                    }
                });

                main_dialog.show();
            }
        });

        if(common_variable.User_Shop_ID.equals(""))
        {
            JsonObject jsonParamLogin = new JsonObject();
            jsonParamLogin.addProperty("user_id", common_variable.User_id);
            jsonParamLogin.addProperty("platform", "1");

            Ion.with(getApplicationContext())
            .load(common_variable.main_web_url + "/shop/shop_check")
            .setJsonObjectBody(jsonParamLogin)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try
                    {
                        JsonArray data = (JsonArray) result.get("data");
                        if (data != null)
                        {
                            if (result.get("success").toString().equals("true"))
                            {
                                ShopBean objShop;
                                for (int i = 0; i < data.size(); i++)
                                {
                                    JsonObject obj = (JsonObject) data.get(i);
                                    common_variable.User_Shop_ID = obj.get("_id").toString().replaceAll("^\"|\"$", "");
                                    common_variable.User_Shop_Name = obj.get("shop_name").toString().replaceAll("^\"|\"$", "");

                                    refreshShopData();

                                    String[] temp_cate = obj.get("category").toString().substring(1,obj.get("category").toString().length()-1).replaceAll("^\"|\"$","").split(",");

                                    for (int p=0; p<temp_cate.length; p++)
                                    {
                                        common_variable.User_Shop_Category = common_variable.User_Shop_Category + temp_cate[p].replaceAll("^\"|\"$","");
                                        if (p != temp_cate.length - 1)
                                            common_variable.User_Shop_Category = common_variable.User_Shop_Category + ",";
                                    }
                                }
                                profile_menu = new String[]{
                                        "Add Product",
                                        "Add Offer",
                                        "Add Coupon",
                                        "My Classifieds",
                                        "My Services",
                                        "Grabbed Coupons",
                                        "Edit Shop",
                                        "Change Password"};
                                profile_menuIcon = new Integer[]{
                                        R.drawable.ic_products_black,
                                        R.drawable.ic_offer_gray_new,
                                        R.drawable.ic_coupons,
                                        R.drawable.ic_classified_gray,
                                        R.drawable.ic_service_logo,
                                        R.drawable.ic_coupons,
                                        R.drawable.ic_edit,
                                        R.drawable.ic_policy};
                            }
                            else {
                                profile_menu = new String[]{
                                        "My Classifieds",
                                        "My Services",
                                        "My Coupons",
                                        "Change Password"};
                                profile_menuIcon = new Integer[]{
                                        R.drawable.ic_classified_gray,
                                        R.drawable.ic_service_logo,
                                        R.drawable.ic_coupons,
                                        R.drawable.ic_policy};
                            }
                            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(profile_menu, profile_menuIcon);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(Profile_new.this);
                            recycle_view_profile_menues.setLayoutManager(mLayoutManager);
                            recycle_view_profile_menues.setItemAnimator(new DefaultItemAnimator());
                            recycle_view_profile_menues.setAdapter(mAdapter);

                        }
                    } catch (Exception ec) {}
                }
           });
        }
        else
        {
            profile_menu = new String[]{
                    "Add Product",
                    "Add Offer",
                    "Add Coupon",
                    "My Classifieds",
                    "My Services",
                    "Grabbed Coupons",
                    "Edit Shop",
                    "Change Password"};
            profile_menuIcon = new Integer[]{
                    R.drawable.ic_products_black,
                    R.drawable.ic_offer_gray_new,
                    R.drawable.ic_coupons,
                    R.drawable.ic_classified_gray,
                    R.drawable.ic_service_logo,
                    R.drawable.ic_coupons,
                    R.drawable.ic_edit,
                    R.drawable.ic_policy};

            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(profile_menu, profile_menuIcon);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(Profile_new.this);
            recycle_view_profile_menues.setLayoutManager(mLayoutManager);
            recycle_view_profile_menues.setItemAnimator(new DefaultItemAnimator());
            recycle_view_profile_menues.setAdapter(mAdapter);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset)
    {
        if (offset == 0)
        {
            // Collapsed
            Log.e("1111111111", "Collapsed");
            toolbar.setVisibility(View.GONE);
        }
        else
        {
            // Not collapsed
            Log.e("1111111111", "Not Collapsed");
            toolbar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT)
        {
            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);

            if (resultPath != null)
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(resultPath,bmOptions);
                dialog_profile_img.setImageBitmap(bitmap);
                image = BitMapToString(bitmap);
                //new setImage().execute(bitmap);
            }

            if (sourcePath != null)
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(resultPath,bmOptions);
                dialog_profile_img.setImageBitmap(bitmap);
                image = BitMapToString(bitmap);
                //new setImage().execute(bitmap);
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PREVIEW_RESULT && data != null)
        {   } else {    finish();   }
    }

    public String BitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private String[] menuList;
        private Integer[] menuIconList;
        private String strItem;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tvMenuName;
            ImageView etMenuIcon;

            public RecyclerViewHolder(View itemView)
            {
                super(itemView);
                tvMenuName = (TextView) itemView.findViewById(R.id.tvMenuName);
                etMenuIcon  =(ImageView) itemView.findViewById(R.id.etMenuIcon);
            }
        }

        public RecyclerViewAdapter(String[] menuList, Integer[] menuIconList)
        {
            this.menuList = menuList;
            this.menuIconList = menuIconList;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_profile_menu, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position)
        {
            holder.tvMenuName.setText(menuList[position]);
            holder.etMenuIcon.setImageDrawable(getResources().getDrawable(menuIconList[position]));
            holder.etMenuIcon.setColorFilter(getResources().getColor(R.color.Gray));

            holder.tvMenuName.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    strItem = (String) holder.tvMenuName.getText();
                    switch (strItem)
                    {
                        case "Add Product" : startActivity(new Intent(Profile_new.this, AddProducts.class));
                            break;
                        case "Add Offer" :   startActivity(new Intent(Profile_new.this, Add_Offers.class));
                            break;
                        case "Add Coupon" :  startActivity(new Intent(Profile_new.this, AddCoupons.class));
                            break;
                        case "My Classifieds" :
                                            jsonParam = new JsonObject();
                                            jsonParam.addProperty("user_id", common_variable.User_id);
                                            jsonParam.addProperty("platform", "1");

                                            Ion.with(getApplicationContext())
                                                    .load(common_variable.main_web_url+"/classified/classified_check")
                                                    .setJsonObjectBody(jsonParam)
                                                    .asJsonObject()
                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                        @Override
                                                        public void onCompleted(Exception e, JsonObject result) {
                                                            try {
                                                                if (result.get("success").toString().equals("true")) {
                                                                    startActivity(new Intent(getApplicationContext(), UserClassifieds.class));
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "No Classifieds Available", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            catch (Exception ec){}
                                                        }
                                                    });
                                            break;
                        case "My Services" :
                                            jsonParam = new JsonObject();
                                            jsonParam.addProperty("user_id", common_variable.User_id);
                                            jsonParam.addProperty("platform", "1");

                                            Ion.with(getApplicationContext())
                                                    .load(common_variable.main_web_url+"/serviceprovider/service_check")
                                                    .setJsonObjectBody(jsonParam)
                                                    .asJsonObject()
                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                        @Override
                                                        public void onCompleted(Exception e, JsonObject result) {
                                                            try {
                                                                if (result.get("success").toString().equals("true")) {
                                                                    startActivity(new Intent(getApplicationContext(), UserServices.class));
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "No Service Available", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            catch (Exception ec){}
                                                        }
                                                    });
                                             break;
                        case "Grabbed Coupons" :
                                            break;

                        case "Edit Shop" :
                                            Intent intent = new Intent(Profile_new.this, ShopRegisterForm.class);
//                                            intent.putExtra("shopInfo", shopInfo.toString());
                                            intent.putExtra("editShop", 1);
                                            startActivity(intent);
                                            break;
                        case "My Coupons" : jsonParam = new JsonObject();
                                            jsonParam.addProperty("user_id", common_variable.User_id);
                                            jsonParam.addProperty("platform", "1");

                                            Ion.with(getApplicationContext())
                                                    .load(common_variable.main_web_url+"/coupon/myhistory")
                                                    .setJsonObjectBody(jsonParam)
                                                    .asJsonObject()
                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                        @Override
                                                        public void onCompleted(Exception e, JsonObject result) {
                                                            try
                                                            {
                                                                if(result.get("success").toString().equals("true"))
                                                                { startActivity(new Intent(getApplicationContext(), UserCouponsHistory.class));}
                                                                else
                                                                { Toast.makeText(getApplicationContext(), "you grabbed nothing", Toast.LENGTH_SHORT).show();  }
                                                            }
                                                            catch (Exception es)
                                                            { }
                                                        }
                                                    });
                                            break;
                        case "Change Password":
                            common_variable.forgot_password = 0;

                            LayoutInflater li = LayoutInflater.from(Profile_new.this);
                            final View prompt = li.inflate(R.layout.forget_password, null);

                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(Profile_new.this, R.style.MyAlertTheme));
                            alertDialogBuilder.setView(prompt);
                            alertDialogBuilder.setCancelable(true);
                            final AlertDialog dialog = alertDialogBuilder.show();

                            final EditText etMob = (EditText) prompt.findViewById(R.id.etMob);
                            Button btnGetPass = (Button) prompt.findViewById(R.id.btnGetPass);

                            btnGetPass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    if (etMob.length() < 10) {
                                        Toast.makeText(getApplicationContext(), "Enter valid mobile no.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        dialog.dismiss();

                                        final ProgressDialog dialog = new ProgressDialog(Profile_new.this);
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
                                                        String success = result.get("success").toString();
                                                        if (success.equals("false"))
                                                        {
                                                            Toast.makeText(getApplicationContext(), "Your mobile number is not registered", Toast.LENGTH_LONG).show();
                                                        }

                                                        if (success.equals("true"))
                                                        {
                                                            Toast.makeText(getApplicationContext(), "OTP has been sent to your mobile", Toast.LENGTH_SHORT).show();
                                                            LayoutInflater li = LayoutInflater.from(Profile_new.this);
                                                            final View prompt = li.inflate(R.layout.otp_confirm_pasword, null);

                                                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(Profile_new.this, R.style.MyAlertTheme));
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

                                                            tvReset.setOnClickListener(new View.OnClickListener()
                                                            {
                                                                @Override
                                                                public void onClick(View view)
                                                                {
                                                                    if (etOtp.getText().toString().equals(""))
                                                                    {
                                                                        Toast.makeText(getApplicationContext(), "Enter OTP", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else if (etPass.getText().toString().equals(""))
                                                                    {
                                                                        Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else if (etConPass.getText().toString().equals(""))
                                                                    {
                                                                        Toast.makeText(getApplicationContext(), "Enter confirm password", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else if (!etPass.getText().toString().equals(etConPass.getText().toString()))
                                                                    {
                                                                        Toast.makeText(getApplicationContext(), "Enter same password", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else {
                                                                        dialog.dismiss();

                                                                        final ProgressDialog dialog1 = new ProgressDialog(Profile_new.this);
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
                                                });
                                    }
                                }
                            });
                            break;
                        default:Toast.makeText(getApplicationContext(),"Wrong Selection",Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        }
        @Override
        public int getItemCount() {return menuList.length;}
    }

    public void refreshShopData()
    {
        try
        {
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(getApplicationContext())
                    .load(common_variable.main_web_url+"/shop/shop_info")
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result)
                        {
                            try
                            {
                                shopInfo = result.getAsJsonObject("data");
                                Shop_Name = shopInfo.get("shop_name").toString().replaceAll("^\"|\"$", "");

                                jShopImgs = shopInfo.getAsJsonObject("images");
                                JsonObject addr = (JsonObject) shopInfo.get("address");

                                String location = addr.get("location").toString().replaceAll("^\"|\"$","");
                                location = location.substring(1,location.length()-1);
                                String []loc = location.split(",");
                                Shop_loc = location;
                                Log.e("123","Loc : "+location);

                                Shop_cat = "";

                                String cate = shopInfo.get("category").toString();
                                String []cat_arr = cate.substring(1,cate.length()-1).split(",");
                                for(int s=0; s<cat_arr.length; s++)
                                {
                                    Shop_cat = Shop_cat + cat_arr[s].replaceAll("^\"|\"$","");
                                    if (s != cat_arr.length - 1)
                                        Shop_cat = Shop_cat + ",";
                                }
                                //Glide.with(getApplicationContext()).load(common_variable.main_web_url + "/uploads/shops/" + jShopImgs.get("shop_img1").toString().replaceAll("^\"|\"$", "")).placeholder(R.drawable.ic_img_icon).into(shop_image);
                                HashMap<String, Uri> file_maps = new HashMap<String, Uri>();
                                shopImagesArray = new ArrayList<String>();
                                try{
                                    if(!jShopImgs.get("shop_img1").toString().equals(""))
                                    {
                                        shopImagesArray.add(common_variable.main_web_url + "/uploads/shops/"+jShopImgs.get("shop_img1").toString().replaceAll("^\"|\"$", ""));
                                        file_maps.put("one", Uri.parse(common_variable.main_web_url + "/uploads/shops/"+jShopImgs.get("shop_img1").toString().replaceAll("^\"|\"$", "")));
                                    }
                                }
                                catch (Exception e1){}

                                try {
                                    if(!jShopImgs.get("shop_img2").toString().equals(""))
                                    {
                                        shopImagesArray.add(common_variable.main_web_url + "/uploads/shops/"+jShopImgs.get("shop_img2").toString().replaceAll("^\"|\"$", ""));
                                        file_maps.put("two", Uri.parse(common_variable.main_web_url + "/uploads/shops/"+jShopImgs.get("shop_img2").toString().replaceAll("^\"|\"$", "")));
                                    }
                                }
                                catch (Exception e1){}

                                try {
                                    if(!jShopImgs.get("shop_img3").toString().equals(""))
                                    {
                                        shopImagesArray.add(common_variable.main_web_url + "/uploads/shops/"+jShopImgs.get("shop_img3").toString().replaceAll("^\"|\"$", ""));
                                        file_maps.put("three", Uri.parse(common_variable.main_web_url + "/uploads/shops/"+jShopImgs.get("shop_img3").toString().replaceAll("^\"|\"$", "")));
                                    }
                                }
                                catch (Exception e1){}

                                /*shop_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ShopGallery.shop_images = shopImagesArray;
                                        startActivity(new Intent(Profile_new.this, ShopGallery.class));
                                    }
                                });*/
                            }
                            catch (Exception e1){Log.e("123456","In Catch:"+e1.getMessage().toString());}
                        }
                    });
        }
        catch (Exception e){Log.e("123456","out Catch:"+e.getMessage().toString());}
    }
}
