package com.clickitproduct.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.clickitproduct.R;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;

public class ShopGallery extends AppCompatActivity
{
    Toolbar toolbar;
    public static ArrayList<String> shop_images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_gallery);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Shop Gallery");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Gallery gallery = (Gallery) findViewById(R.id.shop_gallery);
        final ImageView img = (ImageView) findViewById(R.id.shop_image);

       // Glide.with(getApplicationContext()).load(shop_images.get(0)).placeholder(R.drawable.ic_img_icon).into(img);
        Ion.with(ShopGallery.this)
                .load(shop_images.get(0))
                .withBitmap()
                .placeholder(R.raw.loading)
                .error(R.drawable.error)
                .animateLoad(R.anim.zoom_in)
                .animateIn(R.anim.fade_in)
                .intoImageView(img);

        CustomGalleryAdapter customGalleryAdapter = new CustomGalleryAdapter(getApplicationContext(), shop_images); // initialize the adapter
        gallery.setAdapter(customGalleryAdapter); // set the adapter
        gallery.setSpacing(10);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Glide.with(getApplicationContext()).load(shop_images.get(position)).placeholder(R.drawable.ic_img_icon).into(img);
                Ion.with(ShopGallery.this)
                        .load(shop_images.get(position))
                        .withBitmap()
                        .placeholder(R.raw.loading)
                        .error(R.drawable.error)
                        .animateLoad(R.anim.zoom_in)
                        .animateIn(R.anim.fade_in)
                        .intoImageView(img);
            }
        });
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
                convertView = inflater.inflate(R.layout.image_view, null);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view);
            //Picasso.with(getApplicationContext()).load(images.get(position)).placeholder(R.drawable.ic_img_icon).into(imageView);
            Ion.with(ShopGallery.this)
                    .load(images.get(position))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(imageView);
            return convertView;
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
