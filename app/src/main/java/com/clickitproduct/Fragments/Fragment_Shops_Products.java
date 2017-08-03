package com.clickitproduct.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.clickitproduct.R;
import com.clickitproduct.activities.Main_Activity;
import com.clickitproduct.activities.ShopViewActivityNew;
import com.clickitproduct.commonutil.common_variable;
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

import static android.content.Context.MODE_PRIVATE;
import static com.clickitproduct.commonutil.common_variable.sharedpreferences;

public class Fragment_Shops_Products extends Fragment
{
    View rootview=null;
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    public static JsonArray ShopProducts;
    String userId, shopId;
    public static final String MyPREFERENCES = "MyPrefs";
    private TextView txt_count;
    TextView tv_NoData, product_title, product_price, product_views, product_description;
    ImageView single_product_image;
    int refreshed_product  = 0;
    String selected_product_id;
    ScrollView sc_shop_product;

    public static Fragment_Shops_Products newInstance(int instance)
    {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        Fragment_Shops_Products f1 = new Fragment_Shops_Products();
        f1.setArguments(args);
        return f1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.activity_fragment_shops_products, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        sc_shop_product = (ScrollView)rootview.findViewById(R.id.sc_shop_product);
        txt_count = (TextView) rootview.findViewById(R.id.txt_count);
        txt_count.setVisibility(View.GONE);
        tv_NoData = (TextView) rootview.findViewById(R.id.tv_NoData);
        tv_NoData.setText("No Products");
        single_product_image = (ImageView)rootview.findViewById(R.id.single_product_image);
        product_title = (TextView)rootview.findViewById(R.id.product_title);
        product_price = (TextView)rootview.findViewById(R.id.product_price);
        product_views = (TextView)rootview.findViewById(R.id.product_views);
        product_description = (TextView)rootview.findViewById(R.id.product_description);

        try {
            selected_product_id = common_variable.jShopProductObj.get("_id").toString().replaceAll("^\"|\"$", "");

            /*Glide.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/products/"+ common_variable.jShopProductObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .into(single_product_image);*/

            Ion.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/products/"+ common_variable.jShopProductObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(single_product_image);

            product_title.setText(common_variable.jShopProductObj.get("product_name").toString().replaceAll("^\"|\"$", ""));

            if(!common_variable.jShopProductObj.get("product_description").toString().replaceAll("^\"|\"$", "").contains("undefined")){
                product_description.setVisibility(View.VISIBLE);
                product_description.setText(common_variable.jShopProductObj.get("product_description").toString().replaceAll("^\"|\"$", ""));
            }
            else
            {
                product_description.setVisibility(View.GONE);
            }

            try {
                product_views.setText("Views "+common_variable.jShopProductObj.get("totalviews").toString().replaceAll("^\"|\"$", ""));
            }catch (Exception ec){
                product_views.setText("Views 0");
            }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(common_variable.jShopProductObj.get("product_price").toString().replaceAll("^\"|\"$", "")));
            product_price.setText(moneyString);

           /* if(refreshed_product == 0){
                getShopProductsData();
            }*/
        }catch (Exception e){}

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            sc_shop_product.setOnScrollChangeListener(new View.OnScrollChangeListener()
            {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    getShopProductsData();
                }
            });
        }*/

        getShopProductsData();

         /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(refreshed_product == 0){
                    getShopProductsData();
                }
            }
        }, 1000);*/

       /* addToCart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(objArrayList.size() == 0)
                {
                    Toast.makeText(getContext(), "Select products by long click on them and add it to cart", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent i = new Intent(getContext(), ViewCart.class);
                    i.putExtra("amol", objArrayList);
                    startActivity(i);
                }
            }
        });*/

        /*btnSendEnq.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(objArrayList.size() == 0)
                {
                    Toast.makeText(getContext(), "Select products by long click on them and send enquiry", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String pname = "";
                    for (int i=0;i<objArrayList.size();i++)
                    {
                        int j = 1;
                        int c = i+j;
                        pname = pname+" Product :"+c +" :"+objArrayList.get(i).getProductNm()+"\n";
                    }

                    String message = sharedpreferences.getString("first_name", "").replaceAll("^\"|\"$", "") +" "+
                            sharedpreferences.getString("last_name", "").replaceAll("^\"|\"$", "") +"\n"+
                            sharedpreferences.getString("mobile", "").replaceAll("^\"|\"$", "")+"\n"+
                            sharedpreferences.getString("email", "").replaceAll("^\"|\"$", "")+"\n"+
                            pname;

                    String to = DetailsActivity.shop_email;

                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                    email.putExtra(Intent.EXTRA_SUBJECT, "Products enquiry");
                    email.putExtra(Intent.EXTRA_TEXT, message);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }
            }
        });*/
        return rootview;
    }

    public void getShopProductsData()
    {
        try{
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("shop_id", common_variable.Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(getContext())
            .load(common_variable.main_web_url+"/product/product_check")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        refreshed_product = 1;
                        if(result.get("success").toString().equals("true"))
                        {
                            tv_NoData.setVisibility(View.GONE);
                            ShopProducts = result.getAsJsonArray("data");
                            if (ShopProducts != null) {
                                for (int i = 0; i < ShopProducts.size(); i++) {
                                    JsonObject obj = (JsonObject) ShopProducts.get(i);
                                    obj.get("product_price");
                                }

                                userId = result.get("user_id").toString().replaceAll("^\"|\"$", "");
                                shopId = result.get("shop_id").toString().replaceAll("^\"|\"$", "");

                                mAdapter = new RecyclerViewAdapter(ShopProducts);
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
                    }catch (Exception ce){}
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
            public TextView tv_pro_name, tv_pro_price, tv_pro_descri, tv_pro_view;
            public ImageView iv_pro_img, iv_pro_menu;
            RelativeLayout la_pro_main;

            public RecyclerViewHolder(View view)
            {
                super(view);
                tv_pro_name = (TextView) view.findViewById(R.id.tv_pro_name);
                tv_pro_price = (TextView) view.findViewById(R.id.tv_pro_price);
                tv_pro_descri = (TextView) view.findViewById(R.id.tv_pro_descri);
                tv_pro_view = (TextView) view.findViewById(R.id.tv_pro_view);
                iv_pro_img = (ImageView) view.findViewById(R.id.iv_pro_img);
                iv_pro_menu = (ImageView) view.findViewById(R.id.iv_pro_menu);
                la_pro_main = (RelativeLayout) view.findViewById(R.id.la_pro_main);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_view, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position)
        {
            final JsonObject jProObj = (JsonObject) pList.get(position);

            try{
                if(selected_product_id.equals(jProObj.get("_id").toString().replaceAll("^\"|\"$", "")))
                {   holder.la_pro_main.setVisibility(View.GONE);    }
                else
                {   holder.la_pro_main.setVisibility(View.VISIBLE); }
            }catch (Exception e){Log.e("123","Exception In Frg_Shop_Products : "+e.getMessage().toString());}

            holder.tv_pro_name.setText(jProObj.get("product_name").toString().replaceAll("^\"|\"$", ""));

            if(!jProObj.get("product_description").toString().replaceAll("^\"|\"$", "").contains("undefined")){
                holder.tv_pro_descri.setVisibility(View.VISIBLE);
                holder.tv_pro_descri.setText(jProObj.get("product_description").toString().replaceAll("^\"|\"$", ""));
            }
            else
            {
                holder.tv_pro_descri.setVisibility(View.GONE);
            }


            try {
                holder.tv_pro_view.setText("Views "+jProObj.get("totalviews").toString().replaceAll("^\"|\"$", ""));
            }
            catch (Exception ec){
                holder.tv_pro_view.setText("Views 0");
            }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String moneyString = formatter.format(Integer.parseInt(jProObj.get("product_price").toString().replaceAll("^\"|\"$", "")));
            holder.tv_pro_price.setText(moneyString);

            /*Glide.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/products/"+ jProObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .into(holder.iv_pro_img);*/

            Ion.with(getContext())
                    .load(common_variable.main_web_url+"/uploads/products/"+ jProObj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.iv_pro_img);

            holder.iv_pro_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(v.getContext(),v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            switch (item.getItemId())
                            {
                                case R.id.add_to_wishlist:
                                    try
                                    {
                                        JsonObject jsonParam = new JsonObject();
                                        jsonParam.addProperty("shop_id", jProObj.get("shop_id").toString().replaceAll("^\"|\"$",""));
                                        jsonParam.addProperty("user_id", common_variable.User_id);
                                        jsonParam.addProperty("username", common_variable.User_Name);
                                        jsonParam.addProperty("profile_pic", common_variable.User_Profile_Pic);
                                        jsonParam.addProperty("product_id", jProObj.get("_id").toString().replaceAll("^\"|\"$", ""));
                                        jsonParam.addProperty("product_img", jProObj.get("product_img").toString().replaceAll("^\"|\"$", ""));
                                        jsonParam.addProperty("product_name", jProObj.get("product_name").toString().replaceAll("^\"|\"$", ""));
                                        jsonParam.addProperty("product_price", jProObj.get("product_price").toString().replaceAll("^\"|\"$", ""));

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
                                        holder.iv_pro_img.buildDrawingCache();
                                        Bitmap icon = holder.iv_pro_img.getDrawingCache();
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");

                                        f.createNewFile();
                                        FileOutputStream fo = new FileOutputStream(f);
                                        fo.write(bytes.toByteArray());

                                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                                        share.putExtra(Intent.EXTRA_TEXT, jProObj.get("product_name").toString().replaceAll("^\"|\"$", "")+"\n"+" To view this download Clickit App "+ Main_Activity.app_link);
                                        startActivity(Intent.createChooser(share, "Share Image"));
                                    } catch (IOException e) { e.printStackTrace();}
                                    return true;

                                case R.id.sendEnquiry:
                                    String message = sharedpreferences.getString("first_name", "").replaceAll("^\"|\"$", "") +" "+
                                            sharedpreferences.getString("last_name", "").replaceAll("^\"|\"$", "") +"\n"+
                                            sharedpreferences.getString("mobile", "").replaceAll("^\"|\"$", "")+"\n"+
                                            sharedpreferences.getString("email", "").replaceAll("^\"|\"$", "")+"\n"+
                                            jProObj.get("product_name").toString().replaceAll("^\"|\"$", "");

                                    Intent email = new Intent(Intent.ACTION_SEND);
                                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{ShopViewActivityNew.shop_email});
                                    email.putExtra(Intent.EXTRA_SUBJECT, "Products enquiry");
                                    email.putExtra(Intent.EXTRA_TEXT, message);
                                    email.setType("message/rfc822");
                                    startActivity(Intent.createChooser(email, "Choose an Email client :"));

                                default:
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });

            /*holder.laProductImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                    dialog.setContentView(R.layout.single_product_dialog_view);
                    int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                    int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
                    dialog.getWindow().setLayout(width, height);

                    final LinearLayout la_Image = (LinearLayout)dialog.findViewById(R.id.la_Image);
                    TextView tv_Title = (TextView)dialog.findViewById(R.id.tv_Title);
                    TextView tv_Decription = (TextView)dialog.findViewById(R.id.tv_Decription);
                    TextView tv_Category = (TextView)dialog.findViewById(R.id.tv_Category);
                    TextView tv_Price = (TextView)dialog.findViewById(R.id.tv_Price);

                    Glide.with(getContext()).load(common_variable.main_web_url+"/uploads/products/"+jProductObj.get("product_img").toString().replaceAll("^\"|\"$", "")).asBitmap().placeholder(R.drawable.app_icon).into(new SimpleTarget<Bitmap>(200, 200) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(resource);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                la_Image.setBackground(drawable);
                            }
                        }
                    });

                    tv_Title.setText(jProductObj.get("product_name").toString().replaceAll("^\"|\"$", ""));
                    tv_Decription.setText(jProductObj.get("product_description").toString().replaceAll("^\"|\"$", ""));
                    tv_Category.setText(jProductObj.get("product_category").toString().replaceAll("^\"|\"$", ""));
                    tv_Price.setText(jProductObj.get("product_price").toString().replaceAll("^\"|\"$", ""));
                    dialog.show();
                }
            });*/

            /*holder.laProductImg.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    Cart objCart =new Cart();
                    String productId = jProductObj.get("_id").toString().replaceAll("^\"|\"$", "");
                    int i=-1;
                    int f=0;
                    if(objArrayList.size()>0)
                    {
                        for (Cart objtmp : objArrayList)
                        {
                            i++;
                            if (objtmp.getProductId().equals(productId))
                            {
                                objArrayList.remove(i);
                               holder.ivcheck.setVisibility(View.GONE);
                                f = 1;
                                break;
                            }
                            else
                            {
                                f = 0;
                            }
                        }
                        if (f == 0)
                        {
                            objCart.setIsLogClick(1);
                            objCart.setProductId(productId);
                            objCart.setUserId(userId);
                            objCart.setShopId(shopId);
                            objCart.setProductImg(jProductObj.get("product_img").toString().replaceAll("^\"|\"$", ""));
                            objCart.setProductNm(jProductObj.get("product_name").toString().replaceAll("^\"|\"$", ""));
                            objCart.setProductPrice(jProductObj.get("product_price").toString().replaceAll("^\"|\"$", ""));
                            objArrayList.add(objCart);
                           // holder.laProductImg.setBackgroundColor(getResources().getColor(R.color.Aqua));
                            holder.ivcheck.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        objCart.setIsLogClick(1);
                        objCart.setProductId(productId);
                        objCart.setUserId(userId);
                        objCart.setShopId(shopId);
                        objCart.setProductImg(jProductObj.get("product_img").toString().replaceAll("^\"|\"$", ""));
                        objCart.setProductNm(jProductObj.get("product_name").toString().replaceAll("^\"|\"$", ""));
                        objCart.setProductPrice(jProductObj.get("product_price").toString().replaceAll("^\"|\"$", ""));
                        objArrayList.add(objCart);
                        holder.ivcheck.setVisibility(View.VISIBLE);
                    }
                    txt_count.setText(objArrayList.size()+"");
                    return true;
                }
            });*/
        }
        @Override
        public int getItemCount()
        {   return pList.size();}
    }

    @Override
    public void onResume() {
        super.onResume();
//        getShopProductsData();
    }
}
