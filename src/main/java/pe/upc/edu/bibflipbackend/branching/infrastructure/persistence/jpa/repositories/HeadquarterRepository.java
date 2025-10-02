package pe.upc.edu.bibflipbackend.branching.infrastructure.persistence.jpa.repositories;

import pe.upc.edu.bibflipbackend.branching.domain.model.aggregates.Headquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.Coordinates;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.NameHeadquarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadquarterRepository extends JpaRepository<Headquarter, Long> {
    Boolean existsByName (NameHeadquarter name);
    Boolean existsByCoordinates (Coordinates coordinates);
}
