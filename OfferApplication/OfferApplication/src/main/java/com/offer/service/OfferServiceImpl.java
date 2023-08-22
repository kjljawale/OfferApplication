package com.offer.service;

import com.offer.model.Offer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offer.exception.OfferIdDoesNotExist;
import com.offer.exception.OfferTypeNotFound;
import com.offer.exception.ParentRelationException;
import com.offer.exception.ValidityNotExcepted;
import com.offer.model.SubOffer;
import com.offer.repository.OfferRepository;
import com.offer.repository.SubOfferRepository;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    OfferRepository offerRepository;

    @Autowired
    SubOfferRepository subOfferRepository;

    @Override
    public Offer saveOffer(Offer offer) throws OfferTypeNotFound {
        List<String> offerTypeList = Arrays.asList("unlimited", "topup", "validity", "ott offers");
        String offerTypeInLowerCase = offer.getOffer_type().toLowerCase();

        if (offerTypeList.contains(offerTypeInLowerCase)) {
            offer.setOffer_type(offerTypeInLowerCase);
            return offerRepository.save(offer);
        } else
            throw new OfferTypeNotFound("Offer type not matched");

    }

    @Override
    public List<Offer> getOffers() {
        return offerRepository.findAll();
    }

    @Override
    public Offer updateOffer(int offer_id, int subOffer_id, Offer offer)
            throws ValidityNotExcepted, ParentRelationException, OfferIdDoesNotExist {
        Offer updateOffer = offerRepository.findById(offer_id)
                .orElseThrow(() -> new OfferIdDoesNotExist("Offer Id is not available"));

        List<SubOffer> subOffers = updateOffer.getSubOffers();
        if (subOffers == null || subOffer_id < 0 || subOffer_id >= subOffers.size()) {
            return null;
        }

        SubOffer uSubOffer = subOffers.get(subOffer_id);

        if (offer.getActivation_date() != null || offer.getExpiration_date() != null) {
            updateOffer.setActivation_date(offer.getActivation_date());
            updateOffer.setExpiration_date(offer.getExpiration_date());
        }

        uSubOffer.setSubOffer_name(offer.getSubOffers().get(subOffer_id).getSubOffer_name());
        uSubOffer.setPrice(offer.getSubOffers().get(subOffer_id).getPrice());

        if (offer.getSubOffers().get(subOffer_id).getValidity() % 7 == 0) {
            uSubOffer.setValidity(offer.getSubOffers().get(subOffer_id).getValidity());
        } else
            throw new ValidityNotExcepted("Validity should be multiples of week");

        SubOffer.Relation existingParentRelation = uSubOffer.getParentRelation();
        SubOffer.Relation newParentRelation = offer.getSubOffers().get(subOffer_id).getParentRelation();
        if (existingParentRelation == SubOffer.Relation.M) {
            uSubOffer.setParentRelation(newParentRelation);

        } else if (newParentRelation == SubOffer.Relation.M &&
                (existingParentRelation == SubOffer.Relation.O ||
                        existingParentRelation == SubOffer.Relation.D))
            throw new ParentRelationException("Cannot change parent relation from O/D to M");

        offerRepository.save(updateOffer);
        subOfferRepository.save(uSubOffer);

        return updateOffer;
    }

    @Override
    public Offer addNewSubOffer(int offerId, int subOfferId, Offer updatedOffer, boolean addSubOffer)
            throws ValidityNotExcepted, ParentRelationException, OfferIdDoesNotExist {
        Offer existingOffer = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferIdDoesNotExist("Offer Id is not available"));

        SubOffer newSubOffer = new SubOffer();

        newSubOffer.setOffer_id(offerId);
        newSubOffer.setSubOffer_name(updatedOffer.getSubOffers().get(0).getSubOffer_name());
        newSubOffer.setPrice(updatedOffer.getSubOffers().get(0).getPrice());
        if (updatedOffer.getSubOffers().get(0).getValidity() % 7 == 0) {
            newSubOffer.setValidity((updatedOffer.getSubOffers().get(0).getValidity()));
        } else
            throw new ValidityNotExcepted("Validity should be multiples of week");

        newSubOffer.setParentRelation(updatedOffer.getSubOffers().get(0).getParentRelation());
        existingOffer.getSubOffers().add(newSubOffer);

        return offerRepository.save(existingOffer);
    }

}
