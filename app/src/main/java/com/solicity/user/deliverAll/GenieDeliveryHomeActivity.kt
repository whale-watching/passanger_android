package com.solicity.user.deliverAll

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ViewPagerCards.CardFragmentPagerAdapter
import com.ViewPagerCards.CardPagerAdapter
import com.ViewPagerCards.ShadowTransformer
import com.andremion.counterfab.CounterFab
import com.solicity.user.CommonDeliveryTypeSelectionActivity
import com.solicity.user.R
import com.general.files.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.utils.Utils
import com.view.*
import com.view.simpleratingbar.SimpleRatingBar
import org.json.JSONObject
import java.util.*

class GenieDeliveryHomeActivity : AppCompatActivity(), OnPageChangeListener {


    // LoopingCirclePageIndicator bannerCirclePageIndicator;
    // FoodDeliveryHomeAdapter bannerAdapter;


    var pickUpArea: CardView? = null
    var anywhereArea: CardView? = null
    var howItWorksArea: LinearLayout? = null
    var howitWorkImg: ImageView? = null
    var titleTxt: MTextView? = null
    var noteLbl: MTextView? = null
    var noteTxt: MTextView? = null
    lateinit var bannerArea: View
    var bannerViewPager: ViewPager? = null
    var backImgView: ImageView? = null
    var noLocMsgTxt: MTextView? = null
    var deliveryAreaTxt: MTextView? = null
    var storeTagTxtView: MTextView? = null
    var pickupDropTxtView: MTextView? = null
    var pickupDropNoteTxtView: MTextView? = null
    var pickupBtn: MTextView? = null
    var buyAnythingTxtView: MTextView? = null
    var buyAnythingNoteTxtView: MTextView? = null
    var findStoreBtn: MTextView? = null
    var outAreaTitle: MTextView? = null
    var bannerCirclePageIndicator: LoopingCirclePageIndicator? = null
    private var app_bar_layout: AppBarLayout? = null
    var ratingArea: LinearLayout? = null
    var orderHotelName: MTextView? = null
    var ratingCancel: ImageView? = null
    var ratingBar: SimpleRatingBar? = null
    var errorViewArea: LinearLayout? = null
    var errorView: ErrorView? = null
    lateinit var fabcartIcon: CounterFab
    var NoDataTxt: MTextView? = null
    var bottomMenuArea: LinearLayout? = null
    var bottomBar: AddBottomBar? = null
    var container: FrameLayout? = null
    var loading: ProgressBar? = null
    var MainArea: LinearLayout? = null
    var DataArea: LinearLayout? = null
    var DataLoadingArea: RelativeLayout? = null
    var collapsing_toolbar: CollapsingToolbarLayout? = null
    lateinit var generalFunc: GeneralFunctions
    var userProfileJson = ""
    var obj_userProfile: JSONObject? = null
    var currentBannerPosition = 0
    var isgpsview = false
    var internetConnection: InternetConnection? = null
    var mIsLoading = false
    private var mCardAdapter: CardPagerAdapter? = null
    private var mCardShadowTransformer: ShadowTransformer? = null
    private var mFragmentCardAdapter: CardFragmentPagerAdapter? = null
    private var mFragmentCardShadowTransformer: ShadowTransformer? = null


    var LBL_NO_INTERNET_TXT = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genie_delivery_home)
        generalFunc = MyApp.getInstance().getGeneralFun(actContext)
        internetConnection = InternetConnection(actContext)
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)
        obj_userProfile = generalFunc.getJsonObject(userProfileJson)
        //        if (Utils.checkText(generalFunc.getMemberId())) {
        bottomBar = AddBottomBar(actContext, obj_userProfile)
        CreateRoundedView(resources.getColor(R.color.appThemeColor_1), 30, 1, resources.getColor(R.color.appThemeColor_1), findViewById(R.id.pickupBtn))
        CreateRoundedView(resources.getColor(R.color.appThemeColor_1), 30, 1, resources.getColor(R.color.appThemeColor_1), findViewById(R.id.findStoreBtn))
        findViewById<View>(R.id.findStoreBtn).setOnClickListener {
            var intent1 = Bundle()
            intent1.putString("latitude", intent.getStringExtra("latitude"))
            intent1.putString("longitude", intent.getStringExtra("longitude"))

            StartActProcess(actContext).startActWithData(BuyAnythingActivity::class.java, intent1)

        }


        //        }
        loading = findViewById<View>(R.id.loading) as ProgressBar
        MainArea = findViewById<View>(R.id.MainArea) as LinearLayout
        DataArea = findViewById<View>(R.id.DataArea) as LinearLayout
        DataLoadingArea = findViewById<View>(R.id.DataLoadingArea) as RelativeLayout
        if (obj_userProfile != null) {
            val serviceArray = generalFunc.getJsonArray("ServiceCategories", obj_userProfile)
            if (serviceArray != null && serviceArray.length() > 1 && generalFunc.isAnyDeliverOptionEnabled()) {
                val advertise_banner_data = generalFunc.getJsonObject("advertise_banner_data", obj_userProfile)
                if (advertise_banner_data != null && advertise_banner_data.length() > 0) {
                    val image_url = generalFunc.getJsonValueStr("image_url", advertise_banner_data)
                    if (image_url != null && !image_url.equals("", ignoreCase = true)) {
                        val map = HashMap<String, String>()
                        map["image_url"] = generalFunc.getJsonValueStr("image_url", advertise_banner_data)
                        map["tRedirectUrl"] = generalFunc.getJsonValueStr("tRedirectUrl", advertise_banner_data)
                        map["vImageWidth"] = generalFunc.getJsonValueStr("vImageWidth", advertise_banner_data)
                        map["vImageHeight"] = generalFunc.getJsonValueStr("vImageHeight", advertise_banner_data)
                        OpenAdvertisementDialog(actContext, map, generalFunc)
                    }
                }
            }
        }
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar)
        container = findViewById<View>(R.id.container) as FrameLayout
        bottomMenuArea = findViewById<View>(R.id.bottomMenuArea) as LinearLayout
        fabcartIcon = findViewById<View>(R.id.fabcartIcon) as CounterFab


        // mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        app_bar_layout = findViewById<View>(R.id.app_bar_layout) as AppBarLayout

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            app_bar_layout!!.outlineProvider = null
        }
        ratingArea = findViewById<View>(R.id.ratingArea) as LinearLayout
        orderHotelName = findViewById<View>(R.id.orderHotelName) as MTextView
        ratingCancel = findViewById<View>(R.id.ratingCancel) as ImageView
        ratingBar = findViewById<View>(R.id.ratingBar) as SimpleRatingBar
        if (generalFunc.getJsonValue("LastOrderFoodDetailRatingShow", userProfileJson)
                .equals("Yes", ignoreCase = true)
        ) {
            ratingBar!!.pressedFillColor = ContextCompat.getColor(actContext,R.color.white)
        }
        errorViewArea = findViewById<View>(R.id.errorViewArea) as LinearLayout
        errorView = findViewById<View>(R.id.errorView) as ErrorView
        NoDataTxt = findViewById<View>(R.id.NoDataTxt) as MTextView
        ratingCancel!!.setOnClickListener { v: View? ->
            ratingArea!!.visibility = View.GONE

        }
        ratingBar!!.setOnClickListener { v: View? ->
            val bn = Bundle()
            bn.putFloat("rating", ratingBar!!.rating)

            if (generalFunc.getJsonValue("LastOrderFoodDetailRatingShow", userProfileJson)
                    .equals("Yes", ignoreCase = true)
            ) {
                bn.putBoolean("IS_NEW", true)
                bn.putString(
                    "listDriverFeedbackQuestions",
                    generalFunc.getJsonValue("DRIVER_FEEDBACK_QUESTIONS", userProfileJson)
                )
            } else {
                bn.putBoolean("IS_NEW", false)
            }
            bn.putString("iOrderId", generalFunc.getJsonValue("LastOrderId", userProfileJson))
            bn.putString("vOrderNo", generalFunc.getJsonValue("LastOrderNo", userProfileJson))
            bn.putString(
                "driverName",
                generalFunc.getJsonValue("LastOrderDriverName", userProfileJson)
            )
            bn.putString(
                "vCompany",
                generalFunc.getJsonValue("LastOrderCompanyName", userProfileJson)
            )
            bn.putString(
                "eTakeaway",
                generalFunc.getJsonValue("LastOrderTakeaway", userProfileJson)
            )

            StartActProcess(actContext).startActWithData(FoodRatingActivity::class.java, bn)
            ratingArea!!.visibility = View.GONE

        }

        fabcartIcon!!.setOnClickListener(setOnClickLst())
        val Ratings_From_DeliverAll = generalFunc.getJsonValueStr("Ratings_From_DeliverAll", obj_userProfile)
        if (Ratings_From_DeliverAll != null && !Ratings_From_DeliverAll.equals("", ignoreCase = true) && !Ratings_From_DeliverAll.equals("Done", ignoreCase = true)) {
            ratingArea!!.visibility = View.VISIBLE

            orderHotelName!!.text = generalFunc.getJsonValueStr("LastOrderCompanyName", obj_userProfile)
        }

        val isback = intent.getBooleanExtra("isback", false)



        bannerArea = findViewById(R.id.bannerArea)
        bannerViewPager = findViewById<View>(R.id.bannerViewPager) as ViewPager
        howItWorksArea = findViewById<View>(R.id.howItWorksArea) as LinearLayout
        pickUpArea = findViewById<View>(R.id.pickUpArea) as CardView
        anywhereArea = findViewById<View>(R.id.anywhereArea) as CardView
        howitWorkImg = findViewById<View>(R.id.howitWorkImg) as ImageView
        howitWorkImg!!.setOnClickListener(setOnClickLst())
        howItWorksArea!!.visibility = View.VISIBLE
        noteLbl = findViewById<View>(R.id.noteLbl) as MTextView
        noteTxt = findViewById<View>(R.id.noteTxt) as MTextView
        titleTxt = findViewById<View>(R.id.titleTxt) as MTextView
        titleTxt!!.visibility = View.VISIBLE
        titleTxt!!.setText(intent.getStringExtra("vCategory"));
        noteLbl!!.setText(generalFunc.retrieveLangLBl("", "LBL_NOTE"));


        noteTxt!!.setText(Html.fromHtml(generalFunc.retrieveLangLBl("", "LBL_GENIE_NOTE")))


        // noteTxt!!.loadUrl("javascript:document.body.style.color=\"#FF0000\";");
//        noteTxt!!.loadDataWithBaseURL(null, generalFunc.wrapHtml(actContext,generalFunc.retrieveLangLBl("","LBL_GENIE_NOTE")), "text/html", "UTF-8", null);

        //noteTxt!!.(generalFunc.wrapHtml(actContext,generalFunc.retrieveLangLBl("","LBL_GENIE_NOTE")))
        if (intent.getStringExtra("eCatType") != null && intent.getStringExtra("eCatType").equals("Runner")) {
            anywhereArea!!.visibility = View.GONE
            noteTxt!!.setText(Html.fromHtml(generalFunc.retrieveLangLBl("", "LBL_RUNNER_NOTE")))


        } else if (intent.getStringExtra("eCatType") != null && intent.getStringExtra("eCatType").equals("Anywhere")) {
            pickUpArea!!.visibility = View.GONE
            noteTxt!!.setText(Html.fromHtml(generalFunc.retrieveLangLBl("", "LBL_ANYWHERE_NOTE")))


        }


        outAreaTitle = findViewById<View>(R.id.outAreaTitle) as MTextView
        bannerCirclePageIndicator = findViewById<View>(R.id.bannerCirclePageIndicator) as LoopingCirclePageIndicator
        backImgView = findViewById<View>(R.id.backImgView) as ImageView
        if (generalFunc.isRTLmode()) {
            backImgView!!.rotation = 180f
        }



        backImgView!!.setOnClickListener(setOnClickLst())


        noLocMsgTxt = findViewById<View>(R.id.noLocMsgTxt) as MTextView
        deliveryAreaTxt = findViewById<View>(R.id.deliveryAreaTxt) as MTextView
        storeTagTxtView = findViewById<View>(R.id.storeTagTxtView) as MTextView
        pickupDropTxtView = findViewById<View>(R.id.pickupDropTxtView) as MTextView
        pickupDropNoteTxtView = findViewById<View>(R.id.pickupDropNoteTxtView) as MTextView
        pickupBtn = findViewById<View>(R.id.pickupBtn) as MTextView
        buyAnythingTxtView = findViewById<View>(R.id.buyAnythingTxtView) as MTextView
        buyAnythingNoteTxtView = findViewById<View>(R.id.buyAnythingNoteTxtView) as MTextView
        findStoreBtn = findViewById<View>(R.id.findStoreBtn) as MTextView
        // val lyParamsBannerArea = bannerArea.getLayoutParams() as LinearLayout.LayoutParams
        //  lyParamsBannerArea.height = Utils.getHeightOfBanner(actContext, 0, "16:4")
        // bannerArea.setLayoutParams(lyParamsBannerArea)
        if (isback) {
            bottomMenuArea!!.visibility = View.GONE
            backImgView!!.visibility = View.VISIBLE
        } else {
        }
        findViewById<View>(R.id.headerLogo).visibility = View.GONE
        findViewById<View>(R.id.uberXHeaderLayout).setOnClickListener(setOnClickLst())
        findViewById<View>(R.id.headerTxt).setOnClickListener(setOnClickLst())

        findViewById<View>(R.id.headerArrow).setOnClickListener(setOnClickLst())
        setData()
        banners;
        bannerViewPager!!.addOnPageChangeListener(this)

        findViewById<View>(R.id.pickupBtn).setOnClickListener {
            var bn = Bundle()

            if (generalFunc.getJsonValue("PICK_DROP_GENIE", userProfileJson) != null && generalFunc.getJsonValue("PICK_DROP_GENIE", userProfileJson).equals("Yes")) {
                val bn = Bundle()
                bn.putBoolean("PICK_DROP_GENIE", true)
                var intent1 = Bundle()
                intent1.putString("latitude", intent.getStringExtra("latitude"))
                intent1.putString("longitude", intent.getStringExtra("longitude"))
                StartActProcess(actContext).startActWithData(BuyAnythingActivity::class.java, bn)
            } else {
                bn.putString("vCategory", getIntent().getStringExtra("vCategory"))
                bn.putString("iVehicleCategoryId", getIntent().getStringExtra("iVehicleCategoryId"))
                bn.putString("latitude", getIntent().getStringExtra("latitude"))
                bn.putString("longitude", getIntent().getStringExtra("longitude"))
                bn.putString("address", getIntent().getStringExtra("address"))


                bn.putString("vCategory", generalFunc.getJsonValue("DELIVERY_CATEGORY_NAME", userProfileJson))

                StartActProcess(actContext).startActWithData(CommonDeliveryTypeSelectionActivity::class.java, bn)
            }

        }

        storeTagTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_ANY_DELIVER"))
        pickupDropTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_ITEM"))
        pickupDropNoteTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_SUB_TITLE"))
        pickupBtn!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_DETAIL"))
        buyAnythingTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_BUY_ANY_FROM_STORE"))
        buyAnythingNoteTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_FROM_STORE_SUB_TITLE"))
        findStoreBtn!!.setText(generalFunc.retrieveLangLBl("", "LBL_FIND_A_STORE"))
        if (intent.getStringExtra("eCatType") != null && intent.getStringExtra("eCatType").equals("Runner")) {
            storeTagTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_ANY_DELIVER"))
            pickupDropTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_ITEM_RUNNER_ANYWHERE"))
            pickupDropNoteTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_SUB_TITLE_RUNNER_ANYWHERE"))
            pickupBtn!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_DETAIL_RUNNER_ANYWHERE"))
            buyAnythingTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_BUY_ANY_FROM_STORE_RUNNER_ANYWHERE"))
            buyAnythingNoteTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_FROM_STORE_SUB_TITLE_RUNNER"))
            findStoreBtn!!.setText(generalFunc.retrieveLangLBl("", "LBL_FIND_A_STORE_RUNNER_ANYWHERE"))

        } else if (intent.getStringExtra("eCatType") != null && intent.getStringExtra("eCatType").equals("Anywhere")) {
            storeTagTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_ANY_DELIVER"))
            pickupDropTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_ITEM_RUNNER_ANYWHERE"))
            pickupDropNoteTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_SUB_TITLE_RUNNER_ANYWHERE"))
            pickupBtn!!.setText(generalFunc.retrieveLangLBl("", "LBL_PICK_DROP_DETAIL_RUNNER_ANYWHERE"))
            buyAnythingTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_BUY_ANY_FROM_STORE_RUNNER_ANYWHERE"))
            buyAnythingNoteTxtView!!.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_FROM_STORE_SUB_TITLE_RUNNER"))
            findStoreBtn!!.setText(generalFunc.retrieveLangLBl("", "LBL_FIND_A_STORE_RUNNER_ANYWHERE"))

        }

        generateErrorView()
    }


    fun generateErrorView() {
        generalFunc!!.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT")
        if (internetConnection!!.isNetworkConnected) {
            errorViewArea!!.visibility = View.GONE
        } else {
            if (errorViewArea!!.visibility != View.VISIBLE) {
                errorViewArea!!.visibility = View.VISIBLE
            }
            errorView!!.setOnRetryListener {

            }
        }
    }


    val isPubNubEnabled: Boolean
        get() {
            val ENABLE_PUBNUB = generalFunc!!.getJsonValueStr("ENABLE_PUBNUB", obj_userProfile)
            return ENABLE_PUBNUB.equals("Yes", ignoreCase = true)
        }


    private fun setData() {


        LBL_NO_INTERNET_TXT = generalFunc!!.retrieveLangLBl("", "LBL_NO_INTERNET_TXT")


        noLocMsgTxt!!.text = generalFunc!!.retrieveLangLBl("", "LBL_LOCATION_CHANGE_NOTE")


        outAreaTitle!!.text = generalFunc!!.retrieveLangLBl("", "LBL_OUT_OF_DELIVERY_AREA")
        deliveryAreaTxt!!.text = generalFunc!!.retrieveLangLBl("", "LBL_DELIVERY_AREA_NOTE")


    }

    val banners: Unit
        get() {

            val parameters = HashMap<String, String?>()
            parameters["type"] = "getBanners"
            parameters["iMemberId"] = generalFunc!!.memberId
            parameters["eSystem"] = Utils.eSystem_Type
            parameters["eCatType"] = intent.getStringExtra("eCatType")
            val exeWebServer = ExecuteWebServerUrl(actContext, true, parameters)
            exeWebServer.setLoaderConfig(actContext, false, generalFunc)
            exeWebServer.setDataResponseListener { responseString ->


                DataLoadingArea!!.visibility = View.GONE
                MainArea!!.visibility == View.VISIBLE



                if (responseString != null && responseString != "") {
                    val isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString)
                    if (isDataAvail == true) {
                        val arr = generalFunc!!.getJsonArray(Utils.message_str, responseString)
                        if (arr != null && arr.length() > 0) {
                            val imagesList = ArrayList<String>()
                            mCardAdapter = CardPagerAdapter()
                            val height = MyApp.getInstance().currentAct.resources.getDimensionPixelSize(R.dimen._160sdp)
                            val margin = MyApp.getInstance().currentAct.resources.getDimensionPixelSize(R.dimen._20sdp)
                            val width_ = margin * 2
                            val width = Utils.getWidthOfBanner(MyApp.getInstance().currentAct, width_)
                            for (i in 0 until arr.length()) {
                                val obj_temp = generalFunc!!.getJsonObject(arr, i)
                                val vImage = generalFunc!!.getJsonValueStr("vImage", obj_temp)
                                val imageURL = Utils.getResizeImgURL(MyApp.getInstance().currentAct, vImage, width, height)
                                imagesList.add(imageURL)
                                mCardAdapter!!.addCardItem(imageURL, actContext)
                            }
                            mFragmentCardAdapter = CardFragmentPagerAdapter(supportFragmentManager,
                                    dpToPixels(2, actContext))
                            mCardShadowTransformer = ShadowTransformer(bannerViewPager, mCardAdapter)
                            mFragmentCardShadowTransformer = ShadowTransformer(bannerViewPager, mFragmentCardAdapter)
                            bannerViewPager!!.adapter = mCardAdapter
                            bannerViewPager!!.setPageTransformer(false, mCardShadowTransformer)
                            bannerViewPager!!.offscreenPageLimit = 3

                            bannerCirclePageIndicator!!.setDataSize(arr.length());
                            bannerCirclePageIndicator!!.setViewPager(bannerViewPager);
                        } else {
                            bannerArea!!.visibility = View.GONE
                        }
                    }
                }
            }
            exeWebServer.execute()
        }

    val actContext: Context
        get() = this@GenieDeliveryHomeActivity


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager.SCROLL_STATE_IDLE
     *
     * @see ViewPager.SCROLL_STATE_DRAGGING
     *
     * @see ViewPager.SCROLL_STATE_SETTLING
     */
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        currentBannerPosition = position
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.MY_PROFILE_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val userProfileJson = generalFunc!!.retrieveValue(Utils.USER_PROFILE_JSON)
            obj_userProfile = generalFunc!!.getJsonObject(generalFunc!!.retrieveValue(Utils.USER_PROFILE_JSON))

        } else if (requestCode == Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //headerTxt.setText(data.getStringExtra("Address"));

        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val userProfileJson = generalFunc!!.retrieveValue(Utils.USER_PROFILE_JSON)
            obj_userProfile = generalFunc!!.getJsonObject(generalFunc!!.retrieveValue(Utils.USER_PROFILE_JSON))
        } else if (requestCode == Utils.REQUEST_CODE_GPS_ON) {
            isgpsview = true
            if (generalFunc!!.isLocationEnabled) {


            } else {
                isgpsview = false
            }
        } else if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {

            if (resultCode == Activity.RESULT_OK) {

            }
        } else if (requestCode == 111 && resultCode == Activity.RESULT_OK) {

        }
    }

    fun pubNubMsgArrived(message: String?) {
        runOnUiThread {
            val msgType = generalFunc!!.getJsonValue("MsgType", message)
            val iDriverId = generalFunc!!.getJsonValue("iDriverId", message)
            if (msgType == "DriverArrived") {

            } else {

            }
        }
    }

    fun buildMessage(message: String?, positiveBtn: String?) {
        val generateAlert = GenerateAlertBox(actContext)
        generateAlert.setCancelable(false)
        generateAlert.setBtnClickList { btn_id: Int -> generateAlert.closeAlertBox() }
        generateAlert.setContentMessage("", message)
        generateAlert.setPositiveBtn(positiveBtn)
        generateAlert.showAlertBox()
    }


    override fun onDestroy() {
        super.onDestroy()


    }

    override fun onBackPressed() {

        super.onBackPressed()
    }


    inner class setOnClickList : View.OnClickListener {
        override fun onClick(view: View) {
            val i = view.id
            if (i == deliveryAreaTxt!!.id) {
                isgpsview = true
                StartActProcess(actContext).startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON)
            }
        }
    }

    fun setCancelable(dialogview: Dialog, cancelable: Boolean) {
        val touchOutsideView = dialogview.window!!.decorView.findViewById<View>(R.id.touch_outside)
        val bottomSheetView = dialogview.window!!.decorView.findViewById<View>(R.id.design_bottom_sheet)
        if (cancelable) {
            touchOutsideView.setOnClickListener { v: View? ->
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


    inner class setOnClickLst : View.OnClickListener {
        override fun onClick(v: View) {
            Utils.hideKeyboard(actContext)
            val bn = Bundle()
            when (v.id) {


                R.id.backImgView -> onBackPressed()
                R.id.howitWorkImg -> {


                    if (intent.getStringExtra("eCatType") != null && intent.getStringExtra("eCatType").equals("Runner")) {
                        var openDailog = PreferenceDailog(actContext)
                        openDailog.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_DELIVER_ANYTHING_RUNNER"),
                                generalFunc.retrieveLangLBl("", "LBL_DELIVER_PICK_ANY_NOTE_RUNNER"), R.drawable.ic_clearance)
                    } else if (intent.getStringExtra("eCatType") != null && intent.getStringExtra("eCatType").equals("Anywhere")) {
                        var openDailog = PreferenceDailog(actContext)
                        openDailog.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_DELIVER_ANYTHING_ANYWHERE"),
                                generalFunc.retrieveLangLBl("", "LBL_DELIVER_PICK_ANY_NOTE_ANYWHERE"), R.drawable.ic_clearance)

                    } else {
                        var openDailog = PreferenceDailog(actContext)
                        openDailog.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_DELIVER_ANYTHING"),
                                generalFunc.retrieveLangLBl("", "LBL_DELIVER_PICK_ANY_NOTE"), R.drawable.ic_clearance)

                    }

                }


                R.id.fabcartIcon -> StartActProcess(actContext).startAct(EditCartActivity::class.java)
            }
        }
    }


    companion object {

        fun dpToPixels(dp: Int, context: Context): Float {
            return dp * context.resources.displayMetrics.density
        }
    }
}