package musicloud;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Mypage_table")
public class Mypage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long contentId;
        private String creatorName;
        private String title;
        private String type;
        private String description;
        private Long sourceId;
        private String status;
        private Long copyrightId;


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
        public Long getCopyrightId() {
            return copyrightId;
        }

        public void setCopyrightId(Long copyrightId) {
            this.copyrightId = copyrightId;
        }

}
