package musicloud;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Content_table")
public class Content {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String creatorName;
    private String title;
    private String type;
    private String description;
    private Long sourceId;
    private String status;

    @PostPersist
    public void onPostPersist(){
        System.out.println("******** Content ********");
        Uploaded uploaded = new Uploaded();
        uploaded.setId(this.getId());
        uploaded.setCreatorName(this.getCreatorName());
        uploaded.setTitle(this.getTitle());
        uploaded.setType(this.getType());
        uploaded.setDescription(this.getDescription());
        uploaded.setSourceId(this.getSourceId());
        uploaded.setStatus("Uploaded");
        BeanUtils.copyProperties(this, uploaded);
        uploaded.publish();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        musicloud.external.Copyright copyright = new musicloud.external.Copyright();
        copyright.setContentId(this.getId());
        copyright.setArtistName(this.getCreatorName());
        copyright.setMusicTitle(this.getTitle());
        copyright.setStatus("Started");
        // mappings goes here
        ContentApplication.applicationContext.getBean(musicloud.external.CopyrightService.class)
            .approve(copyright.getId(), copyright);


    }

    @PreRemove
    public void onPreRemove(){
        this.setStatus("Deleted");
        Deleted deleted = new Deleted();
        BeanUtils.copyProperties(this, deleted);
        deleted.publish();
        
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
