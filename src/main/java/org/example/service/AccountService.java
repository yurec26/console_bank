package org.example.service;

import org.example.config.AccountProperties;
import org.example.model.Account;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Component
public class AccountService {

    private final List<Account> accountList = new ArrayList<>();

    private static int idCounter = 1;

    private final AccountProperties accountProperties;
    private final UserService userService;

    @Autowired
    public AccountService(AccountProperties accountProperties,
                          @Lazy UserService userService) {
        this.accountProperties = accountProperties;
        this.userService = userService;
    }

    public Account create(int userId) {
        User user = userService.findById(userId);
        Account account = new Account(idCounter, userId, accountProperties.getDefaultAmount());
        userService.addAccountToUser(account, user);
        accountList.add(account);
        idCounter++;
        return account;
    }

    public void close(int accountToCloseId) {
        User user = userService.findUserByAccount(accountToCloseId);
        if (user.getAccountList().size() > 1) {
            Account accountToClose = findAccountById(accountToCloseId);
            Long closingAccountDebit = accountToClose.getMoneyAmount();
            userService.closeAccount(user, accountToClose);
            user.getAccountList().getFirst().setMoneyAmount(user.getAccountList().getFirst()
                    .getMoneyAmount() + closingAccountDebit);
            accountList.remove(accountToClose);
        } else {
            throw new IllegalArgumentException("Нельзя закрыть последний счёт");
        }
    }

    public void withdraw(int id, long amount) {
        Account account = findAccountById(id);
        if (account.getMoneyAmount() >= amount) {
            account.setMoneyAmount(account.getMoneyAmount() - amount);
            accountList.set(accountList.indexOf(account), account);
        } else {
            throw new IllegalArgumentException("Не хватает денег на счету для снятия");
        }
    }

    public void deposit(int userId, long amount) {
        Account account = findAccountById(userId);
        account.setMoneyAmount(account.getMoneyAmount() + amount);
        accountList.set(accountList.indexOf(account), account);
    }

    public Account findAccountById(int id) {
        return accountList.stream()
                .filter(s -> s.getId() == id)
                .findAny().orElseThrow(()-> new NoSuchElementException("Аккаунт не найден"));

    }


    public void transfer(int idAccountFrom, int idAccountTo, long amount) {
        Account accountFrom = findAccountById(idAccountFrom);
        Account accountTo = findAccountById(idAccountTo);
        if (amount > accountFrom.getMoneyAmount()) {
            throw new IllegalArgumentException("Недостаточно средств для перевода");
        }
        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amount);
        long amountToTransfer = accountTo.getUserId() != accountFrom.getUserId()
                ? Math.round(amount * (1 - accountProperties.getTransferCommission
                ()))
                : amount;
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + amountToTransfer);
    }
}
