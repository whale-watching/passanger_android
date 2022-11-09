package com.solicity.user.deliverAll

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.*
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import com.solicity.user.*
import com.flutterwave.raveandroid.RaveConstants
import com.flutterwave.raveandroid.RavePayManager
import com.general.files.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.utils.CommonUtilities
import com.utils.Utils
import com.view.GenerateAlertBox
import com.view.MButton
import com.view.MTextView
import com.view.MaterialRippleLayout
import com.view.editBox.MaterialEditText
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BuyAnythingActivity : BaseActivity() {
    var backImgView: ImageView? = null
    var storeImg: ImageView? = null
    var fareDetailArea: LinearLayout? = null
    var howtoworkArea: LinearLayout? = null
    lateinit var storesearcharrow: ImageView
    lateinit var additemarrow: ImageView
    var addressArea: LinearLayout? = null
    var submitBtnArea: LinearLayout? = null
    var addBtnArea: LinearLayout? = null
    lateinit var changeTxt: MTextView
    lateinit var searchStoreArea: LinearLayout
    var addedStoreArea: LinearLayout? = null
    var addOrRemoveStoreArea: CardView? = null
    var noContactDeliveryNote: LinearLayout? = null
    var fareArea: LinearLayout? = null
    var iv_removeStore: ImageView? = null
    var addressType: MTextView? = null
    var addressTxt: MTextView? = null
    var StoreNameTxt: MTextView? = null
    var addressStoreTxt: MTextView? = null
    var minTxt: MTextView? = null
    var kmTxt: MTextView? = null
    var howbtnTxt: MTextView? = null
    var itemTitleTxt: MTextView? = null
    var NotesTxt: MTextView? = null
    lateinit var generalFunc: GeneralFunctions
    private var storelatitude: String? = null
    private var storelongitude: String? = null
    private var deliverylatitude: String? = null
    private var deliverylongitude: String? = null
    var isAddressAdded = false
    private var StoreLatitude: String? = null
    private var Storelati = 0.0
    private var Storelong = 0.0
    private var StoreLongitude: String? = null
    private var StoreAddress: String? = null
    lateinit var btn_type2: MButton
    lateinit var btn_type_submit: MButton
    lateinit var disableArea: RelativeLayout
    var submitBtnId = 0
    private val SEL_STORE = 1
    private val SEL_ADDRESS = 2
    var ePaymentOption = "Cash"
    lateinit var selectedMethod: String
    lateinit var isWalletSelect: String
    var internetConnection: InternetConnection? = null
    var SITE_TYPE = ""
    var userProfileJson: String? = null
    private var SYSTEM_PAYMENT_FLOW = ""
    var eWalletIgnore = "No"
    var APP_PAYMENT_METHOD: String = ""
    var isCODAllow = true
    var outStandingAmount = ""

    //add item view
    var list = ArrayList<HashMap<String, String>>()
    lateinit var additemTxt: MTextView
    lateinit var additemarea: LinearLayout
    lateinit var itemContainer: LinearLayout
    var isStoreAdded: Boolean = false
    var selAddressId = ""
    var iOrderId = ""
    var PICK_DROP_GENIE = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_anything)
        generalFunc = MyApp.getInstance().getGeneralFun(actContext)
        PICK_DROP_GENIE = intent.getBooleanExtra("PICK_DROP_GENIE", false)
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)
        SYSTEM_PAYMENT_FLOW = generalFunc.getJsonValue("SYSTEM_PAYMENT_FLOW", userProfileJson)
        APP_PAYMENT_METHOD = generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson)
        internetConnection = InternetConnection(actContext)
        SITE_TYPE = generalFunc.getJsonValue("SITE_TYPE", userProfileJson)
        backImgView = findViewById<View>(R.id.backImgView) as ImageView
        storeImg = findViewById<View>(R.id.storeImg) as ImageView
        fareDetailArea = findViewById<View>(R.id.fareDetailArea) as LinearLayout
        howtoworkArea = findViewById<View>(R.id.howtoworkArea) as LinearLayout
        storesearcharrow = findViewById<View>(R.id.storesearcharrow) as ImageView
        additemarrow = findViewById<View>(R.id.additemarrow) as ImageView
        if (generalFunc.isRTLmode) {
            howtoworkArea!!.setBackgroundResource(R.drawable.login_border)
            storesearcharrow!!.rotation = 180f
            additemarrow!!.rotation = 180f
        }
        btn_type2 = (findViewById<View>(R.id.btn_type2) as MaterialRippleLayout).getChildView()
        btn_type_submit =
            (findViewById<View>(R.id.btn_type_submit) as MaterialRippleLayout).getChildView()
        submitBtnArea = findViewById<View>(R.id.submitBtnArea) as LinearLayout
        addBtnArea = findViewById<View>(R.id.addBtnArea) as LinearLayout
        submitBtnId = Utils.generateViewId()
        btn_type2.setId(submitBtnId)
        btn_type2.setOnClickListener(setOnClickList())
        btn_type_submit.setOnClickListener(setOnClickList())

        addressArea = findViewById<View>(R.id.addressArea) as LinearLayout
        changeTxt = findViewById<View>(R.id.changeTxt) as MTextView
        changeTxt!!.setOnClickListener(setOnClickList())
        searchStoreArea = findViewById<View>(R.id.searchStoreArea) as LinearLayout
        addedStoreArea = findViewById<View>(R.id.addedStoreArea) as LinearLayout
        addOrRemoveStoreArea = findViewById<View>(R.id.addOrRemoveStoreArea) as CardView
        noContactDeliveryNote = findViewById<View>(R.id.noContactDeliveryNote) as LinearLayout
        disableArea = findViewById<View>(R.id.disableArea) as RelativeLayout
        fareArea = findViewById<View>(R.id.fareArea) as LinearLayout
        iv_removeStore = findViewById<View>(R.id.iv_removeStore) as ImageView

        addressType = findViewById(R.id.addressType)
        addressTxt = findViewById(R.id.addressTxt)
        StoreNameTxt = findViewById(R.id.StoreNameTxt)
        addressStoreTxt = findViewById(R.id.addressStoreTxt)
        StoreNameTxt!!.isSelected = true
        addressStoreTxt!!.isSelected = true
        minTxt = findViewById(R.id.minTxt)
        kmTxt = findViewById(R.id.kmTxt)
        howbtnTxt = findViewById<View>(R.id.howbtnTxt) as MTextView
        itemTitleTxt = findViewById<View>(R.id.itemTitleTxt) as MTextView
        NotesTxt = findViewById<View>(R.id.NotesTxt) as MTextView



        searchStoreArea.setOnClickListener {

            if ((generalFunc.retrieveValue("LBL_STORE_TIMESTAMP")
                    .equals("") || checkHour()) && !PICK_DROP_GENIE
            ) {
                generalFunc.storeData(
                    "LBL_STORE_TIMESTAMP",
                    Calendar.getInstance().getTimeInMillis().toString()
                )

                StoreSelectionIntro()
            } else {


                var bundle = Bundle()
                bundle.putString("iCompanyId", "1")
                bundle.putBoolean("isGenie", true);
                bundle.putBoolean("PICK_DROP_GENIE", PICK_DROP_GENIE);
                try {
                    bundle.putString("latitude", intent.getStringExtra("latitude"))
                    bundle.putString("longitude", intent.getStringExtra("longitude"))
                } catch (e: Exception) {

                }



                StartActProcess(actContext).startActForResult(
                    AddAddressActivity::class.java,
                    bundle,
                    SEL_STORE
                )
            }

        }

        if (generalFunc.isRTLmode()) {
            backImgView!!.rotation = 180f
        }
        backImgView!!.visibility = View.VISIBLE
        if (PICK_DROP_GENIE) {
            (findViewById<View>(R.id.headerTxt) as MTextView).text =
                generalFunc.retrieveLangLBl("", "LBL_PICKUP_PARCEL")
            storeImg!!.setImageDrawable(resources.getDrawable(R.drawable.ic_locate_place))

        } else {
            (findViewById<View>(R.id.headerTxt) as MTextView).text =
                generalFunc.retrieveLangLBl("", "LBL_OTHER_DELIVERY")
        }
        iv_removeStore!!.setOnClickListener(setOnClickList())
        backImgView!!.setOnClickListener(setOnClickList())


        fareDetailArea!!.setOnClickListener(setOnClickList())
        howtoworkArea!!.setOnClickListener(setOnClickList())
        addedStoreArea!!.visibility = View.GONE
        searchStoreArea!!.visibility = View.VISIBLE



        howbtnTxt!!.setText(generalFunc.retrieveLangLBl("", "LBL_HOW_WORKS"))
        NotesTxt!!.setText(generalFunc.retrieveLangLBl("", "LBL_NOTES"))
        itemTitleTxt!!.setText(generalFunc.retrieveLangLBl("", "LBL_MAKE_LIST_ITEMS"))
        StoreNameTxt!!.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_STORE_FOR_ITEM"))

        changeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE"))
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_DELIVERY_ADDRESS"))
        btn_type_submit.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"))

        if (PICK_DROP_GENIE) {
            itemTitleTxt!!.setText(generalFunc.retrieveLangLBl("", "LBL_MAKE_LIST"))
            StoreNameTxt!!.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_PICKUP_ADD"))
        }

        //add item view
        additemTxt = findViewById(R.id.additemTxt) as MTextView
        additemTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM_NAME"))
        additemarea = findViewById(R.id.additemarea) as LinearLayout
        itemContainer = findViewById(R.id.itemContainer) as LinearLayout
        additemarea.setOnClickListener {
            if (list.size == 0) {
                addData(0)
            } else {
                addData(list.size + 1)
            }
        }

    }

    fun checkHour(): Boolean {
        var lastCheckedMillis = generalFunc.retrieveValue("LBL_STORE_TIMESTAMP")
        var now = System.currentTimeMillis();
        var diffMillis = now - lastCheckedMillis.toLong();
        if (diffMillis >= (3600000 * 24)) {
            return true
        }

        return false
    }
    //add item view

    lateinit var contentView: Any
    fun addView(pos: Int) {
        val contentView = View.inflate(this, R.layout.design_add_item, null)

        var itemName: MaterialEditText
        var img_delete: RelativeLayout? = null
        var itemBulletinImg: ImageView? = null
        var addRemoveItemQtyArea: LinearLayout? = null
        var removeQtyArea: LinearLayout? = null
        var mainDataArea: LinearLayout? = null
        var addItemQtyArea: LinearLayout? = null
        var ItemQTY: MTextView? = null
        var ExtraPayTxt: MTextView? = null
        var ExtrasubPayTxt: MTextView? = null
        var extraPayArea: LinearLayout? = null
        var extrapay_chkBox: AppCompatCheckBox? = null
        var layoutShape: LinearLayout? = null
        itemName = contentView.findViewById(R.id.itemName)
        ItemQTY = contentView.findViewById(R.id.ItemQTY)
        ExtraPayTxt = contentView.findViewById(R.id.ExtraPayTxt)
        ExtrasubPayTxt = contentView.findViewById(R.id.ExtrasubPayTxt)
        extraPayArea = contentView.findViewById(R.id.extraPayArea)
        extrapay_chkBox = contentView.findViewById(R.id.extrapay_chkBox)
        addRemoveItemQtyArea = contentView.findViewById(R.id.addRemoveItemQtyArea)
        addItemQtyArea = contentView.findViewById(R.id.addItemQtyArea)
        layoutShape = contentView.findViewById(R.id.layoutShape)
        removeQtyArea = contentView.findViewById(R.id.removeQtyArea)
        img_delete = contentView.findViewById(R.id.img_delete)
        addRemoveItemQtyArea!!.visibility = View.VISIBLE
        itemName.setTag(pos)
        itemName.setHint(generalFunc.retrieveLangLBl("", "LBL_GENIE_ITEM_DETAILS"))
        ExtraPayTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENIE_EXTRA_PAYMENT"))
        ExtrasubPayTxt.setText(
            generalFunc.retrieveLangLBl(
                "Does this require ayment?",
                "LBL_PAYMENT_REQUIRE_SUB_NOTE"
            )
        )
        itemName.setText(list.get(pos).get("ItemName"))
        ItemQTY.setText(list.get(pos).get("ItemQty"))
        itemName.addTextChangedListener(GenericTextWatcher(pos))
        itemName.requestFocus()

        if (PICK_DROP_GENIE) {
            if (list.get(pos).get("eExtraPayment").equals("Yes")) {
                extrapay_chkBox!!.isChecked = true
            } else {
                extrapay_chkBox!!.isChecked = false
            }

        }


        extrapay_chkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->

            if (b) {

                list.get(pos).put("eExtraPayment", "Yes")
            } else {
                list.get(pos).put("eExtraPayment", "No")
            }
        })




        if (generalFunc.isRTLmode()) {
            layoutShape.setBackgroundResource(R.drawable.ic_shape_rtl);
        }

        Utils.showSoftKeyboard(actContext, itemName)

        addItemQtyArea.setOnClickListener {

            if (checkItemQty()) {
                generalFunc.showMessage(
                    backImgView,
                    generalFunc.retrieveLangLBl(
                        "",
                        "LBL_GENIE_MAXIMUM_LIMIT"
                    ) + " " + generalFunc.getJsonValue("DELIVERY_ITEM_LIMIT", userProfileJson)
                )

            } else {


                var qty: String = ItemQTY.getText().toString();
                var qtyVal: Int = GeneralFunctions.parseIntegerValue(1, qty);
                var qtyNw: Int = qtyVal + 1;

                ItemQTY.setText("" + qtyNw);

                list.get(pos).put("ItemQty", "" + qtyNw);
            }

        }

        removeQtyArea.setOnClickListener {

            val qty: String = ItemQTY.getText().toString()
            val qtyVal = GeneralFunctions.parseIntegerValue(1, qty)
            val qtyNw = qtyVal - 1
            if (qtyNw != 0) {
                ItemQTY.setText("" + qtyNw)
                list.get(pos).put("ItemQty", "" + qtyNw);
            }

        }
        img_delete.setOnClickListener(View.OnClickListener {

            list.remove(list.get(pos))
            if (itemContainer.getChildCount() > 0) {
                itemContainer.removeAllViewsInLayout()
            }
            if (list.size == 0) {
                val inputMethodManager =
                    this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                itemContainer.removeAllViews()

            }
            checkList()
        })
        if (PICK_DROP_GENIE) {
            extraPayArea!!.visibility = View.VISIBLE
        }


        itemContainer.addView(contentView)
    }

    inner class GenericTextWatcher(val view: Int) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString().trim()
//            var map = HashMap<String, String>()
//            map = list.get(view)
//            map["ItemName"] = text
//            list.set(view, map)

            list.get(view).put("ItemName", "" + text);


            if (text.equals("") && isStoreAdded) {
                isEmptyItem = true
                manageView(false)
            } else if (!text.equals("") && !checkempty() && isStoreAdded) {
                isEmptyItem = false;
                manageView(true)
            }

        }

    }


    override fun onResume() {
        super.onResume()
        if (list.size == 0) {
            manageView(false)
        } else {
            if (checkempty()) {
                manageView(false)
            } else {
                manageView(true)
            }
        }
    }

    var isEmptyItem: Boolean = true
    fun checkList() {

        var j: Int = 0
        for (i in list) {
            addView(j)

            j = j + 1


        }


        if (list.size == 0) {
            manageView(false)
        } else {
            if (checkempty()) {
                manageView(false)
            } else {
                manageView(true)
            }
        }
    }

    fun checkempty(): Boolean {
        var j: Int = 0
        for (i in list) {
            if (list.get(j).get("ItemName")?.trim().equals("")) {
                return true
                break
            }
            j++
        }
        return false
    }


    fun checkItemQty(): Boolean {
        var isQtyReach = false;
        if (getItemQty() >= GeneralFunctions.parseIntegerValue(
                0,
                generalFunc.getJsonValue("DELIVERY_ITEM_LIMIT", userProfileJson).toString()
            )
        ) {
            return true
        }

        return isQtyReach

    }

    fun getItemQty(): Int {

        var j: Int = 0
        var qty = 0;
        for (i in list) {
            qty = qty + GeneralFunctions.parseIntegerValue(0, list.get(j).get("ItemQty"))
            j = j + 1


        }
        return qty

    }

    private fun addData(position: Int) {
        if (list.size > 0 && checkempty()) {
            return
        }

        if (checkItemQty()) {
            generalFunc.showMessage(
                backImgView,
                generalFunc.retrieveLangLBl("", "LBL_QUANTITY_CLOSED_TXT")
            )
            return
        }
        val map = HashMap<String, String>()
        map["ItemName"] = ""
        map["ItemQty"] = "1"
        map["StoreName"] = ""
        map["StoreId"] = ""
        map["itemId"] = "" + (list.size + 1)
        map["StoreAddress"] = ""
        if (PICK_DROP_GENIE) {
            map["eExtraPayment"] = "No"
        }
        map["pos"] = "$position"
        list.add(map)


        if (itemContainer.getChildCount() > 0) {
            itemContainer.removeAllViewsInLayout()
        }
        checkList()
    }

    inner class setOnClickList : View.OnClickListener {
        override fun onClick(view: View) {
            val bndl = Bundle()
            if (view.id == backImgView!!.id) {
                onBackPressed()
            } else if (view.id == submitBtnId || view.id == changeTxt!!.id) {


                var bundle = Bundle()
                bundle.putString("iCompanyId", "1")
                bundle.putBoolean("isGenie", true);

                try {
                    bundle.putString("latitude", intent.getStringExtra("latitude"))
                    bundle.putString("longitude", intent.getStringExtra("longitude"))
                } catch (e: Exception) {

                }
                StartActProcess(actContext).startActForResult(
                    ListAddressActivity::class.java,
                    bundle,
                    SEL_ADDRESS
                )
            } else if (view.id == btn_type_submit!!.id) {

                checkEstimateDetails();


            } else if (view.id == howtoworkArea!!.id) {
                howItWorks()
            } else if (view.id == iv_removeStore!!.id) {
                fareArea!!.visibility = View.VISIBLE
                noContactDeliveryNote!!.visibility = View.GONE
                addressArea!!.visibility = View.GONE
                addedStoreArea!!.visibility = View.GONE
                searchStoreArea!!.visibility = View.VISIBLE
            } else if (view.id == addOrRemoveStoreArea!!.id) {
                var bundle = Bundle()
                bundle.putString("iCompanyId", "1")
                bundle.putBoolean("isGenie", true);

                try {
                    bundle.putString("latitude", intent.getStringExtra("latitude"))
                    bundle.putString("longitude", intent.getStringExtra("longitude"))
                } catch (e: Exception) {

                }
                StartActProcess(actContext).startActForResult(
                    FindStoreActivity::class.java,
                    bundle,
                    Utils.ADD_MAP_LOC_REQ_CODE
                )
            }
        }
    }


    val actContext: Context
        get() = this@BuyAnythingActivity


    @SuppressLint("WrongConstant", "ShowToast")
    fun StoreSelectionIntro() {
        var selctionDilaog = Dialog(this)
        selctionDilaog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selctionDilaog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selctionDilaog.setContentView(R.layout.selectstoreinfo)
        selctionDilaog.setCancelable(false)
        var titleTxt: MTextView? = null;
        var msgTxt: MTextView? = null;
        var NotetitleTxt: MTextView? = null;
        var Notemsgxt: MTextView? = null;
        var helpTxt: MTextView? = null;
        var cancelImg: ImageView? = null;
        titleTxt = selctionDilaog.findViewById(R.id.titleTxt) as MTextView
        msgTxt = selctionDilaog.findViewById(R.id.msgTxt) as MTextView
        NotetitleTxt = selctionDilaog.findViewById(R.id.NotetitleTxt) as MTextView
        Notemsgxt = selctionDilaog.findViewById(R.id.Notemsgxt) as MTextView
        helpTxt = selctionDilaog.findViewById(R.id.helpTxt) as MTextView
        cancelImg = selctionDilaog.findViewById(R.id.cancelImg) as ImageView

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STORE_OPEN"))
        msgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STORE_OPEN_NOTE"))
        NotetitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HERE_HOW_HELP"))
        Notemsgxt.setText(generalFunc.retrieveLangLBl("", "LBL_HERE_HOW_HELP_NOTE"))

        val btn_type2: MButton

        val submitBtnId: Int
        btn_type2 =
            (selctionDilaog.findViewById<View>(R.id.btn_type2) as MaterialRippleLayout?)!!.getChildView()
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"))

        submitBtnId = Utils.generateViewId()
        btn_type2.id = submitBtnId

        btn_type2.setOnClickListener {
            selctionDilaog.dismiss()


            var bundle = Bundle()
            bundle.putString("iCompanyId", "1")
            bundle.putBoolean("isGenie", true);
            bundle.putBoolean("PICK_DROP_GENIE", PICK_DROP_GENIE);
            try {
                bundle.putString("latitude", intent.getStringExtra("latitude"))
                bundle.putString("longitude", intent.getStringExtra("longitude"))
            } catch (e: Exception) {

            }
            StartActProcess(actContext).startActForResult(
                AddAddressActivity::class.java,
                bundle,
                SEL_STORE
            )
        }
        cancelImg.setOnClickListener {
            selctionDilaog.dismiss()
        }
        selctionDilaog!!.getWindow()!!.setBackgroundDrawable(
            actContext.getResources().getDrawable(R.drawable.all_roundcurve_card)
        )



        if (generalFunc.isRTLmode) {
            selctionDilaog!!.getWindow()!!.getDecorView()
                .setLayoutDirection(View.LAYOUT_DIRECTION_RTL)
        }
        selctionDilaog.show()
        selctionDilaog.setOnDismissListener { }


    }


    fun fairDetailsView(msg: String) {

        var FareDetailsArr: JSONArray? = null
        FareDetailsArr = generalFunc.getJsonArray("FareDetailsArr", msg)
        val faredialog = BottomSheetDialog(actContext)
        val contentView = View.inflate(actContext, R.layout.bill_details_genie, null)
        if (generalFunc!!.isRTLmode) {
            contentView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        faredialog.setContentView(contentView)
        val mBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(contentView.parent as View)
        mBehavior.peekHeight = 1500
        val bottomSheetView =
            faredialog.window!!.decorView.findViewById<View>(R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheetView).isHideable = false
        setCancelable(faredialog, false)
        val btn_type2: MButton
        var container: LinearLayout
        val submitBtnId: Int
        var billTitleTxt: MTextView
        var infoimg: ImageView


        btn_type2 =
            (faredialog.findViewById<View>(R.id.btn_type2) as MaterialRippleLayout?)!!.getChildView()
        submitBtnId = Utils.generateViewId()
        btn_type2.id = submitBtnId
        btn_type2.isAllCaps = true
        billTitleTxt = faredialog.findViewById<View>(R.id.billTitleTxt) as MTextView
        infoimg = faredialog.findViewById<View>(R.id.infoimg) as ImageView
        container = faredialog.findViewById<View>(R.id.containerview) as LinearLayout

        billTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_INVOICE_DETAILS"))

        addFareDetailLayout(FareDetailsArr, container)

        infoimg.setOnClickListener {

            var openDailog = PreferenceDailog(actContext)
            if (PICK_DROP_GENIE) {

                openDailog.showPreferenceDialog(
                    generalFunc.retrieveLangLBl("", "LBL_INVOICE_DETAILS"),
                    generalFunc.retrieveLangLBl("", "LBL_PICKUP_INVOICE_DETAILS"),
                    R.drawable.ic_receipt_genie
                )
            } else {
                openDailog.showPreferenceDialog(
                    generalFunc.retrieveLangLBl("", "LBL_INVOICE_DETAILS"),
                    generalFunc.retrieveLangLBl("", "LBL_INVOICE_DETAILS_NOTE"),
                    R.drawable.ic_receipt_genie
                )
            }
        }

        btn_type2.text = generalFunc.retrieveLangLBl("", "LBL_PROCEED")
        btn_type2.setOnClickListener {

            val bn = Bundle()
            bn.putBoolean("isCash", if (ePaymentOption == "Cash") true else false)
            bn.putBoolean("isCODAllow", isCODAllow)
            bn.putString("outStandingAmount", outStandingAmount)
            StartActProcess(actContext).startActForResult(
                ProfilePaymentActivity::class.java,
                bn,
                Utils.SELECT_ORGANIZATION_PAYMENT_CODE
            )
            faredialog.dismiss()
        }

        faredialog.setOnDismissListener { }
        faredialog.show()

    }

    private fun addFareDetailLayout(jobjArray: JSONArray, chargeDetailArea: LinearLayout) {
        if (chargeDetailArea.getChildCount() > 0) {
            chargeDetailArea.removeAllViewsInLayout()
        }
        for (i in 0 until jobjArray.length()) {
            val jobject = generalFunc.getJsonObject(jobjArray, i)
            try {
                addFareDetailRow(
                    jobject.names().getString(0),
                    jobject[jobject.names().getString(0)].toString(),
                    if (jobjArray.length() == 1) true else false,
                    chargeDetailArea
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun addFareDetailRow(
        row_name: String,
        row_value: String,
        isLast: Boolean,
        chargeDetailArea: LinearLayout
    ) {
        var convertView: View? = null
        if (row_name.equals("eDisplaySeperator", ignoreCase = true)) {
            convertView = View(actContext)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dipToPixels(actContext, 1f)
            )
            params.setMargins(0, 0, 0, resources.getDimension(R.dimen._5sdp).toInt())
            convertView.setBackgroundColor(Color.parseColor("#dedede"))
            convertView.layoutParams = params
        } else {
            val infalInflater =
                actContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.bill_item_genie, null)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(
                0,
                resources.getDimension(R.dimen._10sdp).toInt(),
                0,
                if (isLast) resources.getDimension(R.dimen._10sdp).toInt() else 0
            )
            convertView.layoutParams = params
            val titleHTxt = convertView.findViewById<View>(R.id.titleHTxt) as MTextView
            val titleVTxt = convertView.findViewById<View>(R.id.titleVTxt) as MTextView

            titleHTxt.text = generalFunc.convertNumberWithRTL(row_name)
            titleVTxt.text = generalFunc.convertNumberWithRTL(row_value)

            if (isLast) {
                // CALCULATE individual fare & show
                titleHTxt.setTextColor(resources.getColor(R.color.black))
                titleHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                val face = Typeface.createFromAsset(assets, "fonts/Poppins_SemiBold.ttf")
                titleHTxt.setTypeface(face)
                titleVTxt.setTypeface(face)
                titleVTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                titleVTxt.setTextColor(resources.getColor(R.color.appThemeColor_1))
            }
        }
        chargeDetailArea.addView(convertView)
    }

    fun howItWorks() {
        val faredialog = BottomSheetDialog(actContext)
        val contentView = View.inflate(actContext, R.layout.dailog_buyanything_deatils, null)
        if (generalFunc!!.isRTLmode) {
            contentView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        faredialog.setContentView(contentView)
        val mBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(contentView.parent as View)
        mBehavior.peekHeight = 1500
        val bottomSheetView =
            faredialog.window!!.decorView.findViewById<View>(R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheetView).isHideable = false
        setCancelable(faredialog, false)
        val btn_type2: MButton
        val submitBtnId: Int
        var howItworksTitleTxt: MTextView
        var actionTitle: WebView
        btn_type2 =
            (faredialog.findViewById<View>(R.id.btn_type2) as MaterialRippleLayout?)!!.getChildView()
        submitBtnId = Utils.generateViewId()
        btn_type2.id = submitBtnId
        btn_type2.isAllCaps = true
        howItworksTitleTxt = faredialog.findViewById<View>(R.id.howItworksTitleTxt) as MTextView
        actionTitle = faredialog.findViewById<View>(R.id.actionTitle) as WebView
        howItworksTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HOW_IT_WORKS_N"))
        // actionTitle.setText(generalFunc.retrieveLangLBl("", "LBL_HOW_IT_WORKS_N_NOTE"))
        actionTitle.loadDataWithBaseURL(
            null,
            generalFunc.wrapHtml(
                actContext,
                generalFunc.retrieveLangLBl("", "LBL_HOW_IT_WORKS_N_NOTE")
            ),
            "text/html",
            "UTF-8",
            null
        )

        if (PICK_DROP_GENIE) {
            //   actionTitle.setText(generalFunc.retrieveLangLBl("", "LBL_HOW_WORKS_PICK_DROP"))
            actionTitle.loadDataWithBaseURL(
                null,
                generalFunc.wrapHtml(
                    actContext,
                    generalFunc.retrieveLangLBl("", "LBL_HOW_WORKS_PICK_DROP")
                ),
                "text/html",
                "UTF-8",
                null
            )

        }

        btn_type2.text = generalFunc.retrieveLangLBl("", "LBL_PROCEED")
        btn_type2.setOnClickListener {


            var bundle = Bundle()
            bundle.putBoolean("isufx", true)
            bundle.putString("iCompanyId", "1")
            bundle.putBoolean("isGenie", true);
            bundle.putBoolean("PICK_DROP_GENIE", PICK_DROP_GENIE);
            try {
                bundle.putString("latitude", intent.getStringExtra("latitude"))
                bundle.putString("longitude", intent.getStringExtra("longitude"))
            } catch (e: Exception) {

            }

            StartActProcess(actContext).startActForResult(
                AddAddressActivity::class.java,
                bundle,
                SEL_STORE
            )
            faredialog.dismiss()
        }

        faredialog.setOnDismissListener { }
        faredialog.show()
    }

    fun setCancelable(dialogview: Dialog, cancelable: Boolean) {
        val touchOutsideView = dialogview.window!!.decorView.findViewById<View>(R.id.touch_outside)
        val bottomSheetView =
            dialogview.window!!.decorView.findViewById<View>(R.id.design_bottom_sheet)
        if (cancelable) {
            touchOutsideView.setOnClickListener {
                if (dialogview.isShowing) {
                    dialogview.cancel()
                }
            }
            BottomSheetBehavior.from(bottomSheetView).setHideable(true)
        } else {
            touchOutsideView.setOnClickListener(null)
            BottomSheetBehavior.from(bottomSheetView).setHideable(false)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.ADD_MAP_LOC_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            StoreLatitude = data.getStringExtra("Latitude")
            StoreLongitude = data.getStringExtra("Longitude")
            StoreAddress = data.getStringExtra("Address")
            Storelati = GeneralFunctions.parseDoubleValue(0.0, StoreLatitude)
            Storelong = GeneralFunctions.parseDoubleValue(0.0, StoreLongitude)
            addedStoreArea!!.visibility = View.VISIBLE
            searchStoreArea!!.visibility = View.GONE
//            selectDeliveryAddressBtn!!.isEnabled = true
//            selectDeliveryAddressBtn!!.isClickable = true
//            selectDeliveryAddressBtn!!.setTextColor(actContext.resources.getColor(R.color.white))
//            selectDeliveryAddressBtn!!.setBackgroundColor(actContext.resources.getColor(R.color.appThemeColor_1))
            //            resetOrAddDest(data.getIntExtra("pos", -1), Address, lati, longi, "" + false);
        } else if (requestCode == SEL_STORE && resultCode == Activity.RESULT_OK && data != null) {

            addressStoreTxt!!.text = data.getStringExtra("vServiceAddress")
            StoreNameTxt!!.text = data.getStringExtra("vstorename")
            if (PICK_DROP_GENIE) {
                addressStoreTxt!!.text =
                    data.getStringExtra("vstorename") + ", " + data.getStringExtra("vServiceAddress")
                StoreNameTxt!!.text = data.getStringExtra("vpersonName")
            }
            addressStoreTxt!!.visibility = View.VISIBLE

            storelatitude =
                if (data.getStringExtra("vLatitude") == null) "0.0" else data.getStringExtra("vLatitude")
            storelongitude =
                if (data.getStringExtra("vLongitude") == null) "0.0" else data.getStringExtra("vLongitude")


            //  returnIntent.putExtra("vLandmark",Utils.getText(landmarkBox) );


            if (!storelatitude.equals("0.0", ignoreCase = true) && !storelongitude.equals(
                    "0.0",
                    ignoreCase = true
                )
            ) {
                isStoreAdded = true;
                if (list.size > 0) {
                    if (!checkempty()) {
                        isEmptyItem = false
                    }
                    manageView(true)
                }

                //    isAddressAdded = true
                //    isAddressAdded = true
//                fareArea!!.visibility = View.GONE
//                noContactDeliveryNote!!.visibility = View.VISIBLE
//                addressArea!!.visibility = View.VISIBLE
                /* HashMap<String, String> storeData = new HashMap<>();
                storeData.put(Utils.SELECT_ADDRESSS, data.getStringExtra("Address"));
                storeData.put(Utils.SELECT_LATITUDE, latitude + "");
                storeData.put(Utils.SELECT_LONGITUDE, longitude + "");


                storeData.put(Utils.CURRENT_ADDRESSS, data.getStringExtra("Address"));
                storeData.put(Utils.CURRENT_LATITUDE, latitude + "");
                storeData.put(Utils.CURRENT_LONGITUDE, longitude + "");

                generalFunc.storeData(storeData);*/
            }
        } else if (requestCode == SEL_ADDRESS && resultCode == Activity.RESULT_OK && data != null) {
            if (data != null && data.getStringExtra("address") != null) {

                addressTxt!!.setText(data.getStringExtra("address"))
                selAddressId = data.getStringExtra("addressId").toString()
                deliverylatitude =
                    if (data.getStringExtra("vLatitude") == null) "0.0" else data.getStringExtra("vLatitude")
                deliverylongitude =
                    if (data.getStringExtra("vLongitude") == null) "0.0" else data.getStringExtra("vLongitude")
                val storeData = HashMap<String, String?>()
                storeData[Utils.SELECT_ADDRESS_ID] = selAddressId
                storeData[Utils.SELECT_ADDRESSS] = data.getStringExtra("address")
                storeData[Utils.SELECT_LATITUDE] = data.getStringExtra("vLatitude")
                storeData[Utils.SELECT_LONGITUDE] = data.getStringExtra("vLongitude")
                generalFunc.storeData(storeData)





                if (!deliverylatitude.equals("0.0", ignoreCase = true) && !deliverylongitude.equals(
                        "0.0",
                        ignoreCase = true
                    )
                ) {
                    isAddressAdded = true
                    fareArea!!.visibility = View.GONE
                    //noContactDeliveryNote!!.visibility = View.VISIBLE
                    addressArea!!.visibility = View.VISIBLE
                    submitBtnArea!!.visibility = View.VISIBLE
                    addBtnArea!!.visibility = View.GONE
                }
            }
        } else if (requestCode == Utils.SELECT_ORGANIZATION_PAYMENT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("isCash", false)) {
                ePaymentOption = "Cash"
            } else {
                ePaymentOption = "Card"

                /* if (generalFunc.isDeliverOnlyEnabled()) {
                            selectedMethod = "Instant";
                        } else {*/selectedMethod = "Manual"
                //}
            }


            if (data.getBooleanExtra("isWallet", false)) {
                isWalletSelect = "Yes"
            } else {
                isWalletSelect = "No"
            }


            placeOrder()


        }
    }

    fun checkEstimateDetails() {
        try {

            val parameters = HashMap<String, String>()
            parameters["type"] = "CheckOutOrderEstimateDetailsGenie"
            parameters["iUserId"] = generalFunc.memberId
            parameters["IS_FOR_PICK_DROP_GENIE"] = if (PICK_DROP_GENIE) "Yes" else "No"
            parameters["iStoreName"] = Utils.getText(StoreNameTxt)
            parameters["iStoreAddress"] = Utils.getText(addressStoreTxt)
            parameters["iStorelatitude"] = storelatitude.toString()
            parameters["iStorelongitude"] = storelongitude.toString()
            parameters["iUserAddressId"] = generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID)
            //   parameters["PassengerLat"] =
            //  parameters["PassengerLon"] =
            parameters.put("vGeneralLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY))
            parameters["vGeneralCurrency"] = generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE)
            parameters["eSystem"] = Utils.eSystem_Type


            val exeWebServer = ExecuteWebServerUrl(actContext, parameters)
            exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc)
            exeWebServer.setLoaderConfig(actContext, true, generalFunc)
            exeWebServer.setDataResponseListener { responseString: String? ->

                if (responseString != null && responseString != "") {
                    val action = generalFunc.getJsonValue(Utils.action_str, responseString)
                    if (action == "1") {
                        fairDetailsView(responseString)

                        val message = generalFunc.getJsonValue(Utils.message_str, responseString);
                        if (generalFunc.getJsonValue("DISABLE_CASH_PAYMENT_OPTION", message)
                                .equals("Yes")
                        ) {
                            isCODAllow = false;
                        }
                        if (!generalFunc.getJsonValue("fOutStandingAmount", message).equals("")) {
                            outStandingAmount =
                                generalFunc.getJsonValue("fOutStandingAmountWithSymbol", message);
                        }
                    } else {
                        generalFunc.showGeneralMessage(
                            "",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString))
                        )
                    }
                } else {
                    generalFunc.showError()
                }
            }
            exeWebServer.execute()
        } catch (e: Exception) {
        }
    }

    fun placeOrder() {
        if (internetConnection!!.isNetworkConnected()) {
            // TODO: 16-04-2021  As per discuss with DT this changes move
            /*if (SITE_TYPE.equals("Demo", ignoreCase = true)) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NOTE_PLACE_ORDER_DEMO"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN")) { buttonId: Int ->
                    if (buttonId == 1) {
                        orderPlaceCall()
                    }
                }
            } else {
                orderPlaceCall()
            }*/
            orderPlaceCall()
        } else {
            generalFunc.showError()
        }
    }

    fun orderPlaceCall() {
        try {
            val orderedItemArr = JSONArray()
            for (i in list.indices) {
                val `object` = JSONObject()

                `object`.put("iQty", list.get(i).get("ItemQty"))
                `object`.put("itemName", list.get(i).get("ItemName"))
                if (PICK_DROP_GENIE) {
                    `object`.put("eExtraPayment", list.get(i).get("eExtraPayment"))
                }
                orderedItemArr.put(`object`)
            }
            val parameters = HashMap<String, String>()
            parameters["type"] = "CheckOutOrderDetails"
            parameters["iUserId"] = generalFunc.memberId
            //  parameters["iCompanyId"] = cartList.get(0).getiCompanyId()
            parameters["IS_FOR_PICK_DROP_GENIE"] = if (PICK_DROP_GENIE) "Yes" else "No"

            parameters["iStoreName"] = Utils.getText(StoreNameTxt)
            parameters["iStoreAddress"] = Utils.getText(addressStoreTxt)
            parameters["iStorelatitude"] = storelatitude.toString()
            parameters["iStorelongitude"] = storelongitude.toString()
            parameters["eCatType"] = "Genie"
            parameters["iUserAddressId"] = generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID)
            //      parameters["vCouponCode"] = appliedPromoCode
            parameters["ePaymentOption"] = ePaymentOption
            //   parameters["vInstruction"] = deliveryInstructionBox.getText().toString().trim({ it <= ' ' })
            parameters["OrderDetails"] = orderedItemArr.toString()
            parameters["CheckUserWallet"] = isWalletSelect
            //            parameters.put("iOrderId", iOrderId);


            if (!ePaymentOption.equals(
                    "Cash",
                    ignoreCase = true
                ) && !SYSTEM_PAYMENT_FLOW.equals("Method-1", ignoreCase = true)
            ) {
                parameters["CheckUserWallet"] = "Yes"
                parameters["ePayWallet"] = "Yes"
            }
            parameters["eSystem"] = Utils.eSystem_Type
            parameters["eWalletIgnore"] = eWalletIgnore

            val exeWebServer = ExecuteWebServerUrl(actContext, parameters)
            exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc)
            exeWebServer.setLoaderConfig(actContext, true, generalFunc)
            exeWebServer.setDataResponseListener { responseString: String? ->
                if (generalFunc.getJsonValue(
                        Utils.message_str,
                        responseString
                    ) != null && generalFunc.getJsonValue(Utils.message_str, responseString)
                        .equals("LBL_PROMOCODE_COMPLETE_USAGE_LIMIT", ignoreCase = true)
                ) {
                    generalFunc.showGeneralMessage(
                        "",
                        generalFunc.retrieveLangLBl("", "LBL_PROMOCODE_COMPLETE_USAGE_LIMIT")
                    )
                    return@setDataResponseListener
                }
                if (responseString != null && responseString != "") {
                    val action = generalFunc.getJsonValue(Utils.action_str, responseString)
                    if (action == "1") {
                        iOrderId = generalFunc.getJsonValue("iOrderId", responseString)
                        val bn = Bundle()
                        bn.putString("iOrderId", iOrderId)
                        if (ePaymentOption.equals(
                                "card",
                                ignoreCase = true
                            ) && SYSTEM_PAYMENT_FLOW.equals("Method-1", ignoreCase = true)
                        ) {
                            orderPlaceCallForCard("", isWalletSelect, "")
                        } else {

                            StartActProcess(actContext).startActWithData(
                                OrderPlaceConfirmActivity::class.java,
                                bn
                            )
                        }
                    } else {
                        iOrderId = generalFunc.getJsonValue("iOrderId", responseString)
                        if (generalFunc.getJsonValue(Utils.message_str, responseString)
                                .equals("LOW_WALLET_AMOUNT", ignoreCase = true)
                        ) {
                            var walletMsg: String? = ""
                            val low_balance_content_msg =
                                generalFunc.getJsonValue("low_balance_content_msg", responseString)
                            walletMsg =
                                if (low_balance_content_msg != null && !low_balance_content_msg.equals(
                                        "",
                                        ignoreCase = true
                                    )
                                ) {
                                    low_balance_content_msg
                                } else {
                                    generalFunc.retrieveLangLBl("", "LBL_WALLET_LOW_AMOUNT_MSG_TXT")
                                }
                            if (!isCODAllow && SYSTEM_PAYMENT_FLOW.equals(
                                    "Method-2",
                                    ignoreCase = true
                                )
                            ) {
                                generalFunc.showGeneralMessage(
                                    generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"),
                                    walletMsg,
                                    generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"),
                                    generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY")
                                ) { button_Id: Int ->
                                    if (button_Id == 1) {
                                        val bn = Bundle()
                                        bn.putString("iServiceId", generalFunc.serviceId)
                                        bn.putString("isCheckout", "")
                                        StartActProcess(actContext).startAct(MyWalletActivity::class.java)
                                    } else if (button_Id == 2) {
                                    }
                                }
                            } else {
                                generalFunc.showGeneralMessage(
                                    generalFunc.retrieveLangLBl(
                                        "",
                                        "LBL_LOW_WALLET_BALANCE"
                                    ),
                                    walletMsg,
                                    if (generalFunc.getJsonValue(
                                            "IS_RESTRICT_TO_WALLET_AMOUNT",
                                            responseString
                                        ).equals("No", ignoreCase = true)
                                    ) generalFunc.retrieveLangLBl(
                                        "",
                                        "LBL_CONTINUE_BTN"
                                    ) else generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"),
                                    generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY"),
                                    if (generalFunc.getJsonValue(
                                            "IS_RESTRICT_TO_WALLET_AMOUNT",
                                            responseString
                                        ).equals("NO", ignoreCase = true)
                                    ) generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT") else ""
                                ) { button_Id: Int ->
                                    if (button_Id == 1) {
                                        StartActProcess(actContext).startAct(MyWalletActivity::class.java)
                                    } else if (button_Id == 0) {
                                        if (generalFunc.getJsonValue(
                                                "IS_RESTRICT_TO_WALLET_AMOUNT",
                                                responseString
                                            ).equals("No", ignoreCase = true)
                                        ) {
                                            eWalletIgnore = "Yes"
                                            placeOrder()
                                        }
                                    } else if (button_Id == 2) {
                                        iOrderId = ""
                                        ePaymentOption = "Cash"
                                        isWalletSelect = "No"
                                    }
                                }
                            }
                            return@setDataResponseListener
                        }
                        if (generalFunc.getJsonValue(Utils.message_str, responseString)
                                .equals("LBL_RESTAURANTS_CLOSE_NOTE", ignoreCase = true)
                        ) {
                            generalFunc.showGeneralMessage(
                                "",
                                generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE")
                            )
                        } else if (generalFunc.getJsonValue(Utils.message_str, responseString)
                                .equals("LBL_MENU_ITEM_NOT_AVAILABLE_TXT", ignoreCase = true)
                        ) {
                            generalFunc.showGeneralMessage(
                                "",
                                generalFunc.retrieveLangLBl("", "LBL_MENU_ITEM_NOT_AVAILABLE_TXT")
                            )
                        } else if (generalFunc.getJsonValue(Utils.message_str, responseString)
                                .equals("LBL_DELIVERY_LOCATION_NOT_ALLOW", ignoreCase = true)
                        ) {
                            generalFunc.showGeneralMessage(
                                "",
                                generalFunc.retrieveLangLBl("", "LBL_DELIVERY_LOCATION_NOT_ALLOW")
                            )
                        } else if (generalFunc.getJsonValue(Utils.message_str, responseString)
                                .equals("DO_PHONE_VERIFY", ignoreCase = true)
                        ) {
                            val phoneVerify =
                                generalFunc.getJsonValue(Utils.message_str, responseString)
                            if (phoneVerify.equals("DO_PHONE_VERIFY", ignoreCase = true)) {
                                if (phoneVerify != null && !phoneVerify.isEmpty()) {
                                    val generateAlert = GenerateAlertBox(actContext)
                                    generateAlert.setCancelable(false)
                                    generateAlert.setBtnClickList { btn_id: Int ->
                                        if (btn_id == 1) {
                                            notifyVerifyMobile()
                                        }
                                    }
                                    generateAlert.setContentMessage(
                                        "",
                                        generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT")
                                    )
                                    generateAlert.setPositiveBtn(
                                        generalFunc.retrieveLangLBl(
                                            "",
                                            "LBL_OK"
                                        )
                                    )
                                    generateAlert.showAlertBox()
                                    return@setDataResponseListener
                                }
                            }
                        } else {
                            var ShowContactUsBtn =
                                generalFunc.getJsonValue("ShowContactUsBtn", responseString)
                            ShowContactUsBtn =
                                if (ShowContactUsBtn == null || ShowContactUsBtn.isEmpty()) "No" else ShowContactUsBtn
                            if (ShowContactUsBtn.equals("Yes", ignoreCase = true)) {
                                val outstanding_restriction_label =
                                    generalFunc.getJsonValue(Utils.message_str, responseString)
                                if (outstanding_restriction_label != null && !outstanding_restriction_label.isEmpty()) {
                                    val generateAlert = GenerateAlertBox(actContext)
                                    generateAlert.setCancelable(false)
                                    generateAlert.setBtnClickList { btn_id: Int ->
                                        if (btn_id == 1) {
                                            StartActProcess(actContext).startAct(ContactUsActivity::class.java)
                                        }
                                    }
                                    generateAlert.setContentMessage(
                                        "",
                                        outstanding_restriction_label
                                    )
                                    generateAlert.setNegativeBtn(
                                        generalFunc.retrieveLangLBl(
                                            "",
                                            "LBL_OK"
                                        )
                                    )
                                    generateAlert.setPositiveBtn(
                                        generalFunc.retrieveLangLBl(
                                            "",
                                            "LBL_CONTACT_US_TXT"
                                        )
                                    )
                                    generateAlert.showAlertBox()
                                    return@setDataResponseListener
                                }
                            }
                            generalFunc.showGeneralMessage(
                                "",
                                generalFunc.getJsonValue(Utils.message_str, responseString)
                            )
                        }
                    }
                } else {
                    generalFunc.showError()
                }
            }
            exeWebServer.execute()
        } catch (e: Exception) {
        }
    }

    fun notifyVerifyMobile() {
        val vPhoneCode = generalFunc.retrieveValue(Utils.DefaultPhoneCode)
        val bn = Bundle()
        bn.putString("MOBILE", vPhoneCode + generalFunc.getJsonValue("vPhone", userProfileJson))
        bn.putString("msg", "DO_PHONE_VERIFY")
        bn.putBoolean("isrestart", false)
        StartActProcess(actContext).startActForResult(
            VerifyInfoActivity::class.java,
            bn,
            Utils.VERIFY_MOBILE_REQ_CODE
        )
    }

    fun orderPlaceCallForCard(token: String?, CheckUserWallet: String?, txref: String?) {
        val parameters = HashMap<String, String?>()
        parameters["type"] = "CaptureCardPaymentOrder"
        parameters["iUserId"] = generalFunc.memberId
        parameters["ePaymentOption"] = "Card"
        parameters["iOrderId"] = iOrderId
        parameters["vStripeToken"] = token

        parameters["CheckUserWallet"] = CheckUserWallet
        parameters["returnUrl"] = CommonUtilities.WEBSERVICE
        parameters["eSystem"] = Utils.eSystem_Type
        parameters["vPayMethod"] = selectedMethod
        val exeWebServer = ExecuteWebServerUrl(actContext, parameters)
        exeWebServer.setLoaderConfig(actContext, true, generalFunc)
        exeWebServer.setDataResponseListener { responseString: String? ->
            if (responseString != null && responseString != "") {
                val action = generalFunc.getJsonValue(Utils.action_str, responseString)
                if (action == "1") {
                    if (generalFunc.getJsonValue(
                            "full_wallet_adjustment",
                            responseString
                        ) != null && generalFunc.getJsonValue(
                            "full_wallet_adjustment",
                            responseString
                        ).equals("Yes", ignoreCase = true)
                    ) {
                        orderCompleted()
                        return@setDataResponseListener
                    }
                    if (selectedMethod.equals("Manual", ignoreCase = true)) {
                        orderCompleted()
                    } else if (selectedMethod.equals("Instant", ignoreCase = true)) {
                        if (APP_PAYMENT_METHOD.equals("Stripe", ignoreCase = true)) {
                            val bn = Bundle()
                            val map = HashMap<String, Any>()
                            map["URL"] = generalFunc.getJsonValue(Utils.message_str, responseString)
                            map["vPageTitle"] = generalFunc.retrieveLangLBl(
                                "",
                                "LBL_INSTANT_PAYMENT_PAGE_TITLE_TXT"
                            )
                            bn.putSerializable("data", map)
                            StartActProcess(actContext).startActForResult(
                                QuickPaymentActivity::class.java,
                                bn,
                                Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE
                            )
                        }
                    }
                } else {
                    generalFunc.showGeneralMessage(
                        "",
                        generalFunc.retrieveLangLBl(
                            "",
                            generalFunc.getJsonValue(Utils.message_str, responseString)
                        )
                    )
                }
            } else {
                generalFunc.showError()
            }
        }
        exeWebServer.execute()
    }

    private fun orderCompleted() {
        val bn = Bundle()
        bn.putBoolean("isRestart", true)
        bn.putString("iOrderId", iOrderId)
        StartActProcess(actContext).startActWithData(OrderPlaceConfirmActivity::class.java, bn)
    }

    fun manageView(isvisible: Boolean) {
        if (isvisible) {
            if (!isEmptyItem) {
                disableArea!!.visibility = View.GONE
            }
        } else {
            disableArea!!.visibility = View.VISIBLE
        }

    }
}