package musicloud;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Source_table")
public class Source {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String artistName;
    private String musicTitle;
    private String status;
    private Long contentId;

    @PostPersist
    public void onPostPersist(){
        Registered registered = new Registered();
        BeanUtils.copyProperties(this, registered);
        registered.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }




}
