package com.clickitproduct.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clickitproduct.Fragments.Fragment_Shop_Coupons;
import com.clickitproduct.Fragments.Fragment_Shops_Offers;
import com.clickitproduct.Fragments.Fragment_Shops_Products;
import com.clickitproduct.R;
import com.clickitproduct.commonutil.common_variable;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ShopViewActivityNew extends AppCompatActivity
{
    int focusParticularTab;
    Toolbar toolbar;
    ViewPager mViewPager;
    TabLayout tabLayout;
    SectionsPagerAdapter mSectionsPagerAdapter;
    String ProductsData;
    public static String owner_mobile, shop_lati, shop_longi, shop_name, shop_email;
    Button btnCall, btnDirection, btnWhatsap;
    public static JsonObject ShopProductsResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shop_view);

        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        btnCall = (Button)findViewById(R.id.btnCall);
        btnDirection = (Button)findViewById(R.id.btnDirection);
        btnWhatsap = (Button)findViewById(R.id.btnWhatsap);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shop Name");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        common_variable.Shop_ID = i.getStringExtra("Shop_Id");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        focusParticularTab = getIntent().getIntExtra("focusParticularTab", 0);
        if(focusParticularTab == 0)
        {
            mViewPager.setCurrentItem(0);
        }
        else if(focusParticularTab == 1)
        {
            mViewPager.setCurrentItem(1);
        }
        else if(focusParticularTab == 2)
        {
            mViewPager.setCurrentItem(2);
        }

        tabLayout.setupWithViewPager(mViewPager);

        /*if(focusParticularTab != 1) {
            try {
                JsonObject jsonParam = new JsonObject();
                jsonParam.addProperty("shop_id", common_variable.Shop_ID);
                jsonParam.addProperty("platform", "1");

                Log.e("shop_info 1111",""+jsonParam.toString());

                Ion.with(ShopViewActivityNew.this)
                        .load(common_variable.main_web_url + "/product/product_check")
                        .setJsonObjectBody(jsonParam)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                try {
                                    ShopProductsResult = result;
                                    JsonArray ShopProducts = result.getAsJsonArray("data");
                                    common_variable.jShopProductObj = (JsonObject) ShopProducts.get(0);
                                } catch (Exception ce) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }*/

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run()
            {
                getShopData();
            }
        };
        handler.postDelayed(r, 1000);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + owner_mobile));
                startActivity(intent);
            }
        });

        btnDirection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(Main_Activity.current_Lattitude == 0.0 && Main_Activity.current_Longitude == 0.0)
                {
                    Toast.makeText(ShopViewActivityNew.this,"Not Getting Your Proper Location",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + Main_Activity.current_Lattitude + "," + Main_Activity.current_Longitude + "&daddr=" + shop_lati + "," + shop_longi + ""));
                    startActivity(intent);
                }
            }
        });

        btnWhatsap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try{
                    Uri mUri = Uri.parse("smsto:+"+owner_mobile);
                    Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
                    mIntent.setPackage("com.whatsapp");
                    mIntent.putExtra("sms_body", "");
                    mIntent.putExtra("chat",true);
                    startActivity(mIntent);
                }catch (Exception e)
                {Toast.makeText(ShopViewActivityNew.this,"No Whatsapp Found",Toast.LENGTH_LONG).show();}
            }
        });
    }

    public void getShopData()
    {
        try{
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("shop_id", common_variable.Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(ShopViewActivityNew.this)
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
                            Log.e("1212121212 shop_info",""+result.toString());
                            JsonObject shopDetailInfo = result.getAsJsonObject("data");
                            JsonObject addr = (JsonObject) shopDetailInfo.get("address");
                            String location = addr.get("location").toString().replaceAll("^\"|\"$","");
                            location = location.substring(1,location.length()-1);
                            String []loc = location.split(",");

                            shop_name = shopDetailInfo.get("shop_name").toString().replaceAll("^\"|\"$", "");
                            shop_email = shopDetailInfo.get("email").toString().replaceAll("^\"|\"$", "");
                            getSupportActionBar().setTitle(shop_name);
                            owner_mobile = shopDetailInfo.get("mobile").toString().replaceAll("^\"|\"$","");
                            shop_lati = loc[0];
                            shop_longi = loc[1];
                        }
                        catch(Exception e1){}
                    }
                });
        } catch (Exception e)
        { e.printStackTrace(); }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            if (position == 0) {
                return Fragment_Shops_Offers.newInstance(position);
            }
            if (position == 1) {
                return Fragment_Shops_Products.newInstance(position);
            }
            if (position == 2) {
                return Fragment_Shop_Coupons.newInstance(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Offers";
                case 1:
                    return "Products";
                case 2:
                    return "Coupons";
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}