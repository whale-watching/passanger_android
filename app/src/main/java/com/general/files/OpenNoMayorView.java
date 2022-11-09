package com.general.files;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.solicity.user.AddComplaintActivity;
import com.solicity.user.MainActivity;
import com.solicity.user.R;
import com.solicity.user.SearchLocationActivity;
import com.solicity.user.UberXActivity;
import com.solicity.user.UberXHomeActivity;
import com.solicity.user.deliverAll.FoodDeliveryHomeActivity;
import com.solicity.user.deliverAll.ServiceHomeActivity;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

public class OpenNoMayorView {
    Activity currentAct;


    //View noLocView;
    GeneralFunctions generalFunc;
    private static OpenNoMayorView currentInst;
    private boolean isViewExecutionLocked = false;
    Dialog noLocView;
    boolean isCity = true;


    public static OpenNoMayorView getInstance(Activity currentAct) {
        if (currentInst == null) {
            currentInst = new OpenNoMayorView();
        }
        currentInst.currentAct = currentAct;

        return currentInst;
    }


    public void configView(boolean isCity) {
        this.isCity = isCity;
        if (currentAct != null) {

            if (isViewExecutionLocked) {
                return;
            }

            isViewExecutionLocked = true;

            generalFunc = MyApp.getInstance().getGeneralFun(MyApp.getInstance().getCurrentAct());

            noLocView = new Dialog(currentAct, R.style.ImageSourceDialogStyle);
            noLocView.setContentView(R.layout.desgin_no_mayor_view);
            LayoutInflater inflater = (LayoutInflater) currentAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // View noLocView = inflater.inflate(R.layout.desgin_no_mayor_view, null);


            ImageView closeImageView = (ImageView) noLocView.findViewById(R.id.closeImageView);
            ImageView noLocImgView = (ImageView) noLocView.findViewById(R.id.noLocImgView);

            MTextView noLocTitleTxt = (MTextView) noLocView.findViewById(R.id.noLocTitleTxt);
            MTextView noLocMsgTxt = (MTextView) noLocView.findViewById(R.id.noLocMsgTxt);

            MButton settingsBtn = ((MaterialRippleLayout) noLocView.findViewById(R.id.settingsBtn)).getChildView();

            noLocTitleTxt.setText(generalFunc.retrieveLangLBl("OUTSIDE THE SERVICE AREA", "LBL_OUT_OF_SERVICE_AREA"));
            noLocMsgTxt.setText(generalFunc.retrieveLangLBl("Please contact our support team OR check back later.", "LBL_CONTACT_TEAM"));


            settingsBtn.setBackgroundColor(MyApp.getInstance().getCurrentAct().getResources().getColor(R.color.appThemeColor_1));
            settingsBtn.setText(generalFunc.retrieveLangLBl("", "LBL_OK"));
            settingsBtn.setTextColor(Color.parseColor("#ffffff"));


            settingsBtn.setOnClickListener(v -> {
                closeView();
            });
            closeImageView.setOnClickListener(v -> {
                closeView();
            });
            noLocView.show();
            noLocView.setCancelable(false);


        } else {
            Logger.e("AssertError", "ViewGroup OR Activity cannot be null");
        }
        isViewExecutionLocked = false;
    }


    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (currentAct.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, currentAct.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, currentAct.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }


    public void closeView() {
        noLocView.dismiss();
        AddComplaintActivity addComplaintActivity = (AddComplaintActivity) currentAct;
        if(isCity) {

            addComplaintActivity.refreshcity();
        }
        else
        {
            addComplaintActivity.refreshcategory();
        }



    }


}

