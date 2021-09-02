package rentalapp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentalStatusViewRepository extends CrudRepository<RentalStatusView, Long> {

    RentalStatusView findByRentalId(Long rentalId);
    RentalStatusView findByBookId(Long bookId);

}