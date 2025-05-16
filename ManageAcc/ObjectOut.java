package ManageAcc;

import java.io.*;

import ManageAcc.Account;

public class ObjectOut {
    public static void main(String[] args) {
        try {
            // 引数から値を取得
            String accountHolder = args[0];
            int initialBalance = Integer.parseInt(args[1]);
            int goal = Integer.parseInt(args[2]);
            int depositPerYear = Integer.parseInt(args[3]);
            String outputFile = args[4];

            // Account のインスタンス myAccount を作成
            Account myAccount = new Account(accountHolder, initialBalance);

            // 預金残額が目標金額に達するまで繰り返す
            int years = 0;
            while (myAccount.getBalance() < goal) {
                myAccount.deposit(depositPerYear);
                years++;
            }

            // ファイルにオブジェクトを出力
            FileOutputStream outFile = new FileOutputStream(outputFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(outFile);
            objectOut.writeObject(myAccount);
            objectOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}