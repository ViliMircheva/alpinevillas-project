package bg.softuni.alpinevillas.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class VillaEditDto {

    @NotNull
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String region;

    @NotNull @Min(1)
    private Integer capacity;

    @NotNull @DecimalMin(value = "0.00")
    private BigDecimal pricePerNight;

    @Size(max = 2000)
    private String description;

    @Size(max = 255)
    private String imageUrl;

    private Set<UUID> amenityIds;

    public @NotNull UUID getId() {
        return id;
    }

    public void setId(@NotNull UUID id) {
        this.id = id;
    }

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public @NotBlank String getRegion() {
        return region;
    }

    public void setRegion(@NotBlank String region) {
        this.region = region;
    }

    public @NotNull @Min(1) Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(@NotNull @Min(1) Integer capacity) {
        this.capacity = capacity;
    }

    public @Size(max = 2000) String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 2000) String description) {
        this.description = description;
    }

    public @NotNull @DecimalMin(value = "0.00") BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(@NotNull @DecimalMin(value = "0.00") BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public @Size(max = 255) String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Size(max = 255) String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<UUID> getAmenityIds() {
        return amenityIds;
    }

    public void setAmenityIds(Set<UUID> amenityIds) {
        this.amenityIds = amenityIds;
    }
}
