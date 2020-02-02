package com.zxwl.szga.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.zxwl.szga.R;
import com.zxwl.szga.view.PopAddMeetingNumber;

import me.jessyan.autosize.internal.CancelAdapt;

public class testpopActivity extends AppCompatActivity implements CancelAdapt {
    private Context context;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testpop);

        this.context = this;

        gson = new Gson();

        Intent intent = getIntent();
        currsmcConfId = intent.getStringExtra("smcConfId");
    }


    private String currsmcConfId = "";//当前的会议ID

    public void pop(View view) {
        PopAddMeetingNumber popAddMeetingNumber = new PopAddMeetingNumber(testpopActivity.this, currsmcConfId);
        popAddMeetingNumber.show();
//        PopMeetingControl popMeetingControl = new PopMeetingControl(testpopActivity.this, currsmcConfId);
//        popMeetingControl.show();
    }
}
