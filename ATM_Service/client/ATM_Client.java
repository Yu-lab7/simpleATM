package ATM_Service.client;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;

public class ATM_Client {
    public static void main(String[] args) {
        int PORT = 49152; // サーバ側のポート番号
        String IP = "127.0.0.1"; // サーバ側のIPアドレス
        byte[] rbuf = new byte[1024]; // 受信データを入れるバッファ
        int len = 0; // 受信データの長さを格納する変数

        try {
            Scanner sc = new Scanner(System.in);
            InetAddress IP_addr = InetAddress.getByName(IP);
            Socket socket = new Socket(IP_addr, PORT);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // 口座名と口座番号を送信
            System.out.println("あなたの口座名を入力してください:");
            String accountName = sc.nextLine();
            System.out.println("あなたの口座番号を入力してください:");
            String accountNumber = sc.nextLine();
            String accountInfo = accountName + ":" + accountNumber;
            out.write(accountInfo.getBytes());
            out.flush();

            // サーバからの応答を確認
            len = in.read(rbuf);
            if (len != -1) {
                String msg = new String(rbuf, 0, len);
                if (msg.equals("NEW")) {
                    System.out.println("口座名が存在しません。新規口座を作成しますか？(y/n)");
                    String createAccount = sc.nextLine();
                    if (createAccount.equals("y")) {
                        System.out.println("口座人名とパスワードを設定してください");
                        System.out.println("口座人名:");
                        String accountName2 = sc.nextLine();
                        System.out.println("パスワード:");
                        String accountPassword = sc.nextLine();
                        String newAccountInfo = accountName2 + ":" + accountPassword;
                        out.write(newAccountInfo.getBytes());
                        out.flush();

                        len = in.read(rbuf);
                        if (len != -1) {
                            String msg2 = new String(rbuf, 0, len);
                            if (msg2.equals("OK")) {
                                System.out.println("新規口座作成が完了しました。");

                                len = in.read(rbuf);
                                if (len != -1) {
                                    String msg3 = new String(rbuf, 0, len);
                                    System.out.println("口座番号: " + msg3 + "が発行されました。");
                                    System.out.println("あなたが設定した口座人名とパスワードで再度ログインしてください。");
                                }
                                System.out.println("");
                                socket.close();
                                sc.close();
                                out.close();
                                in.close();
                                return;
                            } else {
                                System.out.println("新規口座作成に失敗しました。");
                                socket.close();
                                return;
                            }
                        }
                    } else {
                        System.out.println("新規口座作成をキャンセルしました。");
                        socket.close();
                        return;
                    }
                } else if (msg.equals("OK")) {
                    System.out.println("口座名が存在します。");
                } else {
                    System.out.println("不明なエラーが発生しました。");
                    socket.close();
                    return;
                }
            }
            System.out.println("接続先サーバー: " + IP + ":" + PORT);
            System.out.println("***********************************************************");
            System.out.println("ATMサービスにようこそ!");
            System.out.println("***********************************************************");
            System.out.println("");
            System.out.println("以下のサービスから選択してください:");

            while (true) {
                // ATMサービスメニュー
                System.out.println("***********************************************************");
                System.out.println("1.預金");
                System.out.println("2.引き出し");
                System.out.println("3.残高照会");
                System.out.println("4.取引履歴照会");
                System.out.println("5.他口座への振込");
                System.out.println("6.終了");
                System.out.println("***********************************************************");

                System.out.println("");

                String service = sc.nextLine();
                System.out.println("");
                out.write(service.getBytes());
                out.flush();

                // サービスごとの処理
                if (service.equals("1")) {
                    System.out.println("預金額を入力してください:");
                    int depositAmount = Integer.parseInt(sc.nextLine());
                    out.write(String.valueOf(depositAmount).getBytes());
                    out.flush();
                    System.out.println("預金処理完了: " + depositAmount + "円を預金しました。");
                    System.out.println("");
                } else if (service.equals("2")) {
                    String requestData = "REQUEST:" + accountName + ":" + accountNumber;
                    out.write(requestData.getBytes("UTF-8"));

                    len = in.read(rbuf);
                    if (len != -1) {
                        String challenge = new String(rbuf, 0, len);

                        System.out.print("パスワードを入力してください:");
                        String checkPass = sc.nextLine();
                        String workPass = challenge + checkPass;

                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] cipher_byte;
                        md.update(workPass.getBytes());
                        cipher_byte = md.digest();

                        // ハッシュ値を16進数文字列に変換
                        StringBuilder sb = new StringBuilder();
                        for (byte b : cipher_byte) {
                            sb.append(String.format("%02x", b));
                        }
                        String hashString = sb.toString();

                        // ハッシュ値を送信
                        out.write(hashString.getBytes());
                        out.flush();

                        len = in.read(rbuf);
                        if (len != -1) {
                            String msg2 = new String(rbuf, 0, len);
                            if (msg2.equals("OK")) {
                                System.out.println("");
                                System.out.println("引き出し額を入力してください:");
                                int withdrawAmount = Integer.parseInt(sc.nextLine());
                                out.write(String.valueOf(withdrawAmount).getBytes());
                                out.flush();
                                System.out.println("引き出し処理完了: " + withdrawAmount + "円を引き出しました。");
                                System.out.println("");
                            } else {
                                System.out.println("パスワードが間違っています。もう一度やり直してください。");
                                System.out.println("");
                            }
                        }
                    }

                } else if (service.equals("3")) {
                    String requestData = "REQUEST:" + accountName + ":" + accountNumber;
                    out.write(requestData.getBytes("UTF-8"));

                    len = in.read(rbuf);
                    if (len != -1) {
                        String challenge = new String(rbuf, 0, len);

                        System.out.print("パスワードを入力してください:");
                        String checkPass = sc.nextLine();
                        String workPass = challenge + checkPass;

                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] cipher_byte;
                        md.update(workPass.getBytes());
                        cipher_byte = md.digest();
                        // ハッシュ値を16進数文字列に変換
                        StringBuilder sb = new StringBuilder();
                        for (byte b : cipher_byte) {
                            sb.append(String.format("%02x", b));
                        }
                        String hashString = sb.toString();
                        // ハッシュ値を送信
                        out.write(hashString.getBytes());
                        out.flush();

                        len = in.read(rbuf);
                        if (len != -1) {
                            String msg2 = new String(rbuf, 0, len);
                            if (msg2.equals("OK")) {
                                System.out.println("");

                                len = in.read(rbuf);
                                if (len != -1) {
                                    String balance = new String(rbuf, 0, len);
                                    System.out.println("残高: " + balance + "円です。");
                                    System.out.println("");
                                }
                            } else {
                                System.out.println("パスワードが間違っています。もう一度やり直してください。");
                                System.out.println("");
                            }
                        }
                    }
                } else if (service.equals("4")) {
                    String requestData = "REQUEST:" + accountName + ":" + accountNumber;
                    out.write(requestData.getBytes("UTF-8"));

                    len = in.read(rbuf);
                    if (len != -1) {
                        String challenge = new String(rbuf, 0, len);

                        System.out.print("パスワードを入力してください:");
                        String checkPass = sc.nextLine();
                        String workPass = challenge + checkPass;
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] cipher_byte;
                        md.update(workPass.getBytes());
                        cipher_byte = md.digest();
                        // ハッシュ値を16進数文字列に変換
                        StringBuilder sb = new StringBuilder();
                        for (byte b : cipher_byte) {
                            sb.append(String.format("%02x", b));
                        }
                        String hashString = sb.toString();
                        // ハッシュ値を送信
                        out.write(hashString.getBytes());
                        out.flush();
                        len = in.read(rbuf);
                        if (len != -1) {
                            String msg2 = new String(rbuf, 0, len);
                            if (msg2.equals("OK")) {
                                System.out.println("");

                                len = in.read(rbuf);
                                if (len != -1) {
                                    String transactionHistory = new String(rbuf, 0, len);
                                    System.out.println("取引履歴: " + transactionHistory);
                                    System.out.println("");
                                }
                            } else {
                                System.out.println("パスワードが間違っています。もう一度やり直してください。");
                                System.out.println("");
                            }
                        }
                    }
                } else if (service.equals("5")) {
                    String requestData = "REQUEST:" + accountName + ":" + accountNumber;
                    out.write(requestData.getBytes("UTF-8"));
                    len = in.read(rbuf);
                    if (len != -1) {
                        String challenge = new String(rbuf, 0, len);

                        System.out.print("パスワードを入力してください:");
                        String checkPass = sc.nextLine();
                        String workPass = challenge + checkPass;
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] cipher_byte;
                        md.update(workPass.getBytes());
                        cipher_byte = md.digest();
                        // ハッシュ値を16進数文字列に変換
                        StringBuilder sb = new StringBuilder();
                        for (byte b : cipher_byte) {
                            sb.append(String.format("%02x", b));
                        }
                        String hashString = sb.toString();
                        // ハッシュ値を送信
                        out.write(hashString.getBytes());
                        out.flush();
                        len = in.read(rbuf);
                        if (len != -1) {
                            String msg2 = new String(rbuf, 0, len);
                            if (msg2.equals("OK")) {
                                System.out.println("");

                                System.out.println("振込先口座名を入力してください:");
                                String transferAccountName = sc.nextLine();
                                System.out.println("振込先口座番号を入力してください:");
                                String transferAccountNumber = sc.nextLine();
                                System.out.println("振込額を入力してください:");
                                int transferAmount = Integer.parseInt(sc.nextLine());

                                String transferInfo = transferAccountName + ":" + transferAccountNumber + ":"
                                        + transferAmount;
                                out.write(transferInfo.getBytes());
                                out.flush();
                                System.out.println("振込処理完了: " + transferAmount + "円を振り込みました。");
                                System.out.println("");
                            } else {
                                System.out.println("パスワードが間違っています。もう一度やり直してください。");
                                System.out.println("");
                            }
                        }
                    }
                } else if (service.equals("6")) {
                    System.out.println("ATMサービスを終了します。");
                    System.out.println("ご利用ありがとうございました。");
                    System.out.println("");
                    break;
                }
            }
            socket.close();
            sc.close();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}