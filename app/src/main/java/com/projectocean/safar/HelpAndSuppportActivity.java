package com.projectocean.safar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HelpAndSuppportActivity extends AppCompatActivity {
    int f1 = 1, f2 = 1, f3 = 1, f4 = 1, f5 = 1;
    TextView mtv1, mtv2, mtv3, mtv4, mtv5, mtv8, ma1, ma2, ma3, ma4, ma5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_suppport);
        Intent i = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mtv1 = (TextView) findViewById(R.id.tv1);
        mtv2 = (TextView) findViewById(R.id.tv2);
        mtv3 = (TextView) findViewById(R.id.tv3);
        mtv4 = (TextView) findViewById(R.id.tv4);
        mtv5 = (TextView) findViewById(R.id.tv5);
        ma1 = (TextView) findViewById(R.id.a1);
        ma2 = (TextView) findViewById(R.id.a2);
        ma3 = (TextView) findViewById(R.id.a3);
        ma4 = (TextView) findViewById(R.id.a4);
        ma5 = (TextView) findViewById(R.id.a5);
        mtv8 = (TextView) findViewById(R.id.tv8);

        mtv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "adminmail@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enter Complaint Here:");
                startActivity(Intent.createChooser(emailIntent, null));
            }
        });
        mtv1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (f1 == 1) {

                    mtv1.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    ma1.setVisibility(View.VISIBLE);
                    f1 = 0;
                } else if (f1 == 0) {
                    mtv1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                    mtv1.setTextColor(getResources().getColor(R.color.black));
                    mtv1.setBackgroundColor(getResources().getColor(R.color.white));
                    ma1.setVisibility(View.GONE);
                    f1 = 1;
                }
            }
        });

        mtv2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (f2 == 1) {
                    mtv2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                    mtv2.setTextColor(getResources().getColor(R.color.white));
                    mtv2.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    ma2.setVisibility(View.VISIBLE);
                    f2 = 0;
                } else if (f2 == 0) {
                    mtv2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                    mtv2.setTextColor(getResources().getColor(R.color.black));
                    mtv2.setBackgroundColor(getResources().getColor(R.color.white));
                    ma2.setVisibility(View.GONE);
                    f2 = 1;
                }
            }
        });

        mtv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f3 == 1) {
                    mtv3.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                    mtv3.setTextColor(getResources().getColor(R.color.white));
                    mtv3.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    ma3.setVisibility(View.VISIBLE);
                    f3 = 0;
                } else if (f3 == 0) {
                    mtv3.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                    mtv3.setTextColor(getResources().getColor(R.color.black));
                    mtv3.setBackgroundColor(getResources().getColor(R.color.white));
                    ma3.setVisibility(View.GONE);
                    f3 = 1;
                }
            }
        });

        mtv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f4 == 1) {
                    mtv4.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                    mtv4.setTextColor(getResources().getColor(R.color.white));
                    mtv4.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    ma4.setVisibility(View.VISIBLE);
                    f4 = 0;
                } else if (f4 == 0) {
                    mtv4.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                    mtv4.setTextColor(getResources().getColor(R.color.black));
                    mtv4.setBackgroundColor(getResources().getColor(R.color.white));
                    ma4.setVisibility(View.GONE);
                    f4 = 1;
                }
            }
        });

        mtv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f5 == 1) {
                    mtv5.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0);
                    mtv5.setTextColor(getResources().getColor(R.color.white));
                    mtv5.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                    ma5.setVisibility(View.VISIBLE);
                    f5 = 0;
                } else if (f5 == 0) {
                    mtv5.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
                    mtv5.setTextColor(getResources().getColor(R.color.black));
                    mtv5.setBackgroundColor(getResources().getColor(R.color.white));
                    ma5.setVisibility(View.GONE);
                    f5 = 1;
                }
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
