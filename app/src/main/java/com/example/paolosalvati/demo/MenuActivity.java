// TutorialApp
// Created by Spotify on 25/02/14.
// Copyright (c) 2014 Spotify. All rights reserved.
package com.example.paolosalvati.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class MenuActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {


    private static final String CLIENT_ID = "d8e85984e9ac47399e41f0954563cce2";

    private static final String REDIRECT_URI = "my-first-android-app-login://callback";

    //Get ACS ZUMO Access Token provided by the collaing MenuActivity



    private Player mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Button btnLogin =(Button) findViewById(R.id.buttonSpotifyLogin);
        //ggiungo Sentinella al Bottone di Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lancio l'Autenticazione via ACS sul Mobile Service di Azure
                SpotifyAuthentication.openAuthWindow( CLIENT_ID, "token", REDIRECT_URI,
                        new String[]{"user-read-private","user-read-email", "streaming"}, null, MenuActivity.this);


            }
        });



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {

            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Log.i("Spoty",response.getAccessToken());
            Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
            Spotify spotify = new Spotify();
            Intent loadPlayListActivityIntent = new Intent(getApplicationContext(), LoadPlayListsActivity.class);
            Bundle datipassati = getIntent().getExtras();
            final String  zumo_acs_user_id = datipassati.getString("ZUMO_ACS_USER_ID");
            final String  zumo_acs_token = datipassati.getString("ZUMO_ACS_TOKEN");
            loadPlayListActivityIntent.putExtra("SPOTIFY_TOKEN",response.getAccessToken().toString());
            loadPlayListActivityIntent.putExtra("ZUMO_ACS_TOKEN",zumo_acs_token);
            loadPlayListActivityIntent.putExtra("ZUMO_ACS_USER_ID",zumo_acs_user_id);

            startActivity(loadPlayListActivityIntent);
            /*
            mPlayer = spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mPlayer.addConnectionStateCallback(MenuActivity.this);
                    mPlayer.addPlayerNotificationCallback(MenuActivity.this);
                    mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
            */
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
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}