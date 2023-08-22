package com.offer.model;

import java.time.LocalDate;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Table(name = "offers")
@Getter
@Setter
@DynamicUpdate
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int offer_id;

    @Size(max = 30, message = "Name should not be greater than 30 characters")
    @Pattern(regexp = "^[a-zA-Z\\-\\$\\s]*$", message = "Only characters, -, and $ are allowed")
    private String offer_name;

    private String offer_description;

    private String offer_type;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate activation_date;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiration_date;

    @OneToMany(cascade = CascadeType.ALL)
    @Size(min = 1, message = "At least one sub offer is required")
    private List<SubOffer> subOffers;

    public boolean isActive() {
        LocalDate currentDate = LocalDate.now();
        return activation_date != null && activation_date.isBefore(currentDate) && expiration_date != null
                && expiration_date.isAfter(currentDate);
    }

    @AssertTrue(message = "Activation date should be before expiration date")
    public boolean isActivationDateBeforeExpirationDate() {
        return activation_date == null || expiration_date == null || activation_date.isBefore(expiration_date);
    }
}
