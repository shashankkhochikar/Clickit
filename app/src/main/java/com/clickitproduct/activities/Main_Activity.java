package com.clickitproduct.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.Fragments.Fragment_Classifieds;
import com.clickitproduct.Fragments.Fragment_Home;
import com.clickitproduct.Fragments.Fragment_Offers;
import com.clickitproduct.Fragments.Fragment_Services;
import com.clickitproduct.R;
import com.clickitproduct.SQLite.DataBaseConnection;
import com.clickitproduct.commonutil.common_variable;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Main_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    DataBaseConnection objDb;
    public static final String MyPREFERENCES = "MyPrefs";
    public static String app_link = "https://play.google.com/store/apps/details?id=com.clickitproduct&hl=en";
    public static double current_Lattitude = 0.0, current_Longitude = 0.0;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    public static String str_searched_keyword="";
    ImageView userImg;
    TextView userName, userEmail;
    NavigationView nav_view;
    FloatingActionButton fab_notification;
    public static int search_category = 2;
    LinearLayout laProfile;
    SharedPreferences sharedpreferences;
    public String PhotoUrl;
    boolean doubleBackToExitPressedOnce = false;
    public int coupon_count=0,notification_count=0;
    public MenuItem mnu_Grab_Coupon,item,mnu_notification;
    private AdView mAdView;
    public InterstitialAd mInterstitialAd;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        objDb = new DataBaseConnection(Main_Activity.this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getlocation();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        common_variable.screenHeight = displayMetrics.heightPixels;
        common_variable.screenWidth = displayMetrics.widthPixels;

        fab_notification = (FloatingActionButton)findViewById(R.id.fab_notification);
        fab_notification.setVisibility(View.GONE);

        if(!isNetworkConnected())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(Main_Activity.this);
            alert.setTitle("Alert");
            alert.setMessage("Enable internet connection and restart this application");
            alert.setCancelable(false);
            alert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog,int which)
                        {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alert.show();
        }

        //////////////////////////////////////////////////////VERSION CHECK //////////////////////////////////////////////////////////////////////
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;

        try {
            JsonObject jsonParam11 = new JsonObject();
            Ion.with(getApplicationContext())
            .load(common_variable.main_web_url + "/user/checkversion")
            .setJsonObjectBody(jsonParam11)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {
                        if (versionNumber >= Integer.parseInt(result.get("version").toString())) {
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Main_Activity.this);
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
                    }catch (Exception c){}
                }
            });
        }catch (Exception e){}
//////////////////////////////////////////////////////VERSION CHECK //////////////////////////////////////////////////////////////////////

        fab_notification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Main_Activity.this, Notifications.class));
            }
        });

        nav_view = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = nav_view.getHeaderView(0);

        objDb.open();
        common_variable.User_id = objDb.getUSER_ID();
        objDb.close();

        if (common_variable.User_id.equals("0"))
        {
            finish();
            startActivity(new Intent(Main_Activity.this, UserLogin.class));
        }
        else
        {
            objDb.open();
            common_variable.FCM_Token = objDb.getFCM_ID();
            common_variable.User_id = objDb.getUSER_ID();
            objDb.close();

            PhotoUrl = ""+common_variable.main_web_url+"/"+sharedpreferences.getString("profile_pic_url",null).replaceAll("^\"|\"$", "")+sharedpreferences.getString("profile_pic",null).replaceAll("^\"|\"$", "")+" ";
            userImg = (ImageView) headerLayout.findViewById(R.id.userImg);
            userName = (TextView) headerLayout.findViewById(R.id.userName);
            userEmail = (TextView) headerLayout.findViewById(R.id.userEmail);

            common_variable.User_Profile_Pic = sharedpreferences.getString("profile_pic",null).replaceAll("^\"|\"$", "");
            common_variable.User_Profile_Pic_URL = common_variable.main_web_url+"/"+sharedpreferences.getString("profile_pic_url",null).replaceAll("^\"|\"$", "");

            laProfile = (LinearLayout)headerLayout.findViewById(R.id.laProfile);
            laProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Main_Activity.this, Profile_new.class));
                }
            });

            //Picasso.with(Main_Activity.this).load(PhotoUrl).transform(new CircleTransform()).into(userImg);
            Ion.with(Main_Activity.this)
                    .load(PhotoUrl)
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(userImg);

            common_variable.User_Name = sharedpreferences.getString("first_name",null).replaceAll("^\"|\"$","")+" "+sharedpreferences.getString("last_name",null).replaceAll("^\"|\"$","");
            userName.setText(" "+sharedpreferences.getString("first_name",null).replaceAll("^\"|\"$","")+" "+sharedpreferences.getString("last_name",null).replaceAll("^\"|\"$",""));
            userEmail.setText(" "+sharedpreferences.getString("email",null).replaceAll("^\"|\"$", ""));

            if(sharedpreferences.getString("fcm_id", "").toString().replaceAll("^\"|\"$", "").equals("") || !sharedpreferences.getString("fcm_id", "").toString().replaceAll("^\"|\"$", "").equals(common_variable.FCM_Token)) {
                JsonObject jsonParamLogin = new JsonObject();
                jsonParamLogin.addProperty("user_id", sharedpreferences.getString("user_id", "").replaceAll("^\"|\"$", ""));
                jsonParamLogin.addProperty("fcm_id", common_variable.FCM_Token);
                jsonParamLogin.addProperty("platform", "1");

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

        if (Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        /*try{
            JsonObject jsonParam = new JsonObject();

            Ion.with(getApplicationContext())
            .load(common_variable.main_web_url+"/shop/count")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        ShopsCount.setText(result.get("shopcount").toString());
                        ProductsCount.setText(result.get("productcount").toString());
                        OffersCount.setText(result.get("offercount").toString());
                        ClassifiedCount.setText(result.get("classifiedcount").toString());
                        ServiceCount.setText(result.get("servicecount").toString());
                    }catch (Exception e1){
                        ShopsCount.setText("0");
                        ProductsCount.setText("0");
                        OffersCount.setText("0");
                        ClassifiedCount.setText("0");
                        ServiceCount.setText("0");
                    }
                }
            });
        }
        catch (Exception e)
        {}*/

        getGrabCouponCount();
        getNotificationCount();

        MobileAds.initialize(Main_Activity.this, getString(R.string.admobs_app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(Main_Activity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest2 = new AdRequest.Builder() .build();
        mInterstitialAd.loadAd(adRequest2);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded()
            {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });
    }

    public void getlocation()
    {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                current_Lattitude = location.getLatitude();
                current_Longitude = location.getLongitude();
                Log.e("location ", current_Lattitude+"//"+current_Longitude);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {   }

            public void onProviderEnabled(String provider) {   }

            public void onProviderDisabled(String provider) {  }
        };

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

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Main_Activity.this);
                dialog.setMessage("Please Enable Location For Application");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
                dialog.show();
            }
            catch (Exception ec){}
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1000, locationListener);
    }

    public void getGrabCouponCount()
    {
        try{
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("searchkeyword", "");
            jsonParam.addProperty("platform", "1");

            Ion.with(Main_Activity.this)
            .load(common_variable.main_web_url + "/coupon/coupon_search")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {
                        coupon_count = result.getAsJsonArray("data").size();
                        mnu_Grab_Coupon.setTitle("Grab Coupon ("+coupon_count+")");
                    } catch (Exception ec) {
                        coupon_count = 0;
                    }
                }
            });
        }catch (Exception ec){}
    }

    public void getNotificationCount()
    {
        try{
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("user_id", common_variable.User_id);
            jsonParam.addProperty("platform", "1");

            Ion.with(Main_Activity.this)
                    .load(common_variable.main_web_url+"/user/notification")
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result)
                        {
                            try
                            {
                                notification_count = result.getAsJsonArray("data").size();
                                mnu_notification.setTitle("Notifications ("+notification_count+")");
                            } catch (Exception ec) { notification_count = 0; }
                        }
                    });
        }catch (Exception ec){}
    }

    public boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void setupTabIcons()
    {
        View view1 = getLayoutInflater().inflate(R.layout.costum_tabs, null);
        TextView icon_title1 = (TextView) view1.findViewById(R.id.icon_title);
        icon_title1.setText("Offers");
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_offer_logo);

        View view2 = getLayoutInflater().inflate(R.layout.costum_tabs, null);
        TextView icon_title2 = (TextView) view2.findViewById(R.id.icon_title);
        icon_title2.setText("Products");
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_products);

       /*View view3 = getLayoutInflater().inflate(R.layout.costum_tabs, null);
        TextView icon_title3 = (TextView) view3.findViewById(R.id.icon_title);
        icon_title3.setText("Shops");
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_shop_logo);*/

        View view4 = getLayoutInflater().inflate(R.layout.costum_tabs, null);
        TextView icon_title4 = (TextView) view4.findViewById(R.id.icon_title);
        icon_title4.setText("Services");
        view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_service_logo);

        View view5 = getLayoutInflater().inflate(R.layout.costum_tabs, null);
        TextView icon_title5 = (TextView) view5.findViewById(R.id.icon_title);
        icon_title5.setText("Classifieds");
        view5.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_classified_white);

        tabLayout.getTabAt(0).setCustomView(view1);
        tabLayout.getTabAt(1).setCustomView(view2);
        tabLayout.getTabAt(2).setCustomView(view4);
        tabLayout.getTabAt(3).setCustomView(view5);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            Main_Activity.this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to close", Toast.LENGTH_SHORT).show();

            if (doubleBackToExitPressedOnce == true) {
                super.onBackPressed();
                return;
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        inflater.inflate(R.menu.menu_main_activity, menu);

        item = menu.findItem(R.id.mnusearch);
        mnu_Grab_Coupon = menu.findItem(R.id.mnu_Grab_Coupon);
        mnu_notification = menu.findItem(R.id.mnu_Notificatios);

        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconified(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                searchView.setQuery("", false);
                str_searched_keyword = s;

                if (mViewPager.getCurrentItem() == 0){ search_category = 0; }
                else if (mViewPager.getCurrentItem() == 1){ search_category = 1; }
                else if (mViewPager.getCurrentItem() == 2){ search_category = 2; }
                else if (mViewPager.getCurrentItem() == 3){ search_category = 3; }
                else { search_category = 4; }
                search(s);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s)
            {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.mnu_Grab_Coupon:
                startActivity(new Intent(Main_Activity.this, GrabNearestCoupons.class));
                return true;

            case R.id.mnu_Notificatios:
                startActivity(new Intent(Main_Activity.this, Notifications.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void search(String s)
    {
        final ProgressDialog dialog = new ProgressDialog(Main_Activity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading...");
        dialog.show();
        try{
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("searchkeyword", s);
            jsonParam.addProperty("platform", "1");
            String url = "";

            switch (search_category)
            {
                case 0: url = common_variable.main_web_url + "/search/offer_search";
                    break;
                case 1: url = common_variable.main_web_url + "/search/product_search";
                    break;
                case 2: url = common_variable.main_web_url + "/search/shop_search";
                    break;
                case 3:  url = common_variable.main_web_url + "/search/service_search";
                    break;
                case 4:  url = common_variable.main_web_url + "/search/classified_search";
                    break;
                default: url="";
                    break;
            }

            Ion.with(getApplicationContext())
            .load(url)
            .progressDialog(dialog)
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    dialog.dismiss();
                    try {
                        common_variable.ShopDetails = result.getAsJsonArray("data");
                        if(common_variable.ShopDetails.size() > 0)
                        {
                            switch (search_category)
                            {
                                case 0: startActivity(new Intent(Main_Activity.this, Offer_search_list.class));
                                    break;
                                case 1: startActivity(new Intent(Main_Activity.this, Products_Search_list.class));
                                    break;
//                                case 2: startActivity(new Intent(Main_Activity.this, Shops_Search_List .class));
//                                    break;
                                case 3: startActivity(new Intent(Main_Activity.this, Services_Search_List.class));
                                    break;
                                case 4: startActivity(new Intent(Main_Activity.this, Classifieds_Search_List.class));
                                    break;
                                default: Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                        else
                        {Toast.makeText(Main_Activity.this, "No record found", Toast.LENGTH_SHORT).show();}
                    }
                    catch (Exception e1)
                    {

                    }
                }
            });
        }
        catch (Exception e){}
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
       /* if (id == R.id.tHome){
            mViewPager.setCurrentItem(2);
        }else*/
        if (id == R.id.tProfile){
            startActivity(new Intent(this, Profile_new.class));
        }else if(id == R.id.tRegShop){
            if (common_variable.IsShopAvailable == 0){
                try{
                    final JsonObject jsonParam = new JsonObject();
                    jsonParam.addProperty("user_id", common_variable.User_id);
                    jsonParam.addProperty("platform", "1");
                    Ion.with(Main_Activity.this)
                    .load(common_variable.main_web_url + "/shop/shop_check")
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                if (result.get("success").toString().equals("true")){
                                    common_variable.IsShopAvailable = 1;
                                    Toast.makeText(Main_Activity.this, "You can register only one shop", Toast.LENGTH_SHORT).show();
                                }else{
                                    common_variable.IsShopAvailable = 0;
                                    startActivity(new Intent(Main_Activity.this, ShopRegisterForm.class));
                                }
                            }
                            catch (Exception ec){}
                        }
                    });
                } catch (Exception e) {}
            }else{
                Toast.makeText(Main_Activity.this, "You can register only one shop", Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.tShare) {
            String msg = "Download Clickit App "+app_link;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Choose One"));
        }else if (id == R.id.tPolicy){
            startActivity(new Intent(this, Privacy_policy.class));
        }else if (id == R.id.tContact){
            startActivity(new Intent(this, Contact_us.class));
        }else if (id == R.id.tAbout) {
            startActivity(new Intent(this, AboutUsActivity.class));
        }else if(id == R.id.tRegService){
            startActivity(new Intent(Main_Activity.this, ServicesForm.class));
        }else if(id == R.id.tRegClassified){
            startActivity(new Intent(Main_Activity.this, AddClassified.class));
        }else if(id == R.id.tMyWishList){
            startActivity(new Intent(Main_Activity.this, MyWishList.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
        private JsonArray pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {
            public ImageView Category_image;
            public TextView txtCategory;

            public RecyclerViewHolder(View view) {
                super(view);
                Category_image = (ImageView) view.findViewById(R.id.category_Img);
                txtCategory = (TextView) view.findViewById(R.id.txtCategoryname);
            }
        }

        public RecyclerViewAdapter(JsonArray pList)
        {
            this.pList = pList;
        }

        @Override
        public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_data, parent, false);
            return new RecyclerViewAdapter.RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.RecyclerViewHolder holder, int position)
        {
            final JsonObject jobj = (JsonObject) pList.get(position);
            holder.txtCategory.setText("" + jobj.get("offer_name").toString().replaceAll("^\"|\"$", ""));

            /*Glide.with(getApplicationContext())
            .load(common_variable.main_web_url + "/uploads/offers/" + jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
            .placeholder(R.drawable.ic_img_icon)
            .into(holder.Category_image);*/
            Ion.with(Main_Activity.this)
                    .load(common_variable.main_web_url + "/uploads/offers/" + jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.Category_image);


        }

        @Override
        public int getItemCount() {
            return pList.size();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == 0) {
                return Fragment_Offers.newInstance(position);
            }
            if (position == 1) {
                return Fragment_Home.newInstance(position);
            }
            if (position == 2) {
                //return Fragment_Shops.newInstance(position);
                return Fragment_Services.newInstance(position);
            }
            if (position == 3) {
                return Fragment_Classifieds.newInstance(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        if(common_variable.changed_profile_detail == 1)
        {
            //Picasso.with(Main_Activity.this).load(Profile_new.user_profile_image).transform(new CircleTransform()).into(userImg);
            Ion.with(Main_Activity.this)
                    .load(Profile_new.user_profile_image)
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(userImg);

            common_variable.User_Profile_Pic = Profile_new.user_profile_image;
            common_variable.User_Name = Profile_new.first_name+" "+Profile_new.last_name;
            userName.setText(Profile_new.first_name+" "+Profile_new.last_name);
            userEmail.setText(Profile_new.email_id);
            common_variable.changed_profile_detail = 0;
        }

        if(common_variable.Refresh_noti_count == 1)
        {
            getNotificationCount();
            common_variable.Refresh_noti_count = 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
