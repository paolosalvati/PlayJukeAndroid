// TutorialApp
// Created by Spotify on 25/02/14.
// Copyright (c) 2014 Spotify. All rights reserved.
package com.example.paolosalvati.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends Activity implements
         ConnectionStateCallback {

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d8e85984e9ac47399e41f0954563cce2";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "my-first-android-app-login://callback";

    private final static String SERVICE_URI = "http://jukeserver.cloudapp.net/JukeSvc.svc/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Button btnHost =(Button) findViewById(R.id.buttonSpotifyLogin);
        //Aggiungo Sentinella al Bottone di Login
        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lancio l'Autenticazione via ACS sul Mobile Service di Azure
                SpotifyAuthentication.openAuthWindow( CLIENT_ID, "token", REDIRECT_URI,
                        new String[]{"user-read-private","user-read-email", "streaming"}, null, MenuActivity.this);


            }
        });


        Button btnClient =(Button) findViewById(R.id.buttonClient);
        //Aggiungo Sentinella al Bottone di Login
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating root JSONObject
                JSONObject json = new JSONObject();
                JSONObject json_user = new JSONObject();


                try {
                    json_user.put(UserObject.TAG_USERNAMEID, "robanto@microsoft.com");
                    json_user.put(UserObject.TAG_OS, "Android");
                    json_user.put(PlayListObject.TAG_VERSION, "1.0");


                    json.put(UserObject.TAG_CLIENT, json_user);


                WifiManager mainWifiObj;
                mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
                // Creating a JSONArray
                JSONArray arr = new JSONArray();

                int i=0;
                if (wifiScanList != null) {
                    for (      ScanResult scan : wifiScanList) {
                        String a = scan.BSSID.toString() + " "+scan.SSID.toString()  ;
                        Log.d( "WIFI BSSID", a );

                        /*
                        JSONObject element = new JSONObject();
                        try {
                            element.put(UserObject.TAG_MAC,scan.BSSID.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            element.put(UserObject.TAG_BSSID, scan.SSID.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        */

                        // Put it in the array
                       // arr.put(scan.BSSID.toString().replaceAll(":", "-"));
                        arr.put(scan.BSSID.toString());


                        i=i+1;

                    }
                }
                json.put(UserObject.TAG_MACLIST, arr);
                Log.d("URAaaa",json.toString()) ;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ;


                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                ;

                // POST request to WCF
                HttpPost request = new HttpPost(SERVICE_URI + "ClientRegistration");
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");


                String s_json = "";

                StringEntity entity = null;
                try {
                     s_json=json.toString();

                    Log.d("WebInvoke Client", "uuu : " + s_json);
                     entity = new StringEntity(s_json, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                request.setEntity(entity);

                // Send request to WCF service
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = null;

                try {
                    ResponseHandler<String> responseHandler=new BasicResponseHandler();
                    String responseBody = httpClient.execute(request,responseHandler);
                    Log.d("FATTO",responseBody);
                } catch (ClientProtocolException e) {

                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }





            Intent playActivityIntent = new Intent(getApplicationContext(), PlayActivity.class);
                startActivity(playActivityIntent);


            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {

            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);



            Intent loadPlayListActivityIntent = new Intent(getApplicationContext(), LoadPlayListsActivity.class);
            //Bundle datipassati = getIntent().getExtras();
            //final String  zumo_acs_user_id = datipassati.getString("ZUMO_ACS_USER_ID");
            //final String  zumo_acs_token = datipassati.getString("ZUMO_ACS_TOKEN");
            //loadPlayListActivityIntent.putExtra("SPOTIFY_TOKEN",response.getAccessToken().toString());
            //loadPlayListActivityIntent.putExtra("ZUMO_ACS_TOKEN",zumo_acs_token);
            //loadPlayListActivityIntent.putExtra("ZUMO_ACS_USER_ID",zumo_acs_user_id);
            GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
            globalObjects.setSpotifyAccessToken(response.getAccessToken().toString());
            startActivity(loadPlayListActivityIntent);
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MainActivity", "User credentials blob received");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}