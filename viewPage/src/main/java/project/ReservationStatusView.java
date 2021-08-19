package project;

import javax.persistence.*;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="ReservationStatusView_table")
public class ReservationStatusView {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long customerId;
        private String customerName;
        private Long reservationId;
        private Long hotelId;
        private String hotelName;
        private Long roomId;
        private String roomName;
        private Long roomPrice;
        private Date checkInDate;
        private Date checkOutDate;
        private String reservationStatus;
        private String paymentStatus;

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
        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
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
        public Long getRoomPrice() {
            return roomPrice;
        }

        public void setRoomPrice(Long roomPrice) {
            this.roomPrice = roomPrice;
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
        public String getReservationStatus() {
            return reservationStatus;
        }

        public void setReservationStatus(String reservationStatus) {
            this.reservationStatus = reservationStatus;
        }
        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

}
