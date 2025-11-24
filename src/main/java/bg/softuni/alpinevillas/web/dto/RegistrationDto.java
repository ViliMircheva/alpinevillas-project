package bg.softuni.alpinevillas.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationDto {

    @NotBlank(message = "Потребителското име е задължително!")
    @Size(min = 3, max = 20, message = "Името трябва да е между 3 и 20 символа.")
    private String username;

    @NotBlank(message = "Имейлът е задължителен!")
    @Email(message = "Невалиден имейл.")
    private String email;

    @NotBlank(message = "Паролата е задължителна!")
    @Size(min = 6, message = "Паролата трябва да е поне 6 символа.")
    private String password;

    @NotBlank(message = "Повтори паролата!")
    private String confirmPassword;

    public @NotBlank(message = "Потребителското име е задължително!") @Size(min = 3, max = 20, message = "Името трябва да е между 3 и 20 символа.") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Потребителското име е задължително!") @Size(min = 3, max = 20, message = "Името трябва да е между 3 и 20 символа.") String username) {
        this.username = username;
    }

    public @NotBlank(message = "Имейлът е задължителен!") @Email(message = "Невалиден имейл.") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Имейлът е задължителен!") @Email(message = "Невалиден имейл.") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Паролата е задължителна!") @Size(min = 6, message = "Паролата трябва да е поне 6 символа.") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Паролата е задължителна!") @Size(min = 6, message = "Паролата трябва да е поне 6 символа.") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Повтори паролата!") String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@NotBlank(message = "Повтори паролата!") String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
