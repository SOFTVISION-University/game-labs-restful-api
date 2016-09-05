package com.practicaSV.gameLabz.utils.visitors;

import com.practicaSV.gameLabz.domain.CashGameOrder;
import com.practicaSV.gameLabz.domain.GiftGameOrder;
import com.practicaSV.gameLabz.domain.KeyGameOrder;
import com.practicaSV.gameLabz.domain.PointsGameOrder;

public interface GameOrderVisitor {

    void visit(CashGameOrder cashGameOrder);

    void visit(PointsGameOrder pointsGameOrder);

    void visit(KeyGameOrder keyGameOrder);

    void visit(GiftGameOrder giftGameOrder);
}
