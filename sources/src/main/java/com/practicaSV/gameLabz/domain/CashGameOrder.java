package com.practicaSV.gameLabz.domain;

import com.practicaSV.gameLabz.utils.visitors.GameOrderVisitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue(value = GameOrder.CASH_TYPE)
public class CashGameOrder extends GameOrder {

    private BigDecimal cashValue;

    private Long clientCardNumber;

    private Long clientCardExpDate;

    private Integer cvv;

    public BigDecimal getCashValue() {
        return cashValue;
    }

    public void setCashValue(BigDecimal cashValue) {
        this.cashValue = cashValue;
    }

    public Long getClientCardNumber() {
        return clientCardNumber;
    }

    public void setClientCardNumber(Long clientCardNumber) {
        this.clientCardNumber = clientCardNumber;
    }

    public Long getClientCardExpDate() {
        return clientCardExpDate;
    }

    public void setClientCardExpDate(Long clientCardExpDate) {
        this.clientCardExpDate = clientCardExpDate;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }

    @Override
    public void accept(GameOrderVisitor gameOrderVisitor) {
        gameOrderVisitor.visit(this);
    }
}
