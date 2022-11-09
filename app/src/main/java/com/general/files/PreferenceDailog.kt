package com.general.files

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.solicity.user.R
import com.view.MTextView


class PreferenceDailog(val actContext: Context) {


    lateinit var hotoUseDialog: AlertDialog
    var generalFunc = GeneralFunctions(actContext)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun showPreferenceDialog(title: String, Msg: String, img: Int) {
        val builder = AlertDialog.Builder(actContext)
        val inflater =
            actContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.desgin_preference_help, null)
        builder.setView(dialogView)
        val iamage_source = dialogView.findViewById<View>(R.id.iamage_source) as ImageView

        if (!img.equals("")) {
            iamage_source.setImageResource(img)
        }

        val okTxt = dialogView.findViewById<View>(R.id.okTxt) as MTextView
        val titileTxt = dialogView.findViewById<View>(R.id.titileTxt) as MTextView
        val msgTxt = dialogView.findViewById<View>(R.id.msgTxt) as WebView

        titileTxt.text = title
        okTxt.text = generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT ")
        // msgTxt.text = "" + Msg
        //  msgTxt.movementMethod = ScrollingMovementMethod()
        msgTxt.loadDataWithBaseURL(
            null,
            generalFunc.wrapHtml(actContext, Msg),
            "text/html",
            "UTF-8",
            null
        )
        okTxt.setOnClickListener { hotoUseDialog.dismiss() }
        hotoUseDialog = builder.create()
        hotoUseDialog.setCancelable(true)
        if (generalFunc.isRTLmode == true) {
            generalFunc.forceRTLIfSupported(hotoUseDialog)
        }
        hotoUseDialog.getWindow()!!
            .setBackgroundDrawable(actContext.resources.getDrawable(R.drawable.all_roundcurve_card,null));
        hotoUseDialog.show()
    }

}