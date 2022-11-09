package com.adapter.files.deliverAll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.solicity.user.R;
import com.general.files.GeneralFunctions;
import com.model.MainCategoryModel;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder>  {

    Context mContext;
    ArrayList<MainCategoryModel> list;
    GeneralFunctions generalFunc;
    CategoryOnClickListener categoryOnClickListener;
    RestaurantChildAdapter.RestaurantOnClickListener restaurantOnClickListener;
    RestaurantChildAdapter restaurantAdapter;
    ArrayList<HashMap<String, String>> childList;
    String LBL_SEE_ALL;

    public MainCategoryAdapter(Context mContext, ArrayList<MainCategoryModel> list, GeneralFunctions generalFunc, CategoryOnClickListener categoryOnClickListener, RestaurantChildAdapter.RestaurantOnClickListener restaurantOnClickListener) {
        // public MainCategoryAdapter(Context mContext, ArrayList<MainCategoryModel> list, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.categoryOnClickListener = categoryOnClickListener;
        this.restaurantOnClickListener = restaurantOnClickListener;
        LBL_SEE_ALL = generalFunc.retrieveLangLBl("", "LBL_SEE_ALL");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_child, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.catetitleTxt.setText(list.get(position).getvTitle());
        if (list.get(position).getvDescription()!=null && !list.get(position).getvDescription().isEmpty()){
            holder.catedescTxt.setText(list.get(position).getvDescription());
            holder.catedescTxt.setVisibility(View.VISIBLE);
        }else {
            holder.catedescTxt.setVisibility(View.GONE);

        }

        holder.seeAllTxt.setText(LBL_SEE_ALL);


        if (list.get(position).getIsShowall().equalsIgnoreCase("Yes")) {
            holder.seeAllTxt.setVisibility(View.VISIBLE);
            holder.seeAllImg.setVisibility(View.VISIBLE);


        } else {
            holder.seeAllTxt.setVisibility(View.GONE);
            holder.seeAllImg.setVisibility(View.GONE);


        }


        if (list.get(position).getvCategoryImage() != null) {
            String imageUrl = list.get(position).getvCategoryImage();
            if (list.get(position).getvCategoryImage().equalsIgnoreCase("")) {
                imageUrl = "Temp";
            }

            Picasso.get()
                    .load(Utils.getResizeImgURL(mContext, imageUrl, holder.item_img.getWidth(), holder.item_img.getHeight()))
                    .placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon)
                    .into(holder.item_img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });


            childList = new ArrayList<>();

            if (list.get(position).geteType().equalsIgnoreCase("list_all")) {
                restaurantAdapter = new RestaurantChildAdapter(mContext, list.get(position).getList(), position, false);
                restaurantAdapter.setOnRestaurantItemClick(restaurantOnClickListener);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                holder.child_category_rec.setLayoutManager(layoutManager);
                holder.child_category_rec.setAdapter(restaurantAdapter);
            } else {
                restaurantAdapter = new RestaurantChildAdapter(mContext, list.get(position).getList(), position, false);
                restaurantAdapter.setOnRestaurantItemClick(restaurantOnClickListener);
                GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, list.get(position).getList().size() > 1 ? 2 : 1, GridLayoutManager.HORIZONTAL, false);

                holder.child_category_rec.setLayoutManager(mLayoutManager);
                holder.child_category_rec.setAdapter(restaurantAdapter);
            }
        }

        if (generalFunc.isRTLmode()) {
            holder.seeAllImg.setRotation(180);
        }

        holder.seeAllTxt.setOnClickListener(view -> categoryOnClickListener.setOnCategoryClick(position));
        holder.seeAllImg.setOnClickListener(view -> categoryOnClickListener.setOnCategoryClick(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface CategoryOnClickListener {
        void setOnCategoryClick(int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        MTextView catetitleTxt;
        MTextView seeAllTxt;
        MTextView catedescTxt;
        ImageView seeAllImg;

        ImageView item_img;
        RecyclerView child_category_rec;

        public ViewHolder(View itemView) {
            super(itemView);
            catetitleTxt = itemView.findViewById(R.id.catetitleTxt);
            catedescTxt = itemView.findViewById(R.id.catedescTxt);
            seeAllTxt = itemView.findViewById(R.id.seeAllTxt);
            seeAllImg = itemView.findViewById(R.id.seeAllImg);
            item_img = itemView.findViewById(R.id.item_img);
            child_category_rec = itemView.findViewById(R.id.child_category_rec);
        }
    }
}
