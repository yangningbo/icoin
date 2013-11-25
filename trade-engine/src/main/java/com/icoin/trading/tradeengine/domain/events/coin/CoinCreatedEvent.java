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

package com.icoin.trading.tradeengine.domain.events.coin;

import com.icoin.trading.tradeengine.domain.model.coin.CoinId;

/**
 * <p>A new coin is created with a certain value and an amount of shares. Those two values can be used to calculate
 * the starting point for the value of a share.</p>
 *
 * @author Jettro Coenradie
 */
public class CoinCreatedEvent {
    private CoinId coinId;
    private String coinName;
    private long coinInitialPrice;
    private long coinInitialAmount;

    public CoinCreatedEvent(CoinId coinId, String coinName, long coinInitialAmount, long coinInitialPrice) {
        this.coinInitialAmount = coinInitialAmount;
        this.coinName = coinName;
        this.coinInitialPrice = coinInitialPrice;
        this.coinId = coinId;
    }

    public CoinId getCoinIdentifier() {
        return this.coinId;
    }

    public long getCoinInitialAmount() {
        return coinInitialAmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public long getCoinInitialPrice() {
        return coinInitialPrice;
    }
}