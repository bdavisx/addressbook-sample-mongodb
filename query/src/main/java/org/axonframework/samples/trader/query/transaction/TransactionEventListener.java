/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.samples.trader.query.transaction;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.samples.trader.orders.api.transaction.AbstractTransactionExecutedEvent;
import org.axonframework.samples.trader.orders.api.transaction.AbstractTransactionPartiallyExecutedEvent;
import org.axonframework.samples.trader.orders.api.transaction.AbstractTransactionStartedEvent;
import org.axonframework.samples.trader.orders.api.transaction.BuyTransactionCancelledEvent;
import org.axonframework.samples.trader.orders.api.transaction.BuyTransactionConfirmedEvent;
import org.axonframework.samples.trader.orders.api.transaction.BuyTransactionExecutedEvent;
import org.axonframework.samples.trader.orders.api.transaction.BuyTransactionPartiallyExecutedEvent;
import org.axonframework.samples.trader.orders.api.transaction.BuyTransactionStartedEvent;
import org.axonframework.samples.trader.orders.api.transaction.SellTransactionCancelledEvent;
import org.axonframework.samples.trader.orders.api.transaction.SellTransactionConfirmedEvent;
import org.axonframework.samples.trader.orders.api.transaction.SellTransactionExecutedEvent;
import org.axonframework.samples.trader.orders.api.transaction.SellTransactionPartiallyExecutedEvent;
import org.axonframework.samples.trader.orders.api.transaction.SellTransactionStartedEvent;
import org.axonframework.samples.trader.orders.api.transaction.TransactionType;
import org.axonframework.samples.trader.query.orderbook.OrderBookEntry;
import org.axonframework.samples.trader.query.orderbook.repositories.OrderBookQueryRepository;
import org.axonframework.samples.trader.query.transaction.repositories.TransactionQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class TransactionEventListener {

    private OrderBookQueryRepository orderBookQueryRepository;
    private TransactionQueryRepository transactionQueryRepository;

    @EventHandler
    public void handleEvent(BuyTransactionStartedEvent event) {
        startTransaction(event, TransactionType.BUY);
    }

    @EventHandler
    public void handleEvent(SellTransactionStartedEvent event) {
        startTransaction(event, TransactionType.SELL);
    }

    @EventHandler
    public void handleEvent(BuyTransactionCancelledEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().asString(), TransactionState.CANCELLED);
    }

    @EventHandler
    public void handleEvent(SellTransactionCancelledEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().asString(), TransactionState.CANCELLED);
    }

    @EventHandler
    public void handleEvent(BuyTransactionConfirmedEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().asString(), TransactionState.CONFIRMED);
    }

    @EventHandler
    public void handleEvent(SellTransactionConfirmedEvent event) {
        changeStateOfTransaction(event.getTransactionIdentifier().asString(), TransactionState.CONFIRMED);
    }

    @EventHandler
    public void handleEvent(BuyTransactionExecutedEvent event) {
        executeTransaction(event);
    }

    @EventHandler
    public void handleEvent(SellTransactionExecutedEvent event) {
        executeTransaction(event);
    }

    @EventHandler
    public void handleEvent(BuyTransactionPartiallyExecutedEvent event) {
        partiallyExecuteTransaction(event);
    }

    @EventHandler
    public void handleEvent(SellTransactionPartiallyExecutedEvent event) {
        partiallyExecuteTransaction(event);
    }

    private void partiallyExecuteTransaction(AbstractTransactionPartiallyExecutedEvent event) {
        TransactionEntry transactionEntry = transactionQueryRepository.findOne(event.getTransactionIdentifier()
                                                                                    .asString());

        long value = transactionEntry.getAmountOfExecutedItems() * transactionEntry.getPricePerItem();
        long additionalValue = event.getAmountOfExecutedItems() * event.getItemPrice();
        long newPrice = (value + additionalValue) / event.getTotalOfExecutedItems();

        transactionEntry.setState(TransactionState.PARTIALLYEXECUTED);
        transactionEntry.setAmountOfExecutedItems(event.getTotalOfExecutedItems());
        transactionEntry.setPricePerItem(newPrice);
        transactionQueryRepository.save(transactionEntry);
    }

    private void executeTransaction(AbstractTransactionExecutedEvent event) {
        TransactionEntry transactionEntry = transactionQueryRepository.findOne(event.getTransactionIdentifier()
                                                                                    .asString());

        long value = transactionEntry.getAmountOfExecutedItems() * transactionEntry.getPricePerItem();
        long additionalValue = event.getAmountOfItems() * event.getItemPrice();
        long newPrice = (value + additionalValue) / transactionEntry.getAmountOfItems();

        transactionEntry.setState(TransactionState.EXECUTED);
        transactionEntry.setAmountOfExecutedItems(transactionEntry.getAmountOfItems());
        transactionEntry.setPricePerItem(newPrice);
        transactionQueryRepository.save(transactionEntry);
    }

    private void changeStateOfTransaction(String identifier, TransactionState newState) {
        TransactionEntry transactionEntry = transactionQueryRepository.findOne(identifier);
        transactionEntry.setState(newState);
        transactionQueryRepository.save(transactionEntry);
    }

    private void startTransaction(AbstractTransactionStartedEvent event, TransactionType type) {
        OrderBookEntry orderBookEntry = orderBookQueryRepository.findOne(event.getOrderbookIdentifier().asString());

        TransactionEntry entry = new TransactionEntry();
        entry.setAmountOfExecutedItems(0);
        entry.setAmountOfItems((int) event.getTotalItems());
        entry.setPricePerItem(event.getPricePerItem());
        entry.setIdentifier(event.getTransactionIdentifier().asString());
        entry.setOrderbookIdentifier(event.getOrderbookIdentifier().asString());
        entry.setPortfolioIdentifier(event.getPortfolioIdentifier().asString());
        entry.setState(TransactionState.STARTED);
        entry.setType(type);
        entry.setCompanyName(orderBookEntry.getCompanyName());

        transactionQueryRepository.save(entry);
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderBookQueryRepository(OrderBookQueryRepository orderBookQueryRepository) {
        this.orderBookQueryRepository = orderBookQueryRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setTransactionQueryRepository(TransactionQueryRepository transactionQueryRepository) {
        this.transactionQueryRepository = transactionQueryRepository;
    }
}
