package project;

import project.PaymentCompleted;
import project.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired RoomManagementRepository roomManagementRepository;

    //결제가 완료되어 최종 계약 요청이 온 경우
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_AddRoomReservation(@Payload PaymentCompleted paymentCompleted){

        if(!paymentCompleted.validate()) return;

        System.out.println("\n\n##### listener paymentCompleted : " + paymentCompleted.toJson() + "\n\n");
        // view 객체 조회
        RoomManagement roomManagement = roomManagementRepository.findByRoomId(paymentCompleted.getRoomId());
        if(roomManagement != null){
            roomManagement.setReservationId(paymentCompleted.getId());
            roomManagement.setCustomerId(paymentCompleted.getCustomerId());
            roomManagement.setCustomerName(paymentCompleted.getCustomerName());
            roomManagement.setCheckInDate(paymentCompleted.getCheckInDate());
            roomManagement.setCheckOutDate(paymentCompleted.getCheckOutDate());
            roomManagement.setRoomStatus("PAY_FINISHED");
            // view 레파지 토리에 insert
            roomManagementRepository.save(roomManagement);
        }
        
    }

    //고객이 취소하여 예약이 취소 처리 진행
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRoomReservationCanceled_CancelReservation(@Payload RoomReservationCanceled roomReservationCanceled){

        if(!roomReservationCanceled.validate()) return;
        System.out.println("\n\n##### listener roomReservationCanceled : " + roomReservationCanceled.toJson() + "\n\n");
        // view 객체 조회
        RoomManagement roomManagement = roomManagementRepository.findByReservationId(roomReservationCanceled.getId());
        if(roomManagement != null){
            roomManagement.setRoomStatus("RSV_CANCELED");
            // view 레파지 토리에 update
            roomManagementRepository.save(roomManagement);
        } 
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

}
