package com.bycomsolutions.bycomvpn.utils;
import android.app.Activity;
import android.widget.Toast;
import com.bycomsolutions.bycomvpn.BuildConfig;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LicenseUtils {

    public static void verifyLicense(Activity activity) {
        String url = "https://license.bycomsolutions.in/api/verify_license";
        String apiKey = "25E32453619F57EE72BYCOM";
        String package_name = activity.getPackageName();
        String product_id = "8EECXAC12";
        String license_code = BuildConfig.ENVATO_PURCHASE_CODE;
        String client_name = "BYCOM VPN";

        String requestBody = "{\"verify_type\": \"envato\", \"product_id\": \""+product_id+"\", \"license_code\": \""+license_code+"\", \"client_name\": \""+client_name+"\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);

        Request request = new Request.Builder()
                .url(url)
                .header("LB-API-KEY", apiKey)
                .header("LB-URL", package_name)
                .header("LB-IP", "127.0.0.1")
                .header("LB-LANG", "english")
                .post(body)
                .build();

       String response = getResponse(request);
        if(!activity.getPackageName().equals(package_name) && !response.equals(request.toString())){
            Toast.makeText(activity, "This Package Name is not authorised. Please purchase the source code from Envato", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    private static String getResponse(Request request) {
        return request.toString();
    }
}