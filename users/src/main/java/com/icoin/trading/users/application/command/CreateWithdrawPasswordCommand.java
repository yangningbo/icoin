package com.icoin.trading.users.application.command;

import com.homhon.base.command.CommandSupport;
import com.homhon.util.Strings;
import com.icoin.trading.api.users.domain.UserId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/10/14
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateWithdrawPasswordCommand extends CommandSupport<CreateWithdrawPasswordCommand> {
    @NotNull(message = "The provided userId cannot be null")
    private UserId userId;
    @NotNull
    @Size(min = 6, message = "The provided username cannot be null", max = 16)
    private String username;

    @Size(min = 6, max = 16)
    private String withdrawPassword;

    @Size(min = 6, max = 16)
    private String confirmedWithdrawPassword;

    private String operatingIp;
    private Date createdTime;

    public CreateWithdrawPasswordCommand(UserId userId,
                                         String username,
                                         String withdrawPassword,
                                         String confirmedWithdrawPassword,
                                         String operatingIp,
                                         Date createdTime) {
        notNull(userId, "The provided userId cannot be null");

        isTrue(withdrawPassword.equals(confirmedWithdrawPassword), "The withdraw password and confirmed withdraw password should be the same.");

        this.userId = userId;
        this.username = username;
        this.withdrawPassword = withdrawPassword;
        this.confirmedWithdrawPassword = confirmedWithdrawPassword;
        this.operatingIp = operatingIp;
        this.createdTime = createdTime;
    }

    public boolean isValid() {
        return Strings.hasText(withdrawPassword)
                && withdrawPassword.equals(confirmedWithdrawPassword);
    }

    public UserId getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getWithdrawPassword() {
        return withdrawPassword;
    }

    public String getConfirmedWithdrawPassword() {
        return confirmedWithdrawPassword;
    }

    public String getOperatingIp() {
        return operatingIp;
    }

    public Date getCreatedTime() {
        return createdTime;
    }
}
