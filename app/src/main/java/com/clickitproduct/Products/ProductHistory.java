package com.clickitproduct.Products;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class ProductHistory extends AppCompatActivity
{

    RecyclerView recycler_view;
    Toolbar mActionBarToolbar;
    ImageView proImg;
    private static final int PRODUCT = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static final int RESULT_CROP = 3;
    String image;
    Uri imageUri;
    private Bitmap bmp;
    String picUserPathUrl = "";
    public int changed_prod_img = 0;
    Dialog main_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Product History");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(ProductHistory.this)
            .load(common_variable.main_web_url + "/product/product_check")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {
                        if (result.get("success").toString().equals("true")) {
                            JsonArray ShopProducts = result.getAsJsonArray("data");

                            if (ShopProducts != null) {
                                RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(ShopProducts);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recycler_view.setLayoutManager(mLayoutManager);
                                recycler_view.setItemAnimator(new DefaultItemAnimator());
                                recycler_view.setAdapter(mAdapter);
                            } else {
                                Toast.makeText(ProductHistory.this, "No record found", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else if (result.get("success").toString().equals("false")) {
                            Toast.makeText(ProductHistory.this, "No record found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }catch (Exception ec){}
                }
            });
        } catch (Exception e) {}
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
        private JsonArray pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder {
            public TextView Product_Name, Product_Price, Product_Desc;
            public LinearLayout la_Product;
            ImageView btn_edit, btn_del, Product_Image;

            public RecyclerViewHolder(View view) {
                super(view);
                Product_Name = (TextView) view.findViewById(R.id.Product_Name);
                Product_Price = (TextView) view.findViewById(R.id.Product_Price);
                Product_Image = (ImageView) view.findViewById(R.id.Product_Image);
                la_Product = (LinearLayout) view.findViewById(R.id.la_Product);
                Product_Desc = (TextView) view.findViewById(R.id.Product_Desc);
                btn_edit = (ImageView) view.findViewById(R.id.btn_edit);
                btn_del = (ImageView) view.findViewById(R.id.btn_del);
            }
        }

        public RecyclerViewAdapter(JsonArray pList) {
            this.pList = pList;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product_history_view, parent, false);
            return new RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            final JsonObject jobj = (JsonObject) pList.get(position);
            holder.Product_Name.setText(jobj.get("product_name").toString().replaceAll("^\"|\"$", ""));
            holder.Product_Price.setText(jobj.get("product_price").toString().replaceAll("^\"|\"$", ""));
            holder.Product_Desc.setText(jobj.get("product_description").toString().replaceAll("^\"|\"$", ""));

            /*Glide.with(getApplicationContext())
            .load(common_variable.main_web_url + "/uploads/products/" + jobj.get("product_img").toString().replaceAll("^\"|\"$", ""))
            .asBitmap()
            .dontAnimate()
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
            Ion.with(ProductHistory.this)
                    .load(common_variable.main_web_url + "/uploads/products/" + jobj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.Product_Image);

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    main_dialog = new Dialog(ProductHistory.this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                    main_dialog.setContentView(R.layout.add_product_dialog);

                    proImg = (ImageView) main_dialog.findViewById(R.id.img);
                    final EditText etProdNm = (EditText) main_dialog.findViewById(R.id.etProdNm);
                    final EditText etProdPrice = (EditText) main_dialog.findViewById(R.id.etProdPrice);
                    final EditText etProdCategory = (EditText) main_dialog.findViewById(R.id.etProdCategory);
                    final EditText etProdDesc = (EditText) main_dialog.findViewById(R.id.etProdDesc);
                    Button btnAdd = (Button) main_dialog.findViewById(R.id.btnAdd);
                    btnAdd.setText("Update");
                    final ImageView changeProductImage = (ImageView) main_dialog.findViewById(R.id.changeProductImage);

                    /*Glide.with(getApplicationContext())
                    .load(common_variable.main_web_url + "/uploads/products/" + jobj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                    .placeholder(R.drawable.ic_img_icon)
                    .into(proImg);*/

                    Ion.with(ProductHistory.this)
                            .load(common_variable.main_web_url + "/uploads/products/" + jobj.get("product_img").toString().replaceAll("^\"|\"$", ""))
                            .withBitmap()
                            .placeholder(R.raw.loading)
                            .error(R.drawable.error)
                            .animateLoad(R.anim.zoom_in)
                            .animateIn(R.anim.fade_in)
                            .intoImageView(proImg);

                    etProdNm.setText(holder.Product_Name.getText().toString());
                    etProdPrice.setText(holder.Product_Price.getText().toString());
                    etProdCategory.setText(jobj.get("product_category").toString().replaceAll("^\"|\"$", ""));
                    etProdDesc.setText(holder.Product_Desc.getText().toString());

                    changeProductImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cameraGalleryDialog();
                        }
                    });

                    etProdCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final ProgressDialog dialog = new ProgressDialog(ProductHistory.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("wait...");
                            dialog.show();

                            JsonObject jsonParam = new JsonObject();
                            jsonParam.addProperty("category", common_variable.User_Shop_Category );
                            jsonParam.addProperty("platform", "1");

                            Ion.with(getApplicationContext())
                            .load(common_variable.main_web_url + "/product/product_category_list")
                            .progressDialog(dialog)
                            .setJsonObjectBody(jsonParam)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>()
                            {
                                @Override
                                public void onCompleted(Exception e, JsonObject result)
                                {
                                    dialog.dismiss();
                                    try {
                                        JsonArray prod_cat = result.getAsJsonArray("data");
                                        JsonObject jCat;
                                        final String []product_Categories = new String[prod_cat.size()];

                                        for (int i = 0; i < prod_cat.size(); i++) {
                                            jCat = (JsonObject) prod_cat.get(i);
                                            product_Categories[i] = jCat.get("sub_categories").toString().replaceAll("^\"|\"$", "");
                                        }

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(ProductHistory.this);
                                        builder.setItems(product_Categories, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                etProdCategory.setText(product_Categories[i] + "");
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }catch (Exception ec){}
                                }
                            });
                        }
                    });

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (etProdNm.getText().toString().equals("")) {
                                etProdNm.requestFocus();
                                etProdNm.setError("Enter product name");
                            } else if (etProdPrice.getText().toString().equals("")) {
                                etProdPrice.requestFocus();
                                etProdPrice.setError("Enter product price");
                            } else if (etProdCategory.getText().toString().equals("")) {
                                etProdCategory.requestFocus();
                                etProdCategory.setError("Select product category");
                            } else if (etProdDesc.getText().toString().equals("")) {
                                etProdDesc.requestFocus();
                                etProdDesc.setError("Select product description");
                            } else {
                                final ProgressDialog dialog1 = new ProgressDialog(ProductHistory.this);
                                dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                dialog1.setMessage("Updating...");
                                dialog1.show();

                                JsonObject jsonParam = new JsonObject();
                                jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                jsonParam.addProperty("user_id", common_variable.User_id);
                                jsonParam.addProperty("_id", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                                jsonParam.addProperty("platform", "1");
                                jsonParam.addProperty("product_name", "" + etProdNm.getText().toString());
                                jsonParam.addProperty("product_price", "" + etProdPrice.getText().toString());
                                jsonParam.addProperty("product_category", "" + etProdCategory.getText().toString());
                                jsonParam.addProperty("product_desc", "" + etProdDesc.getText().toString());
                                if (changed_prod_img == 1) {
                                    jsonParam.addProperty("product_img", "" + BitMapToString(bmp));
                                    changed_prod_img = 0;
                                }

                                Ion.with(ProductHistory.this)
                                .load(common_variable.main_web_url + "/product/updateproduct")
                                .progressDialog(dialog1)
                                .setJsonObjectBody(jsonParam)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        dialog1.dismiss();
                                        main_dialog.dismiss();
                                        JsonObject jsonParam = new JsonObject();
                                        jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                        jsonParam.addProperty("platform", "1");

                                        Ion.with(ProductHistory.this)
                                        .load(common_variable.main_web_url + "/product/product_check")
                                        .setJsonObjectBody(jsonParam)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {
                                                try {
                                                    if (result.get("success").toString().equals("true")) {
                                                        JsonArray ShopProducts = result.getAsJsonArray("data");

                                                        if (ShopProducts != null) {
                                                            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(ShopProducts);
                                                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                                            recycler_view.setLayoutManager(mLayoutManager);
                                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                                            recycler_view.setAdapter(mAdapter);
                                                        }
                                                    }
                                                }catch (Exception ec){}
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });

                    main_dialog.show();
                }
            });


            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductHistory.this);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Are you sure you want to delete your product?");
                    alertDialog.setIcon(R.drawable.ic_alert);

                    alertDialog.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dg, int which) {
                                    dg.dismiss();
                                    final ProgressDialog dialog = new ProgressDialog(ProductHistory.this);
                                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    dialog.setMessage("Loading...");
                                    dialog.show();

                                    JsonObject jsonParam = new JsonObject();
                                    jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                    jsonParam.addProperty("user_id", common_variable.User_id);
                                    jsonParam.addProperty("_id", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                                    jsonParam.addProperty("platform", "1");

                                    Log.e("12345", "parameters " + jsonParam);

                                    Ion.with(ProductHistory.this)
                                    .load(common_variable.main_web_url + "/product/product_delete_android")
                                    .progressDialog(dialog)
                                    .setJsonObjectBody(jsonParam)
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            dialog.dismiss();
                                            JsonObject jsonParam = new JsonObject();
                                            jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                            jsonParam.addProperty("platform", "1");

                                            Ion.with(ProductHistory.this)
                                            .load(common_variable.main_web_url + "/product/product_check")
                                            .setJsonObjectBody(jsonParam)
                                            .asJsonObject()
                                            .setCallback(new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject result) {
                                                    try {
                                                        if (result.get("success").toString().equals("true")) {
                                                            JsonArray ShopProducts = result.getAsJsonArray("data");

                                                            if (ShopProducts != null) {
                                                                RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(ShopProducts);
                                                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                                                recycler_view.setLayoutManager(mLayoutManager);
                                                                recycler_view.setItemAnimator(new DefaultItemAnimator());
                                                                recycler_view.setAdapter(mAdapter);
                                                            }
                                                        }
                                                    }catch (Exception ec){}
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                    alertDialog.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
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

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cameraGalleryDialog() {
        LayoutInflater li = LayoutInflater.from(ProductHistory.this);
        final View prompt = li.inflate(R.layout.choose_photo, null);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(ProductHistory.this, R.style.MyAlertTheme));
        alertDialogBuilder.setView(prompt);
        alertDialogBuilder.setCancelable(true);
        final android.app.AlertDialog dialog = alertDialogBuilder.show();
        FloatingActionButton fab_Camera1, fab_Gallary1;
        fab_Camera1 = (FloatingActionButton) dialog.findViewById(R.id.fab_Camera);
        fab_Gallary1 = (FloatingActionButton) dialog.findViewById(R.id.fab_Gallary);

        fab_Camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click));
                getPath();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, IMAGE_CAPTURE);
                dialog.cancel();
            }
        });

        fab_Gallary1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click));

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i, PRODUCT);
                dialog.cancel();
            }
        });
    }

    public void getPath() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private Bitmap getLargeBitmapFromUri(Uri uri) throws OutOfMemoryError, IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Bitmap scale = Bitmap.createScaledBitmap(image, 500, 600, true);
        return scale;
    }

    private Bitmap getBitmapFromUri4Gallary(Uri uri) throws OutOfMemoryError, IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        Bitmap scale = Bitmap.createScaledBitmap(image, 1300, 1100, true);

        return scale;
    }

    private Bitmap getBitmapFromUri4Camera(Uri uri) throws OutOfMemoryError, IOException {
        Bitmap bmp_img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        Bitmap scale = Bitmap.createScaledBitmap(bmp_img, 1300, 1100, true);
        return scale;
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_CAPTURE:
                    try {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picUserPathUrl = cursor.getString(columnIndex);
                        performCrop(picUserPathUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    break;

                case PRODUCT:
                    Uri selectedImage1 = data.getData();
                    String[] filePathColumn1 = {MediaStore.Images.Media.DATA};
                    Cursor cursor1 = getContentResolver().query(selectedImage1, filePathColumn1, null, null, null);
                    cursor1.moveToFirst();
                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn1[0]);
                    String filePath1 = cursor1.getString(columnIndex1);
                    cursor1.close();

                    bmp = null;
                    try {
                        performCrop(filePath1);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    break;
            }

            if (requestCode == RESULT_CROP && data != null) {

                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    try {
                        Bitmap selectedBitmap = extras.getParcelable("data");
                        new setImage().execute(selectedBitmap);
                    } catch (Exception e) {
                        Uri selectedImage = data.getData();
                        try {
                            bmp = getLargeBitmapFromUri(selectedImage);
                            proImg.setImageBitmap(bmp);
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), "Sorry don't crop in Google Images...please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void performCrop(String picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class setImage extends AsyncTask<Bitmap, Void, Void> {
        private ProgressDialog Dialog = new ProgressDialog(ProductHistory.this);
        Bitmap bit = null;

        protected void onPreExecute() {
            Dialog.setMessage("please wait");
            Dialog.setIndeterminate(true);
            Dialog.setProgress(android.R.drawable.progress_indeterminate_horizontal);
            Dialog.show();
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            try {
                bit = params[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            proImg.setImageBitmap(null);
            bmp = bit;
            changed_prod_img = 1;
            proImg.setImageBitmap(bit);

            if (Dialog.isShowing() == true) {
                Dialog.dismiss();
            }
        }
    }
}
