package user.model;

import java.math.BigDecimal;

public class TransferRequest {
    long transferId;
     long toAccountId;
     long fromAccount;
    BigDecimal amount;


    public TransferRequest(long transferId, long toAccountId, long fromAccount, BigDecimal amount) {
        this.transferId = transferId;
        this.toAccountId = toAccountId;
        this.fromAccount = fromAccount;
        this.amount = amount;
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransferRequest{" +
                "transferId=" + transferId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                '}';
    }
}
