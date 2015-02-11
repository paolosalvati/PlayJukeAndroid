package com.example.paolosalvati.demo;

/**
 * Created by paolo.salvati on 22/01/2015.
 */
public class SpotifyTracksObject {


        // JSON Node names
        public static final String TAG_ITEMS = "items";
        public static final String TAG_TRACKS = "track";
        public static final String TAG_ID = "id";
        public static final String TAG_NAME = "name";





        private String id;

        private String name;




        public String getID() {
        return id;
        }
        public void setID(String id) {
        this.id = id;
        }


        public String getName() {return name;}
        public void setName(String name) {
        this.name = name;
        }




    }
