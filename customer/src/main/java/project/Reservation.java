package project;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long customerId;
    private Long roomId;
    private String roomName;
    private String customerName;
    private String reservationStatus;   //status : "RSV_REQUESTED", "RSV_APPROVED", "RSV_CANCELED", "RSV_REJECTED", 
    private Long hotelId;
    private String hotelName;
    private Date checkInDate;
    private Date checkOutDate;
    private Long roomPrice;

    private String paymentStatus;   //status: "PAY_REQUESTED", "PAY_FINISHED", "PAY_CANCELED"


    @PostPersist
    public void onPostPersist(){
        System.out.println("*****객실 예약이 요청됨*****");

        /* 객실 예약이 요청됨 */

        // mappings goes here
        /* 결제(payment) 동기 호출 진행 */
        /* 결제 진행 가능 여부 확인 후 결제 */
        project.external.Payment payment = new project.external.Payment();
        if(this.getReservationStatus().equals("RSV_REQUESTED") && this.getPaymentStatus().equals("PAY_REQUESTED")){

            payment.setReservationId(this.getId());
            payment.setCustomerId(this.getCustomerId());
            payment.setRoomId(this.getRoomId());
            payment.setRoomName(this.getRoomName());
            payment.setRoomPrice(this.getRoomPrice());
            payment.setCustomerName(this.getCustomerName());
            payment.setHotelId(this.getHotelId());
            payment.setHotelName(this.getHotelName());
            payment.setCheckInDate(this.getCheckInDate());
            payment.setCheckOutDate(this.getCheckOutDate());
            payment.setReservationStatus("RSV_REQUESTED");
            payment.setPaymentStatus("PAY_FINISHED");
        }
        
         CustomerApplication.applicationContext.getBean(project.external.PaymentService.class)
            .requestPayment(payment);

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        RoomReservationReqeusted roomReservationReqeusted = new RoomReservationReqeusted();
        BeanUtils.copyProperties(this, roomReservationReqeusted);
        roomReservationReqeusted.publishAfterCommit();


    }
    @PostUpdate
    public void onPostUpdate(){
        /* 예약 취소일 때 */
        if(this.getReservationStatus().equals("RSV_CANCELED") && !this.getPaymentStatus().equals("PAY_CANCELED")){
            RoomReservationCanceled roomReservationCanceled = new RoomReservationCanceled();
            BeanUtils.copyProperties(this, roomReservationCanceled);
            roomReservationCanceled.publishAfterCommit();
        }else if(this.getPaymentStatus().equals("PAY_FINISHED") && !this.getReservationStatus().equals("RSV_APPROVED")){/* 결제 완료일 때 */
            PaymentCompleted paymentCompleted = new PaymentCompleted();
            BeanUtils.copyProperties(this, paymentCompleted);
            paymentCompleted.publishAfterCommit();
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }
    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    public Long getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(Long roomPrice) {
        this.roomPrice = roomPrice;
    }



    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}