package ManageAcc;
import java.io.Serializable;

public class Account implements Serializable {
    // プロパティの定義(フィールド)
    public final String bankName; // 銀行名
    public final String branchName; // 支店名
    public final String accountHolder; // 口座所有者
    private int amount; // 預金残高
    // メソッドの定義private int amount; // 預金残高
    public Account(String bankName, String branchName, String accountHolder, int
    initialValue){
    
    // コンストラクタ
    // String bankName :銀行名
    
    // String branchName :支店名
    // String accountHolder :口座所有者
    // int initialValue :口座開設時の預け金
    
        this.bankName = bankName;
        this.branchName = branchName;
        this.accountHolder = accountHolder;
        amount = initialValue;
    }

    public Account(String Holder, int initialValue){
        // コンストラクタ
        // String bankName :銀行名
        // String branchName :支店名
        // String accountHolder :口座所有者
        // int initialValue :口座開設時の預け金
        
        this.bankName = "大工大銀行";
        this.branchName = "本店";
        this.accountHolder = Holder;
        this.amount = initialValue;
    }

    public void deposit(int depositValue){ // 預金のメソッド
        amount = amount + depositValue;
    }

    public int draw(int value){
        int withdrawnAmount = 0;
        if (amount >= value) {
            amount -= value;
            withdrawnAmount = amount;
            return withdrawnAmount;
        } else {
            withdrawnAmount = amount;
            amount = 0;
            return withdrawnAmount;
        }
    }

    public int getBalance(){ // 残高照会メソッド
        return amount;
    }
}
