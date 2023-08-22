package com.offer.service;


import com.offer.exception.ValidityNotExcepted;
import com.offer.model.SubOffer;

public interface SubOfferService {

    SubOffer saveSubOffer(SubOffer subOffer) throws ValidityNotExcepted;
}
