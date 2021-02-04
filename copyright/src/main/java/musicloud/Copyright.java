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

    @PostPersist
    public void onPostPersist(){
        Approved approved = new Approved();
        BeanUtils.copyProperties(this, approved);
        approved.publishAfterCommit();


    }

    @PostUpdate
    public void onPostUpdate(){
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




}
