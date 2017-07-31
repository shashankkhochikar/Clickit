package com.clickitproduct.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.clickitproduct.R;
import com.clickitproduct.Beans.*;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ViewCart extends AppCompatActivity
{
    private RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    ArrayList<Cart> objArrList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        objArrList = (ArrayList<Cart>) getIntent().getSerializableExtra("amol");

        mAdapter = new RecyclerViewAdapter(objArrList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewCart.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private ArrayList pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView product_name, product_price;
            public ImageView product_image;

            public RecyclerViewHolder(View view)
            {
                super(view);
                product_name = (TextView) view.findViewById(R.id.product_name);
                product_price = (TextView) view.findViewById(R.id.product_price);
                product_image = (ImageView) view.findViewById(R.id.product_image);
            }
        }

        public RecyclerViewAdapter(ArrayList pList)
        {
            this.pList = pList;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cart_view, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position)
        {
            final Cart ProductObj = (Cart) pList.get(position);

            if(holder.product_name==null)
            {
                Log.e("fkjdkafjsdk","ssdfsdfs");
            }
            holder.product_name.setText(ProductObj.getProductNm());
            holder.product_price.setText(ProductObj.getProductPrice());

            /*Glide.with(getApplicationContext())
                    .load(common_variable.main_web_url+"/uploads/products/"+ProductObj.getProductImg().toString().replaceAll("^\"|\"$",""))
                    .asBitmap()
                    .placeholder(R.drawable.app_icon)
                    .into(new SimpleTarget<Bitmap>(200, 200) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.product_image.setBackground(drawable);
                    }
                }
            });*/

            Ion.with(ViewCart.this)
                    .load(common_variable.main_web_url+"/uploads/products/"+ProductObj.getProductImg().toString().replaceAll("^\"|\"$",""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.product_image);

        }
        @Override
        public int getItemCount()
        {   return pList.size();    }
    }
}
