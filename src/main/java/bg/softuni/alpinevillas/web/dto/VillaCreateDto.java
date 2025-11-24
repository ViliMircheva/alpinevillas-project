package bg.softuni.alpinevillas.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillaCreateDto {

    @NotBlank(message = "Името е задължително.")
    private String name;

    @NotBlank(message = "Регионът е задължителен.")
    private String region;

    @NotNull(message = "Капацитет е задължителен.")
    @Min(value = 1, message = "Капацитетът трябва да е поне 1.")
    private Integer capacity;                // <-- Integer, не int

    @NotNull(message = "Цената е задължителна.")
    @DecimalMin(value = "0.00", message = "Цената не може да е отрицателна.")
    private BigDecimal pricePerNight;

    @Size(max = 2000, message = "Описанието е твърде дълго.")
    private String description;

    @Size(max = 500, message = "URL е твърде дълъг.")
    private String imageUrl;

    private List<UUID> amenityIds = new ArrayList<>();

    public @NotBlank(message = "Името е задължително.") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Името е задължително.") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Регионът е задължителен.") String getRegion() {
        return region;
    }

    public void setRegion(@NotBlank(message = "Регионът е задължителен.") String region) {
        this.region = region;
    }

    public @NotNull(message = "Капацитет е задължителен.") @Min(value = 1, message = "Капацитетът трябва да е поне 1.") Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(@NotNull(message = "Капацитет е задължителен.") @Min(value = 1, message = "Капацитетът трябва да е поне 1.") Integer capacity) {
        this.capacity = capacity;
    }

    public @NotNull(message = "Цената е задължителна.") @DecimalMin(value = "0.00", message = "Цената не може да е отрицателна.") BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(@NotNull(message = "Цената е задължителна.") @DecimalMin(value = "0.00", message = "Цената не може да е отрицателна.") BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public @Size(max = 2000, message = "Описанието е твърде дълго.") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 2000, message = "Описанието е твърде дълго.") String description) {
        this.description = description;
    }

    public @Size(max = 500, message = "URL е твърде дълъг.") String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Size(max = 500, message = "URL е твърде дълъг.") String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<UUID> getAmenityIds() { return amenityIds; }
    public void setAmenityIds(List<UUID> amenityIds) { this.amenityIds = amenityIds; }

}
