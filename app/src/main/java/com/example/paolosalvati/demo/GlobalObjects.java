package com.example.paolosalvati.demo;

import android.app.Application;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

/**
 * Created by Paolo on 23/02/2015.
 */
public class GlobalObjects extends Application {

    MobileServiceClient zumoClient;
    String spotifyAccessToken;

    public MobileServiceClient getZumoClient() {
        return zumoClient;
    }
    public void setZumoClient(MobileServiceClient zumoClient) {   this.zumoClient = zumoClient;  }
    public String getSpotifyAccessToken() {
        return spotifyAccessToken;
    }
    public void setSpotifyAccessToken(String spotifyAccessToken) {   this.spotifyAccessToken = spotifyAccessToken;  }
}