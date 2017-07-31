package com.clickitproduct.Coupens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.clickitproduct.R;
import com.clickitproduct.activities.New_Shop_View_Activity;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.NumberFormat;
import java.util.Locale;

public class GrabNearestCoupons extends AppCompatActivity
{
    RecyclerView coupons_recycler_view;
    RecyclerViewAdapter mAdapter;
    TextView tv_NoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        tv_NoData = (TextView)findViewById(R.id.tv_NoData);
        tv_NoData.setText("No Coupons");

        coupons_recycler_view = (RecyclerView)findViewById(R.id.recycler_view);

        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty("searchkeyword","");
        jsonParam.addProperty("platform", "1");

        Ion.with(GrabNearestCoupons.this)
        .load(common_variable.main_web_url+"/coupon/coupon_search")
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>()
        {
            @Override
            public void onCompleted(Exception e, JsonObject result)
            {
                try {
                    if(result.getAsJsonArray("data").size()>0) {
                        tv_NoData.setVisibility(View.GONE);
                        JsonArray tmp_coupons = result.getAsJsonArray("data");
                        mAdapter = new RecyclerViewAdapter(tmp_coupons);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        coupons_recycler_view.setLayoutManager(mLayoutManager);
                        coupons_recycler_view.setItemAnimator(new DefaultItemAnimator());
                        coupons_recycler_view.setAdapter(mAdapter);
                    }
                    else{
                        tv_NoData.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception ec){
                    tv_NoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;
        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView tvShopName, tvMinPurchase, tvDiscount;
            Button bGrabCouponNow;
            ImageView Coupon_Image;

            public RecyclerViewHolder(View view)
            {
                super(view);
                tvShopName = (TextView) view.findViewById(R.id.tvShopName);
                tvMinPurchase = (TextView) view.findViewById(R.id.tvMinPurchase);
                tvDiscount = (TextView) view.findViewById(R.id.tvDiscount);
                bGrabCouponNow = (Button) view.findViewById(R.id.bGrabCouponNow);
                Coupon_Image = (ImageView)view.findViewById(R.id.Coupon_Image);
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
        public void onBindViewHolder(RecyclerViewHolder holder, int position)
        {
            final JsonObject jobj = (JsonObject)pList.get(position);
            JsonObject jCouponsObj = (JsonObject) jobj.get("coupons");

            holder.tvShopName.setText(jobj.get("shop_name").toString().replaceAll("^\"|\"$", ""));

            try {
                /*Glide.with(GrabNearestCoupons.this)
                .load(common_variable.main_web_url + "/uploads/coupon/" + jCouponsObj.get("coupon_img").toString().replaceAll("^\"|\"$", ""))
                .placeholder(R.drawable.ic_img_icon)
                .into(holder.Coupon_Image);*/

                Ion.with(GrabNearestCoupons.this)
                        .load(common_variable.main_web_url + "/uploads/coupon/" + jCouponsObj.get("coupon_img").toString().replaceAll("^\"|\"$", ""))
                        .withBitmap()
                        .placeholder(R.raw.loading)
                        .error(R.drawable.error)
                        .animateLoad(R.anim.zoom_in)
                        .animateIn(R.anim.fade_in)
                        .intoImageView(holder.Coupon_Image);
            }
            catch (Exception e)
            { }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(jCouponsObj.get("coupon_minimum_price").toString().replaceAll("^\"|\"$", "")));
            String disString = formatter.format(Integer.parseInt(jCouponsObj.get("coupon_discount").toString().replaceAll("^\"|\"$", "")));

            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvMinPurchase.setText(Html.fromHtml("At purchase of "+moneyString+ " get <font color=\"#FF0000\"><b>" + disString + "</b></font>"));
            holder.bGrabCouponNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(GrabNearestCoupons.this, New_Shop_View_Activity.class);
                    i.putExtra("Shop_Id", jobj.get("shop_id").toString().replaceAll("^\"|\"$",""));
                    i.putExtra("focusParticularTab", 4);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount()
        {   return pList.size();  }
    }
}
