package com.practicaSV.gameLabz.domain;

import com.practicaSV.gameLabz.utils.visitors.GameOrderVisitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue(value = GameOrder.GIFT_TYPE)
public class GiftGameOrder extends GameOrder {

    @OneToOne
    private User recieverUser;

    private BigDecimal giftValue;

    public User getRecieverUser() {
        return recieverUser;
    }

    public void setRecieverUser(User recieverUser) {
        this.recieverUser = recieverUser;
    }

    public BigDecimal getGiftValue() {
        return giftValue;
    }

    public void setGiftValue(BigDecimal giftValue) {
        this.giftValue = giftValue;
    }

    @Override
    public void accept(GameOrderVisitor gameOrderVisitor) {
        gameOrderVisitor.visit(this);
    }
}
