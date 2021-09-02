package rentalapp;

import rentalapp.config.kafka.KafkaProcessor;
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
    public void wheneverRequestDenied_PaymentCancel(@Payload RequestDenied requestDenied){
        //대여 요청 거부됨
        if(!requestDenied.validate()) return;
        System.out.println("####PolicyHandler: wheneverRequestDenied_PaymentCancel###" );

        System.out.println("\n\n##### listener PaymentCancel : " + requestDenied.toJson() + "\n\n");

        setChengedStatus(requestDenied.getRentalId(), "REJECTED");


    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRequestCanceled_PaymentCancel(@Payload RequestCanceled requestCanceled){
        //대여 취소됨
        if(!requestCanceled.validate()) return;
        System.out.println("####PolicyHandler: wheneverRequestCanceled_PaymentCancel####" );

        System.out.println("\n\n##### listener PaymentCancel : " + requestCanceled.toJson() + "\n\n");

        setChengedStatus(requestCanceled.getRentalId(), "CANCELED");

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}
    
    public void setChengedStatus(Long rentalId, String rentalStatus){

            
            Optional<Payment> payment = paymentRepository.findById(rentalId);
            Payment pay = payment.get();

            if(rentalStatus.length() > 0) pay.setRentalStatus(rentalStatus);

        
            System.out.println("-----modified rentalId :  " + pay.getId());
            System.out.println("-----modified rentalStatus :  " + pay.getRentalStatus());

        
            /* DB Update */
            paymentRepository.save(pay);


    }

}
