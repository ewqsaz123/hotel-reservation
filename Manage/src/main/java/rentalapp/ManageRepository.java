package rentalapp;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="manages", path="manages")
public interface ManageRepository extends PagingAndSortingRepository<Manage, Long>{


}
