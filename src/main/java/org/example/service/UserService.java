package org.example.service;

import org.example.model.Account;
import org.example.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Component
public class UserService {

    private final List<User> userList = new ArrayList<>();

    private static int idCounter = 1;
    private final AccountService accountService;

    public UserService(AccountService accountService) {
        this.accountService = accountService;
    }

    public List<User> getAll() {
        return userList;
    }

    public User createUser(String login) {
        userList.stream().filter(s -> s.getLogin().equals(login)).findAny()
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Имя учётной записи занято");
                });
        var newUser = new User(idCounter, login, new ArrayList<>());
        userList.add(newUser);
        accountService.create(newUser.getId());
        idCounter++;
        return newUser;
    }

    public void closeAccount(User user, Account account) {
        user.getAccountList().remove(account);
    }

    public void addAccountToUser(Account account, User user) {
        user.getAccountList().add(account);
    }


    public User findById(int id) {
        return userList.stream().filter(s -> s.getId() == id)
                .findAny().orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
    }

    public User findUserByAccount(int accountId) {
        return userList.stream()
                .filter(s -> s.getAccountList().stream()
                        .anyMatch(q -> q.getId() == accountId))
                .findFirst().orElseThrow(() -> new NoSuchElementException("Счёт не найден"));
    }

}
