package com.solicity.user;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidaudiorecorder.AndroidAudioRecorder;
import com.androidaudiorecorder.AudioChannel;
import com.androidaudiorecorder.AudioSampleRate;
import com.androidaudiorecorder.AudioSource;
import com.androidaudiorecorder.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.countryview.presenter.CountryPickerContractor;
import com.cropper.CropImage;
import com.cropper.CropImageView;

import com.deep.videotrimmer.utils.FileUtils;
import com.dialogs.OpenListView;
import com.fragments.EditProfileFragment;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.ImageFilePath;
import com.general.files.MyApp;
import com.general.files.OpenMainProfile;
import com.general.files.OpenNoLocationView;
import com.general.files.OpenNoMayorView;
import com.general.files.SetUserData;
import com.general.files.StartActProcess;
import com.general.files.UploadProfileImage;
import com.realmModel.Cart;
import com.solicity.user.deliverAll.CheckOutActivity;
import com.utils.Logger;
import com.utils.Utils;

import com.videorecording.VideoPicker;
import com.videorecording.VideoTrimmerActivity;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import static com.videorecording.Constants.EXTRA_VIDEO_PATH;


public class AddComplaintActivity extends AppCompatActivity {

    ImageView backImgView;
    GeneralFunctions generalFunc;

    String userProfileJson = "";
    MTextView titleTxt;
    FrameLayout citySelectArea, categorySelectArea, stateSelectArea, subcategorySelectArea;
    MaterialEditText cityBox;
    MaterialEditText categoryBox, subcategoryBox;
    MaterialEditText InstructionEditBox;
    MaterialEditText stateBox;
    LinearLayout cameraArea, cameraContainer, audioArea, videoArea;
    MButton btn_type2;
    int submitBtnId;

    int selCityPosition = -1;
    int selStatePosition = -1;
    int selCategoryPosition = -1;
    int selsubCategoryPosition = -1;
    String selected_state = "";
    String selected_city = "";
    String selected_category = "";
    String selected_subcategory = "";
    ArrayList<HashMap<String, String>> stateDataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> cityDataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> categoryDataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> subcategoryDataList = new ArrayList<>();
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";
    private static final int SELECT_PICTURE = 2;
    private static final int CROP_IMAGE = 3;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    ArrayList<String> imageListPath = new ArrayList<>();
    LinearLayout mapLocArea;
    MTextView mapLocTxt;
    String required_str = "";
    String Longitude = "";
    String Latitude = "";
    ArrayList<String> imageIdList = new ArrayList<>();
    private static final int REQUEST_RECORD_AUDIO = 0;
    //    private static final String AUDIO_FILE_PATH =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/audio.wav";
    private String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.wav";
    View playArea;
    ImageView playBtn, dltBtn;
    SeekBar seekbar;
    MTextView timeTxt;
    boolean isAudioAdded = false;
    boolean isVideoAdded = false;
    String audiofilePath = "";
    String audioId = "";
    String videoId = "";
    String audioDuration = "";
    private final int REQUEST_VIDEO_TRIMMER = 0x12;
    private final int REQUEST_VIDEO_TRIMMER_RESULT = 342;
    SelectableRoundedImageView videothumbimg;
    View videoThumbArea;
    MTextView videoRecordtxt;
    private String selectedVideoName = null, selectedVideoFile = null;
    private File thumbFile;

    MTextView audioRecordtxt;
    boolean isAddressAdded = false;
    boolean isFirstTimeCalled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        seekbar = findViewById(R.id.seekbar);
        seekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        videothumbimg = findViewById(R.id.videothumbimg);
        videoThumbArea = findViewById(R.id.videoThumbArea);
        audioRecordtxt = findViewById(R.id.audioRecordtxt);
        videoRecordtxt = findViewById(R.id.videoRecordtxt);
        playArea = findViewById(R.id.playArea);
        playBtn = findViewById(R.id.playBtn);
        dltBtn = findViewById(R.id.dltBtn);
        seekbar = findViewById(R.id.seekbar);
        timeTxt = findViewById(R.id.timeTxt);
        playBtn.setOnClickListener(new setOnClickList());
        dltBtn.setOnClickListener(new setOnClickList());
        mapLocArea = (LinearLayout) findViewById(R.id.mapLocArea);
        mapLocTxt = (MTextView) findViewById(R.id.mapLocTxt);
        mapLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_COMPLAIN_LOCATION"));
        mapLocArea.setOnClickListener(new setOnClickList());
        titleTxt = (MTextView) findViewById(R.id.titleTxt);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        citySelectArea = (FrameLayout) findViewById(R.id.citySelectArea);
        categorySelectArea = (FrameLayout) findViewById(R.id.categorySelectArea);
        subcategorySelectArea = (FrameLayout) findViewById(R.id.subcategorySelectArea);
        stateSelectArea = (FrameLayout) findViewById(R.id.stateSelectArea);
        cityBox = (MaterialEditText) findViewById(R.id.cityBox);
        stateBox = (MaterialEditText) findViewById(R.id.stateBox);
        categoryBox = (MaterialEditText) findViewById(R.id.categoryBox);
        subcategoryBox = (MaterialEditText) findViewById(R.id.subcategoryBox);
        cameraContainer = (LinearLayout) findViewById(R.id.cameraContainer);
        cameraArea = (LinearLayout) findViewById(R.id.cameraArea);
        audioArea = (LinearLayout) findViewById(R.id.audioArea);
        videoArea = (LinearLayout) findViewById(R.id.videoArea);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());
        audioArea.setOnClickListener(new setOnClickList());
        videoArea.setOnClickListener(new setOnClickList());
        cameraArea.setOnClickListener(new setOnClickList());
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_SUBMIT_TXT"));
        InstructionEditBox = (MaterialEditText) findViewById(R.id.InstructionEditBox);
        InstructionEditBox.setHideUnderline(true);
        if (generalFunc.isRTLmode()) {
            InstructionEditBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._10sdp), 0);
        } else {
            InstructionEditBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, 0, 0);
        }
        InstructionEditBox.setSingleLine(false);
        InstructionEditBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        InstructionEditBox.setGravity(Gravity.TOP);
        Utils.removeInput(cityBox);
        Utils.removeInput(categoryBox);
        Utils.removeInput(stateBox);
        Utils.removeInput(subcategoryBox);
        stateBox.setOnClickListener(new setOnClickList());
        cityBox.setOnClickListener(new setOnClickList());
        cityBox.setOnTouchListener(new setOnTouchList());
        stateBox.setOnTouchListener(new setOnTouchList());
        categoryBox.setOnTouchListener(new setOnTouchList());
        categoryBox.setOnClickListener(new setOnClickList());
        subcategoryBox.setOnTouchListener(new setOnTouchList());
        subcategoryBox.setOnClickListener(new setOnClickList());
        InstructionEditBox.setHint(generalFunc.retrieveLangLBl("Write Your complaint here...", "LBL_COMPLAINT_WRITE"));

        cityBox.setBothText(generalFunc.retrieveLangLBl("Select City", "LBL_SELECT_CITY"));
        stateBox.setBothText(generalFunc.retrieveLangLBl("Select State", "LBL_SELECT_STATE"));
        categoryBox.setBothText(generalFunc.retrieveLangLBl("Select category", "LBL_SELECT_CATEGORY"));
        subcategoryBox.setBothText(generalFunc.retrieveLangLBl("Select Sub category", "LBL_SELECT_SUBCATEGORY"));


        titleTxt.setText(generalFunc.retrieveLangLBl("ADD COMPLAINT", "LBL_ADD_COMPLAINT"));
        audioRecordtxt.setText(generalFunc.retrieveLangLBl("", "LBL_RECORDING"));
        videoRecordtxt.setText(generalFunc.retrieveLangLBl("", "LBL_RECORDING"));
        //buildCategoryList();

        buildstateList();


    }

    public void managePlayArea() {
        if (isAudioAdded) {
            playArea.setVisibility(View.VISIBLE);

            timeTxt.setText(audioDuration);
        } else {
            playArea.setVisibility(View.GONE);
        }
    }


    public class setOnTouchList implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                view.performClick();
            }
            return true;
        }
    }

    public void buildstateList() {
        JSONArray statusList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue("STATELIST"));
        stateDataList.clear();

        for (int i = 0; i < statusList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(statusList_arr, i);

            HashMap<String, String> mapData = new HashMap<>();
            mapData.put("iStateId", generalFunc.getJsonValueStr("iStateId", obj_temp));
            mapData.put("vState", generalFunc.getJsonValueStr("vState", obj_temp));


            if (Utils.getText(stateBox).equalsIgnoreCase(generalFunc.getJsonValueStr("vState", obj_temp))) {
                selStatePosition = i;
            }
            if (generalFunc.getJsonValue("vStateReport", userProfileJson) != null && generalFunc.getJsonValue("vStateReport", userProfileJson).equalsIgnoreCase(generalFunc.getJsonValueStr("iStateId", obj_temp))) {
                selStatePosition = i;
                selected_state = mapData.get("vState");
                stateBox.setText(mapData.get("vState"));
            }

            stateDataList.add(mapData);


        }
        if (selStatePosition != -1) {
            getCityList();
        }
    }


    public void buildCategoryList() {
        JSONArray categoryList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue("CATEGORYLIST"));
        categoryDataList.clear();

        for (int i = 0; i < categoryList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(categoryList_arr, i);

            HashMap<String, String> mapData = new HashMap<>();
            mapData.put("vVehicleType", generalFunc.getJsonValueStr("vVehicleType", obj_temp));
            mapData.put("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp));


            if (Utils.getText(categoryBox).equalsIgnoreCase(generalFunc.getJsonValueStr("vVehicleType", obj_temp))) {
                selCategoryPosition = i;
            }

            categoryDataList.add(mapData);


        }
    }

    public void ManageCameraView() {
        if (cameraContainer.getChildCount() > 0) {
            cameraContainer.removeAllViewsInLayout();
        }
        if (imageListPath.size() > 0) {
            for (int i = 0; i < imageListPath.size(); i++) {
                LayoutInflater optioninflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View cameraview = optioninflater.inflate(R.layout.camera_list_view, null);
                SelectableRoundedImageView uploadedImg = cameraview.findViewById(R.id.uploadedImg);
                View dltImg = cameraview.findViewById(R.id.dltImg);
                dltImg.setTag(i);
                dltImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletImg((Integer) dltImg.getTag(), false);

                    }
                });
                File imgFile = new File(imageListPath.get(i));

                if (imgFile.exists()) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    uploadedImg.setImageBitmap(myBitmap);


                }


                cameraContainer.addView(cameraview);
            }
        }
    }


    public void refreshcategory() {

        selCategoryPosition = -1;
        selected_category = "";
        categoryBox.setText("");
    }

    public void refreshcity() {
        cityBox.setText("");
        selCityPosition = -1;
        selected_city = "";

        selsubCategoryPosition = -1;

        selected_subcategory = "";
        subcategoryBox.setText("");
        selCategoryPosition = -1;
        selected_category = "";
        categoryBox.setText("");
    }

    public Context getActContext() {
        return AddComplaintActivity.this;
    }

    public void showstateList() {
        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_STATE"), stateDataList, OpenListView.OpenDirection.CENTER, true, position -> {


            selStatePosition = position;
            HashMap<String, String> mapData = stateDataList.get(position);
            selected_state = mapData.get("vState");
            stateBox.setText(mapData.get("vState"));

            refreshcity();
            getCityList();
        }, false, generalFunc.retrieveLangLBl("", "LBL_SELECT_STATE"), false).show(selStatePosition, "vState");

    }


    public void getsubCategoryList() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ReportissueSubCategory");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iReportissueParentId", categoryDataList.get(selCategoryPosition).get("iVehicleCategoryId"));


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    JSONArray subCategoryList_arr = generalFunc.getJsonArray(generalFunc.getJsonValue(Utils.message_str, responseString));
                    subcategoryDataList.clear();

                    for (int i = 0; i < subCategoryList_arr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(subCategoryList_arr, i);

                        HashMap<String, String> mapData = new HashMap<>();
                        mapData.put("iVehicleTypeId", generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp));
                        mapData.put("vVehicleType", generalFunc.getJsonValueStr("vVehicleType", obj_temp));

                        subcategoryDataList.add(mapData);
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

    public void getCityList() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CityList");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iStateId", stateDataList.get(selStatePosition).get("iStateId"));


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    JSONArray cityList_arr = generalFunc.getJsonArray(generalFunc.getJsonValue(Utils.message_str, responseString));
                    cityDataList.clear();

                    for (int i = 0; i < cityList_arr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(cityList_arr, i);

                        HashMap<String, String> mapData = new HashMap<>();
                        mapData.put("iCityId", generalFunc.getJsonValueStr("iCityId", obj_temp));
                        mapData.put("vCity", generalFunc.getJsonValueStr("vCity", obj_temp));
                        mapData.put("iCountryId", generalFunc.getJsonValueStr("iCountryId", obj_temp));
                        mapData.put("iStateId", generalFunc.getJsonValueStr("iStateId", obj_temp));
                        mapData.put("isMayerAvailable", generalFunc.getJsonValueStr("isMayerAvailable", obj_temp));


                        if (generalFunc.getJsonValue("vCityReport", userProfileJson) != null && generalFunc.getJsonValue("vCityReport", userProfileJson).equalsIgnoreCase(generalFunc.getJsonValueStr("iCityId", obj_temp))
                                && isFirstTimeCalled) {
                            isFirstTimeCalled = false;

                            if (generalFunc.getJsonValueStr("isMayerAvailable", obj_temp).equalsIgnoreCase("NO")) {
                                OpenNoMayorView.getInstance(this).configView(true);

                            }
                            selCityPosition = i;
                            selected_city = mapData.get("vCity");
                            cityBox.setText(mapData.get("vCity"));
                            getcategoryList(mapData.get("iCityId"));


                        }

                        if (Utils.getText(cityBox).equalsIgnoreCase(generalFunc.getJsonValueStr("vCity", obj_temp))) {
                            selCityPosition = i;
                        }

                        cityDataList.add(mapData);
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

    public void getcategoryList(String icityid) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ReportCategoryList");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iCityId", icityid);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    JSONArray cityList_arr = generalFunc.getJsonArray(generalFunc.getJsonValue(Utils.message_str, responseString));
                    categoryDataList.clear();

                    for (int i = 0; i < cityList_arr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(cityList_arr, i);

                        HashMap<String, String> mapData = new HashMap<>();
                        mapData.put("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp));
                        mapData.put("vVehicleType", generalFunc.getJsonValueStr("vVehicleType", obj_temp));
                        mapData.put("isMayerAvailable", generalFunc.getJsonValueStr("isMayerAvailable", obj_temp));


                        categoryDataList.add(mapData);
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

    public void showCityList() {
        if (selStatePosition == -1) {
            generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("Please Select State First.", "LBL_SELECT_STATE_NOTIFY"));
            return;
        }

        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_CITY"), cityDataList, OpenListView.OpenDirection.CENTER, true, position -> {


            if (cityDataList.get(position).get("isMayerAvailable").equalsIgnoreCase("NO")) {


                OpenNoMayorView.getInstance(this).configView(true);
                return;

            }


            selCityPosition = position;
            HashMap<String, String> mapData = cityDataList.get(position);
            selected_city = mapData.get("vCity");
            cityBox.setText(mapData.get("vCity"));

            getcategoryList(cityDataList.get(selCityPosition).get("iCityId"));
        }, false, generalFunc.retrieveLangLBl("", "LBL_SELECT_CITY"), false).show(selCityPosition, "vCity");

    }

    public void showCategoryList() {

        if (selCityPosition == -1) {
            generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("Please Select City First.", "LBL_SELECT_CITY_NOTIFY"));
            return;

        }


        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_CATEGORY"), categoryDataList, OpenListView.OpenDirection.CENTER, true, position -> {


            if (categoryDataList.get(position).get("isMayerAvailable").equalsIgnoreCase("NO")) {
                OpenNoMayorView.getInstance(this).configView(false);
                return;

            }
            selCategoryPosition = position;
            HashMap<String, String> mapData = categoryDataList.get(position);
            selected_category = mapData.get("vVehicleType");
            categoryBox.setText(mapData.get("vVehicleType"));

            selsubCategoryPosition = -1;

            selected_subcategory = "";
            subcategoryBox.setText("");

            getsubCategoryList();
        }, false, generalFunc.retrieveLangLBl("", "LBL_SELECT_CATEGORY"), false).show(selCategoryPosition, "vVehicleType");


    }

    public void showsubCategoryList() {
        if (selCategoryPosition == -1) {
            generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("Please Select Category First.", "LBL_SELECT_CATEGORY_NOTIFY"));
            return;
        }
        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_SUBCATEGORY"), subcategoryDataList, OpenListView.OpenDirection.CENTER, true, position -> {


            selsubCategoryPosition = position;
            HashMap<String, String> mapData = subcategoryDataList.get(position);
            selected_subcategory = mapData.get("vVehicleType");
            subcategoryBox.setText(mapData.get("vVehicleType"));
            getsubCategoryList();
        }, false, generalFunc.retrieveLangLBl("", "LBL_SELECT_SUBCATEGORY"), false).show(selsubCategoryPosition, "vVehicleType");


    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Bundle bn = new Bundle();

            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == R.id.stateBox) {
                showstateList();
            } else if (i == R.id.cityBox) {
                showCityList();
            } else if (i == R.id.categoryBox) {
                showCategoryList();
            } else if (i == R.id.subcategoryBox) {
                showsubCategoryList();
            } else if (i == R.id.cameraArea) {
                if (generalFunc.isCameraStoragePermissionGranted()) {
                    if (imageIdList.size() >= 3) {
                        generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_REPORTISSUE_MAX_IMAGE_LIMIT"));
                    } else {
                        new ImageSourceDialog().run();
                    }
                } else {
                    generalFunc.showMessage(getCurrView(), "Allow this app to use camera.");
                }

            } else if (i == R.id.audioArea) {

                openAudio();

            } else if (i == R.id.videoArea) {
                openVideo();

            } else if (i == submitBtnId) {
                addComplaint();
            } else if (i == R.id.mapLocArea) {
//
//                new StartActProcess(getActContext()).startActForResult(SearchPickupLocationActivity.class,
//                        bn, Utils.ADD_MAP_LOC_REQ_CODE);

                bn.putString("locationArea", "source");


                new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class,
                        bn, Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE);
            } else if (i == R.id.dltBtn) {
                deletImg(0, true);
            }
        }
    }

    boolean isVideoClick = false;

    public void openVideo() {
        if (generalFunc.isCameraStoragePermissionGranted()) {
            isVideoClick = true;
            new ImageSourceDialog().run();

        } else {
            generalFunc.showMessage(getCurrView(), "Allow this app to use camera.");
        }
//        new VideoPicker(this) {
//            @Override
//            protected void onCameraClicked() {
//                openVideoCapture();
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            protected void onGalleryClicked() {
//                Intent intent = new Intent();
//                intent.setTypeAndNormalize("video/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_TRIMMER);
//            }
//        }.show();


    }

    private void openVideoCapture() {
        Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoCapture.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
    }

    public void openAudio() {
        final ArrayList<String> requestPermissions = new ArrayList<>();
        requestPermissions.add(Manifest.permission.RECORD_AUDIO);
        requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (!generalFunc.isAllPermissionGranted(true, requestPermissions)) {

            return;
        }

        //  AUDIO_FILE_PATH = getCacheDir().getPath()+"/recorded_audio.wav";
        AndroidAudioRecorder.with(this)
                // Required


                .setFilePath(AUDIO_FILE_PATH)
                .setColor(ContextCompat.getColor(getActContext(), R.color.recorder_bg))
                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(false)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }

    private String getFilename() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + ".recorded_audio.wav";

        } else {
            return getFilesDir().getAbsolutePath() + ".recorded_audio.wav";
        }
//        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + System.nanoTime() + ".recorded_audio.wav";
//        return filepath;
    }

    private File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "recorded_audio.wav_" + timeStamp + ".wav");
        /// pathForCameraImage = mediaFile.getAbsolutePath();


        return mediaFile;
    }

    public Uri getOutputMediaFileUri() {
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile());
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }


        return mediaFile;
    }

    public Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));

        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    public void chooseFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public View getCurrView() {
        return generalFunc.getCurrentView(AddComplaintActivity.this);
    }

    class ImageSourceDialog implements Runnable {

        @Override
        public void run() {

            final Dialog dialog_img_update = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);
            dialog_img_update.setContentView(R.layout.design_image_source_select);
            MTextView cameraTxt = (MTextView) dialog_img_update.findViewById(R.id.cameraTxt);
            MTextView galleryTxt = (MTextView) dialog_img_update.findViewById(R.id.galleryTxt);
            LinearLayout cameraView = (LinearLayout) dialog_img_update.findViewById(R.id.cameraView);
            LinearLayout galleryView = (LinearLayout) dialog_img_update.findViewById(R.id.galleryView);

            MButton btn_type2 = ((MaterialRippleLayout) dialog_img_update.findViewById(R.id.btn_type2)).getChildView();
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

            cameraTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAMERA"));
            galleryTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GALLERY"));

            SelectableRoundedImageView cameraIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.cameraIconImgView);
            SelectableRoundedImageView galleryIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.galleryIconImgView);

            ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

            closeDialogImgView.setOnClickListener(v -> {
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                    isVideoClick = false;
                }
            });

            btn_type2.setOnClickListener(view -> dialog_img_update.dismiss());


            cameraView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }


                if (!isDeviceSupportCamera()) {
                    generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                } else {
                    if (isVideoClick) {
                        isVideoClick = false;
                        openVideoCapture();

                    } else {
                        chooseFromCamera();
                    }
                }

            });

            galleryView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }

                if (isVideoClick) {
                    isVideoClick = false;
                    Intent intent = new Intent();
                    intent.setTypeAndNormalize("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_TRIMMER);
                } else {
                    chooseFromGallery();
                }
            });

            dialog_img_update.setCanceledOnTouchOutside(true);

            Window window = dialog_img_update.getWindow();
            window.setGravity(Gravity.BOTTOM);

            window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            if (generalFunc.isRTLmode()) {
                dialog_img_update.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            dialog_img_update.show();

        }
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, Utils.TempProfileImageName, null);
        return Uri.parse(path);
    }

    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, VideoTrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
        startActivityForResult(intent, REQUEST_VIDEO_TRIMMER_RESULT);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
        /*if (requestCode == MANAGE_APP_ALL_FILES_ACCESS_REQUEST_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (generalFunc.isCameraPermissionGranted()) {
                    if (Environment.isExternalStorageManager()) {
                        new ImageSourceDialog().run();
                    } else {
                        generalFunc.showMessage(getCurrView(), "Allow this app to use storage access.");
                    }
                } else {
                    generalFunc.showMessage(getCurrView(), "Allow this app to use camera.");
                }

            }
        } else */


        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                audiofilePath = data.getStringExtra("path");
                audioDuration = data.getStringExtra("duration");
                uploadAudio();

            }
        } else if (requestCode == REQUEST_VIDEO_TRIMMER && resultCode == RESULT_OK) {
            final Uri selectedUri = data.getData();
            if (selectedUri != null) {
                startTrimActivity(selectedUri);
            } else {
                // showToastShort(getString(R.string.toast_cannot_retrieve_selected_video));
            }

        } else if (requestCode == REQUEST_VIDEO_TRIMMER_RESULT && resultCode == RESULT_OK) {
            final Uri selectedVideoUri = data.getData();
            selectedVideoFile = data.getData().getPath();
            selectedVideoName = data.getData().getLastPathSegment();
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedVideoUri.getPath(),
                    MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);

            Glide.with(this)
                    .load(getFileFromBitmap(thumb))
                    // .apply(simpleOptions)
                    .into(videothumbimg);
//            videothumbimg.setVideoURI(data.getData());
//            videothumbimg.seekTo(1);
//            final MediaController mediacontroller = new MediaController(this);
//            mediacontroller.setAnchorView(videothumbimg);
//            videothumbimg.setMediaController(mediacontroller);
//
//            videothumbimg.requestFocus();
            uploadVideo();

        }
        if (requestCode == Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {
            isAddressAdded = true;

            Latitude = data.getStringExtra("Latitude");
            Longitude = data.getStringExtra("Longitude");
            String Address = data.getStringExtra("Address");
            mapLocTxt.setText(Address);


        }

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE || requestCode == SELECT_PICTURE || requestCode == CROP_IMAGE) {
            if (resultCode == RESULT_OK) {

                if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                    // successfully captured the image
                    // display it in image view
//                    fileUri = Uri.parse(fileUriFilePath);
                    try {
                        cropImage(fileUri, fileUri);

                    } catch (Exception e) {
                        if (fileUri != null) {
//                            generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("Some problem occurred.can't able to get cropped image.so we are uploading original captured image.", "LBL_CROP_ERROR_TXT"));
                            imageUpload(fileUri);
                        } else if (data != null) {
//                            generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("Some problem occurred.can't able to get cropped image.so we are uploading original captured image.", "LBL_CROP_ERROR_TXT"));
                            imageUpload(data.getData());
                        } else {
                            generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_ERROR_OCCURED"));

                        }
                        e.printStackTrace();
                    }

                } else if (requestCode == SELECT_PICTURE) {

                    try {
                        Uri cropPictureUrl = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
                        String realPathFromURI = new ImageFilePath().getPath(getActContext(), data.getData());
                        File file = new File(realPathFromURI == null ? getImageUrlWithAuthority(this, data.getData()) : realPathFromURI);

                        if (file == null || realPathFromURI == null || realPathFromURI.equalsIgnoreCase("")) {
                            generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Can't read selected image. Please try again.", "LBL_IMAGE_READ_FAILED"));
                            return;
                        }

                        if (file.exists()) {
                            if (Build.VERSION.SDK_INT > 23) {
                                cropImage(FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file), cropPictureUrl);
                            } else {
                                cropImage(Uri.fromFile(file), cropPictureUrl);
                            }

                        } else {
                            cropImage(data.getData(), cropPictureUrl);
                        }

                    } catch (Exception e) {
                        if (data != null) {
//                            generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("Some problem occurred.can't able to get cropped image.so we are uploading original captured image.", "LBL_CROP_ERROR_TXT"));
                            imageUpload(data.getData());
                        } else {
                            generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_ERROR_OCCURED"));

                        }
                        e.printStackTrace();
                    }
                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    imageUpload(resultUri);
                } else if (requestCode == CROP_IMAGE) {

                    imageUpload(fileUri);
                }
            } else {
//                if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
//                    generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_FAILED_CAPTURE_IMAGE_TXT"));
//                } else {
//                    generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_ERROR_OCCURED"));
//                }
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            imageUpload(resultUri);
        }
    }

    private File getFileFromBitmap(Bitmap bmp) {
        /*//create a file to write bitmap data*/
        thumbFile = new File(this.getCacheDir(), "thumb_" + selectedVideoName + ".png");
        try {
            thumbFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*//Convert bitmap to byte array*/
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        /*//write the bytes in file*/
        try {
            FileOutputStream fos = new FileOutputStream(thumbFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbFile;
    }

    private void cropImage(final Uri sourceImage, Uri destinationImage) {

        try {
            CropImage.activity(sourceImage)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(false)
                    .setDoneButtonText(generalFunc.retrieveLangLBl("Done", "LBL_DONE"))
                    .setCancelButtonText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"))
                    .setAspectRatio(1024, 1024)
                    .setNoOutputImage(false)
                    .start(this);
        } catch (Exception e) {
            imageUpload(sourceImage);
        }
    }

    public String[] generateImageParams(String key, String content) {
        String[] tempArr = new String[2];
        tempArr[0] = key;
        tempArr[1] = content;

        return tempArr;
    }

    private void imageUpload(Uri fileUri) {

        if (fileUri == null) {
            generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_ERROR_OCCURED"));
            return;
        }

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("iUserId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("action_type", "IMAGE"));
        paramsList.add(generateImageParams("type", "AddReportIssue"));

        String selectedImagePath = new ImageFilePath().getPath(getActContext(), fileUri);

        /*boolean isStoragePermissionAvail;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isStoragePermissionAvail = Environment.isExternalStorageManager();
        } else {
            isStoragePermissionAvail = generalFunc.isStoragePermissionGranted();
        }*/
        // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
        boolean isStoragePermissionAvail = generalFunc.isStoragePermissionGranted();
        if (isValidImageResolution(selectedImagePath) && isStoragePermissionAvail) {

            imageListPath.add(selectedImagePath);
            ManageCameraView();

            new UploadProfileImage(AddComplaintActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList).execute();
        } else {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please select image which has minimum is 256 * 256 resolution.", "LBL_MIN_RES_IMAGE"));
        }

    }

    public boolean isValidImageResolution(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;

        if (width >= Utils.ImageUpload_MINIMUM_WIDTH && height >= Utils.ImageUpload_MINIMUM_HEIGHT) {
            return true;
        }
        return false;
    }

    public void handleImgUploadResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail) {
                imageIdList.add(generalFunc.getJsonValue("iImageId", responseString));


            } else {
                generalFunc.showGeneralMessage("",
                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        } else {
            generalFunc.showError();
        }
    }


    public void deletImg(int pos, boolean isAudio) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "AddReportIssue");

        parameters.put("action_type", "DELETE");
        if (isAudio) {
            parameters.put("iImageId", audioId);
        } else {
            parameters.put("iImageId", imageIdList.get(pos));
        }


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    if (isAudio) {
                        audioId = "";
                        playArea.setVisibility(View.GONE);
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                    } else {
                        imageIdList.remove(pos);
                        imageListPath.remove(pos);
                        ManageCameraView();
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
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

    public void uploadAudio() {
        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("iUserId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("action_type", "AUDIO"));
        paramsList.add(generateImageParams("type", "AddReportIssue"));


        new UploadProfileImage(AddComplaintActivity.this, audiofilePath, AUDIO_FILE_PATH + "_" + "/" + UUID.randomUUID() + "-audio.wav", paramsList, true).execute();

    }

    public void uploadVideo() {
        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("iUserId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("action_type", "VIDEO"));
        paramsList.add(generateImageParams("type", "AddReportIssue"));


        new UploadProfileImage(AddComplaintActivity.this, selectedVideoFile, selectedVideoFile + "_" + "/" + UUID.randomUUID() + "-video.mp4", paramsList, false, true).execute();

    }

    public void handleImgUploadResponse(String responseString, boolean isAudio, boolean isVideo) {

        if (responseString != null && !responseString.equals("") && isAudio) {
            isAudioAdded = true;
            audioId = generalFunc.getJsonValue("iImageId", responseString);
            managePlayArea();

        } else if (responseString != null && !responseString.equals("") && isVideo) {
            videoThumbArea.setVisibility(View.VISIBLE);
            isVideoAdded = true;
            videoId = generalFunc.getJsonValue("iImageId", responseString);

        } else {
            generalFunc.showError();
        }
    }

    public void addComplaint() {
        boolean isStateSelect = selStatePosition == -1 ? false : true;
        boolean isCitySelect = selCityPosition == -1 ? false : true;
        boolean isCategorySelect = selCategoryPosition == -1 ? false : true;
        boolean issubCategorySelect = selsubCategoryPosition == -1 ? false : true;
        boolean isImageAdded = imageIdList.size() == 0 ? false : true;
        boolean isDescriptionAdded = Utils.checkText(InstructionEditBox) ? true : false;
        if (!isStateSelect) {
            Utils.setErrorFields(stateBox, required_str);
        }
        if (!isCitySelect) {
            Utils.setErrorFields(cityBox, required_str);
        }
        if (!isCategorySelect) {
            Utils.setErrorFields(categoryBox, required_str);
        }
        if (!issubCategorySelect) {
            Utils.setErrorFields(subcategoryBox, required_str);
        }
        if (!isDescriptionAdded) {
            Utils.setErrorFields(InstructionEditBox, required_str);
        }
        if (!isAddressAdded) {
            generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("Please select location address", "LBL_REPORTISSUE_LOCATION_REQUIRED"));
        }
        if (!isImageAdded) {
            generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("Required  at least 1 image.", "LBL_REPORTISSUE_IMAGE_REQUIRED"));

        }


        if (!isCitySelect || !isCategorySelect || !isImageAdded || !isDescriptionAdded || !isStateSelect || !isAddressAdded || !issubCategorySelect) {
            return;
        }


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "AddReportIssue");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("tDescription", Utils.getText(InstructionEditBox));
        parameters.put("iVehicleCategoryId", categoryDataList.get(selCategoryPosition).get("iVehicleCategoryId"));
        parameters.put("iCityId", cityDataList.get(selCityPosition).get("iCityId"));
        parameters.put("iStateId", stateDataList.get(selStatePosition).get("iStateId"));
        parameters.put("action_type", "ADD");
        parameters.put("iVehiclesubCategoryId", subcategoryDataList.get(selsubCategoryPosition).get("iVehicleTypeId"));
        parameters.put("iImageId", android.text.TextUtils.join(",", imageIdList));
        if (!audioId.equalsIgnoreCase("")) {
            parameters.put("iImageId", android.text.TextUtils.join(",", imageIdList) + "," + audioId);
        }
        if (!videoId.equalsIgnoreCase("")) {
            parameters.put("iImageId", android.text.TextUtils.join(",", imageIdList) + "," + videoId);
        }
        if (!audioId.equalsIgnoreCase("") && !videoId.equalsIgnoreCase("")) {
            parameters.put("iImageId", android.text.TextUtils.join(",", imageIdList) + "," + audioId + "," + videoId);
        }
        parameters.put("tAddress", Utils.getText(mapLocTxt));
        parameters.put("vlatitude", Latitude);
        parameters.put("vlongitude", Longitude);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), buttonId -> MyApp.getInstance().restartWithGetDataApp());

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
}