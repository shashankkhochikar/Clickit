package com.clickitproduct.Classifieds;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.clickitproduct.R;
import com.clickitproduct.User_Profile.UserClassifieds;
import com.clickitproduct.activities.MapsActivity;
import com.clickitproduct.activities.common_variable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import org.json.JSONArray;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class AddClassified extends AppCompatActivity
{
    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    EditText etAdTitle, etTypes, etCategory, etDescription, etPrice, etAddress, etCity, etPincode, etLat, etLon, etMobile;
    ImageView ivAd1, ivAd2, ivAd3, btnMap;
    Button btnRegister;
    String image_flag;
    Uri imageUri;
    private static final int AD1 = 1;
    private static final int AD2 = 2;
    private static final int AD3 = 3;
    private static final int AD_CAPTURE1 = 4;
    private static final int AD_CAPTURE2 = 5;
    private static final int AD_CAPTURE3 = 6;
    Bitmap bmp1, bmp2, bmp3;
    String first_name, last_name, mobile, email, AdTitle, AdType, address, city, category, descri, price,latitude, longitude, ad_image1, ad_image2, ad_image3;
    public static String lat = "", lon = "";
    int editClassifieds = 0;
    String Classified_Id;
    ListView dialog_ListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classifieds);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        etAdTitle=(EditText)findViewById(R.id.etAdTitle);
        etTypes=(EditText)findViewById(R.id.etTypes);
        etCategory=(EditText)findViewById(R.id.etCategory);
        etDescription=(EditText)findViewById(R.id.etDescription);
        etPrice=(EditText)findViewById(R.id.etPrice);
        etAddress=(EditText)findViewById(R.id.etAddress);
        etCity=(EditText)findViewById(R.id.etCity);
        etPincode=(EditText)findViewById(R.id.etPincode);
        etLat=(EditText)findViewById(R.id.etLat);
        etLon=(EditText)findViewById(R.id.etLon);
        btnMap=(ImageView)findViewById(R.id.btnMap);
        etMobile = (EditText)findViewById(R.id.etMobile);
        ivAd1 = (ImageView)findViewById(R.id.ivAd1);
        ivAd2 = (ImageView)findViewById(R.id.ivAd2);
        ivAd3 = (ImageView)findViewById(R.id.ivAd3);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        etTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddClassified.this);
                builder.setItems(common_variable.Classified_Types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        etTypes.setText(common_variable.Classified_Types[i] + "");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddClassified.this);
                LayoutInflater inflater = AddClassified.this.getLayoutInflater();
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
                dialog_ListView.setChoiceMode(dialog_ListView.CHOICE_MODE_MULTIPLE); // used for click check box
                dialog_ListView.setAdapter(new ArrayAdapter<String>(AddClassified.this, android.R.layout.simple_list_item_checked, common_variable.Classified_Categories));
            }
        });

        ivAd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_flag = "img1";
                etPincode.requestFocus();
                Dialog4Images();
            }
        });

        ivAd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_flag = "img2";
                etPincode.requestFocus();
                Dialog4Images();
            }
        });

        ivAd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_flag = "img3";
                etPincode.requestFocus();
                Dialog4Images();
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddClassified.this, MapsActivity.class);
                startActivity(i);
            }
        });

        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    final ProgressDialog dialog = new ProgressDialog(AddClassified.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    dialog.show();

                    JsonObject jsonParam = new JsonObject();
                    Ion.with(AddClassified.this)
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

                                final AlertDialog.Builder builder = new AlertDialog.Builder(AddClassified.this);
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

        etMobile.setText(sharedpreferences.getString("mobile",null).replaceAll("^\"|\"$",""));

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                first_name = sharedpreferences.getString("first_name",null).replaceAll("^\"|\"$","");
                last_name = sharedpreferences.getString("last_name",null).replaceAll("^\"|\"$","");
                email = sharedpreferences.getString("email",null).replaceAll("^\"|\"$","");

                AdTitle = etAdTitle.getText().toString();
                AdType = etTypes.getText().toString();
                address = etAddress.getText().toString();
                city = etCity.getText().toString();
                category = etCategory.getText().toString();
                latitude = etLat.getText().toString();
                longitude = etLon.getText().toString();
                descri = etDescription.getText().toString();
                price = etPrice.getText().toString();
                mobile = etMobile.getText().toString();

                if (AdTitle.equals(""))
                {
                    etAdTitle.requestFocus();
                    etAdTitle.setError("Enter advertise title");
                }
                else if (AdType.equals(""))
                {
                    etTypes.requestFocus();
                    etTypes.setError("Select advertise type");
                }
                else if(category.equals(""))
                {
                    etCategory.requestFocus();
                    etCategory.setError("Select category");
                }
                else if(descri.equals(""))
                {
                    etDescription.requestFocus();
                    etDescription.setError("Enter decription");
                }
                else if(price.equals(""))
                {
                    etPrice.requestFocus();
                    etPrice.setError("Enter price");
                }
                else if(mobile.equals(""))
                {
                    etMobile.requestFocus();
                    etMobile.setError("Enter mobile number");
                }
                else if (address.equals(""))
                {
                    etAddress.requestFocus();
                    etAddress.setError("Enter your address");
                }
                else if (city.equals(""))
                {
                    etCity.requestFocus();
                    etCity.setError("Select city name");
                }
                else if (bmp1 == null && editClassifieds == 0)
                {
                    Toast.makeText(getApplicationContext(), "Select first image", Toast.LENGTH_SHORT).show();
                }
                else if (latitude.equals("") && longitude.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Select Your Location", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    JsonObject jsonParam = new JsonObject();
                    JSONArray jCategory = new JSONArray();
                    String[] cats = category.split(",");
                    for (int j = 0; j < cats.length; j++)
                    {
                        jCategory.put(cats[j]);
                    }

                    final ProgressDialog dialog = new ProgressDialog(AddClassified.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading...");
                    dialog.show();

                    if(editClassifieds == 1)
                    {
                        jsonParam.addProperty("_id", Classified_Id);
                    }

                    jsonParam.addProperty("user_id", common_variable.User_id);
                    jsonParam.addProperty("ownername", first_name+" "+last_name);
                    jsonParam.addProperty("classprodname", AdTitle);
                    jsonParam.addProperty("classprodtype", AdType);
                    jsonParam.addProperty("classprodprice", price);
                    jsonParam.addProperty("classproddesc", descri);
                    jsonParam.addProperty("mobile", mobile);
                    jsonParam.addProperty("email", email);
                    jsonParam.addProperty("address", address);
                    jsonParam.addProperty("city", city.trim());
                    jsonParam.addProperty("category", category);
                    jsonParam.addProperty("latitude", latitude);
                    jsonParam.addProperty("longitude", longitude);
                    jsonParam.addProperty("platform", "1");

                    if (bmp1 != null)
                    {
                        jsonParam.addProperty("classpic1", ad_image1);
                        jsonParam.addProperty("classpic1name", "classpic1");
                    }

                    if (bmp2 != null)
                    {
                        jsonParam.addProperty("classpic2", ad_image2);
                        jsonParam.addProperty("classpic2name", "classpic2");
                    }

                    if (bmp3 != null)
                    {
                        jsonParam.addProperty("classpic3", ad_image3);
                        jsonParam.addProperty("classpic3name", "classpic3");
                    }

                    String classi_url = "";
                    if(editClassifieds == 0)
                    {
                        classi_url = "/classified/classified_reg";
                    }
                    else
                    {
                        classi_url = "/classified/updateclassified";
                    }

                    Ion.with(getApplicationContext())
                    .load(common_variable.main_web_url+classi_url)
                    .progressDialog(dialog)
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            dialog.dismiss();
                            common_variable.Refresh_Classified = 1;
                            finish();
                        }
                    });
                }
            }
        });

        editClassifieds = getIntent().getIntExtra("editClassifieds", 0);
        if(editClassifieds == 1)
        {
            btnRegister.setText("UPDATE");

            JsonObject classifiedJsonObject = UserClassifieds.classifiedJsonObject;
            JsonObject jClassifiedImages = classifiedJsonObject.getAsJsonObject("images");
            JsonObject jClassifiedAddress = classifiedJsonObject.getAsJsonObject("address");

            Classified_Id = classifiedJsonObject.get("_id").toString().replaceAll("^\"|\"$", "");

            etAdTitle.setText(classifiedJsonObject.get("classprod_name").toString().replaceAll("^\"|\"$", ""));
            etTypes.setText(classifiedJsonObject.get("classprod_type").toString().replaceAll("^\"|\"$", ""));
            etAddress.setText(jClassifiedAddress.get("address").toString().replaceAll("^\"|\"$", ""));
            etCity.setText(jClassifiedAddress.get("city").toString().replaceAll("^\"|\"$", ""));
            etDescription.setText(classifiedJsonObject.get("classprod_description").toString().replaceAll("^\"|\"$", ""));
            etPrice.setText(classifiedJsonObject.get("classprod_price").toString().replaceAll("^\"|\"$", ""));
            etMobile.setText(classifiedJsonObject.get("mobile").toString().replaceAll("^\"|\"$", ""));

            String cate = classifiedJsonObject.get("category").toString().replaceAll("^\"|\"$", "");
            String[] itemsCats = cate.substring(1,cate.length()-1).replaceAll("^\"|\"$","").split(",");
            String getCats = "";
            for(int i=0; i<itemsCats.length; i++)
            {
                if (i == 0)
                    getCats = itemsCats[i].replaceAll("^\"|\"$", "");
                else
                    getCats = getCats+", "+itemsCats[i].replaceAll("^\"|\"$", "");
            }
            etCategory.setText(getCats);
            etCategory.setFocusable(false);

            String location = jClassifiedAddress.get("location").toString().replaceAll("^\"|\"$","");
            location = location.substring(1,location.length()-1);
            String []loc = location.split(",");

            lat = loc[0];
            lon = loc[1];

            try{
                /*Glide.with(AddClassified.this)
                .load(common_variable.main_web_url + "/uploads/classifieds/"+jClassifiedImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
                .placeholder(R.drawable.upload)
                .into(ivAd1);*/

                Ion.with(AddClassified.this)
                    .load(common_variable.main_web_url + "/uploads/classifieds/"+jClassifiedImages.get("classified_img1").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)
                    .intoImageView(ivAd1);
            }
            catch (Exception e1) {}

            try{
                /*Glide.with(AddClassified.this)
                .load(common_variable.main_web_url + "/uploads/classifieds/"+jClassifiedImages.get("classified_img2").toString().replaceAll("^\"|\"$", ""))
                .placeholder(R.drawable.upload)
                .into(ivAd2);*/

                Ion.with(AddClassified.this)
                    .load(common_variable.main_web_url + "/uploads/classifieds/"+jClassifiedImages.get("classified_img2").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)
                    .intoImageView(ivAd2);
            }
            catch (Exception e1){}

            try{
                /*Glide.with(AddClassified.this)
                .load(common_variable.main_web_url + "/uploads/classifieds/"+jClassifiedImages.get("classified_img3").toString().replaceAll("^\"|\"$", ""))
                .placeholder(R.drawable.upload)
                .into(ivAd3);*/

                Ion.with(AddClassified.this)
                    .load(common_variable.main_web_url + "/uploads/classifieds/"+jClassifiedImages.get("classified_img3").toString().replaceAll("^\"|\"$", ""))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(ivAd3);
            }
            catch (Exception e1){}
        }
    }

    public void Dialog4Images() {
        final String items[] = {"Take Photo", "Choose From Gallery", "Remove Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent;

                if (items[item].contains("Take Photo")) {
                    switch (image_flag) {
                        case "img1":
                            getPath();
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, AD_CAPTURE1);
                            break;

                        case "img2":
                            getPath();
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, AD_CAPTURE2);
                            break;

                        case "img3":
                            getPath();
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, AD_CAPTURE3);
                            break;

                        default:
                            break;
                    }
                } else if (items[item].contains("Choose From Gallery")) {
                    switch (image_flag) {
                        case "img1":
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(intent, AD1);
                            dialog.cancel();
                            break;

                        case "img2":
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(intent, AD2);
                            dialog.cancel();
                            break;

                        case "img3":
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(intent, AD3);
                            dialog.cancel();
                            break;

                        default:
                            break;
                    }
                } else {
                    switch (image_flag) {
                        case "img1":
                            ivAd1.setImageResource(R.drawable.upload);
                            break;

                        case "img2":
                            ivAd2.setImageResource(R.drawable.upload);
                            break;

                        case "img3":
                            ivAd3.setImageResource(R.drawable.upload);
                            break;

                        default:
                            break;
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getPath() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case AD_CAPTURE1:
                    try {
                        bmp1 = getBitmapFromUri4Camera(imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    ad_image1 = BitMapToString(bmp1);
                    ivAd1.setImageBitmap(bmp1);
                    break;

                case AD1:
                    Uri selectedImage1 = data.getData();
                    String[] filePathColumn1 = {MediaStore.Images.Media.DATA};
                    Cursor cursor1 = getContentResolver().query(selectedImage1, filePathColumn1, null, null, null);
                    cursor1.moveToFirst();
                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn1[0]);
                    String filePath1 = cursor1.getString(columnIndex1);
                    cursor1.close();

                    bmp1 = null;
                    try {
                        bmp1 = getBitmapFromUri4Gallary(selectedImage1);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    ivAd1.setImageBitmap(bmp1);
                    ad_image1 = BitMapToString(bmp1);
                    break;

                case AD_CAPTURE2:
                    try {
                        bmp2 = getBitmapFromUri4Camera(imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    ivAd2.setImageBitmap(bmp2);
                    ad_image2 = BitMapToString(bmp2);
                    break;

                case AD2:
                    Uri selectedImage2 = data.getData();
                    String[] filePathColumn2 = {MediaStore.Images.Media.DATA};
                    Cursor cursor2 = getContentResolver().query(selectedImage2, filePathColumn2, null, null, null);
                    cursor2.moveToFirst();
                    int columnIndex2 = cursor2.getColumnIndex(filePathColumn2[0]);
                    String filePath2 = cursor2.getString(columnIndex2);
                    cursor2.close();

                    bmp2 = null;
                    try {
                        bmp2 = getBitmapFromUri4Gallary(selectedImage2);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    ivAd2.setImageBitmap(bmp2);
                    ad_image2 = BitMapToString(bmp2);
                    break;

                case AD_CAPTURE3:
                    try {
                        bmp3 = getBitmapFromUri4Camera(imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    ivAd3.setImageBitmap(bmp3);
                    ad_image3 = BitMapToString(bmp3);
                    break;

                case AD3:
                    Uri selectedImage3 = data.getData();
                    String[] filePathColumn3 = {MediaStore.Images.Media.DATA};
                    Cursor cursor3 = getContentResolver().query(selectedImage3, filePathColumn3, null, null, null);
                    cursor3.moveToFirst();
                    int columnIndex3 = cursor3.getColumnIndex(filePathColumn3[0]);
                    String filePath3 = cursor3.getString(columnIndex3);
                    cursor3.close();

                    bmp3 = null;
                    try {
                        bmp3 = getBitmapFromUri4Gallary(selectedImage3);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    ivAd3.setImageBitmap(bmp3);
                    ad_image3 = BitMapToString(bmp3);
                    break;

                default:
                    break;
            }
        }
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

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        etLat.setText(lat + "");
        etLon.setText(lon + "");
    }
}
