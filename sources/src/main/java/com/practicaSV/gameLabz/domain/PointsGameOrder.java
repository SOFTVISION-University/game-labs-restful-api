package com.practicaSV.gameLabz.domain;

import com.practicaSV.gameLabz.utils.visitors.GameOrderVisitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = GameOrder.POINT_TYPE)
public class PointsGameOrder extends GameOrder {

    private Long pointsValue;

    public Long getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(Long pointsValue) {
        this.pointsValue = pointsValue;
    }

    @Override
    public void accept(GameOrderVisitor gameOrderVisitor) {
        gameOrderVisitor.visit(this);
    }
}
