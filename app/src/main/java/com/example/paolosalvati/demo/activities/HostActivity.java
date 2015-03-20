package com.example.paolosalvati.demo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

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
        zumoClient.invokeApi("api_pjk_spotify_get_user", input,
                HttpPost.METHOD_NAME, null,
                new ApiJsonOperationCallback()
                {

                    @Override
                    public void onCompleted(JsonElement jsonData,Exception error,ServiceFilterResponse response)
                    {
                        String jsonApiUserResponse = response.getContent().toString();
                        if (jsonApiUserResponse != null)
                        {
                            try
                            {
                                JSONObject jsonUserObj = new JSONObject(jsonApiUserResponse);
                                spotifyUserObject= new SpotifyUserObject(jsonUserObj);

                                        //2.A]Invoke ZUMO CUsom API to get playlist'infos
                                        //Add to the JsonObjent holding headers parameter for the HTTP Call the Spotify Access Token
                                        input.addProperty("User", spotifyUserObject.getId());
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

                                                        if (jsonApiPlayListrResponse != null) {
                                                            try {
                                                                JSONObject jsonPlayListObj = new JSONObject(jsonApiPlayListrResponse);
                                                                // Getting JSON Array node

                                                                spotifyAllPlaylistsObject.setJsonArrayALLPlaylists(jsonPlayListObj);
                                                                spotifyAllPlaylistsObject.setArrayALLPlayList(spotifyAllPlaylistsObject.getJsonArrayALLPlaylists());

                                                                //SHOW PLAYLIST
                                                                playListAdapter = new PlaylistHostAdapetr(spotifyAllPlaylistsObject.getArrayALLPlayList());
                                                                ListView allPlayLists = (ListView) findViewById(R.id.allPlayList);
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
                                                                                        Log.i("Paolo:LoadPlayListsActivity:Track", response.getContent().toString());// getAsJsonObject().getAsString());
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_play_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
