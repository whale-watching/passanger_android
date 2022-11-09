package com.adapter.files.deliverAll;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adapter.files.AddressListAdapter;
import com.solicity.user.CallScreenActivity;
import com.solicity.user.R;
import com.solicity.user.deliverAll.TrackOrderActivity;
import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SinchService;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 01/05/18.
 */

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.ViewHolder> {

    Context mContext;
    ArrayList<HashMap<String, String>> listData;
    GeneralFunctions generalFunctions;
    int blackColor, bgColor, statusColor, color;
    int size2, size44, size36, padding4dp;
    ReviewItemClickListener ClickListener;


    public void onClickListener(ReviewItemClickListener itemClickListener) {
        this.ClickListener = itemClickListener;
    }

    public TrackOrderAdapter(Context mContext, ArrayList<HashMap<String, String>> listData) {

        this.mContext = mContext;
        this.listData = listData;
        generalFunctions = MyApp.getInstance().getGeneralFun(mContext);
        blackColor = Color.parseColor("#000000");
        bgColor = Color.parseColor("#f8f8f8");
        statusColor = Color.parseColor("#5a5a5a");
        size2 = Utils.dipToPixels(mContext, 2);
        color = Color.parseColor("#dddddd");
        size44 = Utils.dipToPixels(mContext, 44);
        size36 = Utils.dipToPixels(mContext, 36);
        padding4dp = Utils.dipToPixels(mContext, 4);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_order_item_design, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HashMap<String, String> mapData = listData.get(position);

        holder.contentTxtView.setText(mapData.get("vStatus"));

        holder.titleTxtView.setText(mapData.get("vStatusTitle"));

        // new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(mContext, 5), 0, Color.parseColor("#FFFFFF"), holder.containerView);

        if (mapData.get("dDate").trim().equalsIgnoreCase("")) {
            holder.timeTxtView.setVisibility(View.INVISIBLE);
            holder.amPmTxtView.setVisibility(View.GONE);

            holder.timeTxtView.setText("");
            holder.amPmTxtView.setText("");
        } else {
            holder.timeTxtView.setVisibility(View.VISIBLE);
            holder.amPmTxtView.setVisibility(View.VISIBLE);

            holder.timeTxtView.setText(mapData.get("dDateConverted"));
            holder.amPmTxtView.setText(mapData.get("dDateAMPM"));
        }

        String eShowCallImg = mapData.get("eShowCallImg");

        if (eShowCallImg != null && eShowCallImg.equalsIgnoreCase("Yes")) {
            holder.callImgView.setVisibility(View.VISIBLE);
        } else {
            holder.callImgView.setVisibility(View.GONE);
        }

        holder.callImgView.setOnClickListener(v -> {


            if (mapData.get("RIDE_DRIVER_CALLING_METHOD").equals("Voip")) {
                sinchCall(mapData);
            } else {
                getMaskNumber(mapData);
            }

        });

        if (position == 0) {
            holder.topView.setVisibility(View.INVISIBLE);
        } else {
            holder.topView.setVisibility(View.VISIBLE);
        }

        if (position == listData.size() - 1) {
            holder.bottomView.setVisibility(View.INVISIBLE);
        } else {
            holder.bottomView.setVisibility(View.VISIBLE);
        }

        String iStatusCode = mapData.get("iStatusCode");


        if (iStatusCode.equalsIgnoreCase("1")) {
            holder.statusImgView.setImageResource(R.drawable.track_status_order_place);
        } else if (iStatusCode.equalsIgnoreCase("2")) {
            holder.statusImgView.setImageResource(R.drawable.ic_shop);
        } else if (iStatusCode.equalsIgnoreCase("4")) {
            if (TrackOrderActivity.serviceId.equalsIgnoreCase("") || TrackOrderActivity.serviceId.equalsIgnoreCase("1")) {
                holder.statusImgView.setImageResource(R.drawable.track_status_order_store);
            } else {
                holder.statusImgView.setImageResource(R.drawable.track_status_order_store);
            }
        } else if (iStatusCode.equalsIgnoreCase("5")) {
            holder.statusImgView.setImageResource(R.drawable.track_order_on_way);
        } else if (iStatusCode.equalsIgnoreCase("8") || iStatusCode.equalsIgnoreCase("9")) {
            holder.statusImgView.setImageResource(R.drawable.track_status_order_cancel);
        }
        else if(iStatusCode.equalsIgnoreCase("13"))
        {
            holder.statusImgView.setImageResource(R.drawable.ic_review);
        }
        else if(iStatusCode.equalsIgnoreCase("14"))
        {
            holder.statusImgView.setImageResource(R.drawable.ic_make_payment);
        }
        else {
            holder.statusImgView.setImageResource(R.drawable.track_status_order_accept);
        }

        if (mapData.get("eCompleted").equalsIgnoreCase("Yes")) {
            holder.titleTxtView.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_1));
            holder.timeTxtView.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_1));
            holder.amPmTxtView.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_1));
            holder.statusImgView.setColorFilter(mContext.getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.SRC_IN);
            holder.topView.setBackgroundColor(mContext.getResources().getColor(R.color.appThemeColor_1));
            new CreateRoundedView(mContext.getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(mContext, mapData.get("OrderCurrentStatusCode").equalsIgnoreCase(iStatusCode) ? 22 : 18), 0, mContext.getResources().getColor(R.color.white), holder.imgContainerView);
        } else {


            holder.titleTxtView.setTextColor(blackColor);
            holder.timeTxtView.setTextColor(blackColor);
            holder.topView.setBackgroundColor(color);
            holder.statusImgView.setColorFilter(statusColor, PorterDuff.Mode.SRC_IN);
            new CreateRoundedView(bgColor, Utils.dipToPixels(mContext, mapData.get("OrderCurrentStatusCode").equalsIgnoreCase(iStatusCode) ? 22 : 18), size2, color, holder.imgContainerView);
        }

        int nwPos = position + 1;
        if (holder.bottomView.getVisibility() == View.VISIBLE && (nwPos) < listData.size()) {
            HashMap<String, String> nextMapData = listData.get(nwPos);
            if (nextMapData.get("eCompleted").equalsIgnoreCase("Yes")) {
                holder.bottomView.setBackgroundColor(mContext.getResources().getColor(R.color.appThemeColor_1));
            } else {
                holder.bottomView.setBackgroundColor(color);
            }
        }

        if (mapData.get("OrderCurrentStatusCode").equalsIgnoreCase(iStatusCode)) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.imgContainerView.getLayoutParams();
            params.height = size44;
            params.width = size44;
            holder.imgContainerView.setLayoutParams(params);

            holder.timeLineContainer.setPaddingRelative(0, 0, 0, 0);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.imgContainerView.getLayoutParams();

            params.height = size36;
            params.width = size36;
            holder.imgContainerView.setLayoutParams(params);

            holder.timeLineContainer.setPaddingRelative(padding4dp, 0, padding4dp, 0);
        }


        if (mapData.get("showViewBillButton").equalsIgnoreCase("Yes")) {
            holder.viewDetailsArea.setVisibility(View.VISIBLE);
            holder.btn_viewDetails.setText(listData.get(position).get("fStoreBillAmount"));
            holder.infoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClickListener.setOnviewBill(position);
                }
            });

        } else {
            holder.viewDetailsArea.setVisibility(View.GONE);

        }
        if (mapData.get("showPaymentButton").equalsIgnoreCase("Yes")) {
            holder.paymentArea.setVisibility(View.VISIBLE);
            holder.btn_payment.setText(generalFunctions.retrieveLangLBl("Make Payment", "LBL_MAKE_PAYMENT"));
            holder.btn_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClickListener.setOnMakePaymentClick(position);

                }
            });

        } else {
            holder.paymentArea.setVisibility(View.GONE);

        }

        if (mapData.get("genieWaitingForUserApproval").equalsIgnoreCase("Yes") && mapData.get("genieUserApproved").equalsIgnoreCase("No")) {

            if (mapData.get("MenuItem").equalsIgnoreCase("")) {
                holder.reviewArea.setVisibility(View.GONE);
            } else {
                holder.reviewArea.setVisibility(View.VISIBLE);
                holder.itemName.setText(mapData.get("iQty") + " x " + mapData.get("MenuItem"));
                Picasso.get().load(mapData.get("vImage")).placeholder(R.mipmap.ic_no_icon).into(holder.itemImg);
                holder.btn_review.setText(generalFunctions.retrieveLangLBl("Review", "LBL_GENIE_REVIEW"));

                holder.btn_review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClickListener.setOnreviewClick(position);
                    }
                });
            }
        } else {
            holder.reviewArea.setVisibility(View.GONE);
        }
    }


    public void sinchCall(HashMap<String, String> mapData) {


        if (generalFunctions.isCallPermissionGranted(false) == false) {
            generalFunctions.isCallPermissionGranted(true);
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Id", generalFunctions.getMemberId());
            hashMap.put("Name", mapData.get("vName"));
            hashMap.put("PImage", mapData.get("vImgName"));
            hashMap.put("type", Utils.userType);
            TrackOrderActivity activity = (TrackOrderActivity) mContext;
            if (new AppFunctions(mContext).checkSinchInstance(activity != null ? activity.getSinchServiceInterface() : null)) {
                activity.getSinchServiceInterface().getSinchClient().setPushNotificationDisplayName(mapData.get("LBL_INCOMING_CALL"));
                Call call = activity.getSinchServiceInterface().callUser(Utils.CALLTODRIVER + "_" + mapData.get("iDriverId"), hashMap);
                String callId = call.getCallId();
                Intent callScreen = new Intent(mContext, CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                callScreen.putExtra("vImage", mapData.get("driverImageUrl"));
                callScreen.putExtra("vName", mapData.get("driverName"));
                callScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                mContext.startActivity(callScreen);
            }
        }
    }


    public void getMaskNumber(HashMap<String, String> mapData) {
        if (mapData.get("CALLMASKING_ENABLED").equalsIgnoreCase("Yes")) {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "getCallMaskNumber");
            parameters.put("iOrderId", mapData.containsKey("iOrderId") ? mapData.get("iOrderId") : "");
            parameters.put("UserType", Utils.userType);
            parameters.put("iMemberId", generalFunctions.getMemberId());

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
            exeWebServer.setLoaderConfig(mContext, true, generalFunctions);

            exeWebServer.setDataResponseListener(responseString -> {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        String message = generalFunctions.getJsonValue(Utils.message_str, responseString);

                        call(message);
                    } else {
                        directCall(mapData);

                    }
                } else {
                    generalFunctions.showError();
                }
            });
            exeWebServer.execute();
        } else {
            directCall(mapData);
        }
    }

    public void directCall(HashMap<String, String> mapData) {
        String DriverPhone = mapData.get("DriverPhone");
        if (DriverPhone != null) {
            call(DriverPhone);
        }
    }

    public void call(String phoneNumber) {

        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            mContext.startActivity(callIntent);

        } catch (Exception e) {
        }
    }

    public interface ReviewItemClickListener {
        void setOnreviewClick(int position);

        void setOnMakePaymentClick(int position);

        void setOnviewBill(int position);


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public LinearLayout containerView;
        public MTextView contentTxtView;
        public ImageView callImgView;
        public MTextView timeTxtView;
        public MTextView amPmTxtView;
        public MTextView titleTxtView;
        public View topView;
        public View bottomView;
        public View imgContainerView;
        public LinearLayout timeLineContainer;
        public ImageView statusImgView;
        public LinearLayout reviewArea;
        public LinearLayout paymentArea;
        public LinearLayout viewDetailsArea;
        public MTextView itemName;
        public ImageView itemImg;
        public MButton btn_review;
        public MTextView btn_payment;
        public MTextView btn_viewDetails;
        public ImageView infoImg;


//        public TimelineView mTimelineView;

        public ViewHolder(View view) {
            super(view);

            contentTxtView = (MTextView) view.findViewById(R.id.contentTxtView);
            containerView = (LinearLayout) view.findViewById(R.id.containerView);
            callImgView = (ImageView) view.findViewById(R.id.callImgView);
            timeTxtView = (MTextView) view.findViewById(R.id.timeTxtView);
            amPmTxtView = (MTextView) view.findViewById(R.id.amPmTxtView);
            titleTxtView = (MTextView) view.findViewById(R.id.titleTxtView);
            timeLineContainer = (LinearLayout) view.findViewById(R.id.timeLineContainer);
            topView = view.findViewById(R.id.topView);
            bottomView = view.findViewById(R.id.bottomView);
            imgContainerView = view.findViewById(R.id.imgContainerView);
            statusImgView = (ImageView) view.findViewById(R.id.statusImgView);
            reviewArea = (LinearLayout) view.findViewById(R.id.reviewArea);
            paymentArea = (LinearLayout) view.findViewById(R.id.paymentArea);
            viewDetailsArea = (LinearLayout) view.findViewById(R.id.viewDetailsArea);
            itemName = (MTextView) view.findViewById(R.id.itemName);
            itemImg = (ImageView) view.findViewById(R.id.itemImg);
            btn_review = ((MaterialRippleLayout) view.findViewById(R.id.btn_review)).getChildView();
            btn_payment = (MTextView) view.findViewById(R.id.btn_payment);
            btn_viewDetails = (MTextView) view.findViewById(R.id.btn_viewDetails);
            infoImg = (ImageView) view.findViewById(R.id.infoImg);

        }
    }
}
