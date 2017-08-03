package com.clickitproduct.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickitproduct.R;
import com.clickitproduct.activities.New_Shop_View_Activity;
import com.clickitproduct.commonutil.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_user_notifications extends Fragment
{
    View rootview=null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    TextView tv_NoData;

    public static Fragment_user_notifications newInstance(int instance)
    {
        Bundle args = new Bundle();
        Fragment_user_notifications f1 = new Fragment_user_notifications();
        f1.setArguments(args);
        return f1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView =(RecyclerView) rootview.findViewById(R.id.recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        tv_NoData = (TextView) rootview.findViewById(R.id.tv_NoData);
        tv_NoData.setText("No Notifications");

        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty("user_id", common_variable.User_id);
        jsonParam.addProperty("platform", "1");

        Ion.with(getContext())
        .load(common_variable.main_web_url+"/user/notification")
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try
                {
                    if(result.getAsJsonArray("data").size() > 0) {
                        tv_NoData.setVisibility(View.GONE);
                        mAdapter = new RecyclerViewAdapter(result.getAsJsonArray("data"));
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }else
                    {
                        tv_NoData.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception ex)
                {
                    tv_NoData.setVisibility(View.VISIBLE);
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                mAdapter.removeItem(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return  rootview;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;
        JsonObject jobj;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView txt_noti;
            public ImageView iv_dlt_noti, iv_noti;
            LinearLayout la_noti;

            public RecyclerViewHolder(View itemView)
            {
                super(itemView);
                txt_noti = (TextView) itemView.findViewById(R.id.txt_noti);
                iv_dlt_noti = (ImageView)itemView.findViewById(R.id.iv_dlt_noti);
                iv_noti = (ImageView)itemView.findViewById(R.id.iv_noti);
                la_noti = (LinearLayout)itemView.findViewById(R.id.la_noti);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList;  }

        @Override
        public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification_view, parent, false);
            return new RecyclerViewAdapter.RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.RecyclerViewHolder holder, final int position)
        {
            jobj = (JsonObject) pList.get(position);

            try {
                holder.txt_noti.setText(jobj.get("message").toString().replaceAll("^\"|\"$", ""));
            }
            catch (Exception ee){}

            /*Picasso.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/"+jobj.get("img_path").toString().replaceAll("^\"|\"$", ""))
                    .transform(new CircleTransform())
                    .into(holder.iv_noti);*/

            Ion.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/"+jobj.get("img_path").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.iv_noti);

            holder.la_noti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    jobj = (JsonObject) pList.get(position);
                    Intent i = new Intent(getContext(), New_Shop_View_Activity.class);
                    i.putExtra("Shop_Id", jobj.get("shop_id").toString().replaceAll("^\"|\"$", ""));
                    if(jobj.get("flag").toString().replaceAll("^\"|\"$", "").equals("1")) {
                        i.putExtra("focusParticularTab", 2);
                    }
                    else{
                        i.putExtra("focusParticularTab", 1);
                    }
                    startActivity(i);
                }
            });

            holder.iv_dlt_noti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog dialog = new ProgressDialog(getContext());
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("wait...");
                    dialog.show();

                    JsonObject jsonParamLogin = new JsonObject();
                    jsonParamLogin.addProperty("id", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                    jsonParamLogin.addProperty("platform", "1");

                    Ion.with(getContext())
                    .load(common_variable.main_web_url + "/user/removenotification")
                    .progressDialog(dialog)
                    .setJsonObjectBody(jsonParamLogin)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result)
                        {

                            common_variable.Refresh_noti_count = 1;
                            JsonObject jsonParam = new JsonObject();
                            jsonParam.addProperty("user_id", sharedpreferences.getString("user_id",null).replaceAll("^\"|\"$",""));
                            jsonParam.addProperty("platform", "1");
                            Ion.with(getContext())
                            .load(common_variable.main_web_url+"/user/notification")
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    try {
                                        mAdapter = new RecyclerViewAdapter(result.getAsJsonArray("data"));
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                        recyclerView.setLayoutManager(mLayoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(mAdapter);
                                    }catch (Exception ec){}
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount()
        {   return pList.size();    }

        public void removeItem(final int position) {

            JsonObject jj = (JsonObject) pList.get(position);

            JsonObject jsonParamLogin = new JsonObject();
            jsonParamLogin.addProperty("id", jj.get("_id").toString().replaceAll("^\"|\"$", ""));
            jsonParamLogin.addProperty("platform", "1");

            Ion.with(getContext())
            .load(common_variable.main_web_url + "/user/removenotification")
            .setJsonObjectBody(jsonParamLogin)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    pList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
}
