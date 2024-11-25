package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.model.Account;
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
        Long accountIdToClose = Long.parseLong(scanner.nextLine());
        try {
            Account account = accountService.close(accountIdToClose);
            System.out.printf("Счёт закрыт, баланс переведё на счёт : %s%n", account);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
