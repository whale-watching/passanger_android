package com.solicity.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MTextView;

public class IssueReoprtActivity extends AppCompatActivity {


    ImageView backImgView;
    GeneralFunctions generalFunc;

    String userProfileJson = "";
    MTextView titleTxt;
    View viewIssueArea, addIssueArea;
    MTextView addComHtxt, viewComHtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_reoprt);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        addComHtxt = (MTextView) findViewById(R.id.addComHtxt);
        viewComHtxt = (MTextView) findViewById(R.id.viewComHtxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        viewIssueArea = findViewById(R.id.viewIssueArea);
        addIssueArea = findViewById(R.id.addIssueArea);
        addIssueArea.setOnClickListener(new setOnClickList());
        viewIssueArea.setOnClickListener(new setOnClickList());
        titleTxt.setText(generalFunc.retrieveLangLBl("DASHBOARD", "LBL_DASHBOARD"));
        viewComHtxt.setText(generalFunc.retrieveLangLBl("View Response", "LBL_VIEW_COMPLAINT"));
        addComHtxt.setText(generalFunc.retrieveLangLBl("ADD COMPLAINT", "LBL_ADD_COMPLAINT"));
    }

    public Context getActContext() {
        return IssueReoprtActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == R.id.addIssueArea) {
                new StartActProcess(getActContext()).startAct(AddComplaintActivity.class);

            } else if (i == R.id.viewIssueArea) {
                new StartActProcess(getActContext()).startAct(ViewComplaintHistoryActivity.class);

            }
        }
    }
}