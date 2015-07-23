package com.webview.receiver;

import com.webview.content.ViewContent;
import com.webview.http.InterfaceOp;
import com.webview.util.AppConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateOrgIdReceiver extends BroadcastReceiver
{
    private Context mContext;

    public UpdateOrgIdReceiver(Context context)
    {
        this.mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ViewContent.ACTION_ORG_ID))
        {
            String org_id = intent.getStringExtra("org_id");
            String url_domain = intent.getStringExtra("url_domain");
            if(org_id.isEmpty() || (org_id == null))return;
            if(url_domain.isEmpty() || (url_domain == null))return;
            AppConstants.ORGANIZE_ID = org_id;
            InterfaceOp.URL_DOMAIN = url_domain;
            //Log.e("UpdateOrgIdReceiver", url_domain);
        }
    }
}
