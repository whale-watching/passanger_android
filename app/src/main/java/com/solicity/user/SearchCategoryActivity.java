package com.solicity.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.files.CategoryListItem;
import com.adapter.files.PinnedCategorySectionListAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.realmModel.CarWashCartData;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.pinnedListView.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class SearchCategoryActivity extends AppCompatActivity implements PinnedCategorySectionListAdapter.ServiceClick {

    View view;
    GeneralFunctions generalFunc;
    PinnedSectionListView service_list;
    PinnedCategorySectionListAdapter pinnedSectionListAdapter;

    ArrayList<CategoryListItem> categoryitems_list;

    ProgressBar loadingBar;

    int hourCnt = 0, regCnt = 0, fixCnt = 0;
    private EditText searchTxtView;
    boolean isSearch = false;
    ImageView imageCancel;
    private AVLoadingIndicatorView loaderView;
    ImageView backImgView;
    MTextView titleTxt, noResTxt;
    int UBERX_CART=001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category);

        generalFunc = new GeneralFunctions(getActContext());
        service_list = (PinnedSectionListView) findViewById(R.id.service_list);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        noResTxt = (MTextView) findViewById(R.id.noResTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SEARCH_SERVICES"));

        service_list.setShadowVisible(true);

        service_list.setFastScrollEnabled(false);
        service_list.setFastScrollAlwaysVisible(false);

        categoryitems_list = new ArrayList<>();
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        imageCancel = (ImageView) findViewById(R.id.imageCancel);
        imageCancel.setOnClickListener(new setOnClickList());
        imageCancel.setVisibility(View.GONE);
        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        loaderView.setVisibility(View.GONE);

        searchTxtView = (EditText) findViewById(R.id.searchTxtView);
        searchTxtView.setHint(generalFunc.retrieveLangLBl("", "LBL_SEARCH_SERVICES"));
        searchTxtView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    isSearch = false;
                    imageCancel.setVisibility(View.GONE);
                    loaderView.setVisibility(View.GONE);
                    loaderView.setVisibility(View.VISIBLE);
                    getServiceInfo("");
                    Utils.hideKeyboard(getActContext());
                } else {


                    if (s.length() > 2) {
                        isSearch = true;
                        loaderView.setVisibility(View.VISIBLE);
                        imageCancel.setVisibility(View.GONE);
                        service_list.setVisibility(View.GONE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                loaderView.setVisibility(View.GONE);
//                                imageCancel.setVisibility(View.VISIBLE);


                                getServiceInfo(searchTxtView.getText().toString().trim());

                            }
                        }, 750);
                    }

                }
            }
        });


    }

    public RealmResults<CarWashCartData> getHourData() {
        try {
            hourCnt = 0;
            regCnt = 0;
            fixCnt = 0;
            Realm realm = MyApp.getRealmInstance();
            RealmResults<CarWashCartData> data = realm.where(CarWashCartData.class).findAll();

            for (int i = 0; i < data.size(); i++) {
                CategoryListItem categoryListItem = data.get(i).getCategoryListItem();
                if (categoryListItem.geteFareType().equals("Hourly")) {
                    hourCnt = hourCnt + 1;
                } else if (categoryListItem.geteFareType().equals("Regular")) {
                    regCnt = regCnt + 1;
                } else {
                    fixCnt = fixCnt + 1;
                }

            }
        } catch (Exception e) {
            Logger.d("RealmException", "::" + e.toString());
        }

        return null;
    }

    @Override
    public void serviceClickList(CategoryListItem serviceListItem) {
        getHourData();
        Realm realm = MyApp.getRealmInstance();

        CarWashCartData carWashCartData = checkSameRecordExist(realm, serviceListItem);

        if ((serviceListItem.geteFareType().equals("Hourly") && fixCnt >= 1) || (serviceListItem.geteFareType().equals("Regular") && fixCnt >= 1)) {
            generalFunc.showMessage(view, generalFunc.retrieveLangLBl("", "LBL_RESTRICT_FIXED_SERVICE"));

        } else if ((serviceListItem.geteFareType().equals("Hourly") && hourCnt > 1) || (serviceListItem.geteFareType().equals("Fixed") && hourCnt == 1)
                || ((serviceListItem.geteFareType().equals("Hourly") && hourCnt >= 1 && carWashCartData == null)) || (serviceListItem.geteFareType().equals("Regular") && hourCnt >= 1)) {
            generalFunc.showMessage(view, generalFunc.retrieveLangLBl("", "LBL_RESTRICT_HOURLY_SERVICE"));

        } else if ((serviceListItem.geteFareType().equals("Regular") && regCnt > 1) || (serviceListItem.geteFareType().equals("Fixed") && regCnt >= 1) || (serviceListItem.geteFareType().equals("Hourly") && regCnt >= 1) || (serviceListItem.geteFareType().equals("Regular") && regCnt >= 1 && carWashCartData == null)) {
            generalFunc.showMessage(view, generalFunc.retrieveLangLBl("", "LBL_RESTRICT_REGULAR_SERVICE"));

        } else {
            Bundle bn = new Bundle();
            bn.putSerializable("data", (Serializable) serviceListItem);
            bn.putString("iDriverId", getIntent().getStringExtra("iDriverId"));
            new StartActProcess(getActContext()).startActForResult(UberxCartActivity.class, bn,UBERX_CART);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            imageCancel.setVisibility(View.GONE);
            loaderView.setVisibility(View.VISIBLE);
            service_list.setVisibility(View.GONE);
            getServiceInfo(Utils.getText(searchTxtView));
        }
    }

    @Override
    public void serviceRemoveClickList(CategoryListItem serviceListItem) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);

        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
            } else {
                Realm realm = MyApp.getRealmInstance();
                realm.beginTransaction();
                CarWashCartData carWashCartData = checkSameRecordExist(realm, serviceListItem);
                carWashCartData.deleteFromRealm();
                realm.commitTransaction();
                imageCancel.setVisibility(View.GONE);
                loaderView.setVisibility(View.VISIBLE);
                service_list.setVisibility(View.GONE);
                getServiceInfo(Utils.getText(searchTxtView));


            }
        });

        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_REMOVE_SERVICE_NOTE"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
        generateAlert.showAlertBox();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == R.id.imageCancel) {
                loaderView.setVisibility(View.GONE);
                searchTxtView.setText("");
                service_list.setVisibility(View.GONE);
                getServiceInfo("");
            }
        }
    }

    public Context getActContext() {
        return SearchCategoryActivity.this;
    }

    public void getServiceInfo(String searchText) {
        loadingBar.setVisibility(View.GONE);


        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDriverServiceCategories");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iDriverId", getIntent().getStringExtra("iDriverId"));
        parameters.put("SelectedCabType", Utils.CabGeneralType_UberX);
        parameters.put("parentId", getIntent().getStringExtra("parentId") != null ? getIntent().getStringExtra("parentId") : "");
        parameters.put("SelectedVehicleTypeId", getIntent().getStringExtra("SelectedVehicleTypeId") != null ? getIntent().getStringExtra("SelectedVehicleTypeId") : "");
        parameters.put("vSelectedLatitude", getIntent().getStringExtra("latitude"));
        parameters.put("vSelectedLongitude", getIntent().getStringExtra("longitude"));
        parameters.put("vSelectedAddress", getIntent().getStringExtra("address"));

        parameters.put("search_keyword", searchText);

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (this.isSearch) {
                    loaderView.setVisibility(View.GONE);
                    imageCancel.setVisibility(View.VISIBLE);
                }
                noResTxt.setVisibility(View.GONE);

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    service_list.setVisibility(View.VISIBLE);


                    pinnedSectionListAdapter = null;
                    categoryitems_list.clear();

                    JSONArray mainListArr = generalFunc.getJsonArray(Utils.message_str, responseString);
                    CategoryListItem[] sections = null;

                    int sectionPosition = 0, listPosition = 0;
                    int lastItemPosition = 0;

                    if (pinnedSectionListAdapter != null) {
                        sectionPosition = pinnedSectionListAdapter.getSections().length - 1;
                        listPosition = pinnedSectionListAdapter.getSections().length - 1;

                        sections = new CategoryListItem[pinnedSectionListAdapter.getSections().length + mainListArr.length()];

                        lastItemPosition = pinnedSectionListAdapter.getSections().length - 1;

                        for (int i = 0; i < pinnedSectionListAdapter.getSections().length; i++) {
                            sections[i] = pinnedSectionListAdapter.getSections()[i];
                        }

                    } else {
                        sections = new CategoryListItem[mainListArr.length()];
                    }

                    Realm realm = MyApp.getRealmInstance();

                    for (int i = 0; i < mainListArr.length(); i++) {
                        JSONObject tempJson = generalFunc.getJsonObject(mainListArr, i);

                        String vCategory = generalFunc.getJsonValueStr("vCategory", tempJson);

                        CategoryListItem section = new CategoryListItem(CategoryListItem.SECTION, vCategory);
                        section.sectionPosition = sectionPosition;
                        section.listPosition = listPosition++;
                        section.CountSubItems = GeneralFunctions.parseIntegerValue(0, vCategory);

                        sections[sectionPosition] = section;

                        categoryitems_list.add(section);

                        JSONArray subListArr = generalFunc.getJsonArray("SubCategories", tempJson);

                        for (int j = 0; j < subListArr.length(); j++) {
                            JSONObject subTempJson = generalFunc.getJsonObject(subListArr, j);

                            CategoryListItem categoryListItem = new CategoryListItem(CategoryListItem.ITEM, generalFunc.getJsonValueStr("vCategory", tempJson));
                            categoryListItem.sectionPosition = sectionPosition;
                            categoryListItem.listPosition = listPosition++;
                            categoryListItem.setvTitle(generalFunc.getJsonValueStr("vVehicleType", subTempJson));
                            categoryListItem.setiVehicleCategoryId(generalFunc.getJsonValueStr("iVehicleCategoryId", subTempJson));
                            categoryListItem.setvDesc(generalFunc.getJsonValueStr("vCategoryDesc", subTempJson));
                            categoryListItem.setvShortDesc(generalFunc.getJsonValueStr("vCategoryShortDesc", subTempJson));
                            categoryListItem.seteFareType(generalFunc.getJsonValueStr("eFareType", subTempJson));
                            categoryListItem.setfFixedFare(generalFunc.getJsonValueStr("fFixedFare", subTempJson));
                            categoryListItem.setfPricePerHour(generalFunc.getJsonValueStr("fPricePerHour", subTempJson));
                            categoryListItem.setfMinHour(generalFunc.getJsonValueStr("fMinHour", subTempJson));
                            categoryListItem.setiVehicleTypeId(generalFunc.getJsonValueStr("iVehicleTypeId", subTempJson));
                            if (checkSameRecordExist(realm, generalFunc.getJsonValueStr("iVehicleTypeId", subTempJson))) {
                                categoryListItem.setAdd(true);
                            } else {
                                categoryListItem.setAdd(false);
                            }
                            categoryitems_list.add(categoryListItem);
                        }

                        sectionPosition++;
                    }

                    pinnedSectionListAdapter = new PinnedCategorySectionListAdapter(getActContext(), categoryitems_list, sections);
                    service_list.setAdapter(pinnedSectionListAdapter);
                    pinnedSectionListAdapter.setserviceClickListener(this);


                } else {
                    loaderView.setVisibility(View.GONE);
                    if (Utils.checkText(searchTxtView)) {
                        noResTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        noResTxt.setVisibility(View.VISIBLE);
                    }

                }
            } else {
                generalFunc.showError(true);
            }

            loadingBar.setVisibility(View.GONE);

        });
        exeWebServer.execute();
    }

    public CarWashCartData checkSameRecordExist(Realm realm, CategoryListItem serviceListItem) {
        CarWashCartData cart = null;

        RealmResults<CarWashCartData> cartlist = realm.where(CarWashCartData.class).findAll();

        if (cartlist != null && cartlist.size() > 0)

            for (int i = 0; i < cartlist.size(); i++) {

                if (serviceListItem.getiVehicleTypeId().equalsIgnoreCase(cartlist.get(i).getCategoryListItem().getiVehicleTypeId())) {
                    return cartlist.get(i);
                }
            }


        return cart;
    }

    public boolean checkSameRecordExist(Realm realm, String iVehicleTypeId) {
        RealmResults<CarWashCartData> cartlist = realm.where(CarWashCartData.class).findAll();

        if (cartlist != null && cartlist.size() > 0)

            for (int i = 0; i < cartlist.size(); i++) {

                if (iVehicleTypeId.equalsIgnoreCase(cartlist.get(i).getCategoryListItem().getiVehicleTypeId())) {
                    return true;
                }
            }
        return false;
    }
}