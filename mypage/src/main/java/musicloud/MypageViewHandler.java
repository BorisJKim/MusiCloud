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
                Mypage mypage  = new Mypage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setContentId(uploaded.getId());
                mypage.setCreatorName(uploaded.getCreatorName());
                mypage.setTitle(uploaded.getTitle());
                mypage.setType(uploaded.getType());
                mypage.setDescription(uploaded.getDescription());
                mypage.setStatus(uploaded.getStatus());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
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
                List<Mypage> mypageList = mypageRepository.findByContentId(approved.getContentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(approved.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
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
                List<Mypage> mypageList = mypageRepository.findByContentId(registered.getContentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(registered.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
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
                List<Mypage> mypageList = mypageRepository.findByContentId(deleted.getId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(deleted.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
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
                List<Mypage> mypageList = mypageRepository.findByContentId(recovered.getContentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus(recovered.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
