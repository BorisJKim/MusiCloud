package musicloud;

import musicloud.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenUploaded_then_CREATE_1 (@Payload Uploaded uploaded) {
        try {
            if (uploaded.isMe()) {
                // view 객체 생성
                  = new ();
                // view 객체에 이벤트의 Value 를 set 함
                .setContentId(.getId());
                .setCreatorName(.getCreatorName());
                .setTitle(.getTitle());
                .setType(.getType());
                .setDescription(.getDescription());
                .setStatus(.getStatus());
                // view 레파지 토리에 save
                Repository.save();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenApproved_then_UPDATE_1(@Payload Approved approved) {
        try {
            if (approved.isMe()) {
                // view 객체 조회
                List<> List = Repository.findByContentId(.getContentId());
                for(  : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRegistered_then_UPDATE_2(@Payload Registered registered) {
        try {
            if (registered.isMe()) {
                // view 객체 조회
                List<> List = Repository.findByContentId(.getContentId());
                for(  : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeleted_then_UPDATE_3(@Payload Deleted deleted) {
        try {
            if (deleted.isMe()) {
                // view 객체 조회
                List<> List = Repository.findByContentId(.getId());
                for(  : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRecovered_then_UPDATE_4(@Payload Recovered recovered) {
        try {
            if (recovered.isMe()) {
                // view 객체 조회
                List<> List = Repository.findByContentId(.getContentId());
                for(  : List){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}