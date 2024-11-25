package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.model.Account;
import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountWithdrawCommand implements OperationCommand {

    public final AccountService accountService;
    private final Scanner scanner;

    public AccountWithdrawCommand(AccountService accountService,
                                  Scanner scanner) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id счёта для списания: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.println("Введите сумму для списания: ");
        long amount = Long.parseLong(scanner.nextLine());
        try {
            Account account = accountService.withdraw(id, amount);
            System.out.printf("Деньги успешно сняты со счёта %s%n", account);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}
