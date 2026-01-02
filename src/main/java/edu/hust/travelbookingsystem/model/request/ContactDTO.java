package edu.hust.travelbookingsystem.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO {
    @NotBlank(message = "ARGUMENT_NOT_VALID")
    @Size(max = 100, message = "ARGUMENT_NOT_VALID")
    private String fullName;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    @Email(message = "EMAIL_NOT_VALID")
    @Size(max = 100, message = "ARGUMENT_NOT_VALID")
    private String email;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    @Size(max = 200, message = "ARGUMENT_NOT_VALID")
    private String subject;

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    @Size(max = 2000, message = "ARGUMENT_NOT_VALID")
    private String message;
}
