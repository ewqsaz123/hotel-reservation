package rentalapp;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long rentalId;
    private String rentalStatus;

    @PostPersist
    public void onPostPersist(){
        // 결제 완료
        PaymentCompleted paymentCompleted = new PaymentCompleted();
        BeanUtils.copyProperties(this, paymentCompleted);
        paymentCompleted.publishAfterCommit();

    }
    @PostUpdate
    public void onPostUpdate(){
        // 결제 취소 
        if(this.getRentalStatus().equals("CANCELED")){
            PaymentCanceled paymentCanceled = new PaymentCanceled();
            BeanUtils.copyProperties(this, paymentCanceled);
            paymentCanceled.publishAfterCommit();
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
    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }




}