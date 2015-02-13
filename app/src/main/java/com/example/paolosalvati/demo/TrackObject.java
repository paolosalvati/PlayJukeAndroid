package com.example.paolosalvati.demo;



/**
 * Created by Paolo on 04/02/2015.
 */
public class TrackObject {
    public static final String TAG_TRACKNAME="tackname";
    public static final String TAG_TRACKID="trackID";
    public static final String TAG_TOTALLIKE="totallike";
    public static final String TAG_POSITION="position";

    private String trackname;
    private String trackID;
    private String totallike;
    private String position;


    public String getTrackName() {
        return trackname;
    }
    public void setTrackName(String trackname) {   this.trackname = trackname;  }

    public String getTrackID() {
        return trackID;
    }
    public void setTrackID(String trackID) {   this.trackID = trackID;  }

    public String getTotalLike() {
        return totallike;
    }
    public void setTotalLike(String totallike) {   this.totallike = totallike;  }

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {   this.position = position;  }

}
