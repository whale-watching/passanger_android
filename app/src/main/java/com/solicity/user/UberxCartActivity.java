package com.solicity.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapter.files.CategoryListItem;
import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.MyClickableSpan;
import com.general.files.StartActProcess;
import com.realmModel.CarWashCartData;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class UberxCartActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    MTextView titleTxt, headingTxt, commentHname;
    ImageView backImgView;
    String userProfileJson;
    CategoryListItem categoryListItem;
    MaterialEditText commentBox;
    LinearLayout fareDetailDisplayArea;
    MButton btn_type2;
    int submitBtnId;
    ImageView minusImgView, addImgView;
    MTextView QTYNumberTxtView;
    MTextView descTxt;
    String iMaxQty = "";
    String eAllowQty = "";
    String eFareType = "";
    String vSymbol = "";
    LinearLayout qtyArea;
    RealmResults<CarWashCartData> realmCartList;
    MTextView removeCartTxt;

    String finalTotal = "";
    String finalTotalNw = "";
    int qty = 1;
    CarWashCartData cart = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uberx_cart);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        categoryListItem = (CategoryListItem) getIntent().getSerializableExtra("data");
        realmCartList = getCartData();
        Realm realm = MyApp.getRealmInstance();
        cart = checksameRecordExist(realm);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        removeCartTxt = (MTextView) findViewById(R.id.removeCartTxt);
        removeCartTxt.setText(generalFunc.retrieveLangLBl("Remove From Cart", "LBL_UFX_REMOVE_FROM_CART"));
        removeCartTxt.setOnClickListener(new setOnClickList());
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));
        commentHname = (MTextView) findViewById(R.id.commentHname);
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        if (cart != null) {
            qty = GeneralFunctions.parseIntegerValue(0, cart.getItemCount());
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_UFX_EDIT_CART"));
            commentBox.setText(cart.getSpecialInstruction());
            removeCartTxt.setVisibility(View.VISIBLE);
        }
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        headingTxt = (MTextView) findViewById(R.id.headingTxt);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        descTxt=(MTextView) findViewById(R.id.descTxt);
        minusImgView = (ImageView) findViewById(R.id.minusImgView);
        addImgView = (ImageView) findViewById(R.id.addImgView);
        QTYNumberTxtView = (MTextView) findViewById(R.id.QTYNumberTxtView);
        QTYNumberTxtView.setText(qty + "");
        qtyArea = (LinearLayout) findViewById(R.id.qtyArea);
        minusImgView.setOnClickListener(new setOnClickList());
        addImgView.setOnClickListener(new setOnClickList());
        fareDetailDisplayArea = (LinearLayout) findViewById(R.id.fareDetailDisplayArea);
        commentBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        commentBox.setSingleLine(false);
        commentBox.setHideUnderline(true);
        commentBox.setGravity(Gravity.START | Gravity.TOP);
        commentBox.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        commentBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_fb_border));
        if (generalFunc.isRTLmode()) {
            commentBox.setPaddings((int) getResources().getDimension(R.dimen._5sdp), 0, (int) getResources().getDimension(R.dimen._10sdp), 0);
        } else {
            commentBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
        }
        commentBox.setIncludeFontPadding(false);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        titleTxt.setText(generalFunc.retrieveLangLBl("Cart", "LBL_UFX_CART"));
        commentHname.setText(generalFunc.retrieveLangLBl("", "LBL_INS_PROVIDER_BELOW"));

        headingTxt.setText(categoryListItem.getvTitle());

        boolean viewMoreSet = false;

        if (!categoryListItem.getvDesc().trim().equals("")) {
            String vShortDesc=categoryListItem.getvShortDesc();
            Spanned descdata = AppFunctions.fromHtml(Utils.checkText(vShortDesc)?vShortDesc:categoryListItem.getvDesc());
            descTxt.setText(AppFunctions.fromHtml(descdata+""));
            if (viewMoreSet == false) {
                generalFunc.makeTextViewResizable((MTextView) findViewById(R.id.descTxt), 2, "...\n+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, R.color.appThemeColor_1, R.dimen.txt_size_16);
                 // Enable this if you have html data print issue
                //                makeTextViewResizable(descTxt, 2, "...\n+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, R.color.appThemeColor_1, R.dimen.txt_size_16);
                viewMoreSet = true;
            }
        } else {
            (findViewById(R.id.descTxt)).setVisibility(View.GONE);
        }


        getDetails();

    }
    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore, final int color, final int dimension) {
        if (tv.getTag() == null) {
            HashMap var7 = new HashMap();
            var7.put("TitleOfTextView", tv.getText().toString());
            var7.put("OrigMaxLines", "" + maxLine);
            tv.setTag(var7);
        }

        ViewTreeObserver var8 = tv.getViewTreeObserver();
        var8.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver var1 = tv.getViewTreeObserver();
                var1.removeGlobalOnLayoutListener(this);
                int var2;
                String var3;
                if (maxLine == 0) {
                    var2 = tv.getLayout().getLineEnd(0);
                    var3 = tv.getText().subSequence(0, var2 - expandText.length() + 1) + " " + expandText;
                    tv.setText(var3);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(a(tv.getText().toString(), tv, maxLine, expandText, viewMore, color, dimension, (GeneralFunctions.ResizableTexViewClickListener)null), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    var2 = tv.getLayout().getLineEnd(maxLine - 1);
                    var3 = tv.getText().subSequence(0, var2 - expandText.length() + 1) + " " + expandText;
                    tv.setText(var3);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(a(tv.getText().toString(), tv, maxLine, expandText, viewMore, color, dimension, (GeneralFunctions.ResizableTexViewClickListener)null), TextView.BufferType.SPANNABLE);
                } else if (tv.getLineCount() > maxLine) {
                    var2 = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    var3 = tv.getText().subSequence(0, var2) + " " + expandText;
                    tv.setText(var3);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(a(tv.getText().toString(), tv, var2, expandText, viewMore, color, dimension, (GeneralFunctions.ResizableTexViewClickListener)null), TextView.BufferType.SPANNABLE);
                }

            }
        });
    }

    private SpannableStringBuilder a(String var1, final TextView var2, int var3, String var4, final boolean var5, int var6, int var7, final GeneralFunctions.ResizableTexViewClickListener var8) {
        SpannableStringBuilder var9 = new SpannableStringBuilder(var1);
        if (var1.contains(var4)) {
            var9.setSpan(new MyClickableSpan(this, var6, var7) {
                public void onClick(View widget) {
                    if (var8 != null) {
                        var8.onResizableTextViewClick(var5);
                    } else {
                        if (var2.getTag() != null && var2.getTag() instanceof HashMap) {
                            HashMap var2x = (HashMap)var2.getTag();
                            if (var5) {
//                                descTxt.performClick();
                                Bundle bn =new Bundle();
                                bn.putString("staticData", categoryListItem.getvDesc());
                                bn.putString("vTitle", categoryListItem.getvTitle());
                                new StartActProcess(getActContext()).startActWithData(StaticPageActivity.class, bn);

                               /* var2.setLayoutParams(var2.getLayoutParams());
                                var2.setText(var2x.get("TitleOfTextView").toString(), TextView.BufferType.SPANNABLE);
                                var2.invalidate();
                                makeTextViewResizable(var2, -5, "\n- " + generalFunc.retrieveLangLBl("Less", "LBL_LESS_TXT"), false, var6, var7);*/
                            } else {
                                var2.setLayoutParams(var2.getLayoutParams());
                                var2.setText(var2x.get("TitleOfTextView").toString(), TextView.BufferType.SPANNABLE);
                                var2.invalidate();
                                makeTextViewResizable(var2, GeneralFunctions.parseIntegerValue(2, var2x.get("OrigMaxLines").toString()), "...\n+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, var6, var7);
                            }
                        }

                    }
                }
            }, var1.indexOf(var4), var1.indexOf(var4) + var4.length(), 0);
        }

        return var9;
    }

    public RealmResults<CarWashCartData> getCartData() {
        try {
            Realm realm = MyApp.getRealmInstance();
            return realm.where(CarWashCartData.class).findAll();
        } catch (Exception e) {
            Logger.d("RealmException", "::" + e.toString());

        }
        return null;
    }

    private void addFareDetailLayout(JSONArray jobjArray, int qty) {

        if (FareDetailsArrNewObj == null) {
            return;
        }

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                String data = jobject.names().getString(0);

                addFareDetailRow(data, jobject.get(data).toString(), (jobjArray.length() - 1) == i ? true : false, qty);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast, int qty) {
        View convertView = null;
        Typeface font = Typeface.createFromAsset(getActContext().getResources().getAssets(), "fonts/Poppins_SemiBold.ttf");
        int Size_20 = (int) getActContext().getResources().getDimension(R.dimen._20sdp);
        int Size_15 = (int) getActContext().getResources().getDimension(R.dimen._15sdp);
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            params.setMarginStart(Size_20);
            params.setMarginEnd(Size_20);
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_deatil_row, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.topMargin=Size_15;
            convertView.setLayoutParams(params);
            convertView.setPaddingRelative(Size_20, 0, Size_20, 0);
            convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), Size_15));

            MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
            MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);

            Logger.d("FinalTotalDataRowVal","::"+row_value+"::"+qty);
            String valuAfter=row_value.replace(vSymbol, "");
            if (!eFareType.equalsIgnoreCase("Fixed")) {
           //    valuAfter= valuAfter.replace(",", ".");
            }
            double qtyWiseVal=0;

            try {
                 qtyWiseVal = GeneralFunctions.parseDoubleValue(0, valuAfter) * qty;
                Logger.d("FinalTotalDataRowVal", "::after::" + Double.parseDouble(valuAfter));
            }
            catch (Exception e)
            {
                Logger.d("FinalTotalDataRowVal","::Error::"+e.toString());
                valuAfter=valuAfter.replace(",","");
                qtyWiseVal = GeneralFunctions.parseDoubleValue(0, valuAfter) * qty;

            }




            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            if (eAllowQty.equalsIgnoreCase("Yes")) {
                titleVTxt.setText(new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,""+generalFunc.convertNumberWithRTL( generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(qtyWiseVal))),vSymbol,true));
//                titleVTxt.setText(generalFunc.convertNumberWithRTL(vSymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(qtyWiseVal))));
            } else {
                titleVTxt.setText(eFareType.equalsIgnoreCase("Fixed")?new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,""+generalFunc.convertNumberWithRTL( generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(qtyWiseVal))),vSymbol,true):row_value);
            }

            titleHTxt.setTextColor(Color.parseColor("#656565"));
            titleVTxt.setTextColor(Color.parseColor("#1c1c1c"));
            if (isLast) {

                titleVTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActContext().getResources().getDimension(R.dimen._15sdp));
                titleHTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActContext().getResources().getDimension(R.dimen._15sdp));

                titleHTxt.setTextColor(Color.parseColor("#000000"));
                titleHTxt.setTypeface(font);
                titleVTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
                titleVTxt.setTypeface(font);

            }
           /* if (isLast) {
                finalTotal = vSymbol + " " + qtyWiseVal;
            }*/

            Logger.d("FinalTotalDataRowVal","::11::"+qtyWiseVal);
            String qtyWiseValNw = "" + new BigDecimal(qtyWiseVal).setScale(2,
                    BigDecimal.ROUND_HALF_UP);

            Logger.d("FinalTotalDataRowVal","::22::"+qtyWiseValNw);
            if (isLast) {
                finalTotal = vSymbol + qtyWiseValNw;
                finalTotalNw=new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,""+qtyWiseValNw,vSymbol,true);
            }
        }

        if (convertView != null)
            fareDetailDisplayArea.addView(convertView);

        findViewById(R.id.fareArea).setVisibility(View.VISIBLE);
    }

    JSONArray FareDetailsArrNewObj = null;

    public void getDetails() {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getVehicleTypeDetails");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("SelectedCabType", Utils.CabGeneralType_UberX);
        parameters.put("iVehicleTypeId", categoryListItem.getiVehicleTypeId());
        parameters.put("iDriverId", getIntent().getStringExtra("iDriverId"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setCancelAble(false);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                if (isDataAvail) {

                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    iMaxQty = generalFunc.getJsonValue("iMaxQty", message);
                    eAllowQty = generalFunc.getJsonValue("eAllowQty", message);
                    eFareType = generalFunc.getJsonValue("eFareType", message);
                    vSymbol = generalFunc.getJsonValue("vSymbol", message);
                    if (eAllowQty.equalsIgnoreCase("Yes")) {
                        qtyArea.setVisibility(View.VISIBLE);
                    }


                    FareDetailsArrNewObj = generalFunc.getJsonArray("fareDetails", message);


                    addFareDetailLayout(FareDetailsArrNewObj, qty);


                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message", responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return UberxCartActivity.this;
    }

    boolean isCartNull;

    public void setRealmData() {
        Realm realm = MyApp.getRealmInstance();
        CarWashCartData cart = checksameRecordExist(realm);

        realm.beginTransaction();
        if (cart == null) {
            isCartNull = true;
            cart = new CarWashCartData();
            cart.setCategoryListItem(categoryListItem);
            cart.setDriverId(getIntent().getStringExtra("iDriverId"));
            cart.setSpecialInstruction(Utils.getText(commentBox));
            cart.setItemCount(QTYNumberTxtView.getText().toString().trim());
            cart.setFinalTotal(finalTotal);
            Logger.d("FinalTotalDataSet","::"+finalTotal);

            cart.setFinalTotalNw(finalTotalNw);
            cart.setvSymbol(vSymbol);
            if (isCartNull) {
                realm.insert(cart);
            } else {
                realm.insertOrUpdate(cart);
            }

        } else {
            cart.setItemCount(QTYNumberTxtView.getText().toString().trim());
            cart.setFinalTotal(finalTotal);
            Logger.d("FinalTotalDataSet","::"+finalTotal);
            cart.setFinalTotalNw(finalTotalNw);
            realm.insertOrUpdate(cart);
        }
        realm.commitTransaction();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public CarWashCartData checksameRecordExist(Realm realm) {
        CarWashCartData cart = null;

        RealmResults<CarWashCartData> cartlist = realm.where(CarWashCartData.class).findAll();

        if (cartlist != null && cartlist.size() > 0)

            for (int i = 0; i < realmCartList.size(); i++) {

                if (categoryListItem.getiVehicleTypeId().equalsIgnoreCase(realmCartList.get(i).getCategoryListItem().getiVehicleTypeId())) {
                    return realmCartList.get(i);
                }
            }


        return cart;
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(UberxCartActivity.this);
            if (view == backImgView) {
                onBackPressed();
            } else if (view == btn_type2) {
                setRealmData();

            }else if (view == descTxt) {
                Bundle bn =new Bundle();
                bn.putString("staticData", categoryListItem.getvDesc());
                bn.putString("vTitle", categoryListItem.getvTitle());
                new StartActProcess(getActContext()).startActWithData(StaticPageActivity.class, bn);
            } else if (view == addImgView) {


                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());


                if (QUANTITY < GeneralFunctions.parseIntegerValue(0, iMaxQty)) {

                    if (QUANTITY >= 1) {
                        QUANTITY = QUANTITY + 1;

                        QTYNumberTxtView.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));
                        addFareDetailLayout(FareDetailsArrNewObj, QUANTITY);
                    }
                } else {
                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_QUANTITY_CLOSED_TXT"));
                }

            } else if (view == minusImgView) {

                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
                if (QUANTITY > 1) {
                    QUANTITY = QUANTITY - 1;
                    QTYNumberTxtView.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));

                    addFareDetailLayout(FareDetailsArrNewObj, QUANTITY);
                }

            } else if (view == removeCartTxt) {
                Realm realm = MyApp.getRealmInstance();
                realm.beginTransaction();
                cart.deleteFromRealm();
                realm.commitTransaction();
                finish();


            }
        }

    }
}
