package project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationStatusViewRepository extends CrudRepository<ReservationStatusView, Long> {

    ReservationStatusView findByReservationId(Long reservationId);
    
    ReservationStatusView findByRoomId(Long roomId);
    

}