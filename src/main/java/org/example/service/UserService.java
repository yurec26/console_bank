package org.example.service;

import org.example.model.Account;
import org.example.model.User;
import org.example.util.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class UserService {


    private final AccountService accountService;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;


    public UserService(AccountService accountService,
                       TransactionHelper transactionHelper, SessionFactory sessionFactory) {
        this.accountService = accountService;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
    }

    public List<User> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return transactionHelper.execute(() -> session
                .createQuery("SELECT u FROM User u JOIN FETCH u.accountList a", User.class)
                .list());
    }

    public User createUser(String login) {
        Session session = sessionFactory.getCurrentSession();
        return transactionHelper.execute(() -> {
            try {
                User user = new User(login);
                session.persist(user);
                Account account = accountService.create(user);
                session.persist(account);
                session.flush();
                session.refresh(user);
                return user;
            } catch (ConstraintViolationException ex) {
                throw new IllegalArgumentException("Такой логин уже занят");
            } catch (Exception e) {
                throw new IllegalArgumentException("Ошибка при создании пользователя: " + e.getMessage());
            }
        });
    }

    public User getUserById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = transactionHelper.execute(() -> session.get(User.class, id));
        if (user != null) {
            return user;
        } else {
            throw new IllegalArgumentException("Пользователь с id %s не найден".formatted(id));
        }
    }

}
