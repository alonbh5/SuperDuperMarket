package course.java.sdm.engine;

import java.util.Date;
import java.util.Objects;

public class Transaction {

    private static Long SerialGenerator = 10000l;

    enum TransactionMethod {
        CHARGE, TRANSFER, RECEIVED
    }

    private final Long SerialNumber;
    private final Order FromOrder;
    private final TransactionMethod transactionMethod;
    private final Date date;
    private final Double AmountOfTransaction;
    private final Double BalanceBefore;
    private final Double BalanceAfter;

    public Transaction(Long serialNumber, Order fromOrder, TransactionMethod transactionMethod, Date date, Double amountOfTransaction, Double balanceBefore, Double balanceAfter) {
        SerialNumber = serialNumber;
        FromOrder = fromOrder;
        this.transactionMethod = transactionMethod;
        this.date = date;
        AmountOfTransaction = amountOfTransaction;
        BalanceBefore = balanceBefore;
        BalanceAfter = balanceAfter;
    }

    public Order getFromOrder() {
        return FromOrder;
    }

    public Long getFromOrderID() {
        if (FromOrder!=null)
            return FromOrder.getOrderSerialNumber();
        else
            return 0l;
    }

    public TransactionMethod getTransactionMethod() {
        return transactionMethod;
    }

    public String getTransactionMethodString() {
        return transactionMethod.toString();
    }

    public Date getDate() {
        return date;
    }

    public Double getAmountOfTransaction() {
        return AmountOfTransaction;
    }

    public Double getBalanceBefore() {
        return BalanceBefore;
    }

    public Double getBalanceAfter() {
        return BalanceAfter;
    }

    public Long getSerialNumber() {
        return SerialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(SerialNumber, that.SerialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SerialNumber);
    }

    public synchronized static Long GetSerial () {
        return SerialGenerator++;
    }
}
