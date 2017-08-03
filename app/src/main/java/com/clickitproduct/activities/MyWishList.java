package com.clickitproduct.activities;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.commonutil.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.NumberFormat;
import java.util.Locale;

public class MyWishList extends AppCompatActivity
{

    RecyclerView recycler_view;
    Toolbar toolbar;
    RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wishlist");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty("user_id", common_variable.User_id);
        jsonParam.addProperty("platform", "1");

        Ion.with(getApplicationContext())
        .load(common_variable.main_web_url+"/user/mywishlist_android")
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try
                {
                    if(result.get("success").toString().equals("true")) {
                        mAdapter = new RecyclerViewAdapter(result.getAsJsonArray("data"));
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_view.setLayoutManager(mLayoutManager);
                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                        recycler_view.setAdapter(mAdapter);
                    }
                    else
                    {
                        finish();
                        Toast.makeText(getApplicationContext(), "no record found", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex){}
            }
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
        private JsonArray pList;
        JsonObject jobj;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {
            public TextView Product_Name, Product_Price, Product_Desc;
            public ImageView Product_Image, btn_edit, btn_del;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                Product_Name = (TextView) itemView.findViewById(R.id.Product_Name);
                Product_Price = (TextView) itemView.findViewById(R.id.Product_Price);
                Product_Desc = (TextView) itemView.findViewById(R.id.Product_Desc);
                Product_Image = (ImageView) itemView.findViewById(R.id.Product_Image);
                btn_edit = (ImageView) itemView.findViewById(R.id.btn_edit);
                btn_del = (ImageView) itemView.findViewById(R.id.btn_del);

                Product_Desc.setVisibility(View.GONE);
                btn_edit.setVisibility(View.GONE);
            }
        }

        public RecyclerViewAdapter(JsonArray pList) {
            this.pList = pList;
        }


        @Override
        public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product_history_view, parent, false);
            return new RecyclerViewAdapter.RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.RecyclerViewHolder holder, final int position) {
            jobj = (JsonObject) pList.get(position);

            holder.Product_Name.setText(jobj.get("product_name").toString().replaceAll("^\"|\"$", ""));

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(jobj.get("product_price").toString().replaceAll("^\"|\"$", "")));
            holder.Product_Price.setText(moneyString);

           /* Glide.with(getApplicationContext())
            .load(common_variable.main_web_url+"/uploads/products/"+jobj.get("product_img").toString().replaceAll("^\"|\"$", ""))
            .asBitmap()
            .centerCrop()
            .into(new BitmapImageViewTarget(holder.Product_Image)
            {
                @Override
                protected void setResource(Bitmap resource)
                {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.Product_Image.setImageDrawable(circularBitmapDrawable);
                }
            });*/

            Ion.with(MyWishList.this)
                    .load(common_variable.main_web_url+"/uploads/products/"+jobj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.Product_Image);


            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    jobj = (JsonObject) pList.get(position);

                    final ProgressDialog dialog = new ProgressDialog(MyWishList.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("wait...");
                    dialog.show();

                    JsonObject jsonParam = new JsonObject();
                    jsonParam.addProperty("user_id", common_variable.User_id);
                    jsonParam.addProperty("_id", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                    jsonParam.addProperty("platform", "1");

                    Log.e("sssssssss iii", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));

                    Ion.with(getApplicationContext())
                    .load(common_variable.main_web_url + "/user/deletefrom_wishlist_android")
                    .progressDialog(dialog)
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            JsonObject jsonParam = new JsonObject();
                            jsonParam.addProperty("user_id", common_variable.User_id);
                            jsonParam.addProperty("platform", "1");
                            Ion.with(getApplicationContext())
                            .load(common_variable.main_web_url + "/user/mywishlist_android")
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    try {
                                        if(result.get("success").toString().equals("true")) {
                                            mAdapter = new RecyclerViewAdapter(result.getAsJsonArray("data"));
                                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                            recycler_view.setLayoutManager(mLayoutManager);
                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                            recycler_view.setAdapter(mAdapter);
                                        }
                                        else
                                        {
                                            finish();
                                            Toast.makeText(getApplicationContext(), "no record found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (Exception ex){}
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return pList.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
