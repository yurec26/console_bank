package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountCloseCommand implements OperationCommand {

    public final UserService userService;
    public final AccountService accountService;
    private final Scanner scanner;

    public AccountCloseCommand(UserService userService,
                               AccountService accountService,
                               Scanner scanner) {
        this.scanner = scanner;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id счёта для закрытия: ");
        int accountIdToClose = Integer.parseInt(scanner.nextLine());
        try {
            var user = userService.findUserByAccount(accountIdToClose);
            if (user.getAccountList().size() > 1) {
                var accountToClose = accountService.findAccountById(accountIdToClose);
                var closingAccountDebit = accountService
                        .findAccountById(accountIdToClose).getMoneyAmount();
                userService.closeAccount(user, accountToClose);
                user.getAccountList().getFirst().setMoneyAmount(user.getAccountList().getFirst()
                        .getMoneyAmount() + closingAccountDebit);
                accountService.close(accountToClose);
                System.out.printf("Счёт закрыт : %s%n", accountIdToClose);
            } else {
                System.out.println("Нельзя закрыть последний счёт");
            }
        } catch (NoSuchElementException e) {
            System.out.printf("Счёта с таким айди не существует : %s%n", accountIdToClose);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
