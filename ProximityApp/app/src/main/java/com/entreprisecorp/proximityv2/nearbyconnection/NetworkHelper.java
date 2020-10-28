package com.entreprisecorp.proximityv2.nearbyconnection;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class NetworkHelper {

    private boolean advertising = false;
    private boolean discovering = false;
    private Context appContext;
    private final ConnectionsClient connectionsClient;
    private String infoConnection;
    private String emailDiscovered;
    private static String TAG = "nearbyConnection";


    //---------PERMISSION HANDLER--------------//

    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER; // Nearby connection strategy for data sending

    public static int getRequestCodeRequiredPermissions() {
        return REQUEST_CODE_REQUIRED_PERMISSIONS;
    }

    public static String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }


    /**
     * Constructor NetworkHelper
     * @param appContext
     * @param infoConnection
     */
    public NetworkHelper(Context appContext, String infoConnection) {
        this.appContext = appContext;
        this.connectionsClient = Nearby.getConnectionsClient(appContext);
        this.infoConnection = infoConnection;
    }

    public void StopAll(){
        connectionsClient.stopAllEndpoints();
        discovering = false;
        advertising = false;

    }


    // find a phone nearby
    public void SearchPeople() {
        if (!discovering && !advertising) {
            startAdvertising();
            startDiscovery();
        }
    }


    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.d(TAG, info.getEndpointName());
                }
                @Override
                public void onEndpointLost(String endpointId) {
                }
            };


    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo info) {
                }
                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                }
                @Override
                public void onDisconnected(String endpointId) {
                }
            };


    private void startDiscovery() {
        if(!discovering) {
            connectionsClient
                    .startDiscovery(
                            appContext.getPackageName(), endpointDiscoveryCallback,
                            new DiscoveryOptions.Builder().setStrategy(STRATEGY).build())
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unusedResult) {
                                    discovering = true;
                                    Log.d(TAG, "Now discovering endpoint " + infoConnection);

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    discovering = false;
                                    Log.w(TAG, "startDiscovery() failed.", e);

                                }
                            });
        }
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        if(!advertising) {
            connectionsClient
                    .startAdvertising(
                            infoConnection, appContext.getPackageName(), connectionLifecycleCallback,
                            new AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unusedResult) {
                                    advertising = true;
                                    Log.d(TAG, "Now advertising endpoint " + infoConnection);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    advertising = false;
                                    Log.w(TAG, "startAdvertising() failed.", e);
                                }
                            });
        }
    }

}
