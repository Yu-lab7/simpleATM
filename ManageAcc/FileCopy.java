package ManageAcc;
import java.io.*;

public class FileCopy {
    public static void main(String[] args) {
        // 入力バッファを定義
        byte[] buf = new byte[1024];
        int length = 0;
        int totalSize = 0;

        try (
             FileInputStream streamRd = new FileInputStream(args[0]);
             FileOutputStream streamWr = new FileOutputStream(args[1])) {

            // ファイルからデータを読み取り、書き込む
            while ((length = streamRd.read(buf)) != -1) {
                streamWr.write(buf, 0, length);
                totalSize += length; // 読み取ったバイト数を累積
            }

            // 入力ファイル名、出力ファイル名、ファイルサイズを画面に出力
            System.out.println("コピーファイル名: " + args[0]);
            System.out.println("コピー先本ファイル名: " + args[1]);
            System.out.println("コピーファイルサイズ: " + totalSize + " bytes");

            streamRd.close(); // ストリームを閉じる
            streamWr.close(); // ストリームを閉じる
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}