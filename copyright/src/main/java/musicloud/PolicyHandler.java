package musicloud;

import musicloud.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }
    
    @Autowired
    CopyrightRepository copyrightRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeleted_CopyrightRecover(@Payload Deleted deleted){

        if(deleted.isMe()){
            System.out.println("##### listener  : " + deleted.toJson());
            
            Copyright copyright = new Copyright();
            copyright.setContentId(deleted.getId());
            copyright.setStatus("Content Deleted.");
            copyrightRepository.save(copyright);
        }
    }

}
