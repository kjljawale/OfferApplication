package com.offer.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.offer.exception.OfferIdDoesNotExist;
import com.offer.exception.OfferTypeNotFound;
import com.offer.exception.ParentRelationException;
import com.offer.exception.ValidityNotExcepted;
import com.offer.model.Offer;
import com.offer.model.SubOffer;
import com.offer.repository.OfferRepository;
import com.offer.service.OfferService;
import com.offer.service.SubOfferService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/offer")
public class OfferController {

    @Autowired
    OfferService offerService;

    @Autowired
    SubOfferService subOfferService;

    @Autowired
    OfferRepository offerRepository;

    @PostMapping()
    ResponseEntity<Offer> saveOffer(@Valid @RequestBody Offer offer) throws OfferTypeNotFound, ValidityNotExcepted {
        Offer savedoffer = offerService.saveOffer(offer);
        List<SubOffer> savedSubOffers = new ArrayList<>();
        for (SubOffer sOffer : offer.getSubOffers()) {
            sOffer.setOffer_id(savedoffer.getOffer_id());
            SubOffer savedOffer = subOfferService.saveSubOffer(sOffer);
            savedSubOffers.add(savedOffer);
        }
        savedoffer.setSubOffers(savedSubOffers);
        return new ResponseEntity<>(savedoffer, HttpStatus.CREATED);
    }

    @PatchMapping("/{offer_id}/{subOffer_id}")
    public ResponseEntity<Offer> updateOffer(@Valid @PathVariable int offer_id,
            @PathVariable int subOffer_id,
            @RequestBody Offer offer,
            @RequestParam(required = false, name = "add_suboffer") boolean addSubOffer)
            throws ValidityNotExcepted, ParentRelationException, OfferIdDoesNotExist {
        Offer updatedOffer;
        if (addSubOffer) {
            updatedOffer = offerService.addNewSubOffer(offer_id, subOffer_id, offer, addSubOffer);

        } else {

            updatedOffer = offerService.updateOffer(offer_id, subOffer_id, offer);
        }

        if (updatedOffer != null) {
            return ResponseEntity.ok(updatedOffer);
        } else {
            return ResponseEntity.notFound().build();
        }
        // return ResponseEntity.ok(updatedOffer);

    }

    @GetMapping("/getActiveOffers")
    public ResponseEntity<List<Offer>> getOffers() {
        List<Offer> offerDetails = offerService.getOffers();
        List<Offer> activeOffers = new ArrayList<>();

        for (Offer offer : offerDetails) {
            if (offer.isActive()) {
                activeOffers.add(offer);
            }
        }
        return ResponseEntity.ok(activeOffers);
    }

}
