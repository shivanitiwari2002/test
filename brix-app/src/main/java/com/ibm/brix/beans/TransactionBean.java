//=============================================================================
//* Name:         TransactionBean.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Transaction Bean
//*
//* Description:  This bean class manages transactions.
//*
//* (C) Copyright IBM Corporation 2024
//*
//* The Source code for this program is not published or otherwise
//* divested of its trade secrets, irrespective of what has been
//* deposited with the U.S. Copyright Office.
//*
//* Note to U.S. Government Users Restricted Rights:  Use,
//* duplication or disclosure restricted by GSA ADP Schedule
//* Contract with IBM Corp.
//*
//* Change Log:
//* Flag Reason   Date     User Id   Description
//* ---- -------- -------- --------  ------------------------------------------
//*               20240326 tollefso  New source file created.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.beans;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.Status;
import com.ibm.brix.common.model.Transaction;
import com.ibm.brix.common.repositories.TransactionRepository;
import com.ibm.brix.model.TransactionList;
import com.ibm.brix.utils.Config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

@ApplicationScoped
@Transactional
public class TransactionBean {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = LoggerFactory.getLogger(TransactionBean.class);

    @Inject
    TransactionRepository transactionRepository;

    /**
     * Method:  create
     * Creates a transaction object for the given exchange.
     * 
     * @param Exchange exchange (Provided by Camel)
     * @throws BrixException
     */
    public Transaction create(Exchange exchange) throws BrixException {
        Transaction transaction = new Transaction(exchange);
        transactionRepository.persist(transaction);
        MDC.put(Constants.BRIX_TRX_ID, transaction.getTrxId());

        Config.getTransactionList().append(transaction);

        LOG.debug(transaction.toString());
        return transaction;
    }

    public Transaction updateStatus(Exchange exchange, String statusMessage) throws BrixException {
        Transaction transaction = null;
        if (exchange != null) {
            String trxId = exchange.getIn().getHeader(Constants.BRIX_TRX_ID, String.class);
            transaction = transactionRepository.findByTransactionId(trxId);
            if (transaction == null) {
                throw new BrixException(String.format("Transaction was not found."));
            }

            Status newStatus = exchange.getIn().getHeader(Constants.BRIX_TRX_STATUS,
                Status.class);
            LOG.debug("setting transaction " + transaction.getTrxId() + " to status " + newStatus);
            transaction.setStatus(newStatus);
            if (statusMessage == null) {
                statusMessage = "";
            }
            LOG.debug("updateStatus:statusMessage=" + statusMessage);
            transaction.setStatusMessage(statusMessage);
        }
        return transaction;
    }
}
