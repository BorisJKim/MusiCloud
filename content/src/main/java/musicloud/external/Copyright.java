package musicloud.external;

public class Copyright {

    private Long id;
    private Long contentId;
    private String status;
    private String artistName;
    private String musicTitle;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getContentId() {
        return contentId;
    }
    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public String getMusicTitle() {
        return musicTitle;
    }
    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

}
