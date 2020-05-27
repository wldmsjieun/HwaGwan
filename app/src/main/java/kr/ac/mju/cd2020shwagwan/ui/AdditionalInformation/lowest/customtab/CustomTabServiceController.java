package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.customtab;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import java.lang.ref.WeakReference;

public class CustomTabServiceController extends CustomTabsServiceConnection {
    // 빨리 웹 들어가게 해주는 거
    private static final String CUSTOM_TABS_EXTRA_SESSION = "android.support.customtabs.extra.SESSION";
    private static final String CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR"; //

    private WeakReference<Context> ctscContextWeakRef;
    // garbage collector가 다 회수
    private String ctscUrlToLoadStr;
    private CustomTabsSession ctscCustomTabsSession;

    public CustomTabServiceController(Context context, String urlToLoad) {
        ctscContextWeakRef = new WeakReference<>(context);
        this.ctscUrlToLoadStr = urlToLoad;
    }

    @Override
    public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
        if (customTabsClient != null) {
            customTabsClient.warmup(0L);
            ctscCustomTabsSession = customTabsClient.newSession(null);
            if (!TextUtils.isEmpty(ctscUrlToLoadStr)) {
                Uri uri = Uri.parse(ctscUrlToLoadStr);
                if (uri != null && ctscCustomTabsSession != null) {
                    ctscCustomTabsSession.mayLaunchUrl(uri, null, null);
                }
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        ctscCustomTabsSession = null;
    }

    public void bindCustomTabService() {
        Context ctscContext = ctscContextWeakRef.get();
        String ctscPackageName = CustomTabHelper.getPackageNameToUse(ctscContext, this.ctscUrlToLoadStr);
        if (ctscContext == null || ctscPackageName == null) { return; }
        CustomTabsClient.bindCustomTabsService(ctscContext, ctscPackageName, this);
    }

    public void unbindCustomTabService() {
        Context ctscContext = ctscContextWeakRef.get();
        if (ctscContext != null) { ctscContext.unbindService(this); }
    }

    @SuppressLint("NewApi")
    public Intent createCustomTabIntent(Binder session, int toolbarColor) {

        Context ctscContext = ctscContextWeakRef.get();
        Intent ctscIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ctscUrlToLoadStr));
        String ctscPackageName = CustomTabHelper.getPackageNameToUse(ctscContext, this.ctscUrlToLoadStr);
        String ctscHex = "c3b0eb";
        int ctscHexToint = Integer.parseInt(ctscHex, 16);
        if (ctscPackageName != null) {
            ctscIntent.setPackage(ctscPackageName);
            Bundle ctscExtras = new Bundle();
            ctscExtras.putInt(CUSTOM_TABS_TOOLBAR_COLOR, ctscHexToint);
            ctscExtras.putBinder(CUSTOM_TABS_EXTRA_SESSION, session);
            ctscExtras.putParcelable(Intent.EXTRA_REFERRER, Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + ctscContext.getPackageName()));
            ctscIntent.putExtras(ctscExtras);
        }
        return ctscIntent;
    }

}