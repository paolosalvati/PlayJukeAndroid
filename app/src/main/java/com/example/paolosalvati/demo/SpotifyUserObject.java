package com.example.paolosalvati.demo;

/**
 * Created by paolo.salvati on 22/01/2015.
 */
public class SpotifyUserObject {


        // JSON Node names
        public static final String TAG_COUNTRY = "country";
        public static final String TAG_DISPLAY_NAME = "display_name";
        public static final String TAG_EMAIL = "email";
        public static final String TAG_ID = "id";


        private String country;
        private String display_name;
        private String email;
        private String id;


        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        public String getCountry() {
        return country;
        }
        public void setCountry(String country) {
        this.country = country;
        }

        public String getDisplayName() {
        return display_name;
        }
        public void setDisplayname(String display_name) {
        this.display_name = display_name;
        }

        public String getEmail() {
        return email;
        }
        public void setEmail(String email) {
        this.email = email;
        }



    }
