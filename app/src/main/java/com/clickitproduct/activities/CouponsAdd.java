package com.clickitproduct.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.R;
import com.clickitproduct.commonutil.common_variable;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class CouponsAdd extends AppCompatActivity
{
    EditText etMinPurchase, etDiscount, etQuantity, etValidity, etYourShop;
    ImageView ivDate, CouponImage;
    Button btnAdd;
    ImageView changeCouponImage;
    private static final int IMAGE_CAPTURE = 2;
    private static final int RESULT_CROP = 3;
    Uri imageUri;
    private static final int PROFILE = 1;
    private Bitmap bmp;
    String picUserPathUrl = "";
    String coupon_image="";
    int mYear, mMonth, mDay;
    Toolbar mActionBarToolbar;
    LinearLayout laCouponForm;
    TextView tvNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coupons);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Coupon");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etMinPurchase = (EditText)findViewById(R.id.etMinPurchase);
        etDiscount = (EditText)findViewById(R.id.etDiscount);
        etQuantity = (EditText)findViewById(R.id.etQuantity);
        etValidity = (EditText)findViewById(R.id.etValidity);
        etYourShop = (EditText)findViewById(R.id.etYourShop);
        ivDate = (ImageView)findViewById(R.id.ivDate);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        changeCouponImage = (ImageView)findViewById(R.id.changeCouponImage);
        changeCouponImage.setVisibility(View.GONE);
        CouponImage = (ImageView)findViewById(R.id.CouponImage);
        laCouponForm = (LinearLayout)findViewById(R.id.laCouponForm);
        tvNote = (TextView)findViewById(R.id.tvNote);
        laCouponForm.setVisibility(View.GONE);

        etYourShop.setText(common_variable.User_Shop_Name);

        JsonObject jsonParamLogin = new JsonObject();
        jsonParamLogin.addProperty("user_id", common_variable.User_id);
        jsonParamLogin.addProperty("shop_id", common_variable.User_Shop_ID+"");
        jsonParamLogin.addProperty("platform", "1");
        Log.e("123","Coupons"+jsonParamLogin.toString());

        Ion.with(getApplicationContext())
        .load(common_variable.main_web_url + "/coupon/check_coupon_validity")
        .setJsonObjectBody(jsonParamLogin)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try{
                    if(result.get("success").toString().equals("true"))
                    {
                        laCouponForm.setVisibility(View.VISIBLE);
                        tvNote.setVisibility(View.GONE);
                    }
                    else
                    {
                        laCouponForm.setVisibility(View.GONE);
                        tvNote.setVisibility(View.VISIBLE);
                        tvNote.setText("You are allowed to add next Coupon after \nprevious coupon get expired.");
                    }
                }catch (Exception ex){}
            }
        });

        CouponImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraGalleryDialog();
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bmp == null)
                {
                    bmp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon);
                    coupon_image = BitMapToString(bmp);
                }

                if(etYourShop.getText().toString().equals(""))
                {
                    etYourShop.requestFocus();
                    etYourShop.setError("Enter Your Shop");
                }
                else if (etMinPurchase.getText().toString().equals(""))
                {
                    etMinPurchase.requestFocus();
                    etMinPurchase.setError("Enter minimum purchase amount");
                }
                else if (etDiscount.getText().toString().equals(""))
                {
                    etDiscount.requestFocus();
                    etDiscount.setError("Enter discount");
                }
                else if (etQuantity.getText().toString().equals(""))
                {
                    etQuantity.requestFocus();
                    etQuantity.setError("Enter quantity");
                }
                else if(etValidity.getText().toString().equals(""))
                {
                    etValidity.requestFocus();
                    etValidity.setError("Enter Validity");
                }
                else
                {
                    final ProgressDialog dialog = new ProgressDialog(CouponsAdd.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    JsonObject jsonParam = new JsonObject();
                    jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                    jsonParam.addProperty("shop_name", common_variable.User_Shop_Name);
                    //jsonParam.addProperty("user_id", sharedpreferences.getString("user_id", null).toString().replaceAll("^\"|\"$", ""));
                    jsonParam.addProperty("user_id", common_variable.User_id.toString().replaceAll("^\"|\"$", ""));
                    jsonParam.addProperty("minimum_price", "" + etMinPurchase.getText().toString());
                    jsonParam.addProperty("quantity", "" + etQuantity.getText().toString());
                    jsonParam.addProperty("discount", "" + etDiscount.getText().toString());
                    jsonParam.addProperty("validity", etValidity.getText().toString());
                    jsonParam.addProperty("coupon_image", coupon_image);
                    jsonParam.addProperty("platform", "1");

                    Ion.with(getApplicationContext())
                    .load(common_variable.main_web_url + "/coupon/addcoupon_android")
                    .progressDialog(dialog)
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result)
                        {
                            dialog.dismiss();
                            Toast.makeText(CouponsAdd.this, "Coupons added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                DatePickerDialog da = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, 1);
                Date newDate = c.getTime();
                da.getDatePicker().setMinDate(newDate.getTime());
                return da;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            etValidity.setText(yearSelected + "-" + pad(monthOfYear + 1) + "-" + pad(dayOfMonth));
        }
    };

    private static String pad(int c){
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public void cameraGalleryDialog()
    {
        LayoutInflater li = LayoutInflater.from(CouponsAdd.this);
        final View prompt = li.inflate(R.layout.choose_photo, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(CouponsAdd.this, R.style.MyAlertTheme));
        alertDialogBuilder.setView(prompt);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.show();

        FloatingActionButton fab_Camera1, fab_Gallary1;
        fab_Camera1 = (FloatingActionButton) dialog.findViewById(R.id.fab_Camera);
        fab_Gallary1 = (FloatingActionButton) dialog.findViewById(R.id.fab_Gallary);

        fab_Camera1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

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
                startActivityForResult(i, PROFILE);
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

    private Bitmap getLargeBitmapFromUri(Uri uri) throws OutOfMemoryError, IOException
    {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Bitmap scale = Bitmap.createScaledBitmap(image, 500, 600, true);
        return scale;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case IMAGE_CAPTURE:
                    try
                    {
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picUserPathUrl = cursor.getString(columnIndex);
                        performCrop(picUserPathUrl);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        try {
                            bmp = getBitmapFromUri4Camera(imageUri);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        CouponImage.setImageBitmap(bmp);
                    }
                    catch (OutOfMemoryError exp)
                    { exp.printStackTrace(); }
                    break;

                case PROFILE:
                    Uri selectedImage1 = data.getData();
                    String[] filePathColumn1 = {MediaStore.Images.Media.DATA};
                    Cursor cursor1 = getContentResolver().query(selectedImage1, filePathColumn1, null, null, null);
                    cursor1.moveToFirst();
                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn1[0]);
                    String filePath1 = cursor1.getString(columnIndex1);
                    cursor1.close();
                    bmp = null;
                    try
                    {
                        performCrop(filePath1);
                    }
                    catch (Exception e)
                    {
                        try {
                            bmp = getBitmapFromUri4Gallary(selectedImage1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        CouponImage.setImageBitmap(bmp);
                        e.printStackTrace(); }
                    catch (OutOfMemoryError exp)
                    {exp.printStackTrace();}
                    break;
            }

            if (requestCode == RESULT_CROP && data != null)
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    Bundle extras = data.getExtras();
                    try
                    {
                        Bitmap selectedBitmap = extras.getParcelable("data");
                        new setImage().execute(selectedBitmap);
                    }
                    catch (Exception e)
                    {
                        Uri selectedImage = data.getData();
                        try
                        {
                            bmp = getLargeBitmapFromUri(selectedImage);
                            CouponImage.setImageBitmap(bmp);
                            coupon_image = BitMapToString(bmp);
                            new setImage().execute(bmp);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(getApplicationContext(), "Sorry don't crop in Google Images...please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public String BitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void performCrop(String picUri)
    {
        try{
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
        catch (ActivityNotFoundException anfe){
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class setImage extends AsyncTask<Bitmap, Void, Void>
    {
        private ProgressDialog Dialog = new ProgressDialog(CouponsAdd.this);
        Bitmap bit = null;
        protected void onPreExecute()
        {
            Dialog.setMessage("please wait");
            Dialog.setIndeterminate(true);
            Dialog.setProgress(android.R.drawable.progress_indeterminate_horizontal);
            Dialog.show();
        }

        @Override
        protected Void doInBackground(Bitmap... params)
        {
            try
            {bit = params[0];}
            catch (Exception e)
            {e.printStackTrace();}
            return null;
        }

        protected void onPostExecute(Void unused)
        {
            CouponImage.setImageBitmap(null);
            bmp = bit;
            coupon_image = BitMapToString(bmp);
            CouponImage.setImageBitmap(bit);

            if (Dialog.isShowing() == true)
            { Dialog.dismiss(); }
        }
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


