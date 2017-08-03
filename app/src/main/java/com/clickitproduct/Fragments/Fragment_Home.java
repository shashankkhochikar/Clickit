package com.clickitproduct.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.activities.Main_Activity;
import com.clickitproduct.activities.ProductSearchList;
import com.clickitproduct.activities.ShopViewActivityNew;
import com.clickitproduct.commonutil.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.NumberFormat;
import java.util.Locale;

public class Fragment_Home extends Fragment
{
    View rootview=null;
    RecyclerViewAdapter mAdapter;
    LinearLayout main_layout;
    LinearLayout.LayoutParams params;
    TextView tvMore[];
    TextView moreTextView;

    public static Fragment_Home newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Home f1 = new Fragment_Home();
        f1.setArguments(args);
        return f1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.fragment_home, container, false);
        main_layout = (LinearLayout)rootview.findViewById(R.id.main_layout);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        setProdDataDynamic();

//        txt_sthirt = (TextView) rootview.findViewById(R.id.txt_tshirt);
//        txt_furniture = (TextView) rootview.findViewById(R.id.txt_furniture);
//        txt_mobile = (TextView) rootview.findViewById(R.id.txt_mobile);
//        txt_1=(TextView) rootview.findViewById(R.id.editText1);
//        txt_2=(TextView) rootview.findViewById(R.id.editText2);
//        txt_3=(TextView) rootview.findViewById(R.id.editText3);
        //txt_homeApplience = (TextView) rootview.findViewById(R.id.txt_homeApplience);

       /* btnGrabCop = (Button) rootview.findViewById(R.id.btnGrabCop);
        btnGrabCop.setVisibility(View.GONE);
        laFashion = (LinearLayout) rootview.findViewById(R.id.laFashion);
        laMobile = (LinearLayout) rootview.findViewById(R.id.laMobile);
        laHardware = (LinearLayout) rootview.findViewById(R.id.laHardware);
        laFurniture = (LinearLayout) rootview.findViewById(R.id.laFurniture);
        laHomeAppliances = (LinearLayout) rootview.findViewById(R.id.laHomeAppliances);
        laGym = (LinearLayout) rootview.findViewById(R.id.laGym);
*/
        /*ScrollView homescroll = (ScrollView) rootview.findViewById(R.id.homescroll);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            homescroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                    setProdDataDynamic();

                    //btnGrabCop.setVisibility(View.GONE);
                   *//* Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            btnGrabCop.setVisibility(View.VISIBLE);
                        }
                    }, 1000);*//*
                }
            });
        }*/

//        rv_Computer_Accesories = (RecyclerView)rootview.findViewById(R.id.recyclerView_Computer_Accesories);
//        rv_Art_Gallery = (RecyclerView)rootview.findViewById(R.id.recycle_Art_Gallery);
//        rv_Furniture = (RecyclerView)rootview.findViewById(R.id.recycle_Furniture);

        //getProductData();


       /* laFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });

        laMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });

        laHardware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });

        laFurniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });

        laHomeAppliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });

        laGym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });*/

        /*btnGrabCop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getContext(), CouponsGrabNearest.class));
            }
        });*/

       /* txt_sthirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Main_Activity.str_searched_keyword = ""+tmp1.get("_id").toString().replaceAll("^\"|\"$", "");
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });
        txt_furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main_Activity.str_searched_keyword = ""+tmp2.get("_id").toString().replaceAll("^\"|\"$", "");
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });

        txt_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main_Activity.str_searched_keyword = ""+tmp3.get("_id").toString().replaceAll("^\"|\"$", "");
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });*/

       /* txt_homeApplience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });*/

        return rootview;
    }


    public void setTvMoreOnclick(TextView tvMor, final String cat)
    {
        tvMor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Activity.str_searched_keyword = ""+cat;
                startActivity(new Intent(getContext(), ProductSearchList.class));
            }
        });
    }

    public void setProdDataDynamic()
    {
        try{
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("","");
            Ion.with(getContext())
                    .load(common_variable.main_web_url+"/search/product_by_shop_category")
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result)
                        {
                            try
                            {
                                JsonArray proData = result.getAsJsonArray("data");

                                tvMore = new TextView[proData.size()];
                                for(int c = 0; c<proData.size(); c++) {

                                    final JsonObject jsonObj = (JsonObject) proData.get(c);

                                    LinearLayout hori_layout = new LinearLayout(getActivity());
                                    hori_layout.setOrientation(LinearLayout.HORIZONTAL);
                                    hori_layout.setPadding(10,10,10,10);

                                    LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
                                    childParam1.weight = 0.08f;

                                    TextView tv = new TextView(getActivity());
                                    tv.setId(c);
                                    tv.setText(jsonObj.get("_id").toString().replaceAll("^\"|\"$", ""));
                                    tv.setTextColor(getResources().getColor(R.color.textColor));
                                    tv.setTextSize(16);
                                    tv.setPadding(5,5,5,0);
                                    tv.setLayoutParams(childParam1);
                                    hori_layout.addView(tv);

                                    tvMore[c] = new TextView(getContext());
                                    tvMore[c].setText("More");
                                    tvMore[c].setTextColor(getResources().getColor(R.color.textColor));
                                    tvMore[c].setPadding(5,5,10,0);
                                    tvMore[c].setTextSize(16);
                                    hori_layout.addView(tvMore[c]);

                                    main_layout.addView(hori_layout);

                                    setTvMoreOnclick(tvMore[c], jsonObj.get("_id").toString().replaceAll("^\"|\"$", ""));

                                    RecyclerView rv = new RecyclerView(getActivity());
                                    rv.setId(c);
                                    main_layout.addView(rv);

                                    mAdapter = new RecyclerViewAdapter((JsonArray) jsonObj.get("products"));
                                    RecyclerView.LayoutManager lm1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                    rv.setLayoutManager(lm1);
                                    rv.setItemAnimator(new DefaultItemAnimator());
                                    rv.setAdapter(mAdapter);
                                }

//                                product_count = proData.size();
//                                Log.e("product_count after", product_count+"");

                                /*tmp1 = (JsonObject) proData.get(0);
                                txt_1.setText(""+tmp1.get("_id").toString().replaceAll("^\"|\"$", ""));
                                Computer_Accesories = (JsonArray) tmp1.get("products");
                                Log.e("00",Computer_Accesories.toString());
                                mAdapter_Computer_Accesories = new RecyclerViewAdapter(Computer_Accesories);
                                RecyclerView.LayoutManager lm1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                rv_Computer_Accesories.setLayoutManager(lm1);
                                rv_Computer_Accesories.setItemAnimator(new DefaultItemAnimator());
                                rv_Computer_Accesories.setAdapter(mAdapter_Computer_Accesories);

                                tmp2 = (JsonObject) proData.get(2);
                                txt_2.setText(""+tmp2.get("_id").toString().replaceAll("^\"|\"$", ""));
                                Art_Gallery = (JsonArray) tmp2.get("products");
                                Log.e("01",Art_Gallery.toString());
                                mAdapter_Art_Gallery = new RecyclerViewAdapter(Art_Gallery);
                                RecyclerView.LayoutManager lm2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                rv_Art_Gallery.setLayoutManager(lm2);
                                rv_Art_Gallery.setItemAnimator(new DefaultItemAnimator());
                                rv_Art_Gallery.setAdapter(mAdapter_Art_Gallery);

                                tmp3 = (JsonObject) proData.get(3);
                                txt_3.setText(""+tmp3.get("_id").toString().replaceAll("^\"|\"$", ""));
                                Furniture = (JsonArray) tmp3.get("products");
                                Log.e("02",Furniture.toString());
                                mAdapter_Furniture = new RecyclerViewAdapter(Furniture);
                                RecyclerView.LayoutManager lm3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                rv_Furniture.setLayoutManager(lm3);
                                rv_Furniture.setItemAnimator(new DefaultItemAnimator());
                                rv_Furniture.setAdapter(mAdapter_Furniture);*/
                            }
                            catch (Exception e2)
                            {   Toast.makeText(getContext(),"Problem While Get Data",Toast.LENGTH_LONG).show();     Log.e("03",e2.getMessage().toString());}
                        }
                    });

        }
        catch (Exception x)
        {   Log.e("000",x.getMessage().toString());     }
    }

    public void getProductData()
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
            .load(common_variable.main_web_url+"/search/product_search")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {
                        JsonArray productData = result.getAsJsonArray("data");
                        Main_Activity.str_searched_keyword="";
                    }
                    catch (Exception e2){}
                }
            });
        }
        catch (Exception e)
        {}
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList = new JsonArray();

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView txt_title, txt_subtiext, product_views;
            ImageView iv_product_menu, imgThumb;
            LinearLayout laProduct;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                txt_title = (TextView) itemView.findViewById(R.id.title);
                txt_subtiext = (TextView) itemView.findViewById(R.id.count);
                laProduct = (LinearLayout)itemView.findViewById(R.id.laProduct);
                imgThumb = (ImageView) itemView.findViewById(R.id.thumbnail1);
                product_views = (TextView) itemView.findViewById(R.id.product_views);
                iv_product_menu = (ImageView) itemView.findViewById(R.id.iv_product_menu);
            }
        }

        public RecyclerViewAdapter(JsonArray pList) {
            this.pList = pList;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_cart, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
            final JsonObject jobj = (JsonObject) pList.get(position);
            final JsonObject jProductsObj = jobj;

            holder.iv_product_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(holder.iv_product_menu, position, jProductsObj, jobj.get("shop_id").toString().replaceAll("^\"|\"$", ""));
                }
            });

            String title = "";

            if(jProductsObj.get("product_name").toString().replaceAll("^\"|\"$", "").length()>16)
                title = jProductsObj.get("product_name").toString().replaceAll("^\"|\"$", "").substring(0,15)+"...";
            else
                title = jProductsObj.get("product_name").toString().replaceAll("^\"|\"$", "");

            holder.txt_title.setText(title);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(jProductsObj.get("product_price").toString().replaceAll("^\"|\"$", "")));
            holder.txt_subtiext.setText(moneyString);

            try {
                holder.product_views.setText("Views "+jProductsObj.get("totalviews").toString().replaceAll("^\"|\"$", ""));
            }
            catch (Exception ec){
                holder.product_views.setText("Views 0");
            }

           /* Glide.with(getContext())
                    .load(common_variable.main_web_url + "/uploads/products/" + jProductsObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .into(holder.imgThumb);*/

            Ion.with(getContext())
                    .load(common_variable.main_web_url + "/uploads/products/" + jProductsObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.imgThumb);

            holder.imgThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(jProductsObj.get("totalviews").toString().replaceAll("^\"|\"$", "").equals("0"))
                    {
                        holder.product_views.setText("Views "+Integer.parseInt(jProductsObj.get("totalviews").toString().replaceAll("^\"|\"$", "")+1));
                    }

                    /*try{
                        JsonObject jsonParam = new JsonObject();
                        jsonParam.addProperty("_id", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("product_id", jProductsObj.get("_id").toString().replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("user_id", common_variable.User_id);
                        jsonParam.addProperty("username", common_variable.User_Name);
                        jsonParam.addProperty("profile_pic", common_variable.User_Profile_Pic);

                        Log.e("mmmmmmmmmmm hhh param", jsonParam.toString());

                        Ion.with(getContext())
                        .load(common_variable.main_web_url+"/product/product_views")
                        .setJsonObjectBody(jsonParam)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>()
                        {
                            @Override
                            public void onCompleted(Exception e, JsonObject result)
                            {
                                try{
                                    Log.e("mmmmmmmmmmmmmmmmmm hhh", result.toString());
                                    if(result.get("success").toString().equals("true")) {
                                        holder.product_views.setText("Views " + result.get("totalproductviews").toString().replaceAll("^\"|\"$", ""));
                                    }
                                }
                                catch (Exception e1){}
                            }
                        });
                    }catch (Exception e){}*/

                    common_variable.jShopProductObj = jProductsObj;

                    Intent i = new Intent(getContext(), ShopViewActivityNew.class);
                    i.putExtra("Shop_Id", jobj.get("shop_id").toString().replaceAll("^\"|\"$", ""));
                    i.putExtra("focusParticularTab", 1);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return pList.size();
        }
    }

    private void showPopupMenu(View view, int position, final JsonObject proObj, final String shop_id) {
        PopupMenu popup = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());

        Menu mnu = popup.getMenu();
        mnu.removeItem(R.id.share);
        mnu.removeItem(R.id.sendEnquiry);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_wishlist:
                        try
                        {
                            JsonObject jsonParam = new JsonObject();
                            jsonParam.addProperty("shop_id", shop_id);
                            jsonParam.addProperty("user_id", common_variable.User_id);
                            jsonParam.addProperty("username", common_variable.User_Name);
                            jsonParam.addProperty("profile_pic", common_variable.User_Profile_Pic);
                            jsonParam.addProperty("product_id", proObj.get("_id").toString().replaceAll("^\"|\"$", ""));
                            jsonParam.addProperty("product_img", proObj.get("product_img").toString().replaceAll("^\"|\"$", ""));
                            jsonParam.addProperty("product_name", proObj.get("product_name").toString().replaceAll("^\"|\"$", ""));
                            jsonParam.addProperty("product_price", proObj.get("product_price").toString().replaceAll("^\"|\"$", ""));

                            Ion.with(getContext())
                            .load(common_variable.main_web_url+"/user/addto_wishlist_android")
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>()
                            {
                                @Override
                                public void onCompleted(Exception e, JsonObject result)
                                {
                                }
                            });
                        }
                        catch (Exception e)
                        {}
                        return true;

                    default:
                }
                return false;
            }
        });

        popup.show();
    }

    @Override
    public void onResume() {
        super.onResume();

//        product_count = 0;

        if(common_variable.Refresh_Product == 1) {
            getProductData();
            common_variable.Refresh_Product = 0;
        }
    }
}
