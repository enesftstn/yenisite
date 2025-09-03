package com.autoparts.exchange.service;

import com.autoparts.exchange.entity.Location;
import com.autoparts.exchange.repository.jpa.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {
    
    private final LocationRepository locationRepository;
    
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for calculating distance between two points on Earth
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km
        
        return distance;
    }
    
    public List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusKm) {
        return locationRepository.findLocationsWithinRadius(latitude, longitude, radiusKm);
    }
    
    public Location geocodeAddress(String address, String city, String state, String zipCode, String country) {
        // Mock geocoding - in real implementation, use Google Maps API or similar
        Location location = Location.builder()
                .address(address)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .country(country)
                .formattedAddress(address + ", " + city + ", " + state + " " + zipCode + ", " + country)
                .latitude(mockLatitude(zipCode))
                .longitude(mockLongitude(zipCode))
                .build();
        
        return locationRepository.save(location);
    }
    
    private Double mockLatitude(String zipCode) {
        // Mock latitude based on zip code hash
        return 40.0 + (zipCode.hashCode() % 1000) / 100.0;
    }
    
    private Double mockLongitude(String zipCode) {
        // Mock longitude based on zip code hash
        return -74.0 + (zipCode.hashCode() % 2000) / 100.0;
    }
}
