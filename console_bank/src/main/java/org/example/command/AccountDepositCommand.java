package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountDepositCommand implements OperationCommand {

    public final UserService userService;
    public final AccountService accountService;
    private final Scanner scanner;

    public AccountDepositCommand(UserService userService,
                                 AccountService accountService,
                                 Scanner scanner) {
        this.scanner = scanner;
        this.userService = userService;
        this.accountService = accountService;
    }


    @Override
    public void execute() {
        System.out.println("Введите id счёта для пополнения: ");
        int id = Integer.parseInt(scanner.nextLine());
        try {
            var account = accountService.findAccountById(id);
            System.out.println("Введите сумму для пополнения: ");
            long amount = Long.parseLong(scanner.nextLine());
            accountService.deposit(account, amount);
            System.out.printf("счёт с id %s пополнен на %s", id, amount);
        } catch (NoSuchElementException e) {
            System.out.printf("счёт с таким id не найден : %s%n", id);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
