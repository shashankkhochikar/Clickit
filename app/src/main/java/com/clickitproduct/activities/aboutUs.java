package com.clickitproduct.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clickitproduct.R;

public class aboutUs extends AppCompatActivity
{
    TextView tv_video;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        tv_video = (TextView)findViewById(R.id.tv_video);

        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.youtube.com/embed/hswpPYS7uiA/")));
                }
                catch (Exception e)
                {   Toast.makeText(getApplicationContext(),"No Video Available",Toast.LENGTH_SHORT).show();   }
            }
        });

       /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(aboutUs.this);
        alertDialog.setTitle("IP Address");
        alertDialog.setMessage("Enter Address");

        final EditText input = new EditText(aboutUs.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setIcon(R.drawable.app_icon);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        common_variable.main_web_url="http://"+input.getText().toString()+":3000";
                        Toast.makeText(aboutUs.this,"Ewwww\nNew Ip Has Been Set ",Toast.LENGTH_SHORT).show();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {dialog.cancel();}
                });
        alertDialog.show();*/
    }
}