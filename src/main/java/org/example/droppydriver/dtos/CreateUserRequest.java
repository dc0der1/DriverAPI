package org.example.droppydriver.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank
    @Size(min = 4, message = "Username should be at least 4 characters")
    private String username;

    @Email()
    private String email;

    @Size(min = 8, message = "Password should be at least 8 characters")
    @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one number")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[@#$%^&+=!].*", message = "Password must contain at least one special character")
    private String password;

    @Min(value = 18, message = "Your age has to be 18 or above")
    @Max(value = 120, message = "Stop the cap")
    private int age;

}
