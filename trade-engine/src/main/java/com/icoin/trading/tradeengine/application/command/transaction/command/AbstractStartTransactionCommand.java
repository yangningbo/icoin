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

package com.icoin.trading.tradeengine.application.command.transaction.command;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

/**
 * @author Jettro Coenradie
 */
public abstract class AbstractStartTransactionCommand<T extends AbstractStartTransactionCommand> extends CommandSupport<T>{

    private TransactionId transactionId;
    private OrderBookId orderbookIdentifier;
    private PortfolioId portfolioIdentifier;
    private BigMoney tradeAmount;
    private BigMoney itemPrice;

    public AbstractStartTransactionCommand(TransactionId transactionId, OrderBookId orderbookIdentifier,
                                           PortfolioId portfolioIdentifier, BigMoney tradeAmount, BigMoney itemPrice) {
        this.transactionId = transactionId;
        this.itemPrice = itemPrice;
        this.orderbookIdentifier = orderbookIdentifier;
        this.portfolioIdentifier = portfolioIdentifier;
        this.tradeAmount = tradeAmount;
    }

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public OrderBookId getOrderbookIdentifier() {
        return orderbookIdentifier;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionId;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }
}
