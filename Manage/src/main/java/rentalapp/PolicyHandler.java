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
    @Autowired ManageRepository manageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRequestCompleted_RequestRegist(@Payload RequestCompleted requestCompleted){
        //결제 완료 후 최종 대여 요청
        if(!requestCompleted.validate()) return;
        System.out.println("####PolicyHandler: wheneverRequestCompleted_RequestRegist####" );

        System.out.println("\n\n##### listener RequestRegist : " + requestCompleted.toJson() + "\n\n");

        setChengedStatus(requestCompleted.getBookId(), requestCompleted.getRentalId(), requestCompleted.getRentalStatus(), "regist");

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRentalCanceled_RequestCancel(@Payload RentalCanceled rentalCanceled){
        //대여 취소됨
        if(!rentalCanceled.validate()) return;
        System.out.println("####PolicyHandler: wheneverRentalCanceled_RequestCancel####" );
        System.out.println("\n\n##### listener RequestCancel : " + rentalCanceled.toJson() + "\n\n");

        setChengedStatus( rentalCanceled.getBookId(), rentalCanceled.getRentalId(),rentalCanceled.getRentalStatus(), "");
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    public void setChengedStatus(Long bookId, Long rentalId, String rentalStatus, String type){

            Optional<Manage> manage = manageRepository.findById(bookId);
            Manage mn = manage.get();

            if(type.length() > 0 && type.equals("regist")) mn.setRentalId(rentalId);
            if(rentalStatus.length() > 0) mn.setRentalStatus(rentalStatus);

            System.out.println("-----modified rentalId :  " + mn.getId());
            System.out.println("-----modified rentalStatus :  " + mn.getRentalStatus());
        
            /* DB Update */
            manageRepository.save(mn);

    }

}
