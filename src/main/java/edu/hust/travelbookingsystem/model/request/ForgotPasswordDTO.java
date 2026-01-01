package edu.hust.travelbookingsystem.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDTO {

    @NotBlank(message = "ARGUMENT_NOT_VALID")
    @Email(message = "EMAIL_NOT_VALID")
    private String email;
}
