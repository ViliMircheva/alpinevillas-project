package bg.softuni.alpinevillas.service.impl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;


import bg.softuni.alpinevillas.entities.Amenity;
import bg.softuni.alpinevillas.entities.User;
import bg.softuni.alpinevillas.entities.Villa;
import bg.softuni.alpinevillas.integration.review.ReviewClient;
import bg.softuni.alpinevillas.integration.review.ReviewDto;
import bg.softuni.alpinevillas.repositories.AmenityRepository;
import bg.softuni.alpinevillas.repositories.UserRepository;
import bg.softuni.alpinevillas.repositories.VillaRepository;
import bg.softuni.alpinevillas.service.VillaService;
import bg.softuni.alpinevillas.service.exception.ForbiddenOperationException;
import bg.softuni.alpinevillas.web.dto.VillaCreateDto;
import bg.softuni.alpinevillas.web.dto.VillaDetailsDto;
import bg.softuni.alpinevillas.service.exception.NotFoundException;
import bg.softuni.alpinevillas.web.dto.VillaEditDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class VillaServiceImpl implements VillaService {

    private final VillaRepository villaRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final ReviewClient reviewClient;
    private static final Logger log = LoggerFactory.getLogger(VillaServiceImpl.class);

    public VillaServiceImpl(VillaRepository villaRepository,
                            UserRepository userRepository,AmenityRepository amenityRepository, ReviewClient reviewClient) {
        this.villaRepository = villaRepository;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
        this.reviewClient = reviewClient;
    }

    @Override
    @Cacheable("villasCatalog")
    public List<VillaDetailsDto> listAll() {
        return villaRepository.findAll()
                .stream()
                .map(this::toDetailsDto)
                .toList();
    }

    @Override
    @Cacheable(value = "villaDetails", key = "#id")
    public VillaDetailsDto getDetails(UUID id) {
        Villa v = villaRepository.findByIdWithAmenities(id)
                .orElseThrow(() -> new NotFoundException("Villa not found"));

        VillaDetailsDto dto = new VillaDetailsDto();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setRegion(v.getRegion());
        dto.setCapacity(v.getCapacity());
        dto.setPricePerNight(v.getPricePerNight());
        dto.setImageUrl(v.getImageUrl());
        dto.setDescription(v.getDescription());
        dto.setOwnerUsername(v.getOwner().getUsername());
        dto.setAmenities(
                v.getAmenities().stream()
                        .map(Amenity::getName)
                        .toList());

        try {
            List<ReviewDto> reviews = reviewClient.getReviews(id);
            dto.setReviews(reviews);

            if (reviews != null && !reviews.isEmpty()) {
                double avg = reviews.stream()
                        .mapToInt(ReviewDto::getRating)
                        .average()
                        .orElse(0.0);

                avg = Math.round(avg * 10.0) / 10.0;
                dto.setAverageRating(avg);
                dto.setReviewCount(reviews.size());
            }
            else {
                dto.setAverageRating(null);
                dto.setReviewCount(0);
            }

        } catch (Exception ex) {
            dto.setReviews(List.of());
            dto.setAverageRating(null);
            dto.setReviewCount(0);
        }

        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"villasCatalog"}, allEntries = true)
    public UUID createVilla(VillaCreateDto dto, String ownerUsername) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        Villa v = new Villa();
        v.setName(dto.getName());
        v.setRegion(dto.getRegion());
        v.setCapacity(dto.getCapacity());
        v.setPricePerNight(dto.getPricePerNight());
        v.setDescription(dto.getDescription());
        v.setImageUrl(dto.getImageUrl());
        v.setOwner(owner);

        Set<Amenity> amenities = new HashSet<>();
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            amenities.addAll(amenityRepository.findAllById(dto.getAmenityIds()));
        }
        v.setAmenities(amenities);

        return villaRepository.save(v).getId();
    }

    private VillaDetailsDto toDetailsDto(Villa v) {
        VillaDetailsDto d = new VillaDetailsDto();
        d.setId(v.getId());
        d.setName(v.getName());
        d.setRegion(v.getRegion());
        d.setCapacity(v.getCapacity());
        d.setPricePerNight(v.getPricePerNight());
        d.setDescription(v.getDescription());
        d.setImageUrl(v.getImageUrl());
        d.setOwnerUsername(v.getOwner() != null ? v.getOwner().getUsername() : null);

        return d;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"villasCatalog", "villaDetails"}, allEntries = true)
    public void deleteVilla(UUID id, String username) {
        Villa villa = villaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вилата не е намерена."));

        if (!villa.getOwner().getUsername().equals(username)) {
            throw new ForbiddenOperationException("Нямате право да изтриете тази вила.");
        }

        try {
            reviewClient.deleteReviewsByVilla(id);
            log.info("Deleted reviews in review-service for villa {}", id);
        } catch (Exception ex) {
            log.warn("Failed to delete reviews for villa {} in review-service", id, ex);
        }

        villaRepository.delete(villa);
    }

    @Override
    @Transactional(readOnly = true)
    public VillaEditDto getEditData(UUID id, String username) {
        Villa v = villaRepository.findByIdWithAmenities(id)
                .orElseThrow(() -> new NotFoundException("Вилата не е намерена."));

        if (!v.getOwner().getUsername().equals(username)) {
            throw new ForbiddenOperationException("Нямате право да редактирате тази вила.");
        }

        VillaEditDto dto = new VillaEditDto();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setRegion(v.getRegion());
        dto.setCapacity(v.getCapacity());
        dto.setPricePerNight(v.getPricePerNight());
        dto.setDescription(v.getDescription());
        dto.setImageUrl(v.getImageUrl());
        dto.setAmenityIds(
                v.getAmenities().stream()
                        .map(Amenity::getId)
                        .toList()
        );

        return dto;
    }

    @CacheEvict(cacheNames = {"villasCatalog", "villaDetails"}, allEntries = true)
    @Override
    @Transactional
    public void editVilla(VillaEditDto dto, String username) {
        Villa v = villaRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Вилата не е намерена."));

        if (!v.getOwner().getUsername().equals(username)) {
            throw new ForbiddenOperationException("Нямате право да редактирате тази вила.");
        }

        v.setName(dto.getName());
        v.setRegion(dto.getRegion());
        v.setCapacity(dto.getCapacity());
        v.setPricePerNight(dto.getPricePerNight());
        v.setDescription(dto.getDescription());
        v.setImageUrl(dto.getImageUrl());

        if (dto.getAmenityIds() != null) {
            v.setAmenities(new HashSet<>(amenityRepository.findAllById(dto.getAmenityIds())));
        }
    }

    @Override
    @CacheEvict(cacheNames = "villaDetails", key = "#villaId")
    public void evictVillaCache(UUID villaId) {
    }



}
