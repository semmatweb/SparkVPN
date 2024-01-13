package com.bycomsolutions.bycomvpn.utils;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bycomsolutions.bycomvpn.BuildConfig;
import com.bycomsolutions.bycomvpn.Preference;
import com.bycomsolutions.bycomvpn.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import unified.vpn.sdk.AuthMethod;
import unified.vpn.sdk.Callback;
import unified.vpn.sdk.ClientInfo;
import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.HydraTransport;
import unified.vpn.sdk.HydraTransportConfig;
import unified.vpn.sdk.SdkNotificationConfig;
import unified.vpn.sdk.SessionConfig;
import unified.vpn.sdk.TrackingConstants;
import unified.vpn.sdk.TransportConfig;
import unified.vpn.sdk.UnifiedSDK;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.UnifiedSdkConfig;
import unified.vpn.sdk.User;
import unified.vpn.sdk.VpnException;
import unified.vpn.sdk.VpnState;

public class VPNTileService extends TileService {

    public static Tile tile;


    @Override
    public void onStartListening() {
        tile = getQsTile();

    }


    @Override
    public void onClick() {
        tile = getQsTile();
        if (tile.getState() == Tile.STATE_ACTIVE) {
            updateTileState(false);
            disconnectVPN();
        } else {
            updateTileState(true);
            initHydraSdk();
        }
    }

    public static void updateTileState(Boolean isChecked){
        if(tile!=null) {
            if (isChecked) {
                tile.setState(Tile.STATE_ACTIVE);
            }
            else {
                tile.setState(Tile.STATE_INACTIVE);
            }
            tile.updateTile();
        }


    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    public void initHydraSdk() {

        String installerPackageName = getPackageManager().getInstallerPackageName(getPackageName());
        if (!BuildConfig.DEBUG && (installerPackageName == null || !installerPackageName.equals("com.android.vending"))) {
            showToast(getString(R.string.play_verfication_failed));
            updateTileState(false);
        }else {

            ClientInfo clientInfo = UnifiedSDK.getClientInfo(this);
            List<TransportConfig> transportConfigList = new ArrayList<>();
            transportConfigList.add(HydraTransportConfig.create());
            UnifiedSdk.update(transportConfigList, CompletableCallback.EMPTY);
            UnifiedSdkConfig config = UnifiedSDK.getAccessConfig(this);
            UnifiedSdk.getInstance(clientInfo, config);
            SdkNotificationConfig notificationConfig = SdkNotificationConfig.newBuilder()
                    .title(getResources().getString(R.string.app_name))
                    .channelId(getPackageName())
                    .build();
            UnifiedSdk.update(notificationConfig);
            loginToVPN();
            connectVPN();

        }
    }

    public void loginToVPN(){
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSdk.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {
            }

            @Override
            public void failure(@NonNull VpnException e) {
                updateTileState(false);
            }
        });
    }

    public void connectVPN(){

        showToast("Connecting to Fastest Server");

        Preference preference = new Preference(getApplicationContext());

        List<String> excludedApps = new ArrayList<>();
        String json = preference.getStringpreference(BuildConfig.PREFERENCE_KEY_EXCLUDED_LIST);
        if(!json.isEmpty()) {
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            HashMap<String, String> excludedAppMap = new Gson().fromJson(json, type);
            excludedApps = new ArrayList<>(excludedAppMap.keySet());
        }

        UnifiedSdk.getInstance().getVpn().start(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withTransport(HydraTransport.TRANSPORT_ID)
                .exceptApps(excludedApps)
                .build(), new CompletableCallback() {
            @Override
            public void complete() {
                showToast("Connected to Fastest Server");
                updateTileState(true);
            }

            @Override
            public void error(@NonNull VpnException e) {
                showToast("Connection Failed");
                updateTileState(false);
            }
        });
    }


    public void disconnectVPN(){
        UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                showToast("VPN Disconnected");
                updateTileState(false);
            }

            @Override
            public void error(@NonNull VpnException e) {
                showToast("VPN Disconnection Failed");
            }
        });
    }



    public void getVPNState(){
        unified.vpn.sdk.UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState vpnState) {
                updateTileState(vpnState == VpnState.CONNECTED);
                }

            @Override
            public void failure(@NonNull VpnException e) {
                updateTileState(false);
            }
        });
    }


}
