package com.adapter.files.deliverAll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.solicity.user.R;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SafetyDialog;
import com.general.files.StartActProcess;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.anim.loader.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class RestaurantSearchAdapter extends RecyclerView.Adapter<RestaurantSearchAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> resArrList;
    Context mContext;
    GeneralFunctions generalFunctions;
    public RestaurantOnClickListener restaurantOnClickListener;
    boolean isFavStoreEnable = false;
    String userProfileJson = "";

    int enabledColor, disabledColor, appCompactColor;
    PorterDuff.Mode porterDuffMode;
    int dimension;
    String ENABLE_STORE_WISE_BANNER;
    JSONObject userProfileJsonObj;

    public RestaurantSearchAdapter(Context context, ArrayList<HashMap<String, String>> mapArrayList) {
        this.mContext = context;
        this.resArrList = mapArrayList;
        generalFunctions = MyApp.getInstance().getGeneralFun(context);
        userProfileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);
        userProfileJsonObj = generalFunctions.getJsonObject(generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON));
        ENABLE_STORE_WISE_BANNER  = generalFunctions.getJsonValueStr("ENABLE_STORE_WISE_BANNER", userProfileJsonObj);
        String ENABLE_FAVORITE_STORE_MODULE = generalFunctions.getJsonValue("ENABLE_FAVORITE_STORE_MODULE", userProfileJson);
        isFavStoreEnable = ENABLE_FAVORITE_STORE_MODULE.equalsIgnoreCase("Yes");
        enabledColor = mContext.getResources().getColor(R.color.itemgray);
        disabledColor = mContext.getResources().getColor(R.color.gray);
        appCompactColor = ContextCompat.getColor(mContext, R.color.gray);
        porterDuffMode = PorterDuff.Mode.SRC_IN;
        dimension = mContext.getResources().getDimensionPixelSize(R.dimen.restaurant_img_size_home_screen);

    }

    @Override
    public RestaurantSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_list_search_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantSearchAdapter.ViewHolder holder, int position) {

        HashMap<String, String> mapData = resArrList.get(position);

        holder.restaurantNameTxt.setText(mapData.get("vCompany"));
        holder.restaurantRateTxt.setText(mapData.get("vAvgRatingConverted"));
        holder.RestCuisineTXT.setText(mapData.get("Restaurant_Cuisine"));
        holder.pricePerPersonTxt.setText(mapData.get("Restaurant_PricePerPersonConverted"));
        holder.deliveryTimeTxt.setText(mapData.get("Restaurant_OrderPrepareTimeConverted"));
        holder.deliveryLBLTimeTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_DELIVERY_TIME"));

        if (!mapData.get("Restaurant_OfferMessage_short").equalsIgnoreCase("")) {
            holder.offerArea.setVisibility(View.VISIBLE);
            holder.offerTxt.setText(mapData.get("Restaurant_OfferMessage_shortConverted"));
            holder.offerTxt.setSelected(true);
        } else {
            holder.offerArea.setVisibility(View.GONE);
        }

        String image = mapData.get("vImage");
        String vImage = Utils.checkText(image) ? image : "http";

        if (Utils.checkText(vImage)) {
            String imageURL = Utils.getResizeImgURL(mContext, vImage, dimension, dimension);
            Picasso.get()
                    .load(imageURL).placeholder(R.mipmap.ic_no_icon).error(mContext.getResources().getDrawable(R.mipmap.ic_no_icon))
                    .into(holder.vImageImgView);
        }

        String fav = mapData.get("eFavStore");
        //Fav Store Features
        if (isFavStoreEnable && Utils.checkText(fav) && fav.equalsIgnoreCase("Yes")) {
            holder.favImage.setVisibility(View.VISIBLE);

        } else {
            holder.favImage.setVisibility(View.GONE);
        }
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (restaurantOnClickListener != null) {
                    Log.e("resSize", "clickNotnull");
                    restaurantOnClickListener.setOnRestaurantclick(position, true);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (restaurantOnClickListener != null) {
                    Log.e("resSize", "clickNotnull");
                    restaurantOnClickListener.setOnRestaurantclick(position, false);
                }
            }
        });

        if (mapData.get("Restaurant_Status").equalsIgnoreCase("Closed")) {


            holder.restaurantNameTxt.setTextColor(disabledColor);

            //  holder.offerTxt.setTextColor(disabledColor);
            holder.RestCuisineTXT.setTextColor(disabledColor);
            holder.pricePerPersonTxt.setTextColor(disabledColor);
            holder.deliveryTimeTxt.setTextColor(disabledColor);
            holder.minOrderTxt.setTextColor(disabledColor);
            // holder.offerImage.setColorFilter(appCompactColor, porterDuffMode);
            holder.timerImage.setColorFilter(appCompactColor, porterDuffMode);


            holder.resStatusTxt.setVisibility(View.VISIBLE);
            if (!mapData.get("Restaurant_Opentime").equalsIgnoreCase("")) {
                holder.resStatusTxt.setText(mapData.get("LBL_CLOSED_TXT") + ": " + mapData.get("Restaurant_OpentimeConverted"));
            } else {
                holder.resStatusTxt.setText(mapData.get("LBL_CLOSED_TXT"));
            }


            if (mapData.get("eAvailable").equalsIgnoreCase("No")) {
                holder.resStatusTxt.setText(mapData.get("LBL_NOT_ACCEPT_ORDERS_TXT"));


            }
            holder.resStatusTxt.setTextColor(mContext.getResources().getColor(R.color.red));

        } else {
            holder.resStatusTxt.setVisibility(View.GONE);

            holder.restaurantNameTxt.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.RestCuisineTXT.setTextColor(enabledColor);
            holder.pricePerPersonTxt.setTextColor(enabledColor);
            holder.deliveryTimeTxt.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.minOrderTxt.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_1));
            //  holder.offerImage.setColorFilter(ContextCompat.getColor(mContext, R.color.offerColor), porterDuffMode);
            holder.timerImage.setColorFilter(ContextCompat.getColor(mContext, R.color.appThemeColor_1), porterDuffMode);
        }

        if (generalFunctions.getServiceId().equals("1") || generalFunctions.getServiceId().equalsIgnoreCase("")){
            holder.pricePerPersonTxt.setVisibility(View.VISIBLE);
        }else {
            holder.pricePerPersonTxt.setVisibility(View.GONE);
        }

        holder.minOrderTxt.setText(mapData.get("Restaurant_MinOrderValue"));
        holder.minOrderLBLTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_MIN_ORDER_TXT"));
        if (Utils.getText(holder.minOrderTxt).equals("")) {
            holder.minOrderLBLTxt.setVisibility(View.GONE);
        } else {
            holder.minOrderLBLTxt.setVisibility(View.VISIBLE);
        }

        String url = mapData.get("Restaurant_Safety_URL");
        if ((mapData.get("Restaurant_Safety_Status") != null && mapData.get("Restaurant_Safety_Status").equalsIgnoreCase("Yes"))) {

            holder.safetylayout.setVisibility(View.VISIBLE);
            holder.resSsafetyTxt.setText(generalFunctions.retrieveLangLBl("","LBL_SAFETY_NOTE_TITLE_LIST"));


                int grid = mContext.getResources().getDimensionPixelSize(R.dimen.fab_margin);
                String imageURL = Utils.getResizeImgURL(mContext, mapData.get("Restaurant_Safety_Icon"), grid, grid);
                Picasso.get().load(imageURL).placeholder(R.drawable.ic_safety).error(R.drawable.ic_safety).into(holder.safetyImage);




            holder.resSsafetyTxt.setOnClickListener(v -> {
                Intent intent = new Intent(mContext,SafetyDialog.class);
                intent.putExtra("URL", url);
                new StartActProcess(mContext).startAct(intent);
                ((Activity)mContext).overridePendingTransition( R.anim.bottom_up, R.anim.bottom_down );}/*(new SafetyDialog(mContext)).open(url, imageURL)*/);

        }else {
            holder.safetylayout.setVisibility(View.GONE);
        }

        holder.restaurantAdptrLayout.setOnClickListener(v -> {
            Log.e("resSize", "click");
            if (restaurantOnClickListener != null) {
                Log.e("resSize", "clickNotnull");
                restaurantOnClickListener.setOnRestaurantclick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return resArrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MTextView restaurantRateTxt;
        MTextView restaurantNameTxt, deliveryTimeTxt, minOrderTxt, RestCuisineTXT, pricePerPersonTxt, offerTxt, deliveryLBLTimeTxt, minOrderLBLTxt;
        MTextView resStatusTxt;
        SelectableRoundedImageView imgView;
        LinearLayout restaurantAdptrLayout, offerArea;
        ImageView offerImage, timerImage, vImageImgView;
        //Fav Store Features
        LikeButton likeButton;
        ImageView favImage;
        MTextView resSsafetyTxt;
        ImageView safetyImage;
        LinearLayout safetylayout;
        public ViewHolder(View itemView) {
            super(itemView);

            imgView = (SelectableRoundedImageView) itemView.findViewById(R.id.imgView);
            restaurantNameTxt = itemView.findViewById(R.id.restaurantNameTxt);
            resStatusTxt = itemView.findViewById(R.id.resStatusTxt);
            restaurantRateTxt = itemView.findViewById(R.id.restaurantRateTxt);
            RestCuisineTXT = itemView.findViewById(R.id.RestCuisineTXT);
            offerTxt = itemView.findViewById(R.id.offerTxt);
            pricePerPersonTxt = itemView.findViewById(R.id.pricePerPersonTxt);
            deliveryTimeTxt = itemView.findViewById(R.id.deliveryTimeTxt);
            deliveryLBLTimeTxt = itemView.findViewById(R.id.deliveryLBLTimeTxt);
            minOrderTxt = itemView.findViewById(R.id.minOrderTxt);
            minOrderLBLTxt = itemView.findViewById(R.id.minOrderLBLTxt);
            restaurantAdptrLayout = itemView.findViewById(R.id.restaurantAdptrLayout);
            offerArea = itemView.findViewById(R.id.offerArea);
            offerImage = itemView.findViewById(R.id.offerImage);
            timerImage = itemView.findViewById(R.id.timerImage);
            vImageImgView = itemView.findViewById(R.id.imgView);
            favImage = itemView.findViewById(R.id.favImage);
            likeButton = (LikeButton) itemView.findViewById(R.id.likeButton);

            resSsafetyTxt = itemView.findViewById(R.id.resSsafetyTxt);
            safetyImage = itemView.findViewById(R.id.safetyImage);
            safetylayout = itemView.findViewById(R.id.safetylayout);
        }
    }

    public interface RestaurantOnClickListener {
        void setOnRestaurantclick(int position);

        //Fav Store Features
        void setOnRestaurantclick(int position, boolean liked);
    }

    public void setOnRestaurantItemClick(RestaurantOnClickListener onRestaurantItemClick) {
        this.restaurantOnClickListener = onRestaurantItemClick;
    }
}
