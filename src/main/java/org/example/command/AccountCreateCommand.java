package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountCreateCommand implements OperationCommand {

    public final UserService userService;
    public final AccountService accountService;
    private final Scanner scanner;

    public AccountCreateCommand(UserService userService,
                                AccountService accountService,
                                Scanner scanner) {
        this.scanner = scanner;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id пользователя для создания нового счёта: ");
        int id = Integer.parseInt(scanner.nextLine());
        try {
            var user = userService.findById(id);
            var account = accountService.create(id);
            userService.addAccountToUser(account, user);
            System.out.printf("Новый аккаунт создан с id %s для пользователя %s%n",
                    account.getId(), user.getLogin());
        } catch (NoSuchElementException e) {
            System.out.printf("Пользователь с таким id не найден : %s%n%n", id);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
