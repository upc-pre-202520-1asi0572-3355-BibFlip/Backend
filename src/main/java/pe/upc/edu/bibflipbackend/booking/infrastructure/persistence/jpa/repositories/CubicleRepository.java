package pe.upc.edu.bibflipbackend.booking.infrastructure.persistence.jpa.repositories;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.domain.model.entities.AvailabilitySlot;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.HeadquarterId;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.CubicleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CubicleRepository extends JpaRepository<Cubicle, Long> {
    Boolean existsByHeadquarterIdAndCubicleDetails_CubicleNumber(HeadquarterId headquarterId, Integer cubicleDetails_cubicleNumber);

    Boolean existsByHeadquarterIdAndCubicleDetails_CubicleNumberAndStatusNot(HeadquarterId headquarterId, Integer cubicleDetails_cubicleNumber, CubicleStatus status);

    @Query("SELECT a FROM Cubicle t JOIN t.availabilitySlots a WHERE t.id = :cubicleId AND a.dateOfSlot = :date")
    List<AvailabilitySlot> findAvailabilitySlotsByCubicleIdAndDate(@Param("cubicleId") Long cubicleId, @Param("date") LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Cubicle t LEFT JOIN FETCH t.availabilitySlots WHERE t.id = :id")
    Optional<Cubicle> findByIdWithSlotsForUpdate(@Param("id") Long id);

    List<Cubicle> findByHeadquarterIdAndStatusNot(HeadquarterId headquarterId, CubicleStatus status);
}
