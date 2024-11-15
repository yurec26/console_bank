package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AccountProperties {
    private final Long defaultAmount;
    private final double transferCommission;

    public AccountProperties(
            @Value("${account.default-amount}") Long defaultAmount,
            @Value("${account.transfer-commission}") double transferCommission) {
        this.defaultAmount = defaultAmount;
        this.transferCommission = transferCommission;
    }

    public Long getDefaultAmount() {
        return defaultAmount;
    }

    public double getTransferCommission() {
        return transferCommission;
    }
}
