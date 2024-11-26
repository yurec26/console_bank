package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.model.Account;
import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountCreateCommand implements OperationCommand {

    public final AccountService accountService;
    public final UserService userService;
    private final Scanner scanner;

    public AccountCreateCommand(AccountService accountService,
                                UserService userService,
                                Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id пользователя для создания нового счёта: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            Account account = accountService.addAccountToUser(id);
            System.out.printf("Новый аккаунт создан с id %s для пользователя с id %s%n",
                    account.getId(), account.getUserId());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
