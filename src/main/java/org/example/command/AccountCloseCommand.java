package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountCloseCommand implements OperationCommand {

    public final AccountService accountService;
    private final Scanner scanner;

    public AccountCloseCommand(AccountService accountService,
                               Scanner scanner) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id счёта для закрытия: ");
        int accountIdToClose = Integer.parseInt(scanner.nextLine());
        try {
            accountService.close(accountIdToClose);
            System.out.printf("Счёт закрыт : %s%n", accountIdToClose);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
