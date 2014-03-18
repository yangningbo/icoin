package com.icoin.trading.fee.domain.transaction;

import com.homhon.base.domain.Identity;
import com.icoin.axonsupport.domain.AxonAnnotatedAggregateRoot;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.CommissionType;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.events.commission.SellExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.money.BigMoney;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-3
 * Time: PM9:01
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionTransaction extends AxonAnnotatedAggregateRoot<ExecutedCommissionTransaction, String> {

    @AggregateIdentifier
    @Identity
    private String feeTransactionId;
    private BigMoney commissionAmount;
    private String orderId;
    private String orderTransactionId;
    private String portfolioId;
    private Date tradeTime;
    private Date dueDate;
    private CommissionType commissionType;

    @SuppressWarnings("UnusedDeclaration")
    protected ExecutedCommissionTransaction() {
    }

    public ExecutedCommissionTransaction(FeeTransactionId feeTransactionId,
                                         CommissionType commissionType,
                                         BigMoney commissionAmount,
                                         String orderId,
                                         TransactionId orderTransactionId,
                                         PortfolioId portfolioId,
                                         Date tradeTime,
                                         Date dueDate,
                                         TradeType tradeType,
                                         BigMoney tradedPrice,
                                         BigMoney tradeAmount,
                                         BigMoney executedMoney,
                                         OrderBookId orderBookId,
                                         CoinId coinId) {
        notNull(commissionType);

        switch (commissionType) {
            case SELL:
                apply(new SellExecutedCommissionTransactionStartedEvent(
                        feeTransactionId.toString(),
                        commissionAmount,
                        orderId,
                        orderTransactionId.toString(),
                        portfolioId.toString(),
                        tradeTime,
                        dueDate,
                        tradeType,
                        tradedPrice,
                        tradeAmount,
                        executedMoney,
                        orderBookId.toString(),
                        coinId.toString()
                ));
                break;
            case BUY:
                break;
        }
    }

    @EventHandler
    public void on(SellExecutedCommissionTransactionStartedEvent event) {
        feeTransactionId = event.getFeeTransactionId();
        commissionAmount = event.getCommissionAmount();
        orderId = event.getOrderId();
        orderTransactionId = event.getOrderTransactionId();
        portfolioId = event.getPortfolioId();
        tradeTime = event.getTradeTime();
        dueDate = event.getDueDate();
        commissionType = CommissionType.SELL;
    }


}