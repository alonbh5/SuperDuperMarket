package course.java.sdm.classesForUI;

import course.java.sdm.engine.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WalletInfo {

    public final Double Balance;
    public final List<TransactionInfo> AllTransactions = new ArrayList<>();

    public WalletInfo(Double balance, Collection<Transaction> transactions) {
        Balance = balance;
        for (Transaction cur : transactions) {
            AllTransactions.add(new TransactionInfo(
                    cur.getSerialNumber(),cur.getFromOrderID(),cur.getTransactionMethodString(),
                    cur.getDate(),cur.getAmountOfTransaction(),cur.getBalanceBefore(),cur.getBalanceAfter()));
        }
    }


}
