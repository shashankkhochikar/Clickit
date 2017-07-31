package com.clickitproduct.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.NumberFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.clickitproduct.activities.common_variable.sharedpreferences;

public class Fragment_Shop_Coupons extends Fragment {
    View rootview = null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    public static JsonArray CouponData;
    public static final String MyPREFERENCES = "MyPrefs";
    TextView tv_NoData;

    public static Fragment_Shop_Coupons newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Shop_Coupons f1 = new Fragment_Shop_Coupons();
        f1.setArguments(args);
        return f1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        tv_NoData = (TextView) rootview.findViewById(R.id.tv_NoData);
        tv_NoData.setText("No Coupons");

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        try {
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("shop_id", common_variable.Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(getContext())
                    .load(common_variable.main_web_url + "/coupon/coupon_check")
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                if (result.get("success").toString().equals("true")) {
                                    tv_NoData.setVisibility(View.GONE);
                                    CouponData = new JsonArray();
                                    CouponData = result.getAsJsonArray("data");

                                    mAdapter = new RecyclerViewAdapter(CouponData);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(mAdapter);
                                } else {
                                    tv_NoData.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception ec) {
                            }
                        }
                    });
        } catch (Exception e) {
        }

        return rootview;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
        private JsonArray pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {
            public TextView tvMinPurchase, tvDiscount, tvValidNew, tvCouponNo, tvUrCoupNo, bGrabCouponNow;
            ImageView Coupon_Image;

            public RecyclerViewHolder(View view) {
                super(view);

                tvMinPurchase = (TextView) view.findViewById(R.id.tvMinPurchase);
                tvDiscount = (TextView) view.findViewById(R.id.tvDiscount);
                tvValidNew = (TextView) view.findViewById(R.id.tvValidNew);
                tvCouponNo = (TextView) view.findViewById(R.id.tvCoupNo);
                tvUrCoupNo = (TextView) view.findViewById(R.id.tvUrCoupNo);
                bGrabCouponNow = (TextView) view.findViewById(R.id.bGrabCouponNow);
                Coupon_Image = (ImageView) view.findViewById(R.id.Coupon_Image);
                tvCouponNo.setVisibility(View.VISIBLE);
            }
        }

        public RecyclerViewAdapter(JsonArray pList) {
            this.pList = pList;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragments_coupons_info, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            final JsonObject tmpjobj = (JsonObject) pList.get(position);
            final JsonObject jobj = (JsonObject) tmpjobj.get("coupon");

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(jobj.get("coupon_minimum_price").toString().replaceAll("^\"|\"$", "")));
            String disString = formatter.format(Integer.parseInt(jobj.get("coupon_discount").toString().replaceAll("^\"|\"$", "")));

            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvMinPurchase.setText(Html.fromHtml("At purchase of "+moneyString+ " get <font color=\"#FF0000\"><b>" + disString + "</b></font>"));
            holder.tvValidNew.setText("*Valid Till: " + jobj.get("coupon_validity").toString().replaceAll("^\"|\"$", "").substring(0, 10));

            try {
                /*Glide.with(getContext())
                .load(common_variable.main_web_url + "/uploads/coupon/" + jobj.get("coupon_img").toString().replaceAll("^\"|\"$", ""))
                .placeholder(R.drawable.ic_img_icon)
                .into(holder.Coupon_Image);*/

                Ion.with(getContext())
                        .load(common_variable.main_web_url + "/uploads/coupon/" + jobj.get("coupon_img").toString().replaceAll("^\"|\"$", ""))
                        .withBitmap()
                        .placeholder(R.raw.loading)
                        .error(R.drawable.error)
                        .animateLoad(R.anim.zoom_in)
                        .animateIn(R.anim.fade_in)
                        .intoImageView(holder.Coupon_Image);

            } catch (Exception e) {
            }

            holder.bGrabCouponNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final ProgressDialog dialog = new ProgressDialog(getContext());
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("Loading...");
                        dialog.show();

                        JsonObject jsonParam = new JsonObject();
                        jsonParam.addProperty("user_id", sharedpreferences.getString("user_id", "").replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("firstname", sharedpreferences.getString("first_name", "").replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("lastname", sharedpreferences.getString("last_name", "").replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("mobile", sharedpreferences.getString("mobile", "").replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("couponid", "" + jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("platform", "1");

                        Ion.with(getContext())
                        .load(common_variable.main_web_url + "/coupon/grabcoupon_android")
                        .progressDialog(dialog)
                        .setJsonObjectBody(jsonParam)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result)
                            {
                                try
                                {
                                    if (result.get("success").toString().equals("false")) {
                                        Toast.makeText(getContext(), "You Have Already Grabed This Coupon \n See In Coupon History", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    } else {
                                        try {
                                            dialog.dismiss();
                                            holder.bGrabCouponNow.setVisibility(View.GONE);
                                            holder.tvCouponNo.setText("Your coupon number is: " + result.get("couponid").toString().replaceAll("^\"|\"$", ""));
                                        } catch (Exception ec) {
                                        }
                                    }
                                }catch (Exception ez)
                                {Toast.makeText(getContext(),"There is Problem While Grabbing Coupon",Toast.LENGTH_LONG).show();}
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return pList.size();
        }
    }
}
