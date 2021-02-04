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
    ContentRepository contentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegistered_ContentStatus(@Payload Registered registered){

        if(registered.isMe()){
            System.out.println("##### listener  : " + registered.toJson());
            
            Optional<Content> contentOptional = contentRepository.findById(registered.getContentId());
            Content content = contentOptional.get();
            content.setSourceId(registered.getId());
            content.setStatus("Registered");
            contentRepository.save(content);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRecovered_ContentStatus(@Payload Recovered recovered){

        if(recovered.isMe()){
            System.out.println("##### listener  : " + recovered.toJson());
            
            Optional<Content> contentOptional = contentRepository.findById(recovered.getContentId());
            Content content = contentOptional.get();
            content.setStatus("Recovered");
            contentRepository.save(content);
        }
    }

}
