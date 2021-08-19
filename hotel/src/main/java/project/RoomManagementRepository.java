package project;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="roomManagements", path="roomManagements")
public interface RoomManagementRepository extends PagingAndSortingRepository<RoomManagement, Long>{

    RoomManagement findByReservationId(Long reservationId);

    RoomManagement findByRoomId(Long roomId);
}
