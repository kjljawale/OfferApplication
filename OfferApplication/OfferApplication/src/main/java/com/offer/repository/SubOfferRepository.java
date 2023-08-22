package com.offer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.offer.model.SubOffer;

@Repository
public interface SubOfferRepository extends JpaRepository<SubOffer, Integer> {

}
