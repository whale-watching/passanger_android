package com.adapter.files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.general.files.IssueWebDialog;
import com.general.files.SafetyDialog;
import com.general.files.StartActProcess;
import com.model.DeliveryIconDetails;
import com.solicity.user.R;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ComplaintHistoryAdapter extends RecyclerView.Adapter<ComplaintHistoryAdapter.ViewHolder> {

    GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> listData;
    Context mContext;

    int width;

    public ComplaintHistoryAdapter(GeneralFunctions generalFunc, ArrayList<HashMap<String, String>> listData, Context mContext) {
        this.generalFunc = generalFunc;
        this.listData = listData;
        this.mContext = mContext;
        width = (int) ((Utils.getScreenPixelWidth(mContext) - Utils.dipToPixels(mContext, 16)) / 2);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_history_item, parent, false);

        ComplaintHistoryAdapter.ViewHolder viewHolder = new ComplaintHistoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HashMap<String, String> data = listData.get(position);


        holder.decriptionTxt.setText(data.get("tDescription"));

        holder.statusTxt.setText(data.get("eMarkedAs"));

        holder.statusTxt.setTextColor(Color.parseColor(data.get("ColorCode")));

        holder.dateTxt.setText(data.get("LBL_CREATED") + " " + data.get("tAddedDate"));

        holder.cityTxt.setText(data.get("vCity"));
        holder.categoryTxt.setText(data.get("vCategory"));
        if (data.get("isVideoUpload").equalsIgnoreCase("yes")) {
            holder.videoLinkImg.setVisibility(View.VISIBLE);
        } else {
            holder.videoLinkImg.setVisibility(View.GONE);
        }
        if (data.get("isAudioUpload").equalsIgnoreCase("yes")) {
            holder.audioLinkImg.setVisibility(View.VISIBLE);
        } else {
            holder.audioLinkImg.setVisibility(View.GONE);
        }

        holder.cameraLinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mContext, IssueWebDialog.class);
                intent.putExtra("URL", data.get("isDataURL") + "&isImageUpload=Yes");
                new StartActProcess(mContext).startAct(intent);

            }
        });
        holder.videoLinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mContext, IssueWebDialog.class);
                intent.putExtra("URL", data.get("isDataURL") + "&isVideoUpload=Yes");
                new StartActProcess(mContext).startAct(intent);
            }
        });
        holder.audioLinkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, IssueWebDialog.class);
                intent.putExtra("URL", data.get("isDataURL") + "&isAudioUpload=Yes");
                new StartActProcess(mContext).startAct(intent);
            }
        });

        if (data.get("isSolvedDataURL").equalsIgnoreCase("")) {
            holder.adminCommentTxt.setVisibility(View.GONE);
        } else {
            holder.adminCommentTxt.setVisibility(View.VISIBLE
            );

        }

        holder.adminCommentTxt.setText(data.get("LBL_ADMIN_COMMENT"));

        holder.adminCommentTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, IssueWebDialog.class);
                intent.putExtra("URL", data.get("isSolvedDataURL"));
                new StartActProcess(mContext).startAct(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {


        private MTextView categoryTxt;
        private MTextView decriptionTxt;
        private MTextView statusTxt;
        private MTextView cityTxt;
        private MTextView dateTxt;
        private ImageView cameraLinkImg;
        private ImageView videoLinkImg;
        private ImageView audioLinkImg;
        private MTextView adminCommentTxt;


        public ViewHolder(View view) {
            super(view);


            adminCommentTxt = (MTextView) view.findViewById(R.id.adminCommentTxt);
            categoryTxt = (MTextView) view.findViewById(R.id.categoryTxt);
            decriptionTxt = (MTextView) view.findViewById(R.id.decriptionTxt);
            statusTxt = (MTextView) view.findViewById(R.id.statusTxt);
            cityTxt = (MTextView) view.findViewById(R.id.cityTxt);
            dateTxt = (MTextView) view.findViewById(R.id.dateTxt);
            cameraLinkImg = (ImageView) view.findViewById(R.id.cameraLinkImg);
            videoLinkImg = (ImageView) view.findViewById(R.id.videoLinkImg);
            audioLinkImg = (ImageView) view.findViewById(R.id.audioLinkImg);
            adminCommentTxt.setPaintFlags(adminCommentTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        }
    }
}
