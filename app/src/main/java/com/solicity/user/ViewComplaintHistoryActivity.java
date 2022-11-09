package com.solicity.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adapter.files.ComplaintHistoryAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewComplaintHistoryActivity extends AppCompatActivity {

    ImageView backImgView;
    GeneralFunctions generalFunc;

    String userProfileJson = "";
    MTextView titleTxt, noDataTxt;
    RecyclerView complaintRecyclerView;
    ArrayList<HashMap<String, String>> historyData = new ArrayList<>();
    ComplaintHistoryAdapter complaintHistoryAdapter;
    String LBL_CREATED, LBL_RESOLVED, LBL_ADMIN_COMMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaint_history);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        noDataTxt = (MTextView) findViewById(R.id.noDataTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        complaintRecyclerView = findViewById(R.id.complaintRecyclerView);
        backImgView.setOnClickListener(new setOnClickList());
        complaintHistoryAdapter = new ComplaintHistoryAdapter(generalFunc, historyData, getActContext());
        complaintRecyclerView.setAdapter(complaintHistoryAdapter);
        noDataTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO_DATA_AVAIL"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_COMPLAINT"));
        if (historyData.size() == 0) {
            noDataTxt.setVisibility(View.VISIBLE);
        }
        LBL_CREATED = generalFunc.retrieveLangLBl("Created", "LBL_CREATED");
        LBL_RESOLVED = generalFunc.retrieveLangLBl("Resolved", "LBL_RESOLVED");
        LBL_ADMIN_COMMENT = generalFunc.retrieveLangLBl("View Feedback", "LBL_ADMIN_COMMENT");
        getIssueHistory();
    }


    public void getIssueHistory() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ListReportIssue");
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    JSONArray all_issueArray = generalFunc.getJsonArray(Utils.message_str, responseString);
                    if (all_issueArray != null && all_issueArray.length() > 0) {
                        noDataTxt.setVisibility(View.GONE);
                        for (int i = 0; i < all_issueArray.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            JSONObject obj_temp = generalFunc.getJsonObject(all_issueArray, i);
                            String date = generalFunc.getJsonValueStr("tAddedDate", obj_temp);
                            String formattedDate = generalFunc.getDateFormatedType(date, Utils.OriginalDateFormate, CommonUtilities.OriginalDateFormate) + ", " +
                                    generalFunc.getDateFormatedType(date, Utils.OriginalDateFormate, CommonUtilities.OriginalTimeFormate);


                            map.put("tAddedDate", formattedDate);
                            map.put("isSolvedDataURL", generalFunc.getJsonValueStr("isSolvedDataURL", obj_temp));
                            map.put("tDescription", generalFunc.getJsonValueStr("tDescription", obj_temp));
                            map.put("eMarkedAs", generalFunc.getJsonValueStr("eMarkedAs", obj_temp));
                            map.put("ColorCode", generalFunc.getJsonValueStr("ColorCode", obj_temp));
                            map.put("vCategory", generalFunc.getJsonValueStr("vCategory", obj_temp));
                            map.put("vCity", generalFunc.getJsonValueStr("vCity", obj_temp));
                            map.put("isImageUpload", generalFunc.getJsonValueStr("isImageUpload", obj_temp));
                            map.put("isAudioUpload", generalFunc.getJsonValueStr("isAudioUpload", obj_temp));
                            map.put("isVideoUpload", generalFunc.getJsonValueStr("isVideoUpload", obj_temp));
                            map.put("isDataURL", generalFunc.getJsonValueStr("isDataURL", obj_temp));
                            map.put("LBL_CREATED", LBL_CREATED);
                            map.put("LBL_RESOLVED", LBL_RESOLVED);
                            map.put("LBL_ADMIN_COMMENT", LBL_ADMIN_COMMENT);


                            historyData.add(map);
                        }
                        complaintHistoryAdapter.notifyDataSetChanged();
                    }

                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return ViewComplaintHistoryActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            if (i == R.id.backImgView) {
                onBackPressed();
            }
        }
    }

}