package com.example.paolosalvati.demo.dataClasses;



/**
 * Created by Paolo on 22/02/2015.
 */
public class UserObject {




        // TAG for JSON parsing
        public static final String TAG_USER = "host";
        public static final String TAG_CLIENT = "client";
    public static final String TAG_HOST = "host";
        public static final String TAG_MAC = "mac";
        public static final String TAG_MACLIST = "MacList";

        public static final String TAG_BSSID = "bssid";
        public static final String TAG_AUTHPROVIDER="authprovider";
        public static final String TAG_USERNAMEID = "usernameID";
        public static final String TAG_OS = "os";

        //Membri

        private String mac;
        private String bssid;
        private String authprovider;
        private String usernameID;
        private String os;

        //Getter and Setter
        public String getMac() {
            return mac;
        }
        public void setMac(String mac) {
            this.mac = mac;
        }



        public String getSsid() {
            return bssid;
        }
        public void setSsid(String bssid) {
            this.bssid = bssid;
        }


        public String getAuthProvider() {
            return authprovider;
        }
        public void setAuthProvider(String authprovider) {
            this.authprovider= authprovider;
        }


        public String getUsernameID() {
            return usernameID;
        }
        public void setUsernameID(String usernameID) {
            this.usernameID = usernameID;
        }


        public String getOs() {
            return os;
        }
        public void setOs(String os) {
            this.os= os;
        }




}
