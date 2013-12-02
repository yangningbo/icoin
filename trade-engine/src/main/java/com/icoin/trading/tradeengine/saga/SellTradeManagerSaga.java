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

package com.icoin.trading.tradeengine.saga;

import com.icoin.trading.tradeengine.application.command.order.CreateSellOrderCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ConfirmAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ReserveAmountCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ConfirmTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemsAvailableToReserveInPortfolio;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class SellTradeManagerSaga extends TradeManagerSaga {

    private static final long serialVersionUID = 5337051021661868242L;
    private final static Logger logger = LoggerFactory.getLogger(SellTradeManagerSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionStartedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "A new sell transaction is started with identifier {}, for portfolio with identifier {} and orderbook with identifier {}",
                    event.getTransactionIdentifier(),
                    event.getPortfolioIdentifier(),
                    event.getOrderbookIdentifier());
            logger.debug("The sell transaction with identifier {} is for selling {} items for the price of {}",
                    event.getTransactionIdentifier(),
                    event.getTotalItems(),
                    event.getPricePerItem());
        }

        setTransactionIdentifier(event.getTransactionIdentifier());
        setOrderbookIdentifier(event.getOrderbookIdentifier());
        setPortfolioIdentifier(event.getPortfolioIdentifier());
        setPricePerItem(event.getPricePerItem());
        setTotalItems(event.getTotalItems());

        ReserveAmountCommand reserveAmountCommand =
                new ReserveAmountCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        event.getTotalItems());
        getCommandBus().dispatch(new GenericCommandMessage<ReserveAmountCommand>(reserveAmountCommand));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(ItemsReservedEvent event) {
        final Date confirmDate = new Date();
        logger.debug("Items for transaction {} are reserved, set confirm date {}", getTransactionIdentifier(), confirmDate);
        ConfirmTransactionCommand confirmTransactionCommand = new ConfirmTransactionCommand(getTransactionIdentifier(), confirmDate);
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmTransactionCommand>(confirmTransactionCommand));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(NotEnoughItemsAvailableToReserveInPortfolio event) {
        logger.debug("Cannot continue with transaction with id {} since the items needed cannot be reserved",
                getTotalItems());
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionConfirmedEvent event) {
        logger.debug("Sell Transaction {} is approved to make the sell order", event.getTransactionIdentifier());

        CreateSellOrderCommand command = new CreateSellOrderCommand(new OrderId(),
                getPortfolioIdentifier(),
                getOrderbookIdentifier(),
                getTransactionIdentifier(),
                getTotalItems(),
                getPricePerItem(),
                event.getConfirmedDate());
        getCommandBus().dispatch(new GenericCommandMessage<CreateSellOrderCommand>(command));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(SellTransactionCancelledEvent event) {
        BigDecimal amountOfCancelledItems = event.getTotalAmountOfItems().subtract(event.getAmountOfExecutedItems());
        logger.debug("Sell Transaction {} is cancelled, amount of cash reserved to cancel is {}",
                event.getTransactionIdentifier(),
                amountOfCancelledItems);
        CancelAmountReservationForPortfolioCommand command =
                new CancelAmountReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        amountOfCancelledItems);
        getCommandBus().dispatch(new GenericCommandMessage<CancelAmountReservationForPortfolioCommand>(command));
    }

    @SagaEventHandler(associationProperty = "sellTransactionId", keyName = "transactionIdentifier")
    public void handle(TradeExecutedEvent event) {
        logger.debug("Sell Transaction {} is executed, items for transaction are {} for a price of {}",
                new Object[]{getTransactionIdentifier(), event.getTradeAmount(), event.getTradePrice()});
        ExecutedTransactionCommand command = new ExecutedTransactionCommand(getTransactionIdentifier(),
                event.getTradeAmount(),
                event.getTradePrice());
        getCommandBus().dispatch(new GenericCommandMessage<ExecutedTransactionCommand>(command));
    }


    @SagaEventHandler(associationProperty = "transactionIdentifier")
    @EndSaga
    public void handle(SellTransactionExecutedEvent event) {
        logger.debug("Sell Transaction {} is executed, last amount of executed items is {} for a price of {}",
                new Object[]{event.getTransactionIdentifier(), event.getAmountOfItems(), event.getItemPrice()});

        ConfirmAmountReservationForPortfolioCommand confirmCommand =
                new ConfirmAmountReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        event.getAmountOfItems());
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmAmountReservationForPortfolioCommand>(confirmCommand));
        DepositCashCommand depositCommand =
                new DepositCashCommand(getPortfolioIdentifier(),
                        event.getItemPrice().multiply(event.getAmountOfItems()));
        getCommandBus().dispatch(new GenericCommandMessage<DepositCashCommand>(depositCommand));
    }

    @SagaEventHandler(associationProperty = "transactionIdentifier")
    public void handle(SellTransactionPartiallyExecutedEvent event) {
        logger.debug("Sell Transaction {} is partially executed, amount of executed items is {} for a price of {}",
                new Object[]{event.getTransactionIdentifier(),
                        event.getAmountOfExecutedItems(),
                        event.getItemPrice()});

        ConfirmAmountReservationForPortfolioCommand confirmCommand =
                new ConfirmAmountReservationForPortfolioCommand(getPortfolioIdentifier(),
                        getOrderbookIdentifier(),
                        getTransactionIdentifier(),
                        event.getAmountOfExecutedItems());
        getCommandBus().dispatch(new GenericCommandMessage<ConfirmAmountReservationForPortfolioCommand>(confirmCommand));
        DepositCashCommand depositCommand =
                new DepositCashCommand(getPortfolioIdentifier(),
                        event.getItemPrice().multiply(event.getAmountOfExecutedItems()));
        getCommandBus().dispatch(new GenericCommandMessage<DepositCashCommand>(depositCommand));
    }
}
