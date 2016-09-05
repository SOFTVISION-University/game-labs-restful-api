package com.practicaSV.gameLabz.domain;

import com.practicaSV.gameLabz.utils.visitors.GameOrderVisitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = GameOrder.KEY_TYPE)
public class KeyGameOrder extends GameOrder {

    private String keyValue;

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    @Override
    public void accept(GameOrderVisitor gameOrderVisitor) {
        gameOrderVisitor.visit(this);
    }
}
