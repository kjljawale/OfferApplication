package com.offer.service;

import com.offer.model.Offer;

import java.time.temporal.ChronoUnit;
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
    public Offer saveOffer(Offer offer) throws OfferTypeNotFound, ValidityNotExcepted {
        List<String> offerTypeList = Arrays.asList("unlimited", "topup", "validity", "ott offers");
        String offerTypeInLowerCase = offer.getOffer_type().toLowerCase();

        if (offerTypeList.contains(offerTypeInLowerCase)) {
            offer.setOffer_type(offerTypeInLowerCase);

            int activationYear = offer.getActivation_date().getYear();
            if (activationYear < 2000) {
                throw new ValidityNotExcepted("Activation date year should not be before 2000");
            }

            long weeks = ChronoUnit.WEEKS.between(offer.getActivation_date(), offer.getExpiration_date());
            if (weeks < offer.getSubOffers().get(0).getValidity()) {
                throw new ValidityNotExcepted("Validity should be between activation and expiration date");
            }
            return offerRepository.save(offer);
        } else
            throw new OfferTypeNotFound("Offer type not matched");

    }

    @Override
    public List<Offer> getOffers() {
        return offerRepository.findAll();
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

        long weeks = ChronoUnit.WEEKS.between(updatedOffer.getActivation_date(), updatedOffer.getExpiration_date());
        if (weeks < updatedOffer.getSubOffers().get(0).getValidity()) {

            throw new ValidityNotExcepted("Validity should be between activation and expiration date");

        } else if (updatedOffer.getSubOffers().get(0).getValidity() % 7 == 0) {
            newSubOffer.setValidity(updatedOffer.getSubOffers().get(0).getValidity());
        } else
            throw new ValidityNotExcepted("Validity should be multiples of week");

        newSubOffer.setParentRelation(updatedOffer.getSubOffers().get(0).getParentRelation());
        existingOffer.getSubOffers().add(newSubOffer);

        return offerRepository.save(existingOffer);
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

        SubOffer uSubOffer = null;
        int count = 0;
        for (SubOffer s : subOffers) {
            if (s.getSubOffer_id() == subOffer_id) {
                uSubOffer = s;
                break;
            }
            count++;
        }

        if (offer.getActivation_date() != null || offer.getExpiration_date() != null) {
            updateOffer.setActivation_date(offer.getActivation_date());
            updateOffer.setExpiration_date(offer.getExpiration_date());
            int activationYear = updateOffer.getActivation_date().getYear();
            if (activationYear < 2000) {
                throw new ValidityNotExcepted("Activation date year should not be before 2000");
            }
        }

        uSubOffer.setSubOffer_name(offer.getSubOffers().get(count).getSubOffer_name());
        uSubOffer.setPrice(offer.getSubOffers().get(count).getPrice());
        long weeks = ChronoUnit.WEEKS.between(offer.getActivation_date(), offer.getExpiration_date());
        if (weeks < offer.getSubOffers().get(count).getValidity()) {
            throw new ValidityNotExcepted("Validity should be between activation and expiration date");
        } else if (offer.getSubOffers().get(count).getValidity() % 7 == 0) {
            uSubOffer.setValidity(offer.getSubOffers().get(count).getValidity());
        } else
            throw new ValidityNotExcepted("Validity should be multiples of week");

        SubOffer.Relation existingParentRelation = uSubOffer.getParentRelation();
        SubOffer.Relation newParentRelation = offer.getSubOffers().get(count).getParentRelation();
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

}
