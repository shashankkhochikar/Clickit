package com.clickitproduct.User_Profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.clickitproduct.Classifieds.AddClassified;
import com.clickitproduct.R;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class UserClassifieds extends AppCompatActivity
{
    RecyclerView recyclerView;
    Toolbar mActionBarToolbar;
    RecyclerViewAdapter mAdapter;
    public static JsonObject classifiedJsonObject;
    Handler handler;
    Runnable r;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("My Classifieds");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getClassifiedData();
    }

    public void getClassifiedData()
    {
        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty("user_id", common_variable.User_id);
        jsonParam.addProperty("platform", "1");

        Ion.with(UserClassifieds.this)
        .load(common_variable.main_web_url+"/classified/classified_check")
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try
                {
                    Log.e("service_check", "Res " + result.toString() + "//" + result.get("data").toString());
                    mAdapter = new RecyclerViewAdapter(result.getAsJsonArray("data"));
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserClassifieds.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                }
                catch (Exception es)
                {

                }
            }
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;
        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView Ad_Title, Ad_Price, Ad_Description;
//            public CircularImageView classified_Image;
            LinearLayout layout_classified;
            ImageView btn_edit, btn_del, classified_Image;

            public RecyclerViewHolder(View view)
            {
                super(view);
                Ad_Title = (TextView) view.findViewById(R.id.Ad_Title);
                Ad_Price = (TextView) view.findViewById(R.id.Ad_Price);
                Ad_Description = (TextView) view.findViewById(R.id.Ad_Description);
                classified_Image = (ImageView) view.findViewById(R.id.classified_Image);
                layout_classified = (LinearLayout)view.findViewById(R.id.layout_classified);
                btn_edit = (ImageView)view.findViewById(R.id.btn_edit);
                btn_del = (ImageView)view.findViewById(R.id.btn_del);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList; Log.e("123","IN Recycle"+pList.size()); }

        @Override
        public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_classified_view, parent, false);
            return new RecyclerViewAdapter.RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.RecyclerViewHolder holder, int position)
        {
            classifiedJsonObject = (JsonObject)pList.get(position);
            final JsonObject jImages = (JsonObject) classifiedJsonObject.get("images");

            holder.Ad_Title.setText(classifiedJsonObject.get("classprod_name").toString().replaceAll("^\"|\"$", ""));
            holder.Ad_Price.setText(classifiedJsonObject.get("classprod_price").toString().replaceAll("^\"|\"$", "").replaceAll("\\[", "").replaceAll("\\]","") );
            holder.Ad_Description.setText(classifiedJsonObject.get("classprod_description").toString().replaceAll("^\"|\"$", ""));

            /*Glide.with(UserClassifieds.this)
            .load(common_variable.main_web_url+"/uploads/classifieds/"+jImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
            .into(holder.classified_Image);*/

            Ion.with(UserClassifieds.this)
                    .load(common_variable.main_web_url+"/uploads/classifieds/"+jImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.classified_Image);

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(UserClassifieds.this, AddClassified.class);
                    in.putExtra("editClassifieds", 1);
                    startActivity(in);
                }
            });

            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserClassifieds.this);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Are you sure you want to delete your service?");
                    alertDialog.setIcon(R.drawable.ic_alert);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dg, int which)
                        {
                            dg.dismiss();
                            final ProgressDialog dialog = new ProgressDialog(UserClassifieds.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("Loading...");
                            dialog.show();

                            JsonObject jsonParam = new JsonObject();
                            try
                            {
                                jsonParam.addProperty("_id", classifiedJsonObject.get("_id").toString().replaceAll("^\"|\"$", ""));
                                jsonParam.addProperty("platform", "1");

                                Ion.with(UserClassifieds.this)
                                        .load(common_variable.main_web_url+"/classified/classified_delete_android")
                                        .progressDialog(dialog)
                                        .setJsonObjectBody(jsonParam)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>()
                                        {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result)
                                            {
                                                dialog.dismiss();
                                                getClassifiedData();
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
                getClassifiedData();
            }
        };
        handler.postDelayed(r, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
