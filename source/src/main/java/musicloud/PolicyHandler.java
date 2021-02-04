package musicloud;

import musicloud.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    
    @Autowired
    SourceRepository sourceRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverApproved_Register(@Payload Approved approved){

        if(approved.isMe()){
            System.out.println("##### listener  : " + approved.toJson());
            System.out.println("source_policy_approved_register");
            
            Source source = new Source();
            source.setContentId(approved.getContentId());
            source.setArtistName(approved.getArtistName());
            source.setMusicTitle(approved.getMusicTitle());
            source.setStatus("Registered");
            sourceRepository.save(source);
        }
    }

}
