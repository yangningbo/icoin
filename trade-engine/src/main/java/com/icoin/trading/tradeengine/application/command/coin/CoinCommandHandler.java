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

package com.icoin.trading.tradeengine.application.command.coin;

import com.icoin.trading.api.tradeengine.command.coin.AddOrderBookToCoinCommand;
import com.icoin.trading.api.tradeengine.command.coin.CreateCoinCommand;
import com.icoin.trading.tradeengine.domain.model.coin.Coin;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class CoinCommandHandler {

    private Repository<Coin> repository;

    @CommandHandler
    public void handleCreateCoin(CreateCoinCommand command) {
        Coin coin = new Coin(command.getCoinId(),
                command.getCoinName(),
                command.getCoinInitialPrice(),
                command.getCoinInitialAmount());
        repository.add(coin);
    }

    @CommandHandler
    public void handleAddOrderBook(AddOrderBookToCoinCommand command) {
        Coin coin = repository.load(command.getCoinId());
        coin.addOrderBook(command.getOrderBookId(), command.getCurrencyPair());
    }

    @Autowired
    @Qualifier("coinRepository")
    public void setRepository(Repository<Coin> coinRepository) {
        this.repository = coinRepository;
    }
}
