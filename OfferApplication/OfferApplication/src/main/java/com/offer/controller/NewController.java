package com.offer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.offer.exception.ValidityNotExcepted;
import com.offer.model.Offer;
import com.offer.model.SubOffer;
import com.offer.repository.OfferRepository;
import com.offer.service.OfferService;
import com.offer.service.SubOfferService;

@RestController
@RequestMapping("api/offer")
public class NewController {

    @Autowired
    SubOfferService subOfferService;

    @Autowired
    OfferService offerService;

    @Autowired
    OfferRepository offerRepository;

    @PostMapping("/addSubOffer")
    public ResponseEntity<Offer> saveSuboffer(@RequestBody SubOffer subOffer) throws ValidityNotExcepted {
        int offerId = subOffer.getOffer_id();

        Offer offer = offerRepository.findById(offerId).orElse(null);

        if (offer != null) {
            List<SubOffer> subOffers = offer.getSubOffers();
            subOffers.add(subOffer);
            offerRepository.save(offer);

            return ResponseEntity.status(HttpStatus.CREATED).body(offer);
        } else {
            throw new ValidityNotExcepted("Offer not found");
        }
    }

    @DeleteMapping("/deleteOffer/{offerId}")
    public ResponseEntity<String> deleteOffer(@PathVariable int offerId) {
        try {
            offerRepository.deleteById(offerId);
            return ResponseEntity.ok("Offer with ID " + offerId + " deleted successfully.");
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAllOffers")
    public List<Offer> getAllOffers() {
        return offerService.getOffers();
    }
}
