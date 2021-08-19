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
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationRejected_CancelPayment(@Payload ReservationRejected reservationRejected){
        /* 객실 예약 거부(RSV_REJECTED/PAY_CANCELED) */

        if(!reservationRejected.validate()) return;

        System.out.println("\n\n##### listener CancelPayment : " + reservationRejected.toJson() + "\n\n");


        saveChangedStatus(reservationRejected.getReservationId(), "RSV_REJECTED", "PAY_CANCELED");

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCanceled_CancelPayment(@Payload ReservationCanceled reservationCanceled){
        /* 객실 예약 취소(RSV_CANCELED) */

        if(!reservationCanceled.validate()) return;

        System.out.println("\n\n##### listener CancelPayment : " + reservationCanceled.toJson() + "\n\n");

        saveChangedStatus(reservationCanceled.getReservationId(), "RSV_CANCELED", "PAY_CANCELED");


    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


    public void saveChangedStatus(Long reservationId, String reservationStatus, String paymentStatus){

            //상태 변경하려는 db 객체를 get 
            Optional<Payment> pay = paymentRepository.findById(reservationId);
            Payment payment = pay.get();

            if(reservationStatus.length() > 0) payment.setReservationStatus(reservationStatus);
            if(paymentStatus.length() > 0) payment.setPaymentStatus(paymentStatus);

            
            System.out.println("-----modified reservationId :  " + payment.getReservationId());
            System.out.println("-----modified reservationStatus :  " + payment.getReservationStatus());
            System.out.println("-----modified paymentStatus :  " + payment.getPaymentStatus());
            

            /* DB Update */
            paymentRepository.save(payment);


    }


}
