package com.clickitproduct.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.clickitproduct.R;
import com.clickitproduct.activities.Main_Activity;
import com.clickitproduct.activities.New_Shop_View_Activity;
import com.clickitproduct.activities.common_variable;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Fragment_Offers extends Fragment
{
    View rootview=null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    Button btnFilter;
    FloatingActionButton fab_filter;
    String seekBarValue = 5000+"", categoryValue="";
    TextView tv_NoData;
    String shop_cats[] = new String[0];

    public static Fragment_Offers newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Offers f1 = new Fragment_Offers();
        f1.setArguments(args);
        return f1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.fragment_product_list, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        fab_filter = (FloatingActionButton)rootview.findViewById(R.id.fab_filter);
        tv_NoData = (TextView) rootview.findViewById(R.id.tv_NoData);
        tv_NoData.setText("No Offers");

        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog_filter=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                dialog_filter.setContentView(R.layout.filter_offer);

                Button btnApplyFilter = (Button)dialog_filter.findViewById(R.id.btnApplyFilter);
                final ListView lv_Category = (ListView)dialog_filter.findViewById(R.id.lv_Category);
                final SeekBar locationSeekBar = (SeekBar)dialog_filter.findViewById(R.id.seekBar);

                lv_Category.setChoiceMode(lv_Category.CHOICE_MODE_MULTIPLE);

                try{
                    JsonObject jsonParam = new JsonObject();
                    Ion.with(getContext())
                    .load(common_variable.main_web_url+"/admin/getshopcategory")
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                JsonArray Shop_Category = result.getAsJsonArray("data");
                                shop_cats = new String[Shop_Category.size()];
                                for (int c=0; c<Shop_Category.size(); c++)
                                {
                                    JsonObject objCat = (JsonObject) Shop_Category.get(c);
                                    shop_cats[c] = objCat.get("category_name").toString().replaceAll("^\"|\"$", "");
                                }
                                lv_Category.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_checked, shop_cats));
                            }
                            catch (Exception e2){}
                        }
                    });
                }
                catch (Exception e)
                {}

                locationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
                    {   seekBarValue = (5*i*1000)+"";    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar)
                    {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar)
                    {}
                });

                btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try
                        {
                            JsonObject jsonParam = new JsonObject();
                            jsonParam.addProperty("category", categoryValue);
                            jsonParam.addProperty("lat", Main_Activity.current_Lattitude+"");
                            jsonParam.addProperty("lng", Main_Activity.current_Longitude+"");
                            jsonParam.addProperty("range", seekBarValue);
                            jsonParam.addProperty("platform", "1");

                            Ion.with(getContext())
                            .load(common_variable.main_web_url + "/search/offer_filter_android")
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    try
                                    {
                                        dialog_filter.dismiss();
                                        JsonArray offerData = result.getAsJsonArray("data");
                                        mAdapter = new RecyclerViewAdapter(offerData);
                                        RecyclerView.LayoutManager lLayout = new LinearLayoutManager(getContext());
                                        recyclerView.setLayoutManager(lLayout);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(mAdapter);

                                    } catch (Exception ed) {}
                                }
                            });
                        }
                        catch (Exception e){}
                    }
                });

                dialog_filter.show();
            }
        });

        Refresh();
        return rootview;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(common_variable.Refresh_Offer == 1) {
            Refresh();
        }
    }
    public void Refresh()
    {
        try
        {
            JsonObject jsonParam = new JsonObject();
            if(Main_Activity.str_searched_keyword.equals(""))
            {
                jsonParam.addProperty("searchkeyword", "");
                jsonParam.addProperty("platform", "1");
            }
            else
            {
                jsonParam.addProperty("searchkeyword", Main_Activity.str_searched_keyword);
                jsonParam.addProperty("platform", "1");
            }

            Ion.with(getContext())
            .load(common_variable.main_web_url+"/search/offer_search")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try
                    {
                        if(result.get("success").toString().equals("true")) {
                            JsonArray offerData = result.getAsJsonArray("data");
                            mAdapter = new RecyclerViewAdapter(offerData);
                            RecyclerView.LayoutManager lLayout = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(lLayout);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter); }else{
                            tv_NoData.setVisibility(View.VISIBLE);
                        }

                        Main_Activity.str_searched_keyword = "";
                    }
                    catch (Exception ex){}
                }
            });
        }
        catch (Exception e){}
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public ImageView Offer_image;
            public TextView txtOffer,txtDesc, txtValidDate;
            public RecyclerViewHolder(View view)
            {
                super(view);

                Offer_image = (ImageView) view.findViewById(R.id.offer_Img);
                txtOffer = (TextView)view.findViewById(R.id.txtOffername);
                txtDesc = (TextView)view.findViewById(R.id.txtOfferDesc);
                txtValidDate = (TextView)view.findViewById(R.id.txtValidDate);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList; }
        
        @Override
        public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_offer_view, parent, false);
            return new RecyclerViewAdapter.RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.RecyclerViewHolder holder, int position)
        {
            final JsonObject jobj = (JsonObject)pList.get(position);
            JsonObject jOffersObj = (JsonObject) jobj.get("offers");

            holder.txtOffer.setText(jOffersObj.get("offer_name").toString().replaceAll("^\"|\"$",""));
            holder.txtDesc.setText("Description : "+jOffersObj.get("offer_description").toString().replaceAll("^\"|\"$",""));
            holder.txtDesc.setVisibility(View.GONE);
            String toDate = jOffersObj.get("offer_to_date").toString().replaceAll("^\"|\"$","").substring(0, 10);

            holder.txtValidDate.setText("*Valid Till: "+toDate);

            /*Glide.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/offers/"+jOffersObj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                    .placeholder(R.drawable.ic_img_icon)
                    .into(holder.Offer_image);*/

            Ion.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/offers/"+jOffersObj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.Offer_image);

            holder.Offer_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getContext(), New_Shop_View_Activity.class);
                    i.putExtra("Shop_Id", jobj.get("shop_id").toString().replaceAll("^\"|\"$",""));
                    i.putExtra("focusParticularTab", 0);
                    common_variable.jShopProductObj = null;
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount()
        {   return pList.size();    }
    }
}
