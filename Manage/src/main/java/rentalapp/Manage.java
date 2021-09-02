package rentalapp;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Manage_table")
public class Manage {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long rentalId;
    private Long bookId;
    private String rentalStatus;

    @PostPersist
    public void onPostPersist(){
        //책 추가됨
        BookAdded bookAdded = new BookAdded();
        BeanUtils.copyProperties(this, bookAdded);
        bookAdded.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate(){
        //결제 승인
        if(this.getRentalStatus().equals("APPROVED")){           
            RequestApproved requestApproved = new RequestApproved();
            BeanUtils.copyProperties(this, requestApproved);
            requestApproved.publishAfterCommit();
        }
        // 대여 거절
        if(this.getRentalStatus().equals("REJECTED")){
            RequestDenied requestDenied = new RequestDenied();
            BeanUtils.copyProperties(this, requestDenied);
            requestDenied.publishAfterCommit();
        }
        // 대여 취소
        else if(this.getRentalStatus().equals("CANCELED")){
            RequestCanceled requestCanceled = new RequestCanceled();
            BeanUtils.copyProperties(this, requestCanceled);
            requestCanceled.publishAfterCommit();
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