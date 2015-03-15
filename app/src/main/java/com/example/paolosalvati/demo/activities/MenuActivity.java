// TutorialApp
// Created by Spotify on 25/02/14.
// Copyright (c) 2014 Spotify. All rights reserved.
package com.example.paolosalvati.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.dataClasses.UserObject;
import com.example.paolosalvati.demo.dataClasses.WifiObject;
import com.example.paolosalvati.demo.jsonWcf.JsonParserObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyUserObject;
import com.example.paolosalvati.demo.utilities.GlobalObjects;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;




public class MenuActivity extends Activity implements
         ConnectionStateCallback {


    //private   String CLIENT_ID ;//"d8e85984e9ac47399e41f0954563cce2";
    //private   String REDIRECT_URI ;// "my-first-android-app-login://callback";
    private   String SERVICE_URI;//  "http://jukeserver.cloudapp.net/JukeSvc.svc/";

    private WifiObject wifiObject;
    private UserObject userObject;
    private JsonParserObject jsonParserObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        //CLIENT_ID = getApplication().getString(R.string.spotify_client_id);
        //REDIRECT_URI = getApplication().getString(R.string.spotify_redirect_uri);
        SERVICE_URI = getApplication().getString(R.string.azure_wcf_service_uri);

        Button btnHost =(Button) findViewById(R.id.buttonSpotifyLogin);
        //Aggiungo Sentinella al Bottone di Login
        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lancio l'Autenticazione su Spotify
                SpotifyUserObject.userAuthSpotify(MenuActivity.this);
            }
        });


        Button btnClient =(Button) findViewById(R.id.buttonClient);
        //Aggiungo Sentinella al Bottone di Login
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Create WifiObject and Scan wifi list in range
                wifiObject = new WifiObject(getApplicationContext());

                //Create User Object
                userObject = new UserObject();
                userObject.setAuthProvider("Facebook");
                userObject.setMac(wifiObject.getWifiMAC());
                userObject.setSsid(wifiObject.getWifiSSID());
                userObject.setOs("Android");
                userObject.setUsernameID("paolo.salvati@hotmail.it");


                //Con oggetti pre costruiti relativi a User Playlist e Wifi, costruisco Json per la chiamata del wcf
                JsonParserObject jsonParserObject = new JsonParserObject(wifiObject,userObject);
                String s_json = null;
                try {
                    s_json = jsonParserObject.jsonClientRegistration();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                };

                // POST request to WCF
                HttpPost request = new HttpPost(SERVICE_URI + "ClientRegistration");
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");


                StringEntity entity = null;
                try {
                     Log.d("WebInvoke Client muffa", "uuu : " + s_json);
                     entity = new StringEntity(s_json, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                request.setEntity(entity);

                // Send request to WCF service

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = null;
                String songs="";
                Log.d("FATTO","1");
                try {
                    ResponseHandler<String> responseHandler=new BasicResponseHandler();
                    String responseBody = httpClient.execute(request,responseHandler);
                    Log.d("FATTO",responseBody);
                    songs=responseBody;
                } catch (ClientProtocolException e) {
                    Log.d("FATTO","e1"+e.getMessage());
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    Log.d("FATTO","e2"+e.getMessage());
                }





            Intent playActivityIntent = new Intent(getApplicationContext(), ClientActivity.class);
            //String a ="{\"playlist\":{\"playlistID\":\"6W8yEES5dhRCvFjhfRksA7\",\"playlistname\":\"The Smashing Pumpkins – Oceania\",\"provider\":\"Spotify\",\"version\":\"1.0\"},\"tracks\":[{\"position\":0,\"artist\":\"Gigetto\",\"trackID\":\"5GtxiVJjtY8l08mwcNSHhJ\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Quasar\"},{\"position\":1,\"artist\":\"Gigetto\",\"trackID\":\"5hueioKtEJ6GfBvj1JyTH9\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"The Celestials\"},{\"position\":2,\"artist\":\"Gigetto\",\"trackID\":\"3wRJ8kRFf3czgTR5QePcKP\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Panopticon\"},{\"position\":3,\"artist\":\"Gigetto\",\"trackID\":\"2gJg3tbqksHSo6NoRf4F5V\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Violet Rays\"},{\"position\":4,\"artist\":\"Gigetto\",\"trackID\":\"5ERIROxMGj3x5QokC2TuFv\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"My Love Is Winter\"},{\"position\":5,\"artist\":\"Gigetto\",\"trackID\":\"1JKTytj9Fa9wkeqjmwegSR\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"One Diamond, One Heart\"},{\"position\":6,\"artist\":\"Gigetto\",\"trackID\":\"129HD6dvoUFhtHjG6ObV4T\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Pinwheels\"},{\"position\":7,\"artist\":\"Gigetto\",\"trackID\":\"3adfvQ7X2bma9IKIvaRFSp\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Oceania\"},{\"position\":8,\"artist\":\"Gigetto\",\"trackID\":\"3pJ2HaTTQmsGOW1tH4ebOi\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Pale Horse\"},{\"position\":9,\"artist\":\"Gigetto\",\"trackID\":\"1eeomkGph9F87Lfi4PTYhr\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"The Chimera\"},{\"position\":10,\"artist\":\"Gigetto\",\"trackID\":\"3AEuVz87s96g3XsOcPEEsT\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Glissandra\"},{\"position\":11,\"artist\":\"Gigetto\",\"trackID\":\"2aJFpdFNFoimFahi657hJy\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Inkless\"},{\"position\":12,\"artist\":\"Gigetto\",\"trackID\":\"3aGAB5LqNlwzM6zg7bbHZL\",\"album\":\"Del meglio del suo meglio\",\"trackname\":\"Wildflower\"}],\"host\":{\"os\":\"ANdroid\",\"bssid\":\"TISCALI\",\"mac\":\"00:18:60:55:76:18\",\"authprovider\":\"Facebook\",\"usernameID\":\"paolo.salvati@hotmail.it\"}}\"";
            //Log.d("SONGS",a);
            playActivityIntent.putExtra("SONGS",songs);
            startActivity(playActivityIntent);




            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();

        //Se avrò vari music provider potrò eseguire il case sull uri...!!!
        if (uri != null) {

            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Intent loadPlayListActivityIntent = new Intent(getApplicationContext(), HostActivity.class);
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
        Log.d("MenuActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MenuActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MenuActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MenuActivity", "Temporary error occurred");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MenuActivity", "User credentials blob received");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MenuActivity", "Received connection message: " + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}