package musicloud;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Copyright_table")
public class Copyright {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long contentId;
    private String status;
    private String artistName;
    private String musicTitle;

    @PostPersist
    public void onPostPersist(){
        this.setStatus("Approved");
        Approved approved = new Approved();
        BeanUtils.copyProperties(this, approved);
        approved.publishAfterCommit();


    }

    @PostUpdate
    public void onPostUpdate(){
        this.setStatus("Recovered");
        Recovered recovered = new Recovered();
        BeanUtils.copyProperties(this, recovered);
        recovered.publishAfterCommit();


    }


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
