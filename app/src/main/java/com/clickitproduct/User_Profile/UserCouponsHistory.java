package com.clickitproduct.User_Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickitproduct.R;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class UserCouponsHistory extends AppCompatActivity
{
    RecyclerViewAdapter mAdapter;
    RecyclerView recycler_view;
    Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("My Coupons");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getCouponsHitoryData();
    }

    public void getCouponsHitoryData()
    {
        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty("user_id", common_variable.User_id);
        jsonParam.addProperty("platform", "1");

        Log.e("coupon history_check", "jsonParam " + jsonParam.toString());

        Ion.with(UserCouponsHistory.this)
        .load(common_variable.main_web_url+"/coupon/myhistory")
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try
                {
                    Log.e("coupon ress", result.getAsJsonArray("data").toString());

                    mAdapter = new RecyclerViewAdapter(result.getAsJsonArray("data"));
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserCouponsHistory.this);
                    recycler_view.setLayoutManager(mLayoutManager);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(mAdapter);
                }
                catch (Exception es)
                {      }
            }
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tvMinPurchase,tvDiscount,tvValidNew,tvItemsRemain,tvCouponNo, tvUrCoupNo, tvShopName,bGrabCouponNow;
            ImageView Coupon_Image;

            public RecyclerViewHolder(View view)
            {
                super(view);

                tvMinPurchase=(TextView)view.findViewById(R.id.tvMinPurchase);
                tvDiscount=(TextView)view.findViewById(R.id.tvDiscount);
                tvValidNew=(TextView)view.findViewById(R.id.tvValidNew);
                tvCouponNo = (TextView) view.findViewById(R.id.tvCoupNo);
                tvUrCoupNo = (TextView) view.findViewById(R.id.tvUrCoupNo);
                bGrabCouponNow = (TextView) view.findViewById(R.id.bGrabCouponNow);
                bGrabCouponNow.setVisibility(View.GONE);
                Coupon_Image = (ImageView)view.findViewById(R.id.Coupon_Image);
                tvShopName = (TextView)view.findViewById(R.id.tvShopName);
                tvShopName.setVisibility(View.VISIBLE);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList; }
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragments_coupons_info, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position)
        {
            final JsonObject jobj = (JsonObject)pList.get(position);
            final JsonObject jCoupons = (JsonObject) jobj.get("coupons");
            final JsonObject jGrabber = (JsonObject) jobj.get("grabber");

            holder.tvShopName.setText(jobj.get("shop_name").toString().replaceAll("^\"|\"$",""));
            holder.tvMinPurchase.setText("Min. Purchase Rs:"+jCoupons.get("coupon_minimum_price").toString().replaceAll("^\"|\"$",""));
            holder.tvDiscount.setText(""+jCoupons.get("coupon_discount").toString().replaceAll("^\"|\"$","")+"% OFF");
            holder.tvValidNew.setText("*Valid Till: "+jCoupons.get("coupon_validity").toString().replaceAll("^\"|\"$","").substring(0, 10));
            holder.tvCouponNo.setText(jGrabber.get("c_id").toString().replaceAll("^\"|\"$",""));

            try
            {
                /*Glide.with(UserCouponsHistory.this)
                    .load(common_variable.main_web_url+"/uploads/coupon/"+jCoupons.get("coupon_img").toString().replaceAll("^\"|\"$",""))
                    .placeholder(R.drawable.ic_img_icon)
                    .into(holder.Coupon_Image);*/

                Ion.with(UserCouponsHistory.this)
                        .load(common_variable.main_web_url+"/uploads/coupon/"+jCoupons.get("coupon_img").toString().replaceAll("^\"|\"$",""))
                        .withBitmap()
                        .placeholder(R.raw.loading)
                        .error(R.drawable.error)
                        .animateLoad(R.anim.zoom_in)
                        .animateIn(R.anim.fade_in)
                        .intoImageView(holder.Coupon_Image);
            }
            catch (Exception e)
            {}
        }
        @Override
        public int getItemCount()
        {   return pList.size();    }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}