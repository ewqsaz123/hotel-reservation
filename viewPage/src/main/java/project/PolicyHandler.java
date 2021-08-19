package project;

import project.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    private ReservationStatusViewRepository reservationStatusViewRepository;

    //호텔 매니저가 룸을 생성함
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRoomCreated_then_CREATE(@Payload RoomCreated roomCreated) {
        try {

            if (!roomCreated.validate()) return;
            
            System.out.println("\n\n##### listener RoomCreated : " + roomCreated.toJson() + "\n\n");
            // view 객체 생성
            ReservationStatusView reservationStatusView = new ReservationStatusView();
            // view 객체에 이벤트의 Value 를 set 함
            reservationStatusView.setHotelId(roomCreated.getHotelId());
            reservationStatusView.setHotelName(roomCreated.getHotelName());
            reservationStatusView.setRoomId(roomCreated.getRoomId());
            reservationStatusView.setRoomName(roomCreated.getRoomName());
            reservationStatusView.setRoomPrice(roomCreated.getRoomPrice());
            reservationStatusView.setReservationStatus("ROOM_CREATED");
            // view 레파지 토리에 insert
            reservationStatusViewRepository.save(reservationStatusView);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //고객이 예약을 요청함
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRoomReservationReqeusted_then_UPDATE(@Payload RoomReservationReqeusted roomReservationReqeusted) {
        try {
            if (!roomReservationReqeusted.validate()) return;
            
            System.out.println("\n\n##### listener RoomReservationReqeusted : " + roomReservationReqeusted.toJson() + "\n\n");
            // view 객체 조회
            ReservationStatusView reservationStatusView = reservationStatusViewRepository.findByRoomId(roomReservationReqeusted.getRoomId());
            if(reservationStatusView != null){
                reservationStatusView.setReservationId(roomReservationReqeusted.getId());
                reservationStatusView.setCustomerId(roomReservationReqeusted.getCustomerId());
                reservationStatusView.setCustomerName(roomReservationReqeusted.getCustomerName());
                reservationStatusView.setCheckInDate(roomReservationReqeusted.getCheckInDate());
                reservationStatusView.setCheckOutDate(roomReservationReqeusted.getCheckOutDate());
                reservationStatusView.setReservationStatus("RSV_REQUESTED");
                // view 레파지 토리에 update
                reservationStatusViewRepository.save(reservationStatusView);  
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //고객이 결제를 완료함
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCompleted_then_UPDATE(@Payload PaymentCompleted paymentCompleted) {
        try {
            if (!paymentCompleted.validate()) return;
            
            System.out.println("\n\n##### listener PaymentCompleted : " + paymentCompleted.toJson() + "\n\n");
            // view 객체 조회
            ReservationStatusView reservationStatusView = reservationStatusViewRepository.findByReservationId(paymentCompleted.getId());
            if(reservationStatusView != null){
                reservationStatusView.setPaymentStatus("PAY_FINISHED");
                // view 레파지 토리에 update
                reservationStatusViewRepository.save(reservationStatusView);  
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //고객의 취소로 인해 호텔 예약 취소됨
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCanceled_then_UPDATE(@Payload ReservationCanceled reservationCanceled) {
        try {
            if (!reservationCanceled.validate()) return;
            
            System.out.println("\n\n##### listener ReservationCanceled : " + reservationCanceled.toJson() + "\n\n");
            // view 객체 조회
            ReservationStatusView reservationStatusView = reservationStatusViewRepository.findByReservationId(reservationCanceled.getReservationId());
            if(reservationStatusView != null){
                reservationStatusView.setReservationStatus("RSV_CANCELED");
                // view 레파지 토리에 save
                reservationStatusViewRepository.save(reservationStatusView);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //호텔의 거절 혹은 고객의 취소로 인해 결제가 취소됨
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCanceled_then_UPDATE(@Payload PaymentCanceled paymentCanceled) {
        try {
            if (!paymentCanceled.validate()) return;
            
            System.out.println("\n\n##### listener PaymentCanceled : " + paymentCanceled.toJson() + "\n\n");
            // view 객체 조회
            ReservationStatusView reservationStatusView = reservationStatusViewRepository.findByReservationId(paymentCanceled.getReservationId());
            if(reservationStatusView != null){
                reservationStatusView.setPaymentStatus("PAY_CANCELED");
                // view 레파지 토리에 update
                reservationStatusViewRepository.save(reservationStatusView);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //호텔에서 예약 승인됨
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationApproved_then_UPDATE(@Payload ReservationApproved reservationApproved) {
        try {
            if (!reservationApproved.validate()) return;

            System.out.println("\n\n##### listener ReservationApproved : " + reservationApproved.toJson() + "\n\n");
            // view 객체 조회
            ReservationStatusView reservationStatusView = reservationStatusViewRepository.findByReservationId(reservationApproved.getReservationId());
            if(reservationStatusView != null){
                reservationStatusView.setReservationStatus("RSV_APPROVED");
                // view 레파지 토리에 update
                reservationStatusViewRepository.save(reservationStatusView);
            } 
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //호텔에서 예약 거절됨
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationRejected_then_UPDATE(@Payload ReservationRejected reservationRejected) {
        try {
            if (!reservationRejected.validate()) return;

            System.out.println("\n\n##### listener ReservationRejected : " + reservationRejected.toJson() + "\n\n");
            // view 객체 조회
            ReservationStatusView reservationStatusView = reservationStatusViewRepository.findByReservationId(reservationRejected.getReservationId());
            if(reservationStatusView != null){
                reservationStatusView.setReservationStatus("RSV_REJECTED");
                // view 레파지 토리에 save
                reservationStatusViewRepository.save(reservationStatusView);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

}
