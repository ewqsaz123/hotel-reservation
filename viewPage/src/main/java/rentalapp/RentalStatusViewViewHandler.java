package rentalapp;

import rentalapp.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RentalStatusViewViewHandler {


    @Autowired
    private RentalStatusViewRepository rentalStatusViewRepository;

    //관리자가 책 추가
    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookAdded_then_CREATE (@Payload BookAdded bookAdded) {
        try {

            if (!bookAdded.validate()) return;

            // view 객체 생성
            RentalStatusView rentalStatusView = new RentalStatusView();
            // view 객체에 이벤트의 Value 를 set 함
            rentalStatusView.setBookId(bookAdded.getBookId());
            rentalStatusView.setRentalStatus("RENTAL_AVAILABLE");
            // view 레파지 토리에 save
            rentalStatusViewRepository.save(rentalStatusView);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //고객이 대여 요청함
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRentalRequested_then_UPDATE(@Payload RentalRequested rentalRequested) {
        try {
            if (!rentalRequested.validate()) return;
                // view 객체 조회

                RentalStatusView rentalStatusView = rentalStatusViewRepository.findByRentalId(rentalRequested.getBookId());
                
                if(rentalStatusView != null){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    rentalStatusView.setRentalId(rentalRequested.getRentalId());
                    rentalStatusView.setRentalStatus(rentalRequested.getRentalStatus());
                    // view 레파지 토리에 save
                    rentalStatusViewRepository.save(rentalStatusView);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //고객이 결제 완료함
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCompleted_then_UPDATE(@Payload PaymentCompleted paymentCompleted) {
        try {
            if (!paymentCompleted.validate()) return;
                // view 객체 조회

                RentalStatusView rentalStatusView = rentalStatusViewRepository.findByRentalId(paymentCompleted.getRentalId());
                
                if(rentalStatusView != null){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    rentalStatusView.setRentalStatus(paymentCompleted.getRentalStatus());
                    // view 레파지 토리에 save
                    rentalStatusViewRepository.save(rentalStatusView);
                }
                

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //고객이 결제완료 후 대여요청이 완료됨
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRequestCompleted_then_UPDATE(@Payload RequestCompleted requestCompleted) {
        try {
            if (!requestCompleted.validate()) return;
                // view 객체 조회

                RentalStatusView rentalStatusView = rentalStatusViewRepository.findByRentalId(requestCompleted.getRentalId());
                if(rentalStatusView != null){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    rentalStatusView.setRentalStatus(requestCompleted.getRentalStatus());
                    // view 레파지 토리에 save
                    rentalStatusViewRepository.save(rentalStatusView);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //관리자가 대여요청을 승인함
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRequestApproved_then_UPDATE(@Payload RequestApproved requestApproved) {
        try {
            if (!requestApproved.validate()) return;
                // view 객체 조회

                RentalStatusView rentalStatusView = rentalStatusViewRepository.findByRentalId(requestApproved.getRentalId());
                if(rentalStatusView != null){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    rentalStatusView.setRentalStatus(requestApproved.getRentalStatus());
                    // view 레파지 토리에 save
                    rentalStatusViewRepository.save(rentalStatusView);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //관리자가 대여요청을 거절함
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRequestDenied_then_UPDATE(@Payload RequestDenied requestDenied) {
        try {
            if (!requestDenied.validate()) return;
                // view 객체 조회

                RentalStatusView rentalStatusView = rentalStatusViewRepository.findByRentalId(requestDenied.getRentalId());
                if(rentalStatusView != null){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    rentalStatusView.setRentalStatus(requestDenied.getRentalStatus());
                    // view 레파지 토리에 save
                    rentalStatusViewRepository.save(rentalStatusView);
                }
                
                

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

