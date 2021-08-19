package project;

public class RoomReservationCanceled extends AbstractEvent {

    private Long id;
    private Long roomId;
    private String reservationStatus;


    public Long getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getReservationStatus() {
        return this.reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public RoomReservationCanceled(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
