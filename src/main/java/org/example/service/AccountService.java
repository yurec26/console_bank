package org.example.service;

import org.example.config.AccountProperties;
import org.example.model.Account;
import org.example.model.User;
import org.example.util.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class AccountService {

    private final AccountProperties accountProperties;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;
    private final UserService userService;

    @Autowired
    public AccountService(AccountProperties accountProperties,
                          TransactionHelper transactionHelper,
                          SessionFactory sessionFactory,
                          @Lazy UserService userService) {
        this.accountProperties = accountProperties;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
        this.userService = userService;
    }

    public Account create(User user) {
        Long initAmount = user.getAccountList().isEmpty()
                ? accountProperties.getDefaultAmount()
                : 0;
        Session session = sessionFactory.getCurrentSession();
        return transactionHelper.execute(() -> {
            Account account = new Account(user, initAmount);
            session.persist(account);
            return account;
        });
    }

    public Account close(Long accountToCloseId) {
        Session session = sessionFactory.getCurrentSession();
        return transactionHelper.execute(() -> {
            Account accountToClose = findAccById(accountToCloseId);
            User user = userService.getUserById(accountToClose.getUserId());
            if (user.getAccountList().size() > 1) {
                Long closingAccountDebit = accountToClose.getMoneyAmount();
                user.getAccountList().remove(accountToClose);
                Account accountToTransfer = user.getAccountList().getFirst();
                accountToTransfer.setMoneyAmount(accountToTransfer.getMoneyAmount() + closingAccountDebit);
                session.remove(accountToClose);
                return accountToTransfer;
            } else {
                throw new IllegalArgumentException("Нельзя закрыть последний аккаунт");
            }
        });
    }

    public Account withdraw(Long id, long amount) {
        return transactionHelper.execute(() -> {
            Account account = findAccById(id);
            if (account.getMoneyAmount() >= amount) {
                account.setMoneyAmount(account.getMoneyAmount() - amount);
            } else {
                throw new IllegalArgumentException("Не хватает денег на счету для снятия");
            }
            return account;
        });
    }

    public Account deposit(Long id, long amount) {
        return transactionHelper.execute(() -> {
            Account account = findAccById(id);
            account.setMoneyAmount(account.getMoneyAmount() + amount);
            return account;

        });
    }


    public Account transfer(Long idAccountFrom, Long idAccountTo, long amount) {
        return transactionHelper.execute(() -> {
            Account accountFrom = findAccById(idAccountFrom);
            Account accountTo = findAccById(idAccountTo);
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
        });
    }

    public Account addAccountToUser(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        return transactionHelper.execute(() -> {
            User user = session.get(User.class, userId);
            Account account = create(user);
            session.persist(account);
            session.merge(user);
            session.flush();
            session.refresh(account);
            return account;
        });
    }

    public Account findAccById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Account account = transactionHelper.execute(() -> session.get(Account.class, id));
        if (account != null) {
            return account;
        } else {
            throw new IllegalArgumentException("Аккаунт с id %s не найден".formatted(id));
        }
    }
}
