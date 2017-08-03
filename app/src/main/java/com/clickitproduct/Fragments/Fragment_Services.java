package com.clickitproduct.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.activities.Main_Activity;
import com.clickitproduct.commonutil.common_variable;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Fragment_Services extends Fragment
{
    View rootview=null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    FloatingActionButton fab_filter;
    TextView tv_NoData;
    String filter_category = "";
    public Button btnApplyFilter;

    public static Fragment_Services newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Services f1 = new Fragment_Services();
        f1.setArguments(args);
        return f1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.fragment_product_list, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        fab_filter = (FloatingActionButton)rootview.findViewById(R.id.fab_filter);
        tv_NoData = (TextView)rootview.findViewById(R.id.tv_NoData);
        tv_NoData.setText("No Services");


        fab_filter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                dialog.setContentView(R.layout.filter_services);

                final ListView Service_Category = (ListView)dialog.findViewById(R.id.Service_Category);
                btnApplyFilter = (Button)dialog.findViewById(R.id.btnApplyFilter12);
                JsonObject jsonParam = new JsonObject();

                Service_Category.setChoiceMode(Service_Category.CHOICE_MODE_SINGLE);

                Service_Category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        long stdrno[] = Service_Category.getCheckItemIds();

                        for (int i = 0; i < stdrno.length; i++) {
                            filter_category = Service_Category.getItemAtPosition((int) stdrno[i]).toString();
                        }
                    }
                });

                Ion.with(getContext())
                .load(common_variable.main_web_url+"/serviceprovider/serviceCategory_check")
                .setJsonObjectBody(jsonParam)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>()
                {
                    @Override
                    public void onCompleted(Exception e, JsonObject result)
                    {
                        try {
                            JsonArray srvc_cat = result.getAsJsonArray("data");
                            Log.e("srv",""+srvc_cat.toString());
                            String []shop_cats = new String[srvc_cat.size()];
                            for (int c=0; c<srvc_cat.size(); c++)
                            {
                                JsonObject objCat = (JsonObject) srvc_cat.get(c);
                                shop_cats[c] = objCat.get("servicecat_name").toString().replaceAll("^\"|\"$", "");
                            }
                            Service_Category.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_checked, shop_cats));
                        }
                        catch (Exception c){Toast.makeText(getContext(),"No Categiory Found",Toast.LENGTH_LONG).show();}
                    }
                });

                btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try
                        {
                            JsonObject jsonParam = new JsonObject();
                            jsonParam.addProperty("category", filter_category);
                            Ion.with(getContext())
                            .load(common_variable.main_web_url+"/serviceprovider/getserviceproviders_by_category_android")
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>()
                            {
                                @Override
                                public void onCompleted(Exception e, JsonObject result)
                                {
                                    try{
                                        JsonArray serviceData = result.getAsJsonArray("data");
                                        mAdapter = new RecyclerViewAdapter(serviceData);
                                        GridLayoutManager lLayout;
                                        lLayout = new GridLayoutManager(getContext(), 1);
                                        recyclerView.setLayoutManager(lLayout);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(mAdapter);
                                    }catch (Exception es){
                                        mAdapter = null;
                                        recyclerView.setAdapter(mAdapter);
                                        Toast.makeText(getContext(),"No data Available",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                        catch (Exception e){}
                    }
                });

                dialog.show();
            }
        });
        Refresh_service();
        return rootview;
    }

    public void Refresh_service()
    {
        try{
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
            .load(common_variable.main_web_url+"/search/service_search")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try{
                        if(result.getAsJsonArray("data").size() > 0) {
                            tv_NoData.setVisibility(View.GONE);
                            JsonArray serviceData = result.getAsJsonArray("data");
                            mAdapter = new RecyclerViewAdapter(serviceData);
                            GridLayoutManager lLayout;
                            lLayout = new GridLayoutManager(getContext(), 1);
                            recyclerView.setLayoutManager(lLayout);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }
                        else
                        {    tv_NoData.setVisibility(View.VISIBLE); }
                        Main_Activity.str_searched_keyword="";
                    }catch (Exception es){tv_NoData.setVisibility(View.VISIBLE);}
                }
            });
        }
        catch (Exception e){}
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;
        int cal = 0;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView Service_Provider_Name,Service_Category;
            public ImageView Service_Image, ivCall,ivWhatssapp;
            LinearLayout Service_layout;

            public RecyclerViewHolder(View view)
            {
                super(view);
                Service_Provider_Name = (TextView) view.findViewById(R.id.Service_Provider_Name);
                Service_Category = (TextView) view.findViewById(R.id.Service_Category);
                Service_Image = (ImageView) view.findViewById(R.id.Service_Image);
                ivCall = (ImageView)view.findViewById(R.id.ivCall);
                ivWhatssapp = (ImageView)view.findViewById(R.id.ic_WhatsApp);
                Service_layout = (LinearLayout)view.findViewById(R.id.Service_layout);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList; }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_view, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position)
        {
            final JsonObject jobj = (JsonObject)pList.get(position);
            final JsonObject jServiceTimie = (JsonObject) jobj.get("service_time");

            holder.Service_Provider_Name.setText(jobj.get("serviceprovidername").toString().replaceAll("^\"|\"$", ""));
            holder.Service_Category.setText(jobj.get("category").toString().replaceAll("^\"|\"$", ""));

            final JsonObject jImages = (JsonObject) jobj.get("images");

            try {
                /*Glide.with(getContext())
                .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jImages.get("pics").toString().replaceAll("^\"|\"$", ""))
                .placeholder(R.drawable.ic_img_icon)
                .into(holder.Service_Image);*/

                Ion.with(getContext())
                        .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jImages.get("pics").toString().replaceAll("^\"|\"$", ""))
                        .withBitmap()
                        .placeholder(R.raw.loading)
                        .error(R.drawable.error)
                        .animateLoad(R.anim.zoom_in)
                        .animateIn(R.anim.fade_in)
                        .intoImageView(holder.Service_Image);
            }catch (Exception c){}


            holder.ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jobj.get("mobile").toString().replaceAll("^\"|\"$", "")));
                    startActivity(intent);
                }
            });
            holder.ivWhatssapp.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        Uri mUri=Uri.parse("smsto:" + jobj.get("mobile").toString().replaceAll("^\"|\"$", ""));
                        Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
                        mIntent.setPackage("com.whatsapp");
                        mIntent.putExtra("sms_body", "");
                        mIntent.putExtra("chat",true);
                        startActivity(mIntent);
                    }
                    catch (Exception e)
                    {Toast.makeText(getContext(),"No Whatsapp Found",Toast.LENGTH_LONG).show();}
                }
            });

            holder.Service_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                    dialog.setContentView(R.layout.single_service_dialog_view);
                    TextView tv_Category=(TextView)dialog.findViewById(R.id.tv_Category);
                    TextView tv_Name=(TextView)dialog.findViewById(R.id.tv_Name);
                    TextView tv_Holiday=(TextView)dialog.findViewById(R.id.tv_Holiday);
                    TextView tv_AvailableTime=(TextView)dialog.findViewById(R.id.tv_AvailableTime);
                    TextView tv_Addres=(TextView)dialog.findViewById(R.id.tv_Addres);
                    TextView tv_Mobile=(TextView)dialog.findViewById(R.id.tv_Mobile);
                    TextView tv_Email=(TextView)dialog.findViewById(R.id.tv_Email);
                    ImageView iv_call=(ImageView)dialog.findViewById(R.id.iv_call);
                    ImageView iv_offer_Img=(ImageView)dialog.findViewById(R.id.iv_offer_Img);

                    /*Glide.with(getContext())
                            .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jImages.get("pics").toString().replaceAll("^\"|\"$", ""))
                            .placeholder(R.drawable.ic_img_icon)
                            .into(iv_offer_Img);*/

                    Ion.with(getContext())
                            .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jImages.get("pics").toString().replaceAll("^\"|\"$", ""))
                            .withBitmap()
                            .placeholder(R.raw.loading)
                            .error(R.drawable.error)
                            .animateLoad(R.anim.zoom_in)
                            .animateIn(R.anim.fade_in)
                            .intoImageView(iv_offer_Img);

                    JsonObject jServiceSchedule = jobj.getAsJsonObject("service_time");
                    JsonObject jServicAddress = jobj.getAsJsonObject("address");

                    tv_Category.setText(jobj.get("category").toString().replaceAll("^\"|\"$", ""));
                    tv_Name.setText(jobj.get("serviceprovidername").toString().replaceAll("^\"|\"$", ""));
                    tv_Holiday.setText(jServiceSchedule.get("holiday").toString().replaceAll("^\"|\"$", ""));
                    tv_AvailableTime.setText(jServiceSchedule.get("time_from").toString().replaceAll("^\"|\"$", "")+" to "+jServiceSchedule.get("time_to").toString().replaceAll("^\"|\"$", ""));
                    tv_Addres.setText(jServicAddress.get("address").toString().replaceAll("^\"|\"$", "")+", "+jServicAddress.get("city").toString().replaceAll("^\"|\"$", ""));
                    tv_Mobile.setText(jobj.get("mobile").toString().replaceAll("^\"|\"$", ""));
                    tv_Email.setText(jobj.get("email").toString().replaceAll("^\"|\"$", ""));

                    iv_call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jobj.get("mobile").toString().replaceAll("^\"|\"$", ""))));
                        }
                    });
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
        if(common_variable.Refresh_Service == 1)
        {Refresh_service();}
    }
}
