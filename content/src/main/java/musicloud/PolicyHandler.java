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
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegistered_(@Payload Registered registered){

        if(registered.isMe()){
            System.out.println("##### listener  : " + registered.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRecovered_(@Payload Recovered recovered){

        if(recovered.isMe()){
            System.out.println("##### listener  : " + recovered.toJson());
        }
    }

}