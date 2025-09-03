package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByZipCode(String zipCode);
    
    List<Location> findByCityAndState(String city, String state);
    
    @Query(value = "SELECT *, " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(latitude)))) AS distance " +
           "FROM locations " +
           "HAVING distance < :radiusKm " +
           "ORDER BY distance", nativeQuery = true)
    List<Location> findLocationsWithinRadius(@Param("latitude") Double latitude,
                                           @Param("longitude") Double longitude,
                                           @Param("radiusKm") Double radiusKm);
}
