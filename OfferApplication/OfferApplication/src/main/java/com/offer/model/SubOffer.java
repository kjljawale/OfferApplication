package com.offer.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Data
@Table(name = "sub_offers")
@Getter
@Setter
@DynamicUpdate
public class SubOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int subOffer_id;

    @Size(max = 30, message = "Name should not be greater than 30 characters")
    @Pattern(regexp = "^[a-zA-Z\\-\\$\\s]*$", message = "Only letters, -, and $ are allowed")
    private String subOffer_name;

    private int price;

    private int validity;

    private int offer_id;

    @Enumerated(EnumType.STRING)
    private Relation parentRelation;

    public enum Relation {
        D,
        M,
        O
    };

}
