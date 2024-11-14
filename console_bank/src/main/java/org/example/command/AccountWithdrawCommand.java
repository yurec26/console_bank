package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountWithdrawCommand implements OperationCommand {

    public final UserService userService;
    public final AccountService accountService;
    private final Scanner scanner;

    public AccountWithdrawCommand(UserService userService,
                                  AccountService accountService,
                                  Scanner scanner) {
        this.scanner = scanner;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id счёта для списания: ");
        int id = Integer.parseInt(scanner.nextLine());
        try {
            var account = accountService.findAccountById(id);
            System.out.println("Введите сумму для пополнения: ");
            long amount = Long.parseLong(scanner.nextLine());
            try {
                accountService.withdraw(account, amount);
                System.out.println("Деньги успешно сняты");
                System.out.println(account);
            } catch (IllegalArgumentException e) {
                System.out.printf("Не хватает денег на счёте списания: id=%s," +
                                " moneyAmount=%s, attemptedWithdraw=%s%n",
                        account.getId(), account.getMoneyAmount(),
                        amount);
            }
        } catch (NoSuchElementException e) {
            System.out.printf("счёт с таким id не найден : %s%n", id);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}
