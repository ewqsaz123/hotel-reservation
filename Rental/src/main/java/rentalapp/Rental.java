package rentalapp;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Rental_table")
public class Rental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;                //시퀀스 넘버
    private Long rentalId;          //대여 ID
    private Long bookId;            //책 ID
    private String rentalStatus;    //대여 상태 : REQUESTED(대여요청됨), APPROVED(대여승인됨), CANCELED(대여취소됨), PAID(결제됨), REJECTED(대여거절됨)

    @PostPersist
    public void onPostPersist(){
        System.out.println("######대여 요청 되었음#####");

        // 책 대여 요청
        // 결제 동기 호출(req/res) 진행
        // 결제가 진행가능한 지 확인 후 결제 진행
        rentalapp.external.Payment payment = new rentalapp.external.Payment();
        if(this.getRentalStatus().equals("REQUESTED")){
            payment.setRentalId(this.getRentalId());
            payment.setRentalStatus("PAID");
        }

        // mappings goes here
        RentalApplication.applicationContext.getBean(rentalapp.external.PaymentService.class)
            .paymentrequest(payment);

        RentalRequested rentalRequested = new RentalRequested();
        BeanUtils.copyProperties(this, rentalRequested);
        rentalRequested.publishAfterCommit();

    }

    @PostUpdate
    public void onPostUpdate(){
        /* 대여 취소 */
        if(this.getRentalStatus().equals("CANCELED")){
            RentalCanceled rentalCanceled = new RentalCanceled();
            BeanUtils.copyProperties(this, rentalCanceled);
            rentalCanceled.publishAfterCommit();
        }
        /* 결제 완료일 때 */
        else if(this.getRentalStatus().equals("PAID")){
            RequestCompleted requestCompleted = new RequestCompleted();
            BeanUtils.copyProperties(this, requestCompleted);
            requestCompleted.publishAfterCommit();
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }




}