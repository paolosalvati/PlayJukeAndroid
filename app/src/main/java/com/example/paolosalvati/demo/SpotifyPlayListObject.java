package com.example.paolosalvati.demo;

import java.net.URI;

/**
 * Created by paolo.salvati on 22/01/2015.
 */
public class SpotifyPlayListObject {


        // JSON Node names
        public static final String TAG_COLLABORATIVE = "collaborative";
        public static final String TAG_HREF = "href";
        public static final String TAG_ID = "id";
        public static final String TAG_IMAGES = "images";
        public static final String TAG_NAME = "name";
        public static final String TAG_PUBLIC = "public";
        public static final String TAG_TRACKS="tracks";
        public static final String TAG_TRACKS_HREF="href";
        public static final String TAG_TRACKS_TOTAL="total";
        public static final String TAG_URI = "uri";
        public static final String TAG_ITEMS = "items";


        private boolean collaborative;
        private String href;
        private String id;
        private String[] images;
        private String name;
        private boolean playlistpubblica;
        private String tracksHref;
        private int tracksTotal;
        private String playlisturi;


        public boolean getCollaborative() {
        return collaborative;
        }
        public void setCollaborative(Boolean id) {   this.collaborative = collaborative;  }

        public String getHref() { return href;}
        public void setHref(String href) {
        this.href = href;
        }

        public String getID() {
        return id;
        }
        public void setID(String id) {
        this.id = id;
        }

        public String[] getImages() {return images;}
        public void setIDImages(String[] images) {this.images = images;}

        public String getName() {return name;}
        public void setName(String name) {
        this.name = name;
        }

        public boolean getPlayPubblica() {
        return playlistpubblica;
        }
        public void setPlayPubblica(boolean playlistpubblica) {this.playlistpubblica = playlistpubblica; }

        public String getTracksHref() {
        return tracksHref;
    }
        public void setTracksHref(String tracksHref) {this.tracksHref = tracksHref; }

        public int getTracksTotal() {
            return tracksTotal;
        }
        public void setTracksTotal(int tracksTotal) {this.tracksTotal = tracksTotal; }

        public String getPlayListUri() {
        return playlisturi;
        }
        public void setlPlayListUri(String playlisturi) {
        this.playlisturi = playlisturi;
        }




    }
