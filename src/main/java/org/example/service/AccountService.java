package org.example.service;

import org.example.config.AccountProperties;
import org.example.model.Account;
import org.example.model.User;
import org.example.util.TransactionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class AccountService {

    private final AccountProperties accountProperties;
    private final TransactionHelper transactionHelper;

    @Autowired
    public AccountService(AccountProperties accountProperties,
                          TransactionHelper transactionHelper) {
        this.accountProperties = accountProperties;
        this.transactionHelper = transactionHelper;
    }

    public Account create(User user) {
        Long initAmount = user.getAccountList().isEmpty()
                ? accountProperties.getDefaultAmount()
                : 0;
        return new Account(user, initAmount);
    }

    public Account close(Long accountToCloseId) {
        return transactionHelper.execute(session -> {
            Account accountToClose = session.get(Account.class, accountToCloseId);
            if (accountToClose != null) {
                User user = session.get(User.class, accountToClose.getUserId());
                if (user.getAccountList().size() > 1) {
                    session.persist(accountToClose);
                    session.persist(user);
                    Long closingAccountDebit = accountToClose.getMoneyAmount();
                    user.getAccountList().remove(accountToClose);
                    Account accountToTransfer = user.getAccountList().getFirst();
                    accountToTransfer.setMoneyAmount(accountToTransfer.getMoneyAmount() + closingAccountDebit);
                    session.remove(accountToClose);
                    return accountToTransfer;
                } else {
                    throw new IllegalArgumentException("Нельзя закрыть последний аккаунт");
                }
            } else {
                throw new IllegalArgumentException("Аккаунт не найден");
            }
        });
    }

    public Account withdraw(int id, long amount) {
        return transactionHelper.execute(session -> {
            Account account = session.get(Account.class, id);
            if (account!= null){
                session.persist(account);
                if (account.getMoneyAmount() >= amount) {
                    account.setMoneyAmount(account.getMoneyAmount() - amount);
                } else {
                    throw new IllegalArgumentException("Не хватает денег на счету для снятия");
                }
                return account;
            } else {
                throw new IllegalArgumentException("Аккаунт не найден");
            }
        });
    }

    public Account deposit(int id, long amount) {
        return transactionHelper.execute(session -> {
            Account account = session.get(Account.class, id);
            if (account != null) {
                session.persist(account);
                account.setMoneyAmount(account.getMoneyAmount() + amount);
                return account;
            } else {
                throw new IllegalArgumentException("Аккаунт не найден");
            }
        });
    }


    public Account transfer(int idAccountFrom, int idAccountTo, long amount) {
        return transactionHelper.execute(session -> {
            Account accountFrom = session.get(Account.class, idAccountFrom);
            Account accountTo = session.get(Account.class, idAccountTo);
            if (accountTo != null && accountFrom != null){
                session.persist(accountFrom);
                session.persist(accountTo);
                if (amount > accountFrom.getMoneyAmount()) {
                    throw new IllegalArgumentException("Недостаточно средств для перевода");
                }
                accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amount);
                long amountToTransfer = !Objects.equals(accountTo.getUserId(), accountFrom.getUserId())
                        ? Math.round(amount * (1 - accountProperties.getTransferCommission
                        ()))
                        : amount;
                accountTo.setMoneyAmount(accountTo.getMoneyAmount() + amountToTransfer);
                return accountFrom;
            } else {
                throw new IllegalArgumentException("Аккаунт не найден");
            }
        });
    }
}
