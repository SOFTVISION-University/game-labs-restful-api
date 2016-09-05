package com.practicaSV.gameLabz.services;

import com.itextpdf.text.Document;
import com.practicaSV.gameLabz.domain.GameOrder;

public interface GameOrderPDFService {

    Document execute(GameOrder gameOrder);
}
