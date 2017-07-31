package com.clickitproduct.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.clickitproduct.R;
import com.clickitproduct.activities.Main_Activity;
import com.clickitproduct.activities.common_variable;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Fragment_Classifieds extends Fragment
{
    View rootview=null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    String seekBarValue = 1000+"", typeValue="Sell", rangeFromValue, rangeToValue, Categories;
    FloatingActionButton fab_filter;
    String city;
    TextView tv_NoData;
    Double lati=0.0, longi=0.0;

    public static Fragment_Classifieds newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Classifieds f1 = new Fragment_Classifieds();
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
        tv_NoData.setText("No Classifieds");

        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog_filter=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                dialog_filter.setContentView(R.layout.filter_classified);

                Button btnApplyFilter = (Button)dialog_filter.findViewById(R.id.btnApplyFilter);

                final SeekBar locationSeekBar = (SeekBar)dialog_filter.findViewById(R.id.seekBar);
                RadioGroup radioGroup = (RadioGroup) dialog_filter.findViewById(R.id.radio_group);
                final EditText et_range_from = (EditText) dialog_filter.findViewById(R.id.et_range_from);
                final EditText et_range_to = (EditText) dialog_filter.findViewById(R.id.et_range_to);
                final TextView tvSelectCity = (TextView)dialog_filter.findViewById(R.id.tvSelectCity);

                final ListView catList = (ListView) dialog_filter.findViewById(R.id.lv_Category);
                catList.setChoiceMode(catList.CHOICE_MODE_MULTIPLE); // used for click check box
                catList.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_checked, common_variable.Classified_Categories));

                catList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        long[] stdrno = catList.getCheckItemIds();
                        String cats = "";
                        for (int i = 0; i < stdrno.length; i++) {
                            cats = cats + catList.getItemAtPosition((int) stdrno[i]);
                            if (i != stdrno.length - 1)
                                cats = cats + ",";
                        }
                        Categories = cats;
                    }
                });

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

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == R.id.rb_Sell)
                        {
                            typeValue = "Sell";
                        }else if(checkedId == R.id.rb_Rent)
                        {
                            typeValue = "Rent";
                        }else
                        {
                            typeValue = "Other";
                        }
                    }
                });


                tvSelectCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            final ProgressDialog dialog = new ProgressDialog(getContext());
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("Loading...");
                            dialog.setCancelable(false);
                            dialog.show();

                            JsonObject jsonParam = new JsonObject();
                            Ion.with(getContext())
                            .load(common_variable.main_web_url+"/admin/getcities")
                            .progressDialog(dialog)
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>()
                            {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    try {
                                        dialog.dismiss();
                                        JsonArray cityArray = result.getAsJsonArray("data");
                                        final String []cities = new String[cityArray.size()];
                                        for (int c=0; c<cityArray.size(); c++)
                                        {
                                            JsonObject objCat = (JsonObject) cityArray.get(c);
                                            cities[c] = objCat.get("city_name").toString().replaceAll("^\"|\"$", "");
                                        }

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setItems(cities, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                tvSelectCity.setText(cities[i] + "");
                                                city = cities[i];
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
                            rangeFromValue = et_range_from.getText().toString();
                            rangeToValue = et_range_to.getText().toString();

                            JsonObject jsonParam = new JsonObject();
                            jsonParam.addProperty("searchkeyword", Main_Activity.str_searched_keyword);
                            jsonParam.addProperty("range", seekBarValue);
                            jsonParam.addProperty("category", Categories);
                            jsonParam.addProperty("min_price", rangeFromValue);
                            jsonParam.addProperty("max_price", rangeToValue);
                            jsonParam.addProperty("classprod_type", typeValue);
                            jsonParam.addProperty("lat", Main_Activity.current_Lattitude+"");
                            jsonParam.addProperty("lng", Main_Activity.current_Longitude+"");
                            jsonParam.addProperty("city", city+"");
                            jsonParam.addProperty("platform", "1");

                            Ion.with(getContext())
                            .load(common_variable.main_web_url + "/search/classifieds_filter_android")
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    try
                                    {
                                        dialog_filter.dismiss();
                                        JsonArray classifiedData = result.getAsJsonArray("data");
                                        mAdapter = new RecyclerViewAdapter(classifiedData);
                                        GridLayoutManager lLayout;
                                        lLayout = new GridLayoutManager(getContext(), 1);
                                        recyclerView.setLayoutManager(lLayout);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(mAdapter);
                                    } catch (Exception ed) {
                                        Toast.makeText(getContext(), "something error", Toast.LENGTH_SHORT).show();
                                    }
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
    public void onResume() {
        super.onResume();
        if(common_variable.Refresh_Classified == 1) {
            Refresh();
            common_variable.Refresh_Classified = 0;
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
            .load(common_variable.main_web_url+"/search/classified_search")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        if(result.getAsJsonArray("data").size()>0) {
                            tv_NoData.setVisibility(View.GONE);
                            JsonArray classifiedData = result.getAsJsonArray("data");
                            mAdapter = new RecyclerViewAdapter(classifiedData);
                            RecyclerView.LayoutManager lLayout = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(lLayout);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }else{
                            tv_NoData.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Exception ed){tv_NoData.setVisibility(View.VISIBLE);}
                }
            });
        }
        catch (Exception e) {}
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;
        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView AdTitle, Ad_Category;
            public ImageView Ad_Image, iv_Call;

            public RecyclerViewHolder(View view)
            {
                super(view);
                AdTitle = (TextView) view.findViewById(R.id.AdTitle);
                Ad_Category = (TextView) view.findViewById(R.id.Ad_Category);
                Ad_Image = (ImageView) view.findViewById(R.id.Ad_Image);
                iv_Call = (ImageView) view.findViewById(R.id.iv_Call);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList; }

        @Override
        public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_classified_view, parent, false);
            return new RecyclerViewAdapter.RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.RecyclerViewHolder holder, int position)
        {
            final JsonObject jobj = (JsonObject)pList.get(position);
            final JsonObject jobjImages = (JsonObject) jobj.get("images");
            final JsonObject jClassiAddress = jobj.getAsJsonObject("address");

            holder.AdTitle.setText(jobj.get("classprod_name").toString().replaceAll("^\"|\"$", ""));

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(jobj.get("classprod_price").toString().replaceAll("^\"|\"$", "")));
            holder.Ad_Category.setText(moneyString);

            /*Glide.with(getContext())
            .load(common_variable.main_web_url+"/uploads/classifieds/"+jobjImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
            .placeholder(R.drawable.ic_img_icon)
            .into(holder.Ad_Image);*/

            Ion.with(getContext())
                .load(common_variable.main_web_url+"/uploads/classifieds/"+jobjImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
                .withBitmap()
                .placeholder(R.raw.loading)
                .error(R.drawable.error)
                .animateLoad(R.anim.zoom_in)
                .animateIn(R.anim.fade_in)
                .intoImageView(holder.Ad_Image);

            holder.iv_Call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jobj.get("mobile").toString().replaceAll("^\"|\"$", "")));
                    startActivity(intent);
                }
            });

            holder.Ad_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                    dialog.setContentView(R.layout.single_classified_dialog_view);

                    TextView tv_AdName=(TextView)dialog.findViewById(R.id.tv_AdName);
                    TextView tv_Price=(TextView)dialog.findViewById(R.id.tv_Price);
                    TextView tv_Decription=(TextView)dialog.findViewById(R.id.tv_Decription);
                    TextView tv_Mobile=(TextView)dialog.findViewById(R.id.tv_Mobile);
                    TextView tv_Address=(TextView)dialog.findViewById(R.id.tv_Address);
                    final ImageView clasified_image = (ImageView)dialog.findViewById(R.id.clasified_image);
                    Gallery clasified_gallery = (Gallery) dialog.findViewById(R.id.clasified_gallery);
                    ImageView iv_call = (ImageView)dialog.findViewById(R.id.iv_call);
                    ImageView showDirection = (ImageView)dialog.findViewById(R.id.showDirection);

                    final ArrayList<String> classi_images = new ArrayList<String>();

                    /*Picasso.with(getContext())
                            .load(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
                            .resize(common_variable.screenWidth, common_variable.screenWidth)
                            .placeholder(R.drawable.ic_img_icon)
                            .into(clasified_image);*/

                    Ion.with(getContext())
                            .load(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
                            .withBitmap()
                            .placeholder(R.raw.loading)
                            .error(R.drawable.error)
                            .animateLoad(R.anim.zoom_in)
                            .animateIn(R.anim.fade_in)
                            .intoImageView(clasified_image);

                    HashMap<String, Uri> file_maps = new HashMap<String, Uri>();
                    try
                    {
                        if(!jobjImages.get("classified_img1").toString().equals(""))
                        {
                            file_maps.put("one", Uri.parse(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img1").toString().replaceAll("^\"|\"$", "")));
                            classi_images.add(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""));
                        }
                    }
                    catch (Exception e1)
                    {}
                    try {
                        if(!jobjImages.get("classified_img2").toString().equals(""))
                        {
                            file_maps.put("two", Uri.parse(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img2").toString().replaceAll("^\"|\"$", "")));
                            classi_images.add(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img2").toString().replaceAll("^\"|\"$", ""));
                        }
                    }
                    catch (Exception e1)
                    {}
                    try {
                        if(!jobjImages.get("classified_img3").toString().equals(""))
                        {
                            file_maps.put("three", Uri.parse(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img3").toString().replaceAll("^\"|\"$", "")));
                            classi_images.add(common_variable.main_web_url + "/uploads/classifieds/"+jobjImages.get("classified_img3").toString().replaceAll("^\"|\"$", ""));
                        }
                    }
                    catch (Exception e1)
                    {}

                    CustomGalleryAdapter customGalleryAdapter = new CustomGalleryAdapter(getContext(), classi_images); // initialize the adapter
                    clasified_gallery.setAdapter(customGalleryAdapter); // set the adapter
                    clasified_gallery.setSpacing(10);
                    clasified_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            /*Picasso.with(getContext())
                                    .load(classi_images.get(position))
                                    .resize(common_variable.screenWidth, common_variable.screenWidth)
                                    .placeholder(R.drawable.ic_img_icon)
                                    .into(clasified_image);*/

                            Ion.with(getContext())
                                    .load(classi_images.get(position))
                                    .withBitmap()
                                    .placeholder(R.raw.loading)
                                    .error(R.drawable.error)
                                    .animateLoad(R.anim.zoom_in)
                                    .animateIn(R.anim.fade_in)
                                    .intoImageView(clasified_image);
                        }
                    });

                    tv_AdName.setText(jobj.get("classprod_name").toString().replaceAll("^\"|\"$", ""));
                    tv_Price.setText(jobj.get("classprod_price").toString().replaceAll("^\"|\"$", ""));
                    tv_Decription.setText(jobj.get("classprod_description").toString().replaceAll("^\"|\"$", ""));
                    tv_Address.setText(jClassiAddress.get("address").toString().replaceAll("^\"|\"$", "")+", "+jClassiAddress.get("city").toString().replaceAll("^\"|\"$", ""));
                    tv_Mobile.setText(jobj.get("mobile").toString().replaceAll("^\"|\"$", ""));

                    iv_call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jobj.get("mobile").toString().replaceAll("^\"|\"$", "")));
                            startActivity(intent);
                        }
                    });

                    String location = jClassiAddress.get("location").toString().replaceAll("^\"|\"$","");
                    location = location.substring(1,location.length()-1);
                    String []loc = location.split(",");

                    lati = Double.valueOf(loc[0]);
                    longi = Double.valueOf(loc[1]);

                    showDirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Main_Activity.current_Lattitude == 0.0 && Main_Activity.current_Longitude == 0.0)
                            {
                                Toast.makeText(getContext(),"Not Getting Your Proper Location",Toast.LENGTH_LONG).show();
                                return;
                            }
                            else {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + Main_Activity.current_Lattitude + "," + Main_Activity.current_Longitude + "&daddr=" + lati + "," + longi + ""));
                                startActivity(intent);
                            }
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


    public class CustomGalleryAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<String> images;

        public CustomGalleryAdapter(Context c, ArrayList<String> images) {
            context = c;
            this.images = images;
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(convertView==null)
                convertView = inflater.inflate(R.layout.small_gallery_images, null);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view);
            //Picasso.with(getContext()).load(images.get(position)).resize(70, 70).placeholder(R.drawable.ic_img_icon).into(imageView);

            Ion.with(getContext())
                    .load(images.get(position))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(imageView);
            return convertView;
        }
    }
}
