package com.clickitproduct.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.clickitproduct.Beans.ProductBean;
import com.clickitproduct.R;
import com.clickitproduct.adapter.CustomListAdapterProducts;
import com.clickitproduct.commonutil.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;

public class ProductsAdd extends AppCompatActivity
{
    private static final int PRODUCT = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static final int RESULT_CROP = 3;
    String image;
    Uri imageUri;
    private Bitmap bmp1;
    private ImageView img;
    private ListView lstProd;
    final List<ProductBean> prodlist = new ArrayList<>();
    CustomListAdapterProducts prodAdapter;
    JsonArray ShopProducts;
    String p_id;
    public static String prod_url_api;
    Toolbar mActionBarToolbar;
    public static int CAMERA_PREVIEW_RESULT = 1;
    //public String shop_category;
    Button btnAddToList, btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_products);

        lstProd = (ListView) findViewById(R.id.lstProd);
        btnAddToList = (Button)findViewById(R.id.btnAddToList);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Products");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //shop_category = UserShopDetails.Shop_cat.replace("\\/", "/");

        try
        {
            JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(ProductsAdd.this)
            .load(common_variable.main_web_url+"/product/product_check")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        if (result.get("success").toString().equals("true")) {
                            prod_url_api = "/product/product_update";
                            ShopProducts = result.getAsJsonArray("data");
                            try {
                                p_id = result.get("product_id").toString().replaceAll("^\"|\"$", "");
                            } catch (Exception ex) {
                            }
                        } else if (result.get("success").toString().equals("false")) {
                            prod_url_api = "/product/product_registration";
                        }
                    }catch (Exception ec){}
                }
            });
        }
        catch (Exception e){}

        btnAddToList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                bmp1=null;
                prodAdapter.notifyDataSetChanged();
                openProductDialog();
            }
        });

        prodAdapter = new CustomListAdapterProducts(ProductsAdd.this, prodlist);
        lstProd.setAdapter(prodAdapter);
        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(prodlist.size() > 0)
                {
                    final ProgressDialog dialog = new ProgressDialog(ProductsAdd.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    JsonArray req = new JsonArray();
                    JsonObject jsonParam = new JsonObject();
                    jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                    //jsonParam.addProperty("user_id", sharedpreferences.getString("user_id", null).toString().replaceAll("^\"|\"$", ""));
                    jsonParam.addProperty("user_id", common_variable.User_id.toString().replaceAll("^\"|\"$", ""));
                    jsonParam.addProperty("platform", "1");
                    jsonParam.addProperty("category", "" + common_variable.User_Shop_Category);
                    Log.e("123","Prod"+jsonParam.toString());

                    for (int i=0; i < prodlist.size(); i++)
                    {
                        JsonObject reqObj = new JsonObject();
                        reqObj.addProperty("product_name", "" + prodlist.get(i).getProduct_name());
                        reqObj.addProperty("product_price", "" + prodlist.get(i).getProduct_price());
                        reqObj.addProperty("product_category", "" + prodlist.get(i).getSub_category());
                        reqObj.addProperty("product_description", "" + prodlist.get(i).getDesc());
                        reqObj.addProperty("product_img", "" + BitMapToString(prodlist.get(i).getProdimage()));
                        req.add(reqObj);
                    }
                    jsonParam.add("product_data", req);

                    if(prod_url_api.equals("/product/product_update"))
                    {jsonParam.addProperty("product_id", p_id);}

                    Ion.with(getApplicationContext())
                    .load(common_variable.main_web_url+prod_url_api)
                    .progressDialog(dialog)
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result)
                        {
                            dialog.dismiss();
                            finish();
                            try
                            {
                                lstProd.setAdapter(null);
                                prodAdapter.notifyDataSetChanged();
                                Toast.makeText(ProductsAdd.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                common_variable.Refresh_Product = 1;
                            }
                            catch (Exception e3)
                            {Toast.makeText(ProductsAdd.this, "something error", Toast.LENGTH_SHORT).show();}
                        }
                    });
                }
                else
                { Toast.makeText(ProductsAdd.this, "Add atleast one product", Toast.LENGTH_SHORT).show(); }
            }
        });
    }

    public void openProductDialog()
    {
        final Dialog AddDialog=new Dialog(ProductsAdd.this,android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        AddDialog.setContentView(R.layout.add_product_dialog);
        Log.e("User_Shop_Category", common_variable.User_Shop_Category);

        img = (ImageView) AddDialog.findViewById(R.id.img);
        final EditText etProdNm = (EditText) AddDialog.findViewById(R.id.etProdNm);
        final EditText etProdPrice = (EditText) AddDialog.findViewById(R.id.etProdPrice);
        Button btnAdd = (Button) AddDialog.findViewById(R.id.btnAdd);
        final EditText etProdCategory = (EditText)AddDialog.findViewById(R.id.etProdCategory);
        final EditText etProdDesc = (EditText)AddDialog.findViewById(R.id.etProdDesc);

        img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //cameraGalleryDialog();
                SettingsList settingsList = new SettingsList();
                settingsList
                        // Set custom camera export settings
                        .getSettingsModel(CameraSettings.class)
                        .setExportDir(Directory.PICTURES, ".clickit")
                        .setExportPrefix("camera_")
                        // Set custom editor export settings
                        .getSettingsModel(EditorSaveSettings.class)
                        .setExportDir(Directory.PICTURES, ".clickit")
                        .setExportPrefix("result_")
                        .setSavePolicy(EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT);

                new CameraPreviewBuilder(ProductsAdd.this)
                        .setSettingsList(settingsList)
                        .startActivityForResult(ProductsAdd.this, CAMERA_PREVIEW_RESULT);
            }
        });

        etProdCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                final ProgressDialog Pdialog = new ProgressDialog(ProductsAdd.this);
                Pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                Pdialog.setMessage("Loading...");
                Pdialog.setCancelable(false);
                Pdialog.show();

                JsonObject jsonParam = new JsonObject();
                jsonParam.addProperty("category", common_variable.User_Shop_Category );
                jsonParam.addProperty("platform", "1");


                Ion.with(getApplicationContext())
                .load(common_variable.main_web_url+"/product/product_category_list")
                .progressDialog(Pdialog)
                .setJsonObjectBody(jsonParam)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>()
                {
                    @Override
                    public void onCompleted(Exception e, JsonObject result)
                    {
                        try {
                            Pdialog.dismiss();
                            JsonArray prod_cat = result.getAsJsonArray("data");
                            JsonObject jCat;
                            final String []product_Categories = new String[prod_cat.size()];
                            Log.e("123","Cat"+prod_cat.toString());

                            for (int i = 0; i < prod_cat.size(); i++) {
                                jCat = (JsonObject) prod_cat.get(i);
                                product_Categories[i] = jCat.get("sub_categories").toString().replaceAll("^\"|\"$", "");
                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProductsAdd.this);
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

        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if (bmp1 == null)
                {
                    bmp1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon);
                    image = BitMapToString(bmp1);
                }

                if (etProdNm.getText().toString().equals(""))
                {
                    etProdNm.requestFocus();
                    etProdNm.setError("Enter product name");
                }
                else if (etProdPrice.getText().toString().equals(""))
                {
                    etProdPrice.requestFocus();
                    etProdPrice.setError("Enter product price");
                }
                else if(etProdCategory.getText().toString().equals(""))
                {
                    etProdCategory.requestFocus();
                    etProdCategory.setError("Select product category");
                }
                else if(etProdDesc.getText().toString().equals(""))
                {
                    etProdDesc.requestFocus();
                    etProdDesc.setError("Select product description");
                }
                else
                {
                    AddDialog.dismiss();
                    ProductBean pb = new ProductBean();
                    if(bmp1!=null)
                    {
                        pb.setProdimage(bmp1);
                    }
                    pb.setProduct_name(etProdNm.getText().toString());
                    pb.setProduct_price(etProdPrice.getText().toString());
                    pb.setCategory(common_variable.User_Shop_Category);
                    pb.setDesc(etProdDesc.getText().toString());
                    pb.setSub_category(etProdCategory.getText().toString());

                    prodlist.add(pb);
//                    lstProd.setAdapter(prodAdapter);
                    prodAdapter.notifyDataSetChanged();
                }
            }
        });

        AddDialog.show();
    }
    public void cameraGalleryDialog()
    {
        LayoutInflater li = LayoutInflater.from(ProductsAdd.this);
        final View prompt = li.inflate(R.layout.choose_photo, null);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(ProductsAdd.this, R.style.MyAlertTheme));
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

    public void getPath()
    {
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

    private Bitmap getBitmapFromUri4Gallary(Uri uri) throws OutOfMemoryError, IOException
    {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Bitmap scale = Bitmap.createScaledBitmap(image, 1000, 1000, true);
        return scale;
    }
    private Bitmap getBitmapFromUri4Camera(Uri uri) throws OutOfMemoryError, IOException
    {
        Bitmap bmp_img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        Bitmap scale = Bitmap.createScaledBitmap(bmp_img, 1000, 1000, true);
        return scale;
    }
    public String BitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT)
        {
            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);

            if (resultPath != null)
            {
                // Add result file to Gallery
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(resultPath))));
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(resultPath,bmOptions);
                img.setImageBitmap(bitmap);

                image = BitMapToString(bitmap);
                new setImage().execute(bitmap);

                /*Bundle extras = data.getExtras();
                try {
                    Bitmap selectedBitmap = extras.getParcelable("data");
                    new setImage().execute(selectedBitmap);
                }
                catch (Exception e) {
                    Uri selectedImage = data.getData();
                    try {
                        bmp1 = getLargeBitmapFromUri(selectedImage);
                        img.setImageBitmap(bmp1);
                    }
                    catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Sorry don't crop in Google Images...please try again", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }*/
            }

            if (sourcePath != null)
            {
                // Add sourceType file to Gallery
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sourcePath))));
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(resultPath,bmOptions);
                img.setImageBitmap(bitmap);

                image = BitMapToString(bitmap);
                new setImage().execute(bitmap);
            }
            // Toast.makeText(PESDK.getAppContext(), "Image saved on: " + resultPath, Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PREVIEW_RESULT && data != null)
        {
            //String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);
            // Toast.makeText(PESDK.getAppContext(), "Editor canceled, sourceType image is:\n" + sourcePath, Toast.LENGTH_LONG).show();
        } else {    finish();   }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_CAPTURE:
                    try {
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picUserPathUrl = cursor.getString(columnIndex);
                        performCrop(picUserPathUrl);
                        nm = "Product.jpg";
                    } catch (Exception e) {
                        try {
                            bmp1 = getBitmapFromUri4Camera(imageUri);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        img.setImageBitmap(bmp1);
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
                    String[] img1 = filePath1.split("/");
                    img_shop1 = img1[img1.length - 1].split("\\.");
                    bmp1 = null;
                    try {
                        performCrop(filePath1);
                        nm = "Product." + img_shop1[img_shop1.length - 1];
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            bmp1 = getBitmapFromUri4Gallary(selectedImage1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        img.setImageBitmap(bmp1);
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    break;
            }

            if (requestCode == RESULT_CROP && data != null)
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    Bundle extras = data.getExtras();
                    try {
                        Bitmap selectedBitmap = extras.getParcelable("data");
                        new setImage().execute(selectedBitmap);
                    }
                    catch (Exception e) {
                        Uri selectedImage = data.getData();
                        try {
                            bmp1 = getLargeBitmapFromUri(selectedImage);
                            img.setImageBitmap(bmp1);
                        }
                        catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), "Sorry don't crop in Google Images...please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }*/
    private void performCrop(String picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
            cropIntent.setDataAndType(contentUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class setImage extends AsyncTask<Bitmap, Void, Void> {
        private ProgressDialog Dialog = new ProgressDialog(ProductsAdd.this);
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
            img.setImageBitmap(null);
            bmp1 = bit;
            img.setImageBitmap(bit);

            if (Dialog.isShowing() == true)
            {
                Dialog.dismiss();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if(item.getItemId() == android.R.id.home)
        {
            this.finish();
            return true;
        }
        else if(item.getItemId() == R.id.history)
        {
            startActivity(new Intent(ProductsAdd.this, ProductHistory.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
