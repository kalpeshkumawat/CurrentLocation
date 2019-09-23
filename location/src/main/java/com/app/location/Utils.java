package com.app.location;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;


import com.thekhaeng.pushdownanim.PushDownAnim;

public class Utils {


    private static Dialog mProgressDialog = null;


    public static void showProgress(Context mContext) {


        if (mContext != null) {


            mProgressDialog = new Dialog(mContext);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setContentView(R.layout.progress_bar_dialog_location);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);

            Activity activity = (Activity) mContext;

            if (mContext == null || activity.isFinishing()) {
                return;
            }


            Log.d("Tracking_Type", "Show Dialog");

            mProgressDialog.show();


        }

    }

    public static void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();

            Log.d("Tracking_Type", "Dismiss Dialog");

        }
    }


    public static void pushDownAnim(View view, View.OnClickListener clickListener) {
        PushDownAnim.setPushDownAnimTo(view)
                .setScale(PushDownAnim.MODE_STATIC_DP, 5f)
                .setOnClickListener(clickListener);
    }

}
