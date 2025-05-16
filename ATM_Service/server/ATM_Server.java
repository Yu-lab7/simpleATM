package ATM_Service.server;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.*;

import ATM_Service.server.PassAccount;

import java.security.*;
import java.text.SimpleDateFormat;

class ATMServerThread extends Thread {
    private Socket socket;

    public ATMServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            byte[] rbuf = new byte[1024];
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            int len = in.read(rbuf);
            if (len != -1) {
                String accountInfo = new String(rbuf, 0, len);
                String[] accountParts = accountInfo.split(":");
                String accountName = accountParts[0];
                String tmp = accountParts[1];

                System.out.println("クライアントからの口座情報: " + accountName + " (" + tmp + ")");

                // ファイルから口座情報を読み込む
                File file = new File("./ATM_Service/server/accounts/" + accountName + tmp + ".txt");

                if (!file.exists()) {
                    out.write("NEW".getBytes());
                    out.flush();

                    len = in.read(rbuf);
                    if (len != -1) {
                        String newAccountInfo = new String(rbuf, 0, len);
                        String[] newAccountParts = newAccountInfo.split(":");
                        String newAccountName = newAccountParts[0];
                        String newAccountPassword = newAccountParts[1];
                        // ランダムな口座番号を生成
                        Random random = new Random();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 10; i++) {
                            sb.append(random.nextInt(10)); // 0-9の数字を追加
                        }
                        String accountNumber = sb.toString(); // 10桁のランダムな口座番号を生成
                        System.out.println("新規口座開設: " + newAccountName + " (" + accountNumber + ")");

                        File directory = new File("./Ex5_3/server/accounts/");
                        if (!directory.exists()) {
                            directory.mkdirs(); // ディレクトリを作成
                        }

                        file = new File("./ATM_Service/server/accounts/" + accountName + accountNumber + ".txt");

                        PassAccount newAccount = new PassAccount("銀行名", "支店名", newAccountName, 0, newAccountPassword,
                                accountNumber);
                        ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(file));
                        objectOut.writeObject(newAccount);
                        objectOut.close();

                        out.write("OK".getBytes());
                        out.flush();

                        out.write(accountNumber.getBytes());
                        out.flush();
                    }
                } else {
                    ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(file));
                    PassAccount account = (PassAccount) objectIn.readObject();
                    objectIn.close();

                    out.write("OK".getBytes());
                    out.flush();

                    // サービス処理
                    while (true) {
                        len = in.read(rbuf);
                        if (len != -1) {
                            String service = new String(rbuf, 0, len);
                            if (service.equals("1")) {
                                len = in.read(rbuf);
                                if (len != -1) {
                                    int depositAmount = Integer.parseInt(new String(rbuf, 0, len));
                                    account.deposit(depositAmount);

                                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                    account.addTransaction(date, "預金", depositAmount, account.getBalance());
                                    System.out.println("預金処理完了: " + depositAmount + "円");
                                    System.out.println("");
                                }
                            } else if (service.equals("2")) {
                                len = in.read(rbuf);
                                if (len != -1) {
                                    String msg = new String(rbuf, 0, len);
                                    String[] parts = msg.split(":");
                                    String rquest = parts[0];
                                    String accName = parts[1];
                                    String accNum = parts[2];

                                    if (rquest.equals("REQUEST")) {
                                        int randomNum = (int) (Math.random() * 10000);
                                        out.write(String.valueOf(randomNum).getBytes());
                                        out.flush();

                                        len = in.read(rbuf);
                                        if (len != -1) {
                                            String response1 = new String(rbuf, 0, len);

                                            String workpass = String.valueOf(randomNum) + account.getAccountPassword();
                                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                                            byte[] cipher_byte;
                                            md.update(workpass.getBytes());
                                            cipher_byte = md.digest();

                                            StringBuilder sb = new StringBuilder();
                                            for (byte b : cipher_byte) {
                                                sb.append(String.format("%02x", b));
                                            }
                                            String response2 = sb.toString();

                                            if (response1.equals(response2)) {
                                                System.out.println("引き出しシステム: chap認証成功");
                                                out.write("OK".getBytes());
                                                out.flush();

                                                len = in.read(rbuf);
                                                if (len != -1) {
                                                    String amount = new String(rbuf, 0, len);
                                                    int withdrawAmount = Integer.parseInt(amount);
                                                    System.out.println("引き出し額: " + withdrawAmount + "円");
                                                    account.draw(withdrawAmount);
                                                    System.out.println("引き出し処理完了: " + withdrawAmount + "円");
                                                    System.out.println("");

                                                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                            .format(new Date());
                                                    account.addTransaction(date, "引き出し", withdrawAmount,
                                                            account.getBalance());
                                                    out.write("OK".getBytes());
                                                    out.flush();
                                                }
                                            } else {
                                                out.write("NG".getBytes());
                                                out.flush();
                                                System.out.println("レスポンス1と2が一致しません。");
                                                System.out.println("");
                                            }
                                        }
                                    }
                                }
                            } else if (service.equals("3")) {
                                len = in.read(rbuf);
                                if (len != -1) {
                                    String msg = new String(rbuf, 0, len);
                                    String[] parts = msg.split(":");
                                    String rquest = parts[0];
                                    String accName = parts[1];
                                    String accNum = parts[2];

                                    if (rquest.equals("REQUEST")) {
                                        int randomNum = (int) (Math.random() * 10000);
                                        out.write(String.valueOf(randomNum).getBytes());
                                        out.flush();

                                        len = in.read(rbuf);
                                        if (len != -1) {
                                            String response1 = new String(rbuf, 0, len);

                                            String workpass = String.valueOf(randomNum) + account.getAccountPassword();
                                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                                            byte[] cipher_byte;
                                            md.update(workpass.getBytes());
                                            cipher_byte = md.digest();

                                            StringBuilder sb = new StringBuilder();
                                            for (byte b : cipher_byte) {
                                                sb.append(String.format("%02x", b));
                                            }
                                            String response2 = sb.toString();

                                            if (response1.equals(response2)) {
                                                System.out.println("残高照会システム: chap認証成功");
                                                out.write("OK".getBytes());
                                                out.flush();
                                                System.out.println("残高: " + account.getBalance() + "円");
                                                System.out.println("");
                                                out.write(String.valueOf(account.getBalance()).getBytes());
                                                out.flush();

                                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                        .format(new Date());
                                                account.addTransaction(date, "残高照会", 0, account.getBalance());
                                            } else {
                                                out.write("NG".getBytes());
                                                out.flush();
                                                System.out.println("レスポンス1と2が一致しません。");
                                                System.out.println("");
                                            }
                                        }
                                    }
                                }
                            } else if (service.equals("4")) {
                                len = in.read(rbuf);
                                if (len != -1) {
                                    String msg = new String(rbuf, 0, len);
                                    String[] parts = msg.split(":");
                                    String rquest = parts[0];
                                    String accName = parts[1];
                                    String accNum = parts[2];


                                    if (rquest.equals("REQUEST")) {
                                        int randomNum = (int) (Math.random() * 10000);
                                        out.write(String.valueOf(randomNum).getBytes());
                                        out.flush();

                                        len = in.read(rbuf);
                                        if (len != -1) {
                                            String response1 = new String(rbuf, 0, len);

                                            String workpass = String.valueOf(randomNum) + account.getAccountPassword();
                                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                                            byte[] cipher_byte;
                                            md.update(workpass.getBytes());
                                            cipher_byte = md.digest();

                                            StringBuilder sb = new StringBuilder();
                                            for (byte b : cipher_byte) {
                                                sb.append(String.format("%02x", b));
                                            }
                                            String response2 = sb.toString();

                                            if (response1.equals(response2)) {
                                                System.out.println("取引履歴照会システム: chap認証成功");
                                                out.write("OK".getBytes());
                                                out.flush();

                                                // 取引履歴を取得
                                                String history = account.getHistory(); // 取引履歴を取得
                                                System.out.println("取引履歴照会要求: " + history); // サーバー側で履歴を表示
                                                System.out.println("");
                                                out.write(history.getBytes());
                                                out.flush();

                                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                        .format(new Date());
                                                account.addTransaction(date, "取引履歴照会", 0, account.getBalance());
                                            } else {
                                                out.write("NG".getBytes());
                                                out.flush();
                                                System.out.println("レスポンス1と2が一致しません。");
                                                System.out.println("");
                                            }
                                        }
                                    }
                                }
                            } else if (service.equals("5")) {
                                len = in.read(rbuf);
                                if (len != -1) {
                                    String msg = new String(rbuf, 0, len);
                                    String[] parts = msg.split(":");
                                    String rquest = parts[0];
                                    String accName = parts[1];
                                    String accNum = parts[2];

                                    if (rquest.equals("REQUEST")) {
                                        int randomNum = (int) (Math.random() * 10000);
                                        out.write(String.valueOf(randomNum).getBytes());
                                        out.flush();

                                        len = in.read(rbuf);
                                        if (len != -1) {
                                            String response1 = new String(rbuf, 0, len);

                                            String workpass = String.valueOf(randomNum) + account.getAccountPassword();
                                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                                            byte[] cipher_byte;
                                            md.update(workpass.getBytes());
                                            cipher_byte = md.digest();

                                            StringBuilder sb = new StringBuilder();
                                            for (byte b : cipher_byte) {
                                                sb.append(String.format("%02x", b));
                                            }
                                            String response2 = sb.toString();

                                            if (response1.equals(response2)) {
                                                System.out.println("振込システム: chap認証成功");
                                                out.write("OK".getBytes());
                                                out.flush();

                                                len = in.read(rbuf);
                                                if (len != -1) {
                                                    String transferInfo = new String(rbuf, 0, len);
                                                    String[] transferParts = transferInfo.split(":");
                                                    String targetAccountName = transferParts[0];
                                                    String targetAccountNumber = transferParts[1];
                                                    int transferAmount = Integer.parseInt(transferParts[2]);
                                                    System.out.println("振込先口座: " + targetAccountName + " ("
                                                            + targetAccountNumber + ")");
                                                    System.out.println("振込額: " + transferAmount + "円");
                                                    // 振込先口座情報を読み込む
                                                    File targetFile = new File("./ATM_Service/server/accounts/"
                                                            + targetAccountName + targetAccountNumber + ".txt");
                                                    if (!targetFile.exists()) {
                                                        out.write("ERROR: 振込先口座が存在しません".getBytes());
                                                        System.out.println("");
                                                        out.flush();
                                                    } else {
                                                        ObjectInputStream targetObjectIn = new ObjectInputStream(
                                                                new FileInputStream(targetFile));
                                                        PassAccount targetAccount = (PassAccount) targetObjectIn
                                                                .readObject();
                                                        targetObjectIn.close();

                                                        // 振込処理
                                                        if (transferAmount > 0
                                                                && transferAmount <= account.getBalance()) {
                                                            account.draw(transferAmount);
                                                            targetAccount.deposit(transferAmount);

                                                            // 取引履歴を更新
                                                            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                                    .format(new Date());
                                                            account.addTransaction(date, "振込(送金)", transferAmount,
                                                                    account.getBalance());
                                                            targetAccount.addTransaction(date, "振込(受取)", transferAmount,
                                                                    targetAccount.getBalance());

                                                            // 振込先口座情報を保存
                                                            ObjectOutputStream targetObjectOut = new ObjectOutputStream(
                                                                    new FileOutputStream(targetFile));
                                                            targetObjectOut.writeObject(targetAccount);
                                                            targetObjectOut.close();

                                                            System.out.println("振込完了: " + transferAmount + "円を "
                                                                    + targetAccountName + " (" + targetAccountNumber
                                                                    + ") に送金しました。");
                                                            System.out.println("");
                                                            out.write("振込が完了しました".getBytes());
                                                            out.flush();
                                                        } else {
                                                            out.write("ERROR: 振込額が不正です".getBytes());
                                                            out.flush();
                                                        }
                                                    }

                                                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                            .format(new Date());
                                                    account.addTransaction(date, "振込", transferAmount,
                                                            account.getBalance());
                                                    out.write("OK".getBytes());
                                                    out.flush();
                                                }
                                            } else {
                                                out.write("NG".getBytes());
                                                out.flush();
                                                System.out.println("レスポンス1と2が一致しません。");
                                                System.out.println("");
                                            }
                                        }
                                    }
                                }
                            } else if (service.equals("6")) {
                                System.out.println("IPアドレス: " + socket.getInetAddress().getHostAddress() + "アカウント名:"
                                        + accountName + "からの接続を終了します。");
                                System.out.println("");
                                socket.close();
                                return;
                            }
                        }
                        // 口座情報を保存
                        ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(file));
                        objectOut.writeObject(account);
                        objectOut.close();
                    }
                }
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class ATM_Server {
    public static void main(String[] args) {
        try {
            int PORT = 49152;
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                Socket socket = server.accept();
                ATMServerThread thread = new ATMServerThread(socket);
                System.out.println("クライアントが接続しました。");
                System.out.println("接続受付IPアドレス: " + socket.getInetAddress().getHostAddress());
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}