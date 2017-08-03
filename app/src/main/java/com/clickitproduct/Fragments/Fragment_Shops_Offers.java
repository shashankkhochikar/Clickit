package com.clickitproduct.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickitproduct.R;
import com.clickitproduct.commonutil.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Fragment_Shops_Offers extends Fragment
{
    View rootview=null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    public static JsonArray ShopOffers;
    TextView tv_NoData;

    public static Fragment_Shops_Offers newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Shops_Offers f1 = new Fragment_Shops_Offers();
        f1.setArguments(args);
        return f1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        tv_NoData = (TextView) rootview.findViewById(R.id.tv_NoData);
        tv_NoData.setText("No Offers");
        getShopOfferData();
        return rootview;
    }

    public void getShopOfferData()
    {
        JsonObject jsonParam = new JsonObject();
        try
        {
            jsonParam.addProperty("shop_id", common_variable.Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(getContext())
            .load(common_variable.main_web_url+"/offer/offer_check")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        if (result.get("success").toString().equals("true")) {
                            tv_NoData.setVisibility(View.GONE);
                            ShopOffers = new JsonArray();
                            if (ShopOffers != null) {
                                try {
                                    if (result.getAsJsonArray("data") != null) {
                                        ShopOffers = result.getAsJsonArray("data");
                                    }
                                } catch (Exception ee) {}

                                mAdapter = new RecyclerViewAdapter(ShopOffers);
                                GridLayoutManager lLayout;
                                lLayout = new GridLayoutManager(getContext(), 1);
                                recyclerView.setLayoutManager(lLayout);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(mAdapter);
                            }
                        }
                        else
                        {
                            tv_NoData.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception ec){}
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public ImageView offer_Img;
            public TextView txtOffername, txtDesc, txtValidDate;
            public RecyclerViewHolder(View view)
            {
                super(view);
                txtOffername = (TextView)view.findViewById(R.id.txtOffername);
                txtDesc = (TextView)view.findViewById(R.id.txtOfferDesc);
                offer_Img = (ImageView) view.findViewById(R.id.offer_Img);
                txtValidDate = (TextView)view.findViewById(R.id.txtValidDate);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList; Log.e("123","IN Recycle"+pList.size()); }
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_offer_view, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position)
        {
            final JsonObject jOffer = (JsonObject)pList.get(position);
            final JsonObject jobj = (JsonObject) jOffer.get("offer");

            holder.txtOffername.setText(""+jobj.get("offer_name").toString().replaceAll("^\"|\"$",""));
            holder.txtDesc.setText("Description : "+jobj.get("offer_description").toString().replaceAll("^\"|\"$",""));
            holder.txtDesc.setVisibility(View.GONE);

            String toDate = jobj.get("offer_to_date").toString().replaceAll("^\"|\"$","").substring(0, 10);
            holder.txtValidDate.setText("*Valid Till : "+toDate);

           /* Glide.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/offers/"+jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                    .placeholder(R.drawable.ic_img_icon)
                    .into(holder.offer_Img);*/

            Ion.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/offers/"+jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.offer_Img);

            holder.offer_Img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                    dialog.setContentView(R.layout.single_offer_dialog_view);
                    int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                    int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
                    dialog.getWindow().setLayout(width, height);

                    final ImageView iv_offer_Img = (ImageView) dialog.findViewById(R.id.iv_offer_Img);
                    TextView tv_Title = (TextView)dialog.findViewById(R.id.tv_Title);
                    TextView tv_Decription = (TextView)dialog.findViewById(R.id.tv_Decription);
                    TextView tv_ValidDate = (TextView)dialog.findViewById(R.id.tv_ValidDate);

                    /*Glide.with(getContext())
                            .load(common_variable.main_web_url+"/uploads/offers/"+jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                            .placeholder(R.drawable.ic_img_icon)
                            .into(iv_offer_Img);*/

                    Ion.with(getContext())
                            .load(common_variable.main_web_url+"/uploads/offers/"+jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                            .withBitmap()
                            .placeholder(R.raw.loading)
                            .error(R.drawable.error)
                            .animateLoad(R.anim.zoom_in)
                            .animateIn(R.anim.fade_in)
                            .intoImageView(iv_offer_Img);

                    tv_Title.setText(jobj.get("offer_name").toString().replaceAll("^\"|\"$", ""));
                    tv_Decription.setText(jobj.get("offer_description").toString().replaceAll("^\"|\"$", ""));
                    String toDate = jobj.get("offer_to_date").toString().replaceAll("^\"|\"$","").substring(0, 10);
                    tv_ValidDate.setText("*Valid Till : "+toDate);

                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount()
        {   return pList.size();    }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(common_variable.Refresh_Offer == 1){
            getShopOfferData();
            common_variable.Refresh_Offer = 0;
        }
    }
}
