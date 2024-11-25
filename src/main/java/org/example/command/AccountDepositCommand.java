package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.model.Account;
import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountDepositCommand implements OperationCommand {


    public final AccountService accountService;
    private final Scanner scanner;

    public AccountDepositCommand(AccountService accountService,
                                 Scanner scanner) {
        this.scanner = scanner;
        this.accountService = accountService;
    }


    @Override
    public void execute() {
        System.out.println("Введите id счёта для пополнения: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            System.out.println("Введите сумму для пополнения: ");
            long amount = Long.parseLong(scanner.nextLine());
            if (amount>0){
                Account account = accountService.deposit(id, amount);
                System.out.printf("счёт пополнен %s%n", account);
            } else {
                throw new IllegalArgumentException("Введите положительное число");
            }
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
