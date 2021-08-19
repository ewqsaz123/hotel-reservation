package project;

import project.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired ReservationRepository reservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationApproved_UpdateReservationInfo(@Payload ReservationApproved reservationApproved){
        /* 객실 예약 승인 (RSV_APPROVED) */
        /* 객실 예약이 승인되면 객실 상태(reservationStatus)를 변경 */
 
        
        if(!reservationApproved.validate()) return;
        System.out.println("\n\n##### listener UpdateReservationInfo : " + reservationApproved.toJson() + "\n\n");

        saveChangedStatus(reservationApproved.getReservationId(), "RSV_APPROVED", "");


    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentFinished_UpdateReservationInfo(@Payload PaymentFinished paymentFinished){
        /* 결제 완료 (PAY_FINISHED) */
        /* 결제가 완료되면 객실 상태(paymentStatus)를 변경 */
 
        if(!paymentFinished.validate()) return;

        System.out.println("\n\n##### listener UpdateReservationInfo : " + paymentFinished.toJson() + "\n\n");

        saveChangedStatus(paymentFinished.getReservationId(), "", "PAY_FINISHED");

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_UpdateReservationInfo(@Payload PaymentCanceled paymentCanceled){
        /* 결제 취소 (PAY_CANCELED) */
        /* 결제가 취소되면 객실 상태(paymentStatus)를 변경 */

        if(!paymentCanceled.validate()) return;

        System.out.println("\n\n##### listener UpdateReservationInfo : " + paymentCanceled.toJson() + "\n\n");


        saveChangedStatus(paymentCanceled.getReservationId(), "RSV_CANCELED", "PAY_CANCELED");


    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    public void saveChangedStatus(Long reservationId, String reservationStatus, String paymentStatus){

            //상태 변경하려는 db 객체를 get 
            Optional<Reservation> res = reservationRepository.findById(reservationId);
            Reservation reservation = res.get();

            if(reservationStatus.length() > 0) reservation.setReservationStatus(reservationStatus);
            if(paymentStatus.length() > 0) reservation.setPaymentStatus(paymentStatus);

            

            System.out.println("-----modified reservationId :  " + reservation.getId());
            System.out.println("-----modified reservationStatus :  " + reservation.getReservationStatus());
            System.out.println("-----modified paymentStatus :  " + reservation.getPaymentStatus());
            

            /* DB Update */
            reservationRepository.save(reservation);


    }

}
