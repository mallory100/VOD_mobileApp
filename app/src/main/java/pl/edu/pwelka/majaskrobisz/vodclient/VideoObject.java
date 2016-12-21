package pl.edu.pwelka.majaskrobisz.vodclient;

import java.io.Serializable;

/**
 * Klasa pojedynczego obiektu wideo pobranego z serwera
 */
public class VideoObject implements Serializable {

    private int videoID;
    private String videoName;
    private String videoDescription;
    private String videoURL;
    private String imageUrl;
    private boolean isBought;


    public VideoObject(String videoName, String videoURL, String videoDescription, boolean isBought, int videoID, String imageUrl) {
        this.videoName = videoName;
        this.videoURL = videoURL;
        this.videoDescription = videoDescription;
        this.isBought = isBought;
        this.videoID = videoID;
        this.imageUrl=imageUrl;
    }

    public boolean getIsBought() {        return isBought;     }
    public void setIsBought(boolean isBought) {         this.isBought = isBought;    }

    public String getImageUrl() {        return imageUrl;     }
    public void setImageUrl (String imageUrl) {         this.imageUrl = imageUrl;    }

    public int getVideoID() {        return videoID;     }
    public void setVideoID(int videoID) {         this.videoID = videoID;    }

    public String getVideoName() {
        return videoName;
    }
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoDescription() {        return videoDescription;     }
    public void setVideoDescription(String videoDescription) {         this.videoDescription = videoDescription;    }

    public String getVideoURL() {
        return videoURL;
    }
    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

}
