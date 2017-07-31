package com.clickitproduct.User_Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickitproduct.R;

public class Prof_new extends AppCompatActivity{

    Toolbar toolbar;
    ImageView custom_toolbar_icon;
    TextView custom_toolbar_title;
    TextView user_profile_name;
    CollapsingToolbarLayout toolbar_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_prof);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        custom_toolbar_icon = (ImageView)toolbar.findViewById(R.id.custom_toolbar_icon);
        custom_toolbar_title = (TextView)toolbar.findViewById(R.id.custom_toolbar_title);
        user_profile_name = (TextView)findViewById(R.id.user_profile_name);
//        toolbar_layout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);

//        toolbar_layout.setTitle(user_profile_name.getText().toString());

//        custom_toolbar_title.setText(user_profile_name.getText().toString());

    }
}
