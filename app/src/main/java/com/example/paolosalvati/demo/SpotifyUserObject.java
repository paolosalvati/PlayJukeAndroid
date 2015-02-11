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

        private String wifiBSSID;
        private String wifiMAC;
        private String wifiSSID;


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



        public String getwifiBSSID() {
            return wifiBSSID;
        }
        public void setwifiBSSID(String wifiBSSID) {
            this.wifiBSSID = wifiBSSID;
        }


        public String getwifiMAC() {
            return wifiMAC;
        }
        public void setwifiMAC(String wifiMAC) {
            this.wifiMAC = wifiMAC;
        }


        public String getwifiSSID() {
            return wifiSSID;
        }
        public void setwifiSSID(String wifiSSID) {
            this.wifiSSID = wifiSSID;
        }

    }
