package course.java.sdm.engine;

import course.java.sdm.classesForUI.WalletInfo;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Wallet {

    private final Set<Transaction> Transactions = new HashSet<>();
    private Double Balance = 0d;

    public Set<Transaction> getTransactions() {
        return Transactions;
    }

    public Double getBalance() {
        return Balance;
    }

    private void setBalance(Double balance) {
        Balance = balance;
    }

    public void Charge (Double Amount, Date date) {
        Long id = Transaction.GetSerial();
        Transactions.add(new Transaction(id,null, Transaction.TransactionMethod.CHARGE,
                date,Amount,Balance,Amount+Balance));
        Balance += Amount;
    }

    public void Receive(Order order,Double Amount) { //seller get this
        Long id = Transaction.GetSerial();
        Transactions.add(new Transaction(id,order, Transaction.TransactionMethod.RECEIVED,
                order.getDate(),Amount,Balance,Amount+Balance));
        Balance += Amount;
    }

    public void Give(Order order,Double Amount) { //buyer do this
        Long id = Transaction.GetSerial();
        Transactions.add(new Transaction(id,order, Transaction.TransactionMethod.TRANSFER,
                order.getDate(),Amount,Balance,Balance-Amount));
        Balance -= Amount;
    }

    public WalletInfo getWalletInfo () {
        return new WalletInfo(this.getBalance(),this.Transactions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(Transactions, wallet.Transactions) &&
                Objects.equals(Balance, wallet.Balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Transactions, Balance);
    }
}
