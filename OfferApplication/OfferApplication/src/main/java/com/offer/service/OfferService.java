package com.offer.service;

import java.util.List;
import com.offer.exception.OfferIdDoesNotExist;
import com.offer.exception.OfferTypeNotFound;
import com.offer.exception.ParentRelationException;
import com.offer.exception.ValidityNotExcepted;
import com.offer.model.Offer;


public interface OfferService {

    Offer saveOffer(Offer offer) throws OfferTypeNotFound, ValidityNotExcepted;

    public Offer updateOffer(int offer_id, int subOffer_id, Offer offer)
            throws ValidityNotExcepted, ParentRelationException, OfferIdDoesNotExist;

    Offer addNewSubOffer(int offerId, int subOfferId, Offer updatedOffer, boolean addSubOffer)
            throws ValidityNotExcepted, ParentRelationException, OfferIdDoesNotExist;

    public List<Offer> getOffers();
    
}
