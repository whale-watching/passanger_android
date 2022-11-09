package com.videorecording;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.general.files.GeneralFunctions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.solicity.user.R;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;


public abstract class VideoPicker extends BottomSheetDialog implements View.OnClickListener {
    protected long lastClickTime = 0;

    View videoPickerDilaog;
    Context mContext;
    LinearLayout cameraView, galleryView;
    GeneralFunctions generalFunc;

    public VideoPicker(@NonNull Context context) {
        super(context);
        mContext = context;
        generalFunc = new GeneralFunctions(mContext);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video_picker);
        TextView camera, gallery;
        cameraView = findViewById(R.id.cameraView);
        galleryView = findViewById(R.id.galleryView);
        MTextView cameraTxt = (MTextView) findViewById(R.id.cameraTxt);
        MTextView galleryTxt = (MTextView) findViewById(R.id.galleryTxt);
        LinearLayout cameraView = (LinearLayout) findViewById(R.id.cameraView);
        LinearLayout galleryView = (LinearLayout) findViewById(R.id.galleryView);

        MButton btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        cameraTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAMERA"));
        galleryTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GALLERY"));
        btn_type2.setOnClickListener(view -> dismiss());


        cameraView.setOnClickListener(this);
        galleryView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        preventDoubleClick(view);
        dismiss();
        switch (view.getId()) {
            case R.id.cameraView:
                onCameraClicked();
                break;
            case R.id.galleryView:
                onGalleryClicked();
                break;
        }
    }

    private void preventDoubleClick(View view) {
        /*// preventing double, using threshold of 1000 ms*/
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
    }

    protected abstract void onCameraClicked();

    protected abstract void onGalleryClicked();
}