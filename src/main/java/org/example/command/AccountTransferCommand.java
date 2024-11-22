package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.model.Account;
import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountTransferCommand implements OperationCommand {


    public final AccountService accountService;
    private final Scanner scanner;

    public AccountTransferCommand(AccountService accountService,
                                  Scanner scanner) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id счёта для списания: ");
        int idAccountFrom = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите id счёта для пополнения: ");
        int idAccountTo = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите сумму для пополнения: ");
        long amount = Long.parseLong((scanner.nextLine()));
        try {
            Account accountFrom =accountService.transfer(idAccountFrom, idAccountTo, amount);
            System.out.printf("Деньги успешно переведены c аккаунта: %s%n", accountFrom);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
