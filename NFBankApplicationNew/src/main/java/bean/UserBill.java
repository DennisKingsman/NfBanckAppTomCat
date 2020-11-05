package bean;

import utils.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserBill {

    private Long id;
    private Long responsible_user_id;
    private BigDecimal balance;
    private LocalDate openDate;
    private LocalDate closeDate;
    private Status status;

    public UserBill(){

    }

    public UserBill(Long responsible_user_id, BigDecimal balance, LocalDate openDate, Status status) {
        this.responsible_user_id = responsible_user_id;
        this.balance = balance;
        this.openDate = openDate;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bill num: "
                + this.id
                + " with user_id: "
                + this.responsible_user_id
                + ". Current balance: "
                + this.balance
                + ". Open date "
                + this.openDate.toString()
                + ". Status is "
                + status.name();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResponsible_user_id() {
        return responsible_user_id;
    }

    public void setResponsible_user_id(Long responsible_user_id) {
        this.responsible_user_id = responsible_user_id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
