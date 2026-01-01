package edu.hust.travelbookingsystem.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    @NotNull(message = "ARGUMENT_NOT_VALID")
    @Min(value = 1, message = "RATING_NOT_VALID")
    @Max(value = 5, message = "RATING_NOT_VALID")
    private Integer rating;

    @Size(max = 1000, message = "ARGUMENT_NOT_VALID")
    private String comment;
}
