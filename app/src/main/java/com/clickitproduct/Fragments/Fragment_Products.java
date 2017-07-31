package com.clickitproduct.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class Fragment_Products extends Fragment
{
    View rootview=null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    FloatingActionButton fab_filter;
    String filter_main_category = "", filter_sub_category = "";
    ListView dialog_ListView;

    public static Fragment_Products newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Products f1 = new Fragment_Products();
        f1.setArguments(args);
        return f1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.fragment_product_list, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        fab_filter = (FloatingActionButton)rootview.findViewById(R.id.fab_filter);

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
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        JsonArray productData = result.getAsJsonArray("data");
                        if(productData.size()>0) {
                            mAdapter = new RecyclerViewAdapter(productData);
                            GridLayoutManager lLayout;
                            lLayout = new GridLayoutManager(getContext(), 2);
                            recyclerView.setLayoutManager(lLayout);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                    catch (Exception ec){}
                }
            });
        }
        catch (Exception e) {}

        fab_filter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                dialog.setContentView(R.layout.filter_product);
                Button btnApplyFilter = (Button)dialog.findViewById(R.id.btnApplyFilter);
                LinearLayout la_choose_category = (LinearLayout)dialog.findViewById(R.id.la_choose_category);
                final TextView tv_main_cat = (TextView)dialog.findViewById(R.id.tv_main_cat);
                final TextView tv_sub_cat = (TextView)dialog.findViewById(R.id.tv_sub_cat);
                final EditText et_min_price = (EditText)dialog.findViewById(R.id.et_min_price);
                final EditText et_max_price = (EditText)dialog.findViewById(R.id.et_max_price);

                la_choose_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final ProgressDialog pDialog = new ProgressDialog(getContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.setCancelable(false);
                        pDialog.setMessage("Wait...");
                        pDialog.show();

                        try{
                            JsonObject jsonParam = new JsonObject();
                            Ion.with(getContext())
                            .load(common_variable.main_web_url+"/admin/getshopcategory")
                            .progressDialog(pDialog)
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>()
                            {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    pDialog.dismiss();
                                    try {
                                        JsonArray Shop_Category = result.getAsJsonArray("data");
                                        final String []shop_cats = new String[Shop_Category.size()];
                                        for (int c=0; c<Shop_Category.size(); c++)
                                        {
                                            JsonObject objCat = (JsonObject) Shop_Category.get(c);
                                            shop_cats[c] = objCat.get("category_name").toString().replaceAll("^\"|\"$", "");
                                        }

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                        builder.setItems(shop_cats, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                filter_main_category = shop_cats[i];

                                                final ProgressDialog pDialog = new ProgressDialog(getContext());
                                                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                pDialog.setCancelable(false);
                                                pDialog.setMessage("Wait...");
                                                pDialog.show();

                                                JsonObject jsonParam = new JsonObject();
                                                jsonParam.addProperty("category", filter_main_category);
                                                jsonParam.addProperty("platform", 1+"");

                                                Ion.with(getContext())
                                                .load(common_variable.main_web_url+"/product/product_category_list")
                                                .progressDialog(pDialog)
                                                .setJsonObjectBody(jsonParam)
                                                .asJsonObject()
                                                .setCallback(new FutureCallback<JsonObject>()
                                                {
                                                    @Override
                                                    public void onCompleted(Exception e, JsonObject result)
                                                    {
                                                        pDialog.dismiss();
                                                        try {
                                                            JsonArray prod_cat = result.getAsJsonArray("data");
                                                            JsonObject jCat;
                                                            final String sub_category[] = new String[prod_cat.size()];

                                                            for (int i = 0; i < prod_cat.size(); i++) {
                                                                jCat = (JsonObject) prod_cat.get(i);
                                                                sub_category[i] = jCat.get("sub_categories").toString().replaceAll("^\"|\"$", "");
                                                            }

                                                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                                                            LayoutInflater inflater = getActivity().getLayoutInflater();
                                                            View dialogView = inflater.inflate(R.layout.list_view, null);
                                                            dialogBuilder.setView(dialogView);

                                                            dialogBuilder.setPositiveButton("OK",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            long[] stdrno = dialog_ListView.getCheckItemIds();
                                                                            String cats = "";
                                                                            for (int i = 0; i < stdrno.length; i++) {
                                                                                cats = cats + dialog_ListView.getItemAtPosition((int) stdrno[i]);
                                                                                if (i != stdrno.length - 1)
                                                                                    cats = cats + ",";
                                                                            }
                                                                            filter_sub_category = cats;

                                                                            tv_main_cat.setText(filter_main_category);
                                                                            tv_sub_cat.setText(filter_sub_category);

                                                                            dialog.dismiss();
                                                                        }
                                                                    });

                                                            dialogBuilder.setNegativeButton("cancel",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });

                                                            AlertDialog alertDialog = dialogBuilder.create();
                                                            alertDialog.show();

                                                            dialog_ListView = (ListView)alertDialog.findViewById(R.id.listView);
                                                            dialog_ListView.setChoiceMode(dialog_ListView.CHOICE_MODE_MULTIPLE); // used for click check box
                                                            dialog_ListView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_checked, sub_category));

                                                        }catch (Exception ec){}
                                                    }
                                                });
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                    catch (Exception e2){}
                                }
                            });
                        }
                        catch (Exception e)
                        {}
                    }
                });

                btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try
                        {
                            JsonObject jsonParam = new JsonObject();

                            if(!filter_sub_category.equals(""))
                                jsonParam.addProperty("sub_category", filter_sub_category+"");

                            if(!et_min_price.getText().toString().equals("") && !et_max_price.getText().toString().equals("")) {
                                jsonParam.addProperty("min_price", et_min_price.getText().toString() + "");
                                jsonParam.addProperty("max_price", et_max_price.getText().toString() + "");
                            }

                            Ion.with(getContext())
                            .load(common_variable.main_web_url+"/search/product_filter_android")
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>()
                            {
                                @Override
                                public void onCompleted(Exception e, JsonObject result)
                                {
                                    try {
                                        JsonArray productData = result.getAsJsonArray("data");
                                        if(productData.size()>0){
                                        mAdapter = new RecyclerViewAdapter(productData);
                                        GridLayoutManager lLayout;
                                        lLayout = new GridLayoutManager(getContext(), 2);
                                        recyclerView.setLayoutManager(lLayout);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(mAdapter);}
                                    }catch (Exception c){}
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

        return rootview;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;
        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView product_name, product_price, product_views;
            public ImageView product_image, iv_product_menu;

            public RecyclerViewHolder(View view)
            {
                super(view);
                product_name = (TextView) view.findViewById(R.id.product_name);
                product_price = (TextView) view.findViewById(R.id.product_price);
                product_image = (ImageView) view.findViewById(R.id.product_image);
                product_views = (TextView)view.findViewById(R.id.product_views);
                iv_product_menu = (ImageView)view.findViewById(R.id.iv_product_menu);
            }
        }
        public RecyclerViewAdapter(JsonArray pList) {
            this.pList = pList;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product_view, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position)
        {
            final JsonObject jobj = (JsonObject)pList.get(position);

            final JsonObject jProductsObj = (JsonObject) jobj.get("products");

            holder.product_name.setText(jProductsObj.get("product_name").toString().replaceAll("^\"|\"$", ""));

            try {
                holder.product_views.setText("Views "+jProductsObj.get("totalviews").toString().replaceAll("^\"|\"$", ""));
            }
            catch (Exception ec){
                holder.product_views.setText("Views 0");
            }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(jProductsObj.get("product_price").toString().replaceAll("^\"|\"$", "")));
            holder.product_price.setText(moneyString);

            /*Glide.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/products/"+jProductsObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .into(holder.product_image);*/

            Ion.with(getContext())
                .load(common_variable.main_web_url+"/uploads/products/"+jProductsObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                .withBitmap()
                .placeholder(R.raw.loading)
                .error(R.drawable.error)
                .animateLoad(R.anim.zoom_in)
                .animateIn(R.anim.fade_in)
                .intoImageView(holder.product_image);

            holder.product_image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try{
                        JsonObject jsonParam = new JsonObject();
                        jsonParam.addProperty("_id", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("product_id", jProductsObj.get("_id").toString().replaceAll("^\"|\"$", ""));
                        jsonParam.addProperty("user_id", common_variable.User_id);
                        jsonParam.addProperty("username", common_variable.User_Name);
                        jsonParam.addProperty("profile_pic", common_variable.User_Profile_Pic);

                        Log.e("mmmmmmmmmmm ppp param", jsonParam.toString());

                        Ion.with(getContext())
                        .load(common_variable.main_web_url+"/product/product_views")
                        .setJsonObjectBody(jsonParam)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>()
                        {
                            @Override
                            public void onCompleted(Exception e, JsonObject result)
                            {
                                Log.e("mmmmmmmmmmmmmmmmmm ppp", result.toString());

                                try{
                                    if(result.get("success").toString().equals("true")) {
                                        holder.product_views.setText("Views " + result.get("totalproductviews").toString().replaceAll("^\"|\"$", ""));
                                    }
                                }catch (Exception e1) {}
                            }
                        });
                    }
                    catch (Exception ex){}

                    common_variable.jShopProductObj = jProductsObj;

                    Intent i = new Intent(getContext(), New_Shop_View_Activity.class);
                    i.putExtra("Shop_Id", jobj.get("shop_id").toString().replaceAll("^\"|\"$",""));
                    i.putExtra("focusParticularTab", 1);
                    startActivity(i);
                }
            });

            holder.iv_product_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(v.getContext(),v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.add_to_wishlist:
                                    try
                                    {
                                        JsonObject jsonParam = new JsonObject();
                                        jsonParam.addProperty("shop_id", jobj.get("shop_id").toString().replaceAll("^\"|\"$",""));
                                        jsonParam.addProperty("user_id", common_variable.User_id);
                                        jsonParam.addProperty("username", common_variable.User_Name);
                                        jsonParam.addProperty("profile_pic", common_variable.User_Profile_Pic);
                                        jsonParam.addProperty("product_id", jProductsObj.get("_id").toString().replaceAll("^\"|\"$", ""));
                                        jsonParam.addProperty("product_img", jProductsObj.get("product_img").toString().replaceAll("^\"|\"$", ""));
                                        jsonParam.addProperty("product_name", jProductsObj.get("product_name").toString().replaceAll("^\"|\"$", ""));
                                        jsonParam.addProperty("product_price", jProductsObj.get("product_price").toString().replaceAll("^\"|\"$", ""));

                                        Ion.with(getContext())
                                        .load(common_variable.main_web_url+"/user/addto_wishlist_android")
                                        .setJsonObjectBody(jsonParam)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>()
                                        {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result)
                                            {}
                                        });
                                    }
                                    catch (Exception e)
                                    {}
                                    return true;

                                case R.id.share:
                                    try
                                    {
                                        holder.product_image.buildDrawingCache();
                                        Bitmap icon = holder.product_image.getDrawingCache();
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");

                                        f.createNewFile();
                                        FileOutputStream fo = new FileOutputStream(f);
                                        fo.write(bytes.toByteArray());

                                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                                        share.putExtra(Intent.EXTRA_TEXT, jProductsObj.get("product_name").toString().replaceAll("^\"|\"$", "")+"\n"+" To view this download Clickit App "+Main_Activity.app_link);
                                        startActivity(Intent.createChooser(share, "Share Image"));
                                    } catch (IOException e) { e.printStackTrace();}
                                    return true;

                                default:
                            }
                            return false;
                        }
                    });
                    popup.show();
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
        if(common_variable.Refresh_Product == 1) {
            common_variable.Refresh_Product = 0;
        }
    }
}
