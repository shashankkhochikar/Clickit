package com.clickitproduct.Products;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickitproduct.Beans.ProductBean;
import com.clickitproduct.R;

import java.util.List;

public class CustomListAdapterProducts extends BaseAdapter {

    Context context;
    List<ProductBean> UrAds;

    public CustomListAdapterProducts(Context context, List<ProductBean> UrAds)
    {
        this.context = context;
        this.UrAds = UrAds;
    }
    @Override
    public int getCount() {
        return UrAds.size();

    }

    public Object getItem(int position) {
        return UrAds.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null)
            convertView = inflater.inflate(R.layout.custom_product_list_row, null);

        TextView title = (TextView)convertView.findViewById(R.id.title); // title
        TextView price = (TextView)convertView.findViewById(R.id.price); // artist name
        ImageView shopImg =(ImageView)convertView.findViewById(R.id.list_image); // thumb image
        ImageView removerow =(ImageView)convertView.findViewById(R.id.removerow); // thumb image

        removerow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrAds.remove(position);
                notifyDataSetChanged();
            }
        });
        title.setText(UrAds.get(position).getProduct_name());
        price.setText(UrAds.get(position).getProduct_price());
        shopImg.setImageBitmap(UrAds.get(position).getProdimage());

        return convertView;
    }
}
