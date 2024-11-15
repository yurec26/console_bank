package org.example.service;

import org.example.config.AccountProperties;
import org.example.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class AccountService {

    private final List<Account> accountList = new ArrayList<>();

    private static int idCounter = 1;

    private final AccountProperties accountProperties;

    @Autowired
    public AccountService(AccountProperties accountProperties) {
        this.accountProperties = accountProperties;
    }

    public Account create(int userId) {
        var account = new Account(idCounter, userId, accountProperties.getDefaultAmount());
        accountList.add(account);
        idCounter++;
        return account;
    }

    public void close(Account account) {
        accountList.remove(account);
    }

    public void withdraw(Account account, long amount) {
        if (account.getMoneyAmount() >= amount) {
            account.setMoneyAmount(account.getMoneyAmount() - amount);
            accountList.set(accountList.indexOf(account), account);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void deposit(Account account, long amount) {
        account.setMoneyAmount(account.getMoneyAmount() + amount);
        accountList.set(accountList.indexOf(account), account);
    }

    public Account findAccountById(int id) {
        return accountList.stream()
                .filter(s -> s.getId() == id)
                .findAny().orElseThrow(NoSuchElementException::new);

    }


    public void transfer(Account accountFrom, Account accountTo, long amount) {
        if (amount > accountFrom.getMoneyAmount()) {
            throw new IllegalArgumentException();
        }
        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amount);
        long amountToTransfer = accountTo.getUserId() != accountFrom.getUserId()
                ? Math.round(amount * (1 - accountProperties.getTransferCommission
                ()))
                : amount;
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + amountToTransfer);
    }
}
