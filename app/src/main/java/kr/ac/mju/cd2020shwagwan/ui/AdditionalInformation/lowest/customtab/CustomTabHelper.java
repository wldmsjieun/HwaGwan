package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.customtab;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import static androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;

public class CustomTabHelper {

    public static String getPackageNameToUse(Context context, String urlToLoad) {

        PackageManager cthPackageManager = context.getPackageManager();

        Intent cthIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToLoad));
        ResolveInfo defaultViewHandlerInfo = cthPackageManager.resolveActivity(cthIntent, 0);

        String defaultViewHandlerPackageName = null;

        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        List<ResolveInfo> cthResolvedActivityList = cthPackageManager.queryIntentActivities(cthIntent, 0);
        List<String> cthPackagesSupportingCustomTabs = new ArrayList<>();

        for (ResolveInfo cthInfo : cthResolvedActivityList) {
            Intent cthServiceIntent = new Intent();
            cthServiceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            cthServiceIntent.setPackage(cthInfo.activityInfo.packageName);
            if (cthPackageManager.resolveService(cthServiceIntent, 0) != null) {
                cthPackagesSupportingCustomTabs.add(cthInfo.activityInfo.packageName);
            }
        }

        if (cthPackagesSupportingCustomTabs.size() > 0) {
            if (!TextUtils.isEmpty(defaultViewHandlerPackageName) &&
                    cthPackagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
                return defaultViewHandlerPackageName;
            } else {
                return cthPackagesSupportingCustomTabs.get(0);
            }
        }

        return null;
    }

}