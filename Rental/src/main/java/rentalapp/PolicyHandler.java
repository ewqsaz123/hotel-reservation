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
    @Autowired RentalRepository rentalRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_PaymentComplete(@Payload PaymentCompleted paymentCompleted){
        //결제 완료됨
        if(!paymentCompleted.validate()) return;
        System.out.println("####PolicyHandler: wheneverPaymentCompleted_PaymentComplete####" );
        System.out.println("\n\n##### listener PaymentComplete : " + paymentCompleted.toJson() + "\n\n");

        setChengedStatus(paymentCompleted.getRentalId(), "PAID");
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_RentalCancel(@Payload PaymentCanceled paymentCanceled){
        //결제 취소됨
        if(!paymentCanceled.validate()) return;
        System.out.println("####PolicyHandler: wheneverPaymentCanceled_RentalCancel####" );
        System.out.println("\n\n##### listener RentalCancel : " + paymentCanceled.toJson() + "\n\n");

        setChengedStatus(paymentCanceled.getRentalId(), "CANCELED");

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRequestApproved_RequestApprove(@Payload RequestApproved requestApproved){
        //요청 승인됨
        if(!requestApproved.validate()) return;
        System.out.println("####PolicyHandler: wheneverRequestApproved_RequestApprove####" );
        System.out.println("\n\n##### listener RequestApprove : " + requestApproved.toJson() + "\n\n");

        setChengedStatus(requestApproved.getRentalId(), "APPROVED");
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


    public void setChengedStatus(Long rentalId, String rentalStatus){

            
            Optional<Rental> rental = rentalRepository.findById(rentalId);
            Rental rt = rental.get();

            if(rentalStatus.length() > 0) rt.setRentalStatus(rentalStatus);

        
            System.out.println("-----modified rentalId :  " + rt.getId());
            System.out.println("-----modified rentalStatus :  " + rt.getRentalStatus());

        
            /* DB Update */
            rentalRepository.save(rt);


    }

}
