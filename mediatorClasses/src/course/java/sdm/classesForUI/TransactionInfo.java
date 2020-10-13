package course.java.sdm.classesForUI;

import java.util.Date;

public class TransactionInfo {

    public final Long SerialNumber;
    public final Long FromOrderID;
    public final String transactionMethod;
    public final Date date;
    public final Double AmountOfTransaction;
    public final Double BalanceBefore;
    public final Double BalanceAfter;

    public TransactionInfo(Long serialNumber, Long fromOrder, String transactionMethod, Date date, Double amountOfTransaction, Double balanceBefore, Double balanceAfter) {
        SerialNumber = serialNumber;
        FromOrderID = fromOrder;
        this.transactionMethod = transactionMethod;
        this.date = date;
        AmountOfTransaction = Double.parseDouble(String.format("%.2f", amountOfTransaction));
        BalanceBefore = Double.parseDouble(String.format("%.2f", balanceBefore));
        BalanceAfter = Double.parseDouble(String.format("%.2f", balanceAfter));
    }
}
