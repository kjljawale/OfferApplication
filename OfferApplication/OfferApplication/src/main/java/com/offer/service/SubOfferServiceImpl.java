package com.offer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offer.exception.ValidityNotExcepted;
import com.offer.model.SubOffer;
import com.offer.repository.SubOfferRepository;

@Service
public class SubOfferServiceImpl implements SubOfferService {

    @Autowired
    SubOfferRepository subOfferRepository;

    @Override
    public SubOffer saveSubOffer(SubOffer subOffer) throws ValidityNotExcepted {


        
        if (subOffer.getValidity() % 7 == 0) {
            return subOfferRepository.save(subOffer);
        } else
            throw new ValidityNotExcepted("Validity should be multiples of week");

    }

}
