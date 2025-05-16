package ManageAcc;

import java.io.*;

import ManageAcc.Account;

public class ObjectIn {
    public static void main(String[] args) {
        try {
            // ファイルからオブジェクトを読み込む
            String inputFile = args[0];
            FileInputStream inFile = new FileInputStream(inputFile);
            ObjectInputStream objectIn = new ObjectInputStream(inFile);

            Account inAccount = (Account) objectIn.readObject();
            objectIn.close();

            // inAccount から情報を取り出して画面に出力
            System.out.println("銀行名: " + inAccount.bankName);
            System.out.println("支店名: " + inAccount.branchName);
            System.out.println("口座所有者: " + inAccount.accountHolder);
            System.out.println("預金残額: " + inAccount.getBalance() + "円");

            inFile.close();
            objectIn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}