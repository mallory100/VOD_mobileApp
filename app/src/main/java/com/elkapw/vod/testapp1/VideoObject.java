package com.elkapw.vod.testapp1;

import java.io.Serializable;

/**
 * Created by Majka on 2016-09-25.
 */
public class VideoObject implements Serializable {

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    private String videoName;
    private String videoDescription;
    private String videoURL;


    public VideoObject(String videoName, String videoURL, String videoDescription) {
        this.videoName = videoName;
        this.videoURL = videoURL;
        this.videoDescription = videoDescription;
    }
}
