package com.clickitproduct.LoginRegistration;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.clickitproduct.R;
import com.clickitproduct.SQLite.DataBaseConnection;
import com.clickitproduct.activities.common_variable;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class UserRegister extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    DataBaseConnection objDb;
    private EditText etFN, etLN;
    private EditText etMobile;
    private EditText etEmail;
    public static String firstname, lastname, mobile, email;
    private TextView btnRegister, login;
    ImageView profile_img, imgpicker;
    private static final int PROFILE = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static final int RESULT_CROP = 3;
    String picUserPathUrl = "";
    String image="";
    Uri imageUri;
    String nm;
    private String[] img_shop1;
    private SignInButton signInButton;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 100;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    String profilePicFormat = "jpg";
    public  Bitmap bmp1;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
            nextActivity(profile);
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_user_register);
        objDb = new DataBaseConnection(UserRegister.this);

        objDb.open();
        common_variable.FCM_Token = objDb.getFCM_ID();
        objDb.close();

        loginButton = (LoginButton) findViewById(R.id.login_button);

        etFN = (EditText) findViewById(R.id.etFN);
        etLN = (EditText) findViewById(R.id.etLN);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etEmail = (EditText) findViewById(R.id.etEmail);

        btnRegister = (TextView) findViewById(R.id.btnRegister);
        login = (TextView) findViewById(R.id.login);


        profile_img = (ImageView) findViewById(R.id.profile_img);
        imgpicker = (ImageView) findViewById(R.id.profile_img);

        imgpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mypermission();
                cameraGalleryDialog();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserRegister.this, UserLogin.class);
                startActivity(i);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ProgressDialog pro;

                boolean cancel = false;
                View focusView = null;

                firstname = etFN.getText().toString();
                lastname = etLN.getText().toString();
                mobile = etMobile.getText().toString();
                email = etEmail.getText().toString();

                if (TextUtils.isEmpty(firstname))
                {
                    etFN.setError("Enter First Name");
                    etFN.requestFocus();
                    cancel = true;
                }
                else if (TextUtils.isEmpty(lastname))
                {
                    etLN.setError("Enter Last Name");
                    etLN.requestFocus();
                    cancel = true;
                }
                else if(TextUtils.isEmpty(mobile) || mobile.length() < 10)
                {
                    etMobile.setError("Enter 10 digit mobile number");
                    etMobile.requestFocus();
                    cancel = true;
                }
                else if(TextUtils.isEmpty(email) || !email.contains("@"))
                {
                    etEmail.setError("Enter valid Email");
                    etEmail.requestFocus();
                    cancel = true;
                }

                if(cancel)
                { }
                else {

                    final ProgressDialog dialog = new ProgressDialog(UserRegister.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading...");
                    dialog.show();

                    //validation();
                    JsonObject jsonParam = new JsonObject();
                    jsonParam.addProperty("firstname", firstname.trim());
                    jsonParam.addProperty("lastname", lastname.trim());
                    jsonParam.addProperty("email", email.trim());
                    jsonParam.addProperty("mobile", mobile.trim());
                    jsonParam.addProperty("fcm_id", common_variable.FCM_Token);
                    jsonParam.addProperty("platform", "" + 1);

                    if (!image.equals("")) {
                        jsonParam.addProperty("profilepic", image);
                        jsonParam.addProperty("profilepicname", nm);
                        jsonParam.addProperty("profilepicformat", profilePicFormat);
                    }

                    Ion.with(getApplicationContext())
                    .load(common_variable.main_web_url + "/user/signup")
                    .progressDialog(dialog)
                    .setJsonObjectBody(jsonParam)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            dialog.dismiss();
                            String success = result.get("success").toString();

                            if (success.equals("true"))
                            {
                                if (result.toString().contains("success")) {
                                    dialog.dismiss();
                                    Toast.makeText(UserRegister.this, "One Time Password(OTP) has been sent on this mobile number.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), UserLogin.class));
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "something is wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mypermission();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInButton = (SignInButton) findViewById(R.id.MyGoogle_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            { //Calling signin
                    signIn();
                Log.e("Amol3", "Amol3");
            }
        });

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        callback = new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
                Log.e("Click","Clicked");
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel()
            {
                Log.e("Click","Cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("Click","Error "+e);
            }

        };
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);

        loginButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(loginButton.getText().toString().equals("Log Out"))
                {

                }
            }
        });
    }


    public void cameraGalleryDialog() {
        LayoutInflater li = LayoutInflater.from(UserRegister.this);
        final View prompt = li.inflate(R.layout.choose_photo, null);


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(UserRegister.this, R.style.MyAlertTheme));
        alertDialogBuilder.setView(prompt);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.show();

        FloatingActionButton fab_Camera1, fab_Gallary1, fab_Remove1;

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
                startActivityForResult(i, PROFILE);
                dialog.cancel();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case IMAGE_CAPTURE:
                    try {
                       // bmp1 = getBitmapFromUri4Camera(imageUri);
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picUserPathUrl = cursor.getString(columnIndex);
                        performCrop(picUserPathUrl);
                        nm = "Profile.jpg";
                        profilePicFormat = "jpg";
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            bmp1 = getBitmapFromUri4Camera(imageUri);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }

                    break;

                case PROFILE:
                    Uri selectedImage1 = data.getData();
                    String[] filePathColumn1 = {MediaStore.Images.Media.DATA};
                    Cursor cursor1 = getContentResolver().query(selectedImage1, filePathColumn1, null, null, null);
                    cursor1.moveToFirst();
                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn1[0]);
                    String filePath1 = cursor1.getString(columnIndex1);
                    cursor1.close();

                    String[] img1 = filePath1.split("/");
                    img_shop1 = img1[img1.length - 1].split("\\.");
                    profilePicFormat = img_shop1[img_shop1.length - 1];

                    bmp1 = null;
                    try {
                        performCrop(filePath1);
//                        bmp1 = getBitmapFromUri4Gallary(selectedImage1);
                        nm = "Profile." + img_shop1[img_shop1.length - 1];
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        try {
                            bmp1 = getBitmapFromUri4Gallary(selectedImage1);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } catch (OutOfMemoryError exp) {
                        exp.printStackTrace();
                    }
                    break;
            }

            if (requestCode == RC_SIGN_IN)
            {
                Log.e("Amol", "Amol");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.e("123",""+result.getSignInAccount().getEmail().toString());
                handleSignInResult(result);
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
                            Bitmap bi = getLargeBitmapFromUri(selectedImage);
                            profile_img.setImageBitmap(bi);
                            image = BitMapToString(bi);
                        }
                        catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), "Sorry don't crop in Google Images...please try again", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void getPath() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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

    public void mypermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        else
        {
            return;
        }
    }

    private void signIn() {
        Log.e("Amol2", "Amol2");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.e("Amol1", "Amol1");
        if (result.isSuccess())
        {
            GoogleSignInAccount acct = result.getSignInAccount();
            String ln = "";
            //Displaying name and email
            String name = acct.getDisplayName();
            String[] n = name.split(" ");
            String fn = n[0];
            if (ln != "")
            {
                ln = n[1];
            }
            etFN.setText(fn);
            etLN.setText(ln);
            etEmail.setText(acct.getEmail());

            try
            {
                //Glide.with(getApplicationContext()).load(acct.getPhotoUrl().toString()).into(profile_img);
                Ion.with(UserRegister.this)
                        .load(acct.getPhotoUrl().toString())
                        .withBitmap()
                        .placeholder(R.raw.loading)
                        .error(R.drawable.error)
                        .animateLoad(R.anim.zoom_in)
                        .animateIn(R.anim.fade_in)
                        .intoImageView(profile_img);

                image = acct.getPhotoUrl().toString();
            }
            catch (Exception e)
            {
                profile_img.setImageResource(R.mipmap.ic_launcher);
            }

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void nextActivity(Profile profile){
        if(profile != null){

            etFN.setText(profile.getFirstName());
            etLN.setText(profile.getLastName());

           /* Glide.with(getApplicationContext()).load(profile.getProfilePictureUri(200, 200))
                      .thumbnail(0.5f)
                      .diskCacheStrategy(DiskCacheStrategy.ALL)
                      .into(profile_img);*/

            Ion.with(UserRegister.this)
                    .load(String.valueOf(profile.getProfilePictureUri(200, 200)))
                    .withBitmap()
                    .placeholder(R.raw.loading)
                    .error(R.drawable.error)

                    .intoImageView(profile_img);

            Toast.makeText(getApplicationContext(),profile.getFirstName()+" "+profile.getLastName(),Toast.LENGTH_SHORT).show();
            image = profile.getProfilePictureUri(200, 200).toString();
        }
    }

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

    public class setImage extends AsyncTask<Bitmap, Void, Void> {
        private ProgressDialog Dialog = new ProgressDialog(UserRegister.this);
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

        protected void onPostExecute(Void unused)
        {
            profile_img.setImageBitmap(null);
          //  objBitGlobleProfile = getCircleBitmap(bit);
            image = BitMapToString(bit);
            profile_img.setImageBitmap(bit);
           // bit.recycle();
                Dialog.dismiss();
        }
    }
}
