package com.clickitproduct.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.clickitproduct.R;
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

import static com.clickitproduct.commonutil.common_variable.MyPREFERENCES;
import static com.clickitproduct.commonutil.common_variable.sharedpreferences;

public class ServicesForm extends AppCompatActivity
{
    EditText etMobile, etCategory, etLatti, etLongi, etEmail, etAddress, etPinCode, etCity,etHoliday, etTimeFrom, etTimeTo, etAdharNo, etOrganisationName;
    ImageView btnMap, edit_profile_photo, user_profile_photo;
    TextView tvTimeFrom, tvTimeTo;
    Button btnRegisterService;
    String Fname, Lname, Mobile, Category, Address, Pincode, City, Lattitude, Longitude,Email, Holiday, TimeTo="", TimeFrom="", AdharNo, OrganisationName;
    Uri imageUri;
    private static final int PROFILE = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static final int RESULT_CROP = 3;
    private Bitmap bmp;
    String picUserPathUrl = "";
    String nm;
    String image="";
    private String[] img_user;
    int time_flag;
    static final int TIME_DIALOG_ID = 1;
    private int hour;
    private int minute;
    public static String lat = "", lon = "";
    int editService = 0;
    int changed_service_image = 0;
    String Service_Id;
    ListView dialog_ListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_form);

        edit_profile_photo = (ImageView)findViewById(R.id.edit_profile_photo);
        user_profile_photo = (ImageView)findViewById(R.id.user_profile_photo);
        etAdharNo = (EditText)findViewById(R.id.etAdharNo);
        etOrganisationName = (EditText)findViewById(R.id.etOrganisationName);
        etMobile = (EditText)findViewById(R.id.etMobile);
        etCategory = (EditText)findViewById(R.id.etCategory);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etAddress = (EditText)findViewById(R.id.etAddress);
        etPinCode = (EditText)findViewById(R.id.etPinCode);
        etCity = (EditText)findViewById(R.id.etCity);
        etLatti = (EditText)findViewById(R.id.etLatti);
        etLongi = (EditText)findViewById(R.id.etLongi);
        etHoliday = (EditText)findViewById(R.id.etHoliday);
        etTimeFrom = (EditText)findViewById(R.id.etTimeFrom);
        etTimeTo = (EditText)findViewById(R.id.etTimeTo);
        tvTimeFrom = (TextView)findViewById(R.id.tvTimeFrom);
        tvTimeTo = (TextView)findViewById(R.id.tvTimeTo);
        btnMap = (ImageView)findViewById(R.id.btnMap);
        btnRegisterService = (Button)findViewById(R.id.btnRegisterService);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    final ProgressDialog dialog = new ProgressDialog(ServicesForm.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    JsonObject jsonParam = new JsonObject();
                    Ion.with(ServicesForm.this)
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

                                final AlertDialog.Builder builder = new AlertDialog.Builder(ServicesForm.this);
                                builder.setItems(cities, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        etCity.setText(cities[i] + "");
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

        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = new ProgressDialog(ServicesForm.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("wait...");
                dialog.show();

                JsonObject jsonParam = new JsonObject();

                Ion.with(getApplicationContext())
                .load(common_variable.main_web_url+"/serviceprovider/serviceCategory_check")
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
                            JsonArray srvc_cat = result.getAsJsonArray("data");
                            JsonObject jCat;
                            ArrayList<String> service_categories = new ArrayList<String>();

                            for (int i = 0; i < srvc_cat.size(); i++) {
                                jCat = (JsonObject) srvc_cat.get(i);
                                service_categories.add(jCat.get("servicecat_name").toString().replaceAll("^\"|\"$", ""));
                            }

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ServicesForm.this);
                            LayoutInflater inflater = ServicesForm.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.list_view, null);
                            dialogBuilder.setView(dialogView);

                            dialogBuilder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            long[] stdrno = dialog_ListView.getCheckItemIds();
                                            String cats = "";
                                            for (int i = 0; i < stdrno.length; i++) {
                                                cats = cats + dialog_ListView.getItemAtPosition((int) stdrno[i]);
                                                if (i != stdrno.length - 1)
                                                    cats = cats + ", ";
                                            }
                                            etCategory.setText(cats);
                                            dialog.dismiss();
                                        }
                                    });

                            dialogBuilder.setNegativeButton("cancel",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();
                            dialog_ListView = (ListView) alertDialog.findViewById(R.id.listView);
                            dialog_ListView.setChoiceMode(dialog_ListView.CHOICE_MODE_MULTIPLE);
                            dialog_ListView.setAdapter(new ArrayAdapter<String>(ServicesForm.this, android.R.layout.simple_list_item_checked, service_categories));
                        }
                        catch (Exception c){}
                    }
                });
            }
        });

        etHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ServicesForm.this);
                builder.setItems(common_variable.Holidays, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        etHoliday.setText(common_variable.Holidays[i] + "");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        tvTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ServicesForm.this);
                builder.setItems(common_variable.Time_Format, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvTimeFrom.setText(common_variable.Time_Format[i] + "");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        tvTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ServicesForm.this);
                builder.setItems(common_variable.Time_Format, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvTimeTo.setText(common_variable.Time_Format[i] + "");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        etTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_flag = 1;
                showDialog(TIME_DIALOG_ID);
            }
        });

        etTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_flag = 0;
                showDialog(TIME_DIALOG_ID);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ServicesForm.this, MapsActivity.class);
                startActivity(i);
            }
        });

        etMobile.setText(sharedpreferences.getString("mobile", "").toString().replaceAll("^\"|\"$",""));
        etEmail.setText(sharedpreferences.getString("email", "").toString().replaceAll("^\"|\"$",""));

        edit_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraGalleryDialog();
            }
        });

        btnRegisterService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(bmp != null) {
                    image = BitMapToString(bmp);
                }

                Fname = sharedpreferences.getString("first_name", "").toString().replaceAll("^\"|\"$","");
                Lname = sharedpreferences.getString("last_name", "").toString().replaceAll("^\"|\"$","");
                OrganisationName = etOrganisationName.getText().toString();
                AdharNo = etAdharNo.getText().toString();
                Mobile =  etMobile.getText().toString();
                Category = etCategory.getText().toString();
                Email = etEmail.getText().toString();
                Address = etAddress.getText().toString();
                Pincode = etPinCode.getText().toString();
                City = etCity.getText().toString();
                Lattitude = etLatti.getText().toString();
                Longitude = etLongi.getText().toString();
                Holiday = etHoliday.getText().toString();
                if(!etTimeFrom.getText().toString().equals("")) {
                    TimeFrom = etTimeFrom.getText().toString()+" "+tvTimeFrom.getText().toString();
                }
                if(!etTimeTo.getText().toString().equals("")) {
                    TimeTo = etTimeTo.getText().toString()+" "+tvTimeTo.getText().toString();
                }

                if(editService == 0 && bmp == null)
                {
                    Toast.makeText(ServicesForm.this, "Select image", Toast.LENGTH_SHORT).show();
                }
                else if(OrganisationName.equals(""))
                {
                    etOrganisationName.requestFocus();
                    etOrganisationName.setError("Enter organisation name");
                }
                else if(AdharNo.equals(""))
                {
                    etAdharNo.requestFocus();
                    etAdharNo.setError("Enter valid Adhar number");
                }
                else if(Category.equals(""))
                {
                    etCategory.requestFocus();
                    etCategory.setError("Select Category");
                }
                else if(Mobile.equals("") && Mobile.length()<9)
                {
                    etMobile.requestFocus();
                    etMobile.setError("Enter valid Mobile number");
                }
                else if(Email.equals(""))
                {
                    etEmail.requestFocus();
                    etEmail.setError("Enter valid Email Address");
                }
                else if(Address.equals(""))
                {
                    etAddress.requestFocus();
                    etAddress.setError("Enter Address number");
                }
                else if(Pincode.equals(""))
                {
                    etPinCode.requestFocus();
                    etPinCode.setError("Enter Pincode");
                }
                else if(City.equals(""))
                {
                    etCity.requestFocus();
                    etCity.setError("Select City");
                }
                else if(Holiday.equals(""))
                {
                    etHoliday.requestFocus();
                    etHoliday.setError("Select Holiday");
                }
                else if(TimeFrom.equals(""))
                {
                    etTimeFrom.requestFocus();
                    etTimeFrom.setError("Select From Time");
                }
                else if(TimeTo.equals(""))
                {
                    etTimeTo.requestFocus();
                    etTimeTo.setError("Select To Time");
                }
                else if((Lattitude.equals("") && Longitude.equals("")) || (Lattitude.equals("0.0") && Longitude.equals("0.0")))
                {
                    Toast.makeText(getApplicationContext(), "Select Your Location", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(editService == 1)
                    {
                        updateDetails();
                    }
                    else
                    {
                        sendDetails();
                    }
                }
            }
        });

        editService = getIntent().getIntExtra("editService", 0);

        if(editService == 1)
        {
            btnRegisterService.setText("UPDATE");

            JsonObject serviceJsonObject = UserServices.serviceJsonObject;
            Log.e("1234555sssss", serviceJsonObject.toString());
            JsonObject jServiceImages = serviceJsonObject.getAsJsonObject("images");
            JsonObject jServiceAddress = serviceJsonObject.getAsJsonObject("address");
            JsonObject jServiceSchedule = serviceJsonObject.getAsJsonObject("service_time");

            Service_Id = serviceJsonObject.get("_id").toString().replaceAll("^\"|\"$","");
//            etOrganisationName.setText(serviceJsonObject.get("orgnm").toString().replaceAll("^\"|\"$",""));
            etAdharNo.setText(serviceJsonObject.get("aadharno").toString().replaceAll("^\"|\"$",""));
            etMobile.setText(serviceJsonObject.get("mobile").toString().replaceAll("^\"|\"$",""));
            etEmail.setText(serviceJsonObject.get("email").toString().replaceAll("^\"|\"$",""));
            etAddress.setText(jServiceAddress.get("address").toString().replaceAll("^\"|\"$",""));
            etPinCode.setText(jServiceAddress.get("pincode").toString().replaceAll("^\"|\"$",""));
            etCity.setText(jServiceAddress.get("city").toString().replaceAll("^\"|\"$",""));
            etHoliday.setText(jServiceSchedule.get("holiday").toString().replaceAll("^\"|\"$",""));
            String timeFrom = jServiceSchedule.get("time_from").toString().replaceAll("^\"|\"$","");
            String timeTo = jServiceSchedule.get("time_to").toString().replaceAll("^\"|\"$","");

            etCategory.setText(serviceJsonObject.get("category").toString().replaceAll("^\"|\"$", ""));
            etCategory.setFocusable(false);
            etCategory.setClickable(false);

            if(timeFrom.contains(" ")){
                String []t_from = timeFrom.split("\\s+");
                timeFrom = t_from[0];
            }

            if(timeTo.contains(" ")){
                String[]t_to = timeTo.split("\\s+");
                timeTo = t_to[0];
            }

            etTimeFrom.setText(timeFrom);
            etTimeTo.setText(timeTo);
            etLatti.setText(jServiceAddress.get("latitude").toString().replaceAll("^\"|\"$", ""));
            etLongi.setText(jServiceAddress.get("longitude").toString().replaceAll("^\"|\"$", ""));
            lat = jServiceAddress.get("latitude").toString().replaceAll("^\"|\"$", "");
            lon = jServiceAddress.get("longitude").toString().replaceAll("^\"|\"$", "");

           /* Glide.with(ServicesForm.this)
            .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jServiceImages.get("pics").toString().replaceAll("^\"|\"$", ""))
            .into(user_profile_photo);*/

            Ion.with(ServicesForm.this)
                    .load(common_variable.main_web_url+"/uploads/serviceproviders/"+jServiceImages.get("pics").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(user_profile_photo);
        }
    }

    public void sendDetails()
    {
        JsonObject jsonParam = new JsonObject();

        final ProgressDialog dialog = new ProgressDialog(ServicesForm.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        jsonParam.addProperty("orgnm", OrganisationName);
        jsonParam.addProperty("aadharno", AdharNo);
        jsonParam.addProperty("user_id", common_variable.User_id);
        jsonParam.addProperty("fname", Fname);
        jsonParam.addProperty("lname", Lname);
        jsonParam.addProperty("category", Category);
        jsonParam.addProperty("mobile", Mobile);
        jsonParam.addProperty("email", Email);
        jsonParam.addProperty("address", Address);
        jsonParam.addProperty("city", City);
        jsonParam.addProperty("pincode", Pincode);
        jsonParam.addProperty("latitude", Lattitude);
        jsonParam.addProperty("longitude", Longitude);
        jsonParam.addProperty("timefrom", TimeFrom);
        jsonParam.addProperty("timeto", TimeTo);
        jsonParam.addProperty("holiday", Holiday);
        jsonParam.addProperty("pics", image);
        jsonParam.addProperty("platform", "1");

        Log.e("sssssssservicesppp", jsonParam.toString());

        Ion.with(getApplicationContext())
        .load(common_variable.main_web_url +"/serviceprovider/service_registration_android")
        .progressDialog(dialog)
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.e("sssssssservices", result.toString());
                dialog.dismiss();
                Toast.makeText(ServicesForm.this, "Your are regitered successfully", Toast.LENGTH_SHORT).show();
                common_variable.Refresh_Service = 1;
                finish();
            }
        });
    }

    public void updateDetails()
    {
        JsonObject jsonParam = new JsonObject();

        final ProgressDialog dialog = new ProgressDialog(ServicesForm.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        if(changed_service_image == 1)
        {
            jsonParam.addProperty("pics", image);
        }
        jsonParam.addProperty("_id", Service_Id);
        jsonParam.addProperty("orgnm", OrganisationName);
        jsonParam.addProperty("aadharno", AdharNo);
        jsonParam.addProperty("user_id", common_variable.User_id);
        jsonParam.addProperty("fname", Fname);
        jsonParam.addProperty("lname", Lname);
        jsonParam.addProperty("category", Category);
        jsonParam.addProperty("mobile", Mobile);
        jsonParam.addProperty("email", Email);
        jsonParam.addProperty("address", Address);
        jsonParam.addProperty("city", City);
        jsonParam.addProperty("pincode", Pincode);
        jsonParam.addProperty("latitude", Lattitude);
        jsonParam.addProperty("longitude", Longitude);
        jsonParam.addProperty("timefrom", TimeFrom);
        jsonParam.addProperty("timeto", TimeTo);
        jsonParam.addProperty("holiday", Holiday);
        jsonParam.addProperty("platform", "1");

        Ion.with(getApplicationContext())
        .load(common_variable.main_web_url +"/serviceprovider/updateserviceprovider")
        .progressDialog(dialog)
        .setJsonObjectBody(jsonParam)
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.i("ssssssss", result.toString());
                dialog.dismiss();
                Toast.makeText(ServicesForm.this, "Your are updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void cameraGalleryDialog()
    {
        LayoutInflater li = LayoutInflater.from(ServicesForm.this);
        final View prompt = li.inflate(R.layout.choose_photo, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ServicesForm.this, R.style.MyAlertTheme));
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
                        nm = "uimg.jpg";
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        try {
                            bmp = getBitmapFromUri4Camera(imageUri);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        user_profile_photo.setImageBitmap(bmp);
                    }
                    catch (OutOfMemoryError exp)
                    { exp.printStackTrace(); }
                    break;

                case PROFILE:
                    Log.e("12345 ", "inside profile");
                    Uri selectedImage1 = data.getData();
                    String[] filePathColumn1 = {MediaStore.Images.Media.DATA};
                    Cursor cursor1 = getContentResolver().query(selectedImage1, filePathColumn1, null, null, null);
                    cursor1.moveToFirst();
                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn1[0]);
                    String filePath1 = cursor1.getString(columnIndex1);
                    cursor1.close();
                    String[] img1 = filePath1.split("/");
                    img_user = img1[img1.length - 1].split("\\.");
                    bmp = null;
                    try
                    {
                        performCrop(filePath1);
                        nm = "uimg." + img_user[img_user.length - 1];
                    }
                    catch (Exception e)
                    {
                        try {
                            bmp = getBitmapFromUri4Gallary(selectedImage1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        user_profile_photo.setImageBitmap(bmp);
                        e.printStackTrace();
                    }
                    catch (OutOfMemoryError exp)
                    {exp.printStackTrace(); }
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
                            user_profile_photo.setImageBitmap(bmp);
                            image = BitMapToString(bmp);

                            Log.e("1111111", BitMapToString(bmp));

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
        try
        {
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
        catch (ActivityNotFoundException anfe)
        {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class setImage extends AsyncTask<Bitmap, Void, Void>
    {
        private ProgressDialog Dialog = new ProgressDialog(ServicesForm.this);
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
            user_profile_photo.setImageBitmap(null);
            bmp = bit;
            image = BitMapToString(bmp);
            user_profile_photo.setImageBitmap(bit);
            if(editService == 1)
            {
                changed_service_image = 1;
            }


            if (Dialog.isShowing() == true)
            { Dialog.dismiss(); }
        }
    }

    private static String pad(int c)
    {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, timePickerListener, hour, minute, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
        new TimePickerDialog.OnTimeSetListener()
        {
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute)
            {
                if(time_flag == 1)
                {
                    etTimeFrom.setText(new StringBuilder().append(pad(selectedHour)).append(":").append(pad(selectedMinute)));
                    time_flag = 0;
                }
                else
                {
                    etTimeTo.setText(new StringBuilder().append(pad(selectedHour)).append(":").append(pad(selectedMinute)));
                    time_flag = 0;
                }
            }
        };

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        etLatti.setText(lat + "");
        etLongi.setText(lon + "");
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
}
