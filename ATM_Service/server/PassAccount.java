package ATM_Service.server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PassAccount extends ManageAcc.Account {
    private String accountPassword;
    private String accountNumber;
    private List<String> transactionHistory = new ArrayList<>();

    public PassAccount(String bankName, String branchName, String accountHolder, int initialValue,
            String accountPassword, String accountNumber) {
        super(bankName, branchName, accountHolder, initialValue);
        this.accountPassword = accountPassword;
        this.accountNumber = accountNumber;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    private List<String> history = new ArrayList<>();

    public void addTransaction(String date, String type, int amount, int balance) {
        history.add(date + "," + type + "," + amount + "," + balance);
    }

    public String getHistory() {
        return String.join("\n", history);
    }

    public void transfer(PassAccount targetAccount, int amount) {
        if (amount > 0 && amount <= getBalance()) {
            // 振込元の処理
            int tmp = draw(amount);
            targetAccount.deposit(amount);

            // 現在の日付を取得
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // 振込元の取引履歴を更新
            addTransaction(date, "振込(送金)", amount, getBalance());

            // 振込先の取引履歴を更新
            targetAccount.addTransaction(date, "振込(受取)", amount, targetAccount.getBalance());
        } else {
            System.out.println("Invalid transfer amount.");
        }
    }
}