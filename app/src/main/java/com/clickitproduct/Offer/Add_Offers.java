package com.clickitproduct.Offer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.clickitproduct.R;
import com.clickitproduct.User_Profile.Profile_new;
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
import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;

public class Add_Offers extends AppCompatActivity{

    TextView tvNote;
    LinearLayout laOfferForm;
    private static final int RESULT_CROP = 3;
    Uri imageUri;
    private Bitmap bmp1;
    ImageView imgOffer;
    EditText etOfferType, etOfferDescri, etOfferValidFrom, etOfferValidTo;
    Bitmap objBitGlobleProfile;
    public static JsonArray ShopOffers;
    String o_id;
    public static String offer_url_api;
    Toolbar mActionBarToolbar;
    int date_flag = 0;
    final int DATE_DIALOG_ID = 0;
    int day, month, year;
    public int selected_image = 0;

    public static int CAMERA_PREVIEW_RESULT = 1;
    String AddOfferimage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_offer_dialog);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Offers");
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvNote = (TextView)findViewById(R.id.tvNote);
        laOfferForm = (LinearLayout)findViewById(R.id.laOfferForm);
        ImageView changeOfferImage = (ImageView)findViewById(R.id.changeOfferImage);
        imgOffer = (ImageView) findViewById(R.id.imgOffer);
        etOfferType=(EditText)findViewById(R.id.etOfferType);
        etOfferDescri=(EditText)findViewById(R.id.etOfferDescri);
        etOfferValidFrom=(EditText)findViewById(R.id.etOfferValidFrom);
        etOfferValidTo=(EditText)findViewById(R.id.etOfferValidTo);
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        laOfferForm.setVisibility(View.GONE);

        JsonObject jsonParamLogin = new JsonObject();
        jsonParamLogin.addProperty("user_id", common_variable.User_id);
        jsonParamLogin.addProperty("shop_id", common_variable.User_Shop_ID+"");
        jsonParamLogin.addProperty("platform", "1");

        Ion.with(getApplicationContext())
        .load(common_variable.main_web_url + "/offer/check_offer_validity")
        .setJsonObjectBody(jsonParamLogin)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>()
        {
            @Override
            public void onCompleted(Exception e, JsonObject result)
            {

                try{
                    if(result.get("success").toString().equals("true"))
                    {
                        laOfferForm.setVisibility(View.VISIBLE);
                        tvNote.setVisibility(View.GONE);
                    }
                    else
                    {
                        laOfferForm.setVisibility(View.GONE);
                        tvNote.setVisibility(View.VISIBLE);
                        tvNote.setText("You are allowed to add next Offer after \nprevious offer get expired.");
                    }
                }catch (Exception ex){}
            }
        });

        JsonObject jsonParam = new JsonObject();
        try
        {
            jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
            jsonParam.addProperty("platform", "1");
            Log.e("1234","Add Offer "+jsonParam.toString());

            Ion.with(Add_Offers.this)
            .load(common_variable.main_web_url+"/offer/offer_check")
            .setJsonObjectBody(jsonParam)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>()
            {
                @Override
                public void onCompleted(Exception e, JsonObject result)
                {
                    try {
                        if (result.get("success").toString().equals("true")) {
                            offer_url_api = "/offer/offer_update";
                            ShopOffers = result.getAsJsonArray("data");

                            try {
                                o_id = result.get("_id").toString().replaceAll("^\"|\"$", "");
                            } catch (Exception ec) {
                            }
                        } else if (result.get("success").toString().equals("false")) {
                            offer_url_api = "/offer/offer_registration";
                        }
                    }catch (Exception ec){}
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        changeOfferImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selected_image = 1;
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

                new CameraPreviewBuilder(Add_Offers.this)
                        .setSettingsList(settingsList)
                        .startActivityForResult(Add_Offers.this, CAMERA_PREVIEW_RESULT);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
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
                    final ProgressDialog Pdialog = new ProgressDialog(Add_Offers.this);
                    Pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    Pdialog.setMessage("Loading...");
                    Pdialog.setCancelable(false);
                    Pdialog.show();

                    JsonArray req = new JsonArray();
                    JsonObject jsonParam = new JsonObject();
                    try {
                        JsonObject fullObj = new JsonObject();
                        jsonParam = new JsonObject();
                        jsonParam.addProperty("shop_id", common_variable.User_Shop_ID);
                        jsonParam.addProperty("user_id", common_variable.User_id);
                        jsonParam.addProperty("location", Profile_new.Shop_loc);
                        jsonParam.addProperty("category", common_variable.User_Shop_Category.replace("\\", "/"));
                        jsonParam.addProperty("platform", "1");
                        if(offer_url_api.equals("/offer/offer_update"))
                        {jsonParam.addProperty("offer_id", o_id);}

                        JsonObject reqObj = new JsonObject();
                        reqObj.addProperty("offer_name", "" + etOfferType.getText().toString());
                        reqObj.addProperty("offer_desc", "" + etOfferDescri.getText().toString());
                        reqObj.addProperty("offer_from_date", "" + etOfferValidFrom.getText().toString());
                        reqObj.addProperty("offer_to_date", "" + etOfferValidTo.getText().toString());
                        reqObj.addProperty("offer_image", "" + AddOfferimage);
                        req.add(reqObj);
                        jsonParam.add("offer_data", req);

                    } catch (Exception e) {}
                   Log.e("1234","Add Offer 2 "+jsonParam.toString());

                    Ion.with(getApplicationContext())
                    .load(common_variable.main_web_url+offer_url_api )
                    .progressDialog(Pdialog)
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>()
                    {
                        @Override
                        public void onCompleted(Exception e, JsonObject result)
                        {
                            try
                            {
                                Pdialog.dismiss();
                                common_variable.Refresh_Offer = 1;
                                common_variable.Refresh_noti_count = 1;
                                Toast.makeText(Add_Offers.this, "Offer added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            catch (Exception e3)
                            { Toast.makeText(Add_Offers.this, "something error"+e3.getMessage().toString(), Toast.LENGTH_SHORT).show(); }
                        }
                    });
                }
            }
        });
    }

    /*public void cameraGalleryDialog()
    {
        LayoutInflater li = LayoutInflater.from(Add_Offers.this);
        final View prompt = li.inflate(R.layout.choose_photo, null);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(Add_Offers.this, R.style.MyAlertTheme));

        alertDialogBuilder.setView(prompt);
        alertDialogBuilder.setCancelable(true);
        final android.app.AlertDialog dialog = alertDialogBuilder.show();

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
                startActivityForResult(i, PRODUCT);
                dialog.cancel();
            }
        });
    }*/
    public void getPath()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
        if (resultCode == Activity.RESULT_OK)
        {
            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);
            Log.e("123", "ewwwq");

            if (resultPath != null) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(resultPath, bmOptions);
                imgOffer.setImageBitmap(bitmap);
                AddOfferimage = BitMapToString(bitmap);
                Log.e("123", "PH Img" + AddOfferimage);
            }

            if (sourcePath != null) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(resultPath, bmOptions);
                imgOffer.setImageBitmap(bitmap);
                AddOfferimage = BitMapToString(bitmap);
                Log.e("123", "PH Img" + AddOfferimage);
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PREVIEW_RESULT && data != null) {
        } else {     finish();  }
    }

            /*switch (requestCode) {
                case IMAGE_CAPTURE:
                    try {
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picUserPathUrl = cursor.getString(columnIndex);
                        performCrop(picUserPathUrl);
                    } catch (Exception e) {
                        try {
                            bmp1 = getBitmapFromUri4Camera(imageUri);
                            image = BitMapToString(bmp1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        imgOffer.setImageBitmap(bmp1);
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
                    String[] img_offer1 = img1[img1.length - 1].split("\\.");

                    bmp1 = null;
                    try {
                        performCrop(filePath1);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        try {
                            bmp1 = getBitmapFromUri4Gallary(selectedImage1);
                            image = BitMapToString(bmp1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        imgOffer.setImageBitmap(bmp1);
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
                            // objUserRegistration.setBitmapProfileImg(BitMapToString(bi));
                            imgOffer.setImageBitmap(bmp1);
                        }
                        catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), "Sorry don't crop in Google Images...please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }*/




    private Bitmap getLargeBitmapFromUri(Uri uri) throws OutOfMemoryError, IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Bitmap scale = Bitmap.createScaledBitmap(image, 500, 600, true);
        return scale;
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
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);
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

    public class setImage extends AsyncTask<Bitmap, Void, Void>
    {
        private ProgressDialog Dialog = new ProgressDialog(Add_Offers.this);
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
            imgOffer.setImageBitmap(null);
            bmp1 = bit;
            imgOffer.setImageBitmap(bit);
            //image = BitMapToString(bmp1);
            if (Dialog.isShowing() == true)
            {
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            this.finish();
            return true;
        }
        else if(item.getItemId() == R.id.history)
        {
            startActivity(new Intent(Add_Offers.this, OfferHistory.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
