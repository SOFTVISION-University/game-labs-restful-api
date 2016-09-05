package com.practicaSV.gameLabz.domain;

import java.math.BigDecimal;

public class TransactionInformation {

    private String clientName;

    private Long clientCardNumber;

    private Long clientCardExpDate;

    private Integer cvv;

    private Long gameLabzCardNumber;

    private BigDecimal orderPrice;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public Long getGameLabzCardNumber() {
        return gameLabzCardNumber;
    }

    public void setGameLabzCardNumber(Long gameLabzCardNumber) {
        this.gameLabzCardNumber = gameLabzCardNumber;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public static class Builder {

        private String clientName;

        private Long clientCardNumber;

        private Long clientCardExpDate;

        private Integer cvv;

        private Long gameLabzCardNumber;

        private BigDecimal orderPrice;

        public Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public Builder clientCardNumber(Long clientCardNumber) {
            this.clientCardNumber = clientCardNumber;
            return this;
        }

        public Builder clientCardExpDate(Long clientCardExpDate) {
            this.clientCardExpDate = clientCardExpDate;
            return this;
        }

        public Builder cvv(Integer cvv) {
            this.cvv = cvv;
            return this;
        }

        public Builder gameLabzCardNumber(Long gameLabzCardNumber) {
            this.gameLabzCardNumber = gameLabzCardNumber;
            return this;
        }

        public Builder orderPrice(BigDecimal orderPrice) {
            this.orderPrice = orderPrice;
            return this;
        }

        public TransactionInformation build() {
            return new TransactionInformation(this);
        }
    }

    private TransactionInformation(Builder b) {
        this.clientName = b.clientName;
        this.clientCardNumber = b.clientCardNumber;
        this.clientCardExpDate = b.clientCardExpDate;
        this.cvv = b.cvv;
        this.gameLabzCardNumber = b.gameLabzCardNumber;
        this.orderPrice = b.orderPrice;
    }

    public TransactionInformation() {}
}
