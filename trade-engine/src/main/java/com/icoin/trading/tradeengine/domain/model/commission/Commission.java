/*
 * Copyright 2011-2012 the original author or authors.
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
package com.icoin.trading.tradeengine.domain.model.commission;

import com.homhon.base.domain.model.ValueObjectSupport;
import org.joda.money.BigMoney;
import org.joda.money.Money;

import java.math.RoundingMode;

import static com.homhon.util.Asserts.notNull;


/**
 * @author Slawek
 */
public class Commission extends ValueObjectSupport<Commission> {
    private Money commission;
    private String description;

    public Commission(BigMoney commission, String description) {
        this(commission.toMoney(RoundingMode.HALF_EVEN), description);
    }

    public Commission(Money commission, String description) {
        notNull(commission);
        this.commission = commission;
        this.description = description;
    }

    public Money getCommission() {
        return commission;
    }

    public BigMoney getBigMoneyCommission() {
        return commission.toBigMoney();
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Commission{" +
                "commission=" + commission +
                ", description='" + description + '\'' +
                '}';
    }
}