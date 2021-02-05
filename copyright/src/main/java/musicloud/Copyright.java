package musicloud;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
        if("Uploaded".equals(status)) {
            
            Approved approved = new Approved();
            BeanUtils.copyProperties(this, approved);
            
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    approved.publish();
                }
            });
            
            try {
                Thread.currentThread().sleep((long) (400 + Math.random() * 220));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        else if("Deleted".equals(status)) {
            this.setStatus("Recovered");
            Recovered recovered = new Recovered();
            BeanUtils.copyProperties(this, recovered);
            recovered.publish();
        }
    }

    @PostUpdate
    public void onPostUpdate(){
        if("Deleted".equals(status)) {
            this.setStatus("Recovered");
            Recovered recovered = new Recovered();
            BeanUtils.copyProperties(this, recovered);
            recovered.publish();
        }
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
