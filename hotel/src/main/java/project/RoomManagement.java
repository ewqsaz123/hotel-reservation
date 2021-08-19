package project;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="RoomManagement_table")
public class RoomManagement {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long roomId;
    private String roomName;
    private String roomStatus;
    private Long roomPrice;
    private Long reservationId;
    private Long customerId;
    private String customerName;
    private Date checkInDate;
    private Date checkOutDate;
    private Long hotelId;
    private String hotelName;

    @PostPersist
    public void onPostPersist(){ //호텔에서 룸 생성
        RoomCreated roomCreated = new RoomCreated();
        BeanUtils.copyProperties(this, roomCreated);
        roomCreated.publishAfterCommit();
    }
    
    @PostUpdate
    public void onPostUpdate(){

        if(roomStatus.equals("RSV_APPROVED")){ //호텔에서 승인 처리시 
            ReservationApproved reservationApproved = new ReservationApproved();
            System.out.println("-----------------------------------"+this.roomStatus);
            BeanUtils.copyProperties(this, reservationApproved);
            reservationApproved.publishAfterCommit();
        }else if(roomStatus.equals("RSV_REJECTED")){ //호텔에서 예약 거절 시
            ReservationRejected reservationRejected = new ReservationRejected();
            BeanUtils.copyProperties(this, reservationRejected);
            reservationRejected.publishAfterCommit();
        }else if(roomStatus.equals("RSV_CANCELED")){ //호텔에서 취소 시 
            ReservationCanceled reservationCanceled = new ReservationCanceled();
            BeanUtils.copyProperties(this, reservationCanceled);
            reservationCanceled.publishAfterCommit();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Long getRoomPrice() {
        return this.roomPrice;
    }

    public void setRoomPrice(Long roomPrice) {
        this.roomPrice = roomPrice;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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




}