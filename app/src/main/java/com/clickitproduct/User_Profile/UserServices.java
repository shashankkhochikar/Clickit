package com.clickitproduct.User_Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickitproduct.R;
import com.clickitproduct.Services.ServicesForm;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.support.v7.app.AlertDialog;

public class
UserServices extends AppCompatActivity
{
    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    public static JsonObject serviceJsonObject;
    Toolbar mActionBarToolbar;
    Handler handler;
    Runnable r;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("My Services");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        getServiceData();
    }

    public void getServiceData()
    {
        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty("user_id", common_variable.User_id);
        jsonParam.addProperty("platform", "1");

        Log.e("service_check", "jsonParam " + jsonParam.toString());

        Ion.with(UserServices.this)
        .load(common_variable.main_web_url+"/serviceprovider/service_check")
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try
                {
                    mAdapter = new RecyclerViewAdapter(result.getAsJsonArray("data"));
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserServices.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                }
                catch (Exception es) {}
            }
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;
        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView Service_Provider_Name, Service_Category;
            LinearLayout layout_sevices;
            ImageView btn_edit, btn_del, Service_Image;

            public RecyclerViewHolder(View view)
            {
                super(view);
                Service_Provider_Name = (TextView) view.findViewById(R.id.Service_Provider_Name);
                Service_Category = (TextView) view.findViewById(R.id.Service_Category);
                Service_Image = (ImageView) view.findViewById(R.id.Service_Image);
                layout_sevices = (LinearLayout)view.findViewById(R.id.layout_sevices);
                btn_edit = (ImageView)view.findViewById(R.id.btn_edit);
                btn_del = (ImageView)view.findViewById(R.id.btn_del);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList; Log.e("123","IN Recycle"+pList.size()); }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_service_view, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position)
        {
            serviceJsonObject = (JsonObject)pList.get(position);

            holder.Service_Provider_Name.setText(serviceJsonObject.get("serviceprovidername").toString().replaceAll("^\"|\"$", ""));
            holder.Service_Category.setText(serviceJsonObject.get("category").toString().replaceAll("^\"|\"$", ""));

            final JsonObject jImages = (JsonObject) serviceJsonObject.get("images");

           /* Glide.with(UserServices.this)
                .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jImages.get("pics").toString().replaceAll("^\"|\"$", ""))
                .into(holder.Service_Image);*/

            Ion.with(UserServices.this)
                    .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jImages.get("pics").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.Service_Image);

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(UserServices.this, ServicesForm.class);
                    in.putExtra("editService", 1);
                    startActivity(in);
                }
            });

            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserServices.this);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Are you sure you want to delete your service?");
                    alertDialog.setIcon(R.drawable.ic_alert);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dg, int which)
                        {
                            dg.dismiss();

                            final ProgressDialog dialog = new ProgressDialog(UserServices.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("Loading...");
                            dialog.show();

                            JsonObject jsonParam = new JsonObject();
                            try
                            {
                                jsonParam.addProperty("_id", serviceJsonObject.get("_id").toString().replaceAll("^\"|\"$", ""));
                                jsonParam.addProperty("platform", "1");

                                Ion.with(UserServices.this)
                                .load(common_variable.main_web_url+"/serviceprovider/service_delete_android")
                                .progressDialog(dialog)
                                .setJsonObjectBody(jsonParam)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>()
                                {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result)
                                    {
                                        dialog.dismiss();
                                        getServiceData();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    alertDialog.setNegativeButton("NO",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which) {dialog.cancel();}
                            });
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount()
        {   return pList.size();    }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        handler = new Handler();
        r = new Runnable()
        {
            public void run()
            {
                getServiceData();
            }
        };
        handler.postDelayed(r, 1000);
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
