package org.example.service;

import org.example.model.Account;
import org.example.model.User;
import org.example.util.TransactionHelper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class UserService {


    private final AccountService accountService;
    private final TransactionHelper transactionHelper;


    public UserService(AccountService accountService,
                       TransactionHelper transactionHelper) {
        this.accountService = accountService;
        this.transactionHelper = transactionHelper;
    }

    public List<User> getAll() {
        return transactionHelper.execute(session -> session
                .createQuery("SELECT s FROM User s", User.class)
                .list());
    }

    public User createUser(String login) {
        return transactionHelper.execute(session -> {
            try {
                User user = new User(login);
                session.persist(user);
                Account account = accountService.create(user);
                session.persist(account);
                session.flush();
                session.refresh(user);
                return user;
            } catch (Exception e) {
                throw new IllegalArgumentException("Такой логин уже занят");
            }
        });
    }

    public Account addAccountToUser(Long userId) {
        return transactionHelper.execute(session -> {
            User user = session.get(User.class, userId);
            if (user != null){
                Account account = accountService.create(user);
                session.persist(account);
                session.merge(user);
                session.flush();
                session.refresh(account);
                return account;
            } else {
                throw new IllegalArgumentException("Такой пользователь не найден");
            }
        });
    }
}
