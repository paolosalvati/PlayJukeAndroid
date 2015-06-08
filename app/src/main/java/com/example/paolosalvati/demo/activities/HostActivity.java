package com.example.paolosalvati.demo.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.adapters.PlaylistHostAdapetr;
import com.example.paolosalvati.demo.dataClasses.PlayListObject;
import com.example.paolosalvati.demo.dataClasses.TrackObject;
import com.example.paolosalvati.demo.dataClasses.TracksArrayObject;
import com.example.paolosalvati.demo.dataClasses.UserObject;
import com.example.paolosalvati.demo.dataClasses.WifiObject;
import com.example.paolosalvati.demo.jsonWcf.JsonParserObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyAllPlaylistsObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyAllTracksObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyPlaylistObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyTracksObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyUserObject;
import com.example.paolosalvati.demo.utilities.GlobalObjects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class HostActivity extends Activity {

    SpotifyUserObject spotifyUserObject;
    SpotifyAllPlaylistsObject spotifyAllPlaylistsObject=  new SpotifyAllPlaylistsObject();
    SpotifyAllTracksObject spotifyAllTracksObject=  new SpotifyAllTracksObject();

    WifiObject wifiObject;
    UserObject userObject;
    PlayListObject playListObject;
    TrackObject trackObject;
    TracksArrayObject tracksArrayObject;
    //List<TrackObject> tracksList = new ArrayList<TrackObject>();

    private  String SERVICE_URI;// = "http://jukeserver.cloudapp.net/JukeSvc.svc/";

    // contacts JSONArray
    //JSONArray jsonArrayALLPlaylists = null;
    //JSONArray jsonArrayALLTracks = null;

    //JSONObject jsonObjectToSpotify;
    //JSONArray jsonArrayToSpotify;


    private ListAdapter playListAdapter = null;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Marina","g");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        ActionBar ab = getActionBar();
        SERVICE_URI = getApplication().getString(R.string.azure_wcf_service_uri);

        final GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
        final MobileServiceClient zumoClient = globalObjects.getZumoClient();
        final String spotifyToken = globalObjects.getSpotifyAccessToken();

        //Save the application Context
        final Context context = this;

        //Get ACS ZUMO Access Token provided by the collaing MenuActivity
        //Bundle datipassati = getIntent().getExtras();
        //final String zumoAcsToken = datipassati.getString("ZUMO_ACS_TOKEN");
        //final String zumoAcsUserId = datipassati.getString("ZUMO_ACS_USER_ID");
        //final String spotifyToken = datipassati.getString("SPOTIFY_TOKEN");

        //Insance of final classes Spotify User & Spotify PlayList Objects

        //final ArrayList<SpotifyPlaylistObject> ArrayALLPlayList = new ArrayList<SpotifyPlaylistObject>();
        final ArrayList<SpotifyTracksObject> ArrayALLTracks = new ArrayList<SpotifyTracksObject>();

        wifiObject = new WifiObject(context);


        //Costruisco ogetto User
        userObject = new UserObject();
        userObject.setAuthProvider("Facebook");
        userObject.setMac(wifiObject.getWifiBSSID());
        userObject.setSsid(wifiObject.getWifiSSID());
        userObject.setOs("Android");
        userObject.setUsernameID("paolo.salvati@hotmail.it");

        //Insance of a  JsonObjent that ll hold all infos for building the header' parameters in the HTTP Call
        final JsonObject input = new JsonObject();
        //Add to the JsonObjent holding headers parameter for the HTTP Call the Spotify Access Token
        input.addProperty("Token", spotifyToken);
        //1.A]Invoke ZUMO CUsom API to get users'infos
        Log.d("Marina","h");
        zumoClient.invokeApi("api_pjk_spotify_get_user", input,
                HttpPost.METHOD_NAME, null,
                new ApiJsonOperationCallback()
                {

                    @Override
                    public void onCompleted(JsonElement jsonData,Exception error,ServiceFilterResponse response)
                    {
                        Log.d("Marina","i");
                        Log.d("Marina",response.getContent().toString());
                        String jsonApiUserResponse = response.getContent().toString();
                        Log.d("Marina","sssss1");
                        if (jsonApiUserResponse != null)
                        {
                            Log.d("Marina","sssss2");
                            try
                            {
                                JSONObject jsonUserObj = new JSONObject(jsonApiUserResponse);
                                spotifyUserObject= new SpotifyUserObject(jsonUserObj);

                                        //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                        //Add to the JsonObjent holding headers parameter for the HTTP Call the Spotify Access Token
                                final JsonObject input = new JsonObject();
                                input.addProperty("Token", spotifyToken);
                                input.addProperty("User", spotifyUserObject.getId());
                                Log.d("Marina","sssss3");
                                Log.d("Marina",spotifyUserObject.getId().toString());

                                        // HTTP Call the Spotify Get Playlists
                                        zumoClient.invokeApi(
                                                "api_pjk_spotify_get_playlists"
                                                , input
                                                , HttpPost.METHOD_NAME
                                                , null
                                                , new ApiJsonOperationCallback() {
                                                    @Override
                                                    public void onCompleted(JsonElement jsonData, Exception error, ServiceFilterResponse response) {
                                                        String jsonApiPlayListrResponse = response.getContent().toString();
                                                        Log.d("Marina t", response.getContent().toString());
                                                        if (jsonApiPlayListrResponse != null) {
                                                            try {
                                                                JSONObject jsonPlayListObj = new JSONObject(jsonApiPlayListrResponse);
                                                                // Getting JSON Array node

                                                                spotifyAllPlaylistsObject.setJsonArrayALLPlaylists(jsonPlayListObj);
                                                                spotifyAllPlaylistsObject.setArrayALLPlayList(spotifyAllPlaylistsObject.getJsonArrayALLPlaylists());

                                                                //SHOW PLAYLIST
                                                                playListAdapter = new PlaylistHostAdapetr(spotifyAllPlaylistsObject.getArrayALLPlayList());
                                                                GridView allPlayLists = (GridView) findViewById(R.id.allPlayList);
                                                                allPlayLists.setAdapter(playListAdapter);

                                                                //SETTO l'setOnItemClickListener per invocare il WCF passando la playlist selezionata
                                                                allPlayLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                    @Override
                                                                    public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {


                                                                        final SpotifyPlaylistObject playlistItem = (SpotifyPlaylistObject) adattatore.getItemAtPosition(pos);

                                                                        //Costruisco ogetto Playlist
                                                                        playListObject = new PlayListObject();
                                                                        playListObject.setProvider("Spotify");
                                                                        playListObject.setVersion("1.0");
                                                                        playListObject.setPlaylistID(playlistItem.getID());
                                                                        playListObject.setPlaylistuserID(spotifyUserObject.getId());
                                                                        playListObject.setPlaylistName(playlistItem.getName());
                                                                        playListObject.setPlaylistImageUri(playlistItem.getImageUri());
                                                                        Log.d("Marina s", spotifyUserObject.getId());
                                                                        Log.d("Marina s",playlistItem.getID());
                                                                                Log.d("Marina s",spotifyToken);
                                                                        //INIZIO RECUPERO LE TRACKS
                                                                        input.addProperty("User", spotifyUserObject.getId());
                                                                        input.addProperty("Playlist", playlistItem.getID());
                                                                        input.addProperty("Token", spotifyToken);
                                                                        //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                                                        zumoClient.invokeApi(
                                                                                "api_pjk_spotify_get_tracks"
                                                                                , input
                                                                                , HttpPost.METHOD_NAME
                                                                                , null
                                                                                , new ApiJsonOperationCallback() {
                                                                                    @Override
                                                                                    public void onCompleted(JsonElement jsonData, Exception error, ServiceFilterResponse response) {
                                                                                        String jsonApiTracksResponse = response.getContent().toString();
                                                                                        Log.i("Paolo:LoadPlayListsActivity:Track1", response.getContent().toString());// getAsJsonObject().getAsString());
                                                                                        if (jsonApiTracksResponse != null) {

                                                                                            try {
                                                                                                JSONObject jsonTracksObj = new JSONObject(jsonApiTracksResponse);
                                                                                                // Getting JSON Array node

                                                                                                spotifyAllTracksObject.setJsonArrayALLTracks(jsonTracksObj);
                                                                                                spotifyAllTracksObject.setArrayALLTracks(spotifyAllTracksObject.getJsonArrayALLTracks());

                                                                                                //Costruisco ogetto Tracks
                                                                                                tracksArrayObject = new TracksArrayObject(spotifyAllTracksObject);

                                                                                                //Con oggetti pre costruiti relativi a User Playlist e Tracks, costruisco Json per la chiamata del wcf
                                                                                                JsonParserObject jsonParserObject = new JsonParserObject(userObject,playListObject,tracksArrayObject);
                                                                                                String s_json =jsonParserObject.jsonLoadPlaylist();

                                                                                                if (android.os.Build.VERSION.SDK_INT > 9) {
                                                                                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                                                                                    StrictMode.setThreadPolicy(policy);
                                                                                                }
                                                                                                ;

                                                                                                // POST request to WCF
                                                                                                HttpPost request = new HttpPost(SERVICE_URI + "LoadPlaylist");
                                                                                                request.setHeader("Accept", "application/json");
                                                                                                request.setHeader("Content-type", "application/json");



                                                                                                StringEntity entity = null;
                                                                                                try {
                                                                                                    Log.d("WebInvoke: json", s_json);
                                                                                                    entity = new StringEntity(s_json, "UTF-8");
                                                                                                } catch (UnsupportedEncodingException e) {
                                                                                                    e.printStackTrace();
                                                                                                }
                                                                                                request.setEntity(entity);

                                                                                                // Send request to WCF service
                                                                                                DefaultHttpClient httpClient = new DefaultHttpClient();
                                                                                                HttpResponse httpResponse = null;

                                                                                                try {
                                                                                                    httpResponse = httpClient.execute(request);
                                                                                                } catch (IOException e) {
                                                                                                    e.printStackTrace();
                                                                                                    Log.d("WebInvoke", "KO : " + e.toString());
                                                                                                }
                                                                                                Log.d("WebInvoke", "OK : " + httpResponse.getStatusLine().toString());

                                                                                                //Chiamata all'Activity del Player
                                                                                                Intent loadPlayActivityIntent = new Intent(getApplicationContext(), PlayActivity.class);
                                                                                                loadPlayActivityIntent.putExtra("SONGS", s_json);
                                                                                                loadPlayActivityIntent.putExtra("IMAGE_URI", playListObject.getPlaylistImageUri());
                                                                                                startActivity(loadPlayActivityIntent);


                                                                                            } catch (JSONException e) {
                                                                                                e.printStackTrace();
                                                                                                Log.i("BBBBB", e.toString());
                                                                                            }
                                                                                        } else {
                                                                                            Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_playlists");
                                                                                        }
                                                                                    }
                                                                                }
                                                                        );


                                                                        //FINE RECUPERO LE TRACKS


                                                                    }
                                                                });
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_playlists");
                                                        }
                                                    }
                                                }
                                        );

                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        } else
                        {
                            Log.e("Paolo:LoadPlayListsActivity:User", "Error Calling ZUMO API api_pjk_spotify_get_user");
                        }
                     }
                });

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d("marina","3");
        MenuInflater mMenuInflater = getMenuInflater();
        Log.d("marina","4");
        mMenuInflater.inflate(R.menu.menu_tab, menu);
        Log.d("marina","5");
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_mi_client:



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

                SERVICE_URI = getApplication().getString(R.string.azure_wcf_service_uri);
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
                if(songs!="") {
                    //Lancio la Client Activity

                    //Intent playActivityIntent = new Intent(getApplicationContext(), TabMenuActivity.class);

                    Intent playActivityIntent = new Intent(getApplicationContext(), ClientActivity.class);
                    playActivityIntent.putExtra("SONGS", songs);
                    startActivity(playActivityIntent);
                } else{
                    Intent playActivityIntent = new Intent(getApplicationContext(), TabMenuActivity.class);
                    //Intent playActivityIntent = new Intent(getApplicationContext(), ClientActivity.class);
                    //playActivityIntent.putExtra("SONGS", songs);
                    startActivity(playActivityIntent);
                }



                return true;
            case R.id.ab_mi_host:

                return true;
            //Lancio l'Autenticazione su Spotify

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
