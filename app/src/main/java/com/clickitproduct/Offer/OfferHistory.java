package com.clickitproduct.Offer;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import java.util.Calendar;

public class OfferHistory extends AppCompatActivity
{
    RecyclerView recycler_view;
    Toolbar mActionBarToolbar;

    private static final int PRODUCT = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static final int RESULT_CROP = 3;
    Uri imageUri;
    private Bitmap bmp;
    String picUserPathUrl = "";
    public int changed_offer_img = 0;
    ImageView offerImg;
    int date_flag = 0;
    final int DATE_DIALOG_ID = 0;
    int day, month, year;
    EditText etOfferValidFrom;
    EditText etOfferValidTo;
    Dialog main_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Offer History");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        JsonObject jsonParam = new JsonObject();
        try
        {
            jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
            jsonParam.addProperty("platform", "1");

            Ion.with(OfferHistory.this)
            .load(common_variable.main_web_url+"/offer/offer_check_owner")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        if (result.get("success").toString().equals("true")) {
                            JsonArray ShopOffers = result.getAsJsonArray("data");

                            if (ShopOffers != null) {
                                RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(ShopOffers);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recycler_view.setLayoutManager(mLayoutManager);
                                recycler_view.setItemAnimator(new DefaultItemAnimator());
                                recycler_view.setAdapter(mAdapter);
                            } else {
                                Toast.makeText(OfferHistory.this, "No record found", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(OfferHistory.this, "No record found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }catch (Exception ec){}
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>
    {
        private JsonArray pList;

        public class RecyclerViewHolder extends RecyclerView.ViewHolder
        {
            public TextView Offer_Name, offer_valid_date;
            LinearLayout la_Offer;
            ImageView btn_edit, btn_del, Offer_Image;

            public RecyclerViewHolder(View view)
            {
                super(view);
                Offer_Name = (TextView) view.findViewById(R.id.Offer_Name);
                Offer_Image = (ImageView) view.findViewById(R.id.Offer_Image);
                offer_valid_date = (TextView)view.findViewById(R.id.offer_valid_date);
                la_Offer = (LinearLayout)view.findViewById(R.id.la_Offer);
                btn_edit = (ImageView) view.findViewById(R.id.btn_edit);
                btn_del = (ImageView) view.findViewById(R.id.btn_del);
            }
        }
        public RecyclerViewAdapter(JsonArray pList)
        {   this.pList = pList;  }

        @Override
        public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_offer_history_view, parent, false);
            return new RecyclerViewAdapter.RecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.RecyclerViewHolder holder, final int position) {
            final JsonObject jobj = (JsonObject) pList.get(position);

            holder.Offer_Name.setText(jobj.get("offer_name").toString().replaceAll("^\"|\"$", ""));

            String dtFrom = jobj.get("offer_from_date").toString().replaceAll("^\"|\"$", "").substring(0, 10);
            String dtTo = jobj.get("offer_to_date").toString().replaceAll("^\"|\"$", "").substring(0, 10);

            holder.offer_valid_date.setText(dtFrom+"  to  "+dtTo);

            /*Glide.with(getApplicationContext())
            .load(common_variable.main_web_url+"/uploads/offers/"+ jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
            .asBitmap()
            .dontAnimate()
            .centerCrop()
            .into(new BitmapImageViewTarget(holder.Offer_Image)
            {
                @Override
                protected void setResource(Bitmap resource)
                {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.Offer_Image.setImageDrawable(circularBitmapDrawable);
                }
            });*/
            Ion.with(OfferHistory.this)
                    .load(common_variable.main_web_url+"/uploads/offers/"+ jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(holder.Offer_Image);

//            Glide.with(getApplicationContext()).load(common_variable.main_web_url+"/uploads/offers/"+ jobj.get("offer_img").toString().replaceAll("^\"|\"$", "")).into(holder.Offer_Image);

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    main_dialog=new Dialog(OfferHistory.this,android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                    main_dialog.setContentView(R.layout.add_offer_dialog);
//                    int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
//                    int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
//                    main_dialog.getWindow().setLayout(width, height);

                    offerImg = (ImageView)main_dialog.findViewById(R.id.imgOffer);
                    final EditText etOfferType = (EditText)main_dialog.findViewById(R.id.etOfferType);
                    final EditText etOfferDescri = (EditText)main_dialog.findViewById(R.id.etOfferDescri);
                    etOfferValidFrom = (EditText)main_dialog.findViewById(R.id.etOfferValidFrom);
                    etOfferValidTo = (EditText)main_dialog.findViewById(R.id.etOfferValidTo);
                    final ImageView changeOfferImage = (ImageView)main_dialog.findViewById(R.id.changeOfferImage);
                    Button btnAdd = (Button)main_dialog.findViewById(R.id.btnAdd);
                    btnAdd.setText("Update");

                   /* Glide.with(getApplicationContext())
                    .load(common_variable.main_web_url+"/uploads/offers/"+ jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                    .placeholder(R.drawable.ic_img_icon)
                    .into(offerImg);*/

                    Ion.with(OfferHistory.this)
                            .load(common_variable.main_web_url+"/uploads/offers/"+ jobj.get("offer_img").toString().replaceAll("^\"|\"$", ""))
                            .withBitmap()
                            .placeholder(R.raw.loading)
                            .error(R.drawable.error)
                            .animateLoad(R.anim.zoom_in)
                            .animateIn(R.anim.fade_in)
                            .intoImageView(offerImg);

                    etOfferType.setText(holder.Offer_Name.getText().toString());
                    etOfferDescri.setText(jobj.get("offer_description").toString().replaceAll("^\"|\"$", ""));
                    etOfferValidFrom.setText(jobj.get("offer_from_date").toString().replaceAll("^\"|\"$", ""));
                    etOfferValidTo.setText(jobj.get("offer_to_date").toString().replaceAll("^\"|\"$", ""));

                    changeOfferImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cameraGalleryDialog();
                        }
                    });

                    etOfferValidFrom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            date_flag = 0;
                            showDialog(DATE_DIALOG_ID);
                        }
                    });

                    etOfferValidTo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            date_flag = 1;
                            showDialog(DATE_DIALOG_ID);
                        }
                    });

                    btnAdd.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view) {

                            if (etOfferType.getText().toString().equals(""))
                            {
                                etOfferType.requestFocus();
                                etOfferType.setError("Enter offer name");
                            }
                            else if (etOfferDescri.getText().toString().equals(""))
                            {
                                etOfferDescri.requestFocus();
                                etOfferDescri.setError("Enter offer description");
                            }
                            else if(etOfferValidFrom.getText().toString().equals(""))
                            {
                                etOfferValidFrom.requestFocus();
                                etOfferValidFrom.setError("Enter offer valid date from");
                            }
                            else if(etOfferValidTo.getText().toString().equals(""))
                            {
                                etOfferValidTo.requestFocus();
                                etOfferValidTo.setError("Enter offer valid date to");
                            }
                            else
                            {
                                final ProgressDialog dialog = new ProgressDialog(OfferHistory.this);
                                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                dialog.setMessage("Loading...");
                                dialog.show();

                                JsonArray req = new JsonArray();
                                JsonObject jsonParam = new JsonObject();
                                try {
                                    JsonObject fullObj = new JsonObject();
                                    jsonParam = new JsonObject();
                                    jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                    //jsonParam.addProperty("user_id", sharedpreferences.getString("user_id", null).toString().replaceAll("^\"|\"$", ""));
                                    jsonParam.addProperty("user_id", common_variable.User_id.toString().replaceAll("^\"|\"$", ""));
                                    jsonParam.addProperty("_id", "" + jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                                    jsonParam.addProperty("platform", "1");
                                    jsonParam.addProperty("offer_name", "" + etOfferType.getText().toString());
                                    jsonParam.addProperty("offer_description", "" + etOfferDescri.getText().toString());
                                    jsonParam.addProperty("offer_from_date", "" + etOfferValidFrom.getText().toString());
                                    jsonParam.addProperty("offer_to_date", "" + etOfferValidTo.getText().toString());
                                    if(changed_offer_img == 1)
                                    {
                                        jsonParam.addProperty("offer_img",""+BitMapToString(bmp));
                                        changed_offer_img = 0;
                                    }
                                } catch (Exception e) {
                                    Log.e("123", "" + e.getMessage().toString());
                                }
                                Log.e("123","OfferrrrHistory"+jsonParam.toString());
                                Ion.with(getApplicationContext())
                                .load(common_variable.main_web_url+"/offer/updateoffer")
                                .progressDialog(dialog)
                                .setJsonObjectBody(jsonParam)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>()
                                {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result)
                                    {
                                        dialog.dismiss();
                                        main_dialog.dismiss();
                                        common_variable.Refresh_Offer = 1;
                                        JsonObject jsonParam = new JsonObject();
                                        jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                        jsonParam.addProperty("platform", "1");

                                        Ion.with(OfferHistory.this)
                                        .load(common_variable.main_web_url+"/offer/offer_check_owner")
                                        .setJsonObjectBody(jsonParam)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>()
                                        {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result)
                                            {
                                                try {
                                                    if (result.get("success").toString().equals("true")) {
                                                        JsonArray ShopOffers = result.getAsJsonArray("data");

                                                        if (ShopOffers != null) {
                                                            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(ShopOffers);
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
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(OfferHistory.this);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Are you sure you want to delete your offer?");
                    alertDialog.setIcon(R.drawable.ic_alert);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dg, int which)
                        {
                            dg.dismiss();
                            final ProgressDialog dialog = new ProgressDialog(OfferHistory.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setMessage("Loading...");
                            dialog.show();

                            JsonObject jsonParam = new JsonObject();
                            try
                            {
                                jsonParam.addProperty("user_id", common_variable.User_id);
                                jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                jsonParam.addProperty("_id", jobj.get("_id").toString().replaceAll("^\"|\"$", ""));
                                jsonParam.addProperty("platform", "1");

                                Ion.with(OfferHistory.this)
                                .load(common_variable.main_web_url+"/offer/offer_delete_android")
                                .progressDialog(dialog)
                                .setJsonObjectBody(jsonParam)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>()
                                {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result)
                                    {
                                        dialog.dismiss();
                                        common_variable.Refresh_Offer = 1;
                                        JsonObject jsonParam = new JsonObject();
                                        jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                                        jsonParam.addProperty("platform", "1");

                                        Ion.with(OfferHistory.this)
                                        .load(common_variable.main_web_url+"/offer/offer_check_owner")
                                        .setJsonObjectBody(jsonParam)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>()
                                        {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result)
                                            {
                                                try {
                                                    if (result.get("success").toString().equals("true")) {
                                                        JsonArray ShopOffers = result.getAsJsonArray("data");

                                                        if (ShopOffers != null) {
                                                            RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(ShopOffers);
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
        {   return pList.size();  }
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

    public void cameraGalleryDialog()
    {
        LayoutInflater li = LayoutInflater.from(OfferHistory.this);
        final View prompt = li.inflate(R.layout.choose_photo, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(OfferHistory.this, R.style.MyAlertTheme));

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

        Bitmap scale = Bitmap.createScaledBitmap(image, 1300, 1100, true);

        return scale;
    }
    private Bitmap getBitmapFromUri4Camera(Uri uri) throws OutOfMemoryError, IOException
    {
        Bitmap bmp_img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        Bitmap scale = Bitmap.createScaledBitmap(bmp_img, 1300, 1100, true);
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
                            bmp = getLargeBitmapFromUri(selectedImage);
                            offerImg.setImageBitmap(bmp);
                        }
                        catch (Exception ex) {
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
        private ProgressDialog Dialog = new ProgressDialog(OfferHistory.this);
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
            offerImg.setImageBitmap(null);
            bmp = bit;
            changed_offer_img = 1;
            offerImg.setImageBitmap(bit);

            if (Dialog.isShowing() == true) {
                Dialog.dismiss();
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        switch(id){
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, pDateSetList, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pDateSetList = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int Year = year;
            int Month = monthOfYear+1;
            int Day = dayOfMonth;

            String d="", m="";

            if(String.valueOf(Day).length() == 1)
                d = "0" + Day;
            else
                d = Day+"";

            if(String.valueOf(Month).length() == 1)
                m = "0" + Month;
            else
                m = Month+"";

            String dt = year+"-"+m+"-"+d;

            if(date_flag == 0)
            {
                etOfferValidFrom.setText(dt);
                date_flag = 0;
            }
            else
            {
                etOfferValidTo.setText(dt);
                date_flag = 0;
            }
        }
    };
}
