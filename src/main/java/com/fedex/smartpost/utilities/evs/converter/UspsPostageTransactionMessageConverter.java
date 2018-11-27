package com.fedex.smartpost.utilities.evs.converter;

import com.fedex.smartpost.postal.types.UspsPostage;
import com.fedex.smartpost.utilities.evs.errors.EvccRuntimeException;

public interface UspsPostageTransactionMessageConverter {
    String createPostageTransactionMessage(UspsPostage transDto) throws EvccRuntimeException;
}
