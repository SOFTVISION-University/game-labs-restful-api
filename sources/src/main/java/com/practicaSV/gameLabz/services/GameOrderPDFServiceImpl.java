package com.practicaSV.gameLabz.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.practicaSV.gameLabz.domain.*;
import com.practicaSV.gameLabz.exceptions.HttpStatusException;
import com.practicaSV.gameLabz.utils.MailData;
import com.practicaSV.gameLabz.utils.visitors.GameOrderVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameOrderPDFServiceImpl implements GameOrderPDFService, GameOrderVisitor {

    private Document result;

    private static Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);

    private static Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 15);

    private static final String SIGNATURE = "© 2016 GameLabz. All rights reserved.";

    private static final String MAIL_SUBJECT = "Game transaction";

    private static final String SERVICE_MAIL = "gamelabzservice@gmail.com";

    private MailSenderService mailSenderService;

    @Autowired
    public GameOrderPDFServiceImpl(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @Override
    public Document execute(GameOrder gameOrder) {
        GameOrderPDFServiceImpl visitor = new GameOrderPDFServiceImpl(mailSenderService);
        gameOrder.accept(visitor);
        return visitor.getResult();
    }

    @Override
    public void visit(CashGameOrder cashGameOrder) {

        result = new Document(PageSize.A4);
        ByteArrayOutputStream attachment = createAttachment(result);
        result.open();
        addMetaData(result);

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Purchase by cash confirmation.", titleFont));
        addEmptyLine(paragraph, 1);

        List<Game> gameList = cashGameOrder.getGameOfferList().stream().map(GameOffer::getGames).flatMap(games -> games.stream()).collect(Collectors.toList());

        paragraph.add(new Paragraph("You have successfully bought " + getGameNames(gameList) + ". The transaction was" +
                " completed on " + convertDate(cashGameOrder.getDateOfOrder()) + " with a value of " + cashGameOrder.getCashValue() + "$.", textFont));

        if (!cashGameOrder.getKeys().isEmpty()) {
            paragraph.add(new Paragraph("You have acquired the following key(s):\n", textFont));
        }
        addEmptyLine(paragraph, 1);
        addParagraph(result, paragraph);
        addKeyGameNameTable(cashGameOrder.getKeys());
        addSignature(result);
        result.close();

        MailData data = createMailData(cashGameOrder, attachment);

        mailSenderService.sendMail(Arrays.asList(data));
    }

    @Override
    public void visit(PointsGameOrder pointsGameOrder) {

        result = new Document(PageSize.A4);
        ByteArrayOutputStream attachment = createAttachment(result);
        result.open();
        addMetaData(result);

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Purchase by points confirmation.", titleFont));
        addEmptyLine(paragraph, 1);

        List<Game> gameList = pointsGameOrder.getGameOfferList().stream().map(GameOffer::getGames).flatMap(games -> games.stream()).collect(Collectors.toList());

        paragraph.add(new Paragraph("You have successfully bought " + getGameNames(gameList) + ". The transaction was" +
                " completed on " + convertDate(pointsGameOrder.getDateOfOrder()) + " with a value of " + pointsGameOrder.getPointsValue() + " points.", textFont));

        if (!pointsGameOrder.getKeys().isEmpty()) {
            paragraph.add(new Paragraph("You have acquired the following key(s):\n", textFont));
        }

        addEmptyLine(paragraph, 1);
        addParagraph(result, paragraph);
        addKeyGameNameTable(pointsGameOrder.getKeys());
        addSignature(result);
        result.close();

        MailData data = createMailData(pointsGameOrder, attachment);

        mailSenderService.sendMail(Arrays.asList(data));
    }

    @Override
    public void visit(KeyGameOrder keyGameOrder) {

        result = new Document(PageSize.A4);
        ByteArrayOutputStream attachment = createAttachment(result);
        result.open();
        addMetaData(result);

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Purchase with key confirmation.", titleFont));
        addEmptyLine(paragraph, 1);

        paragraph.add(new Paragraph("You have successfully bought " + getGameNames(keyGameOrder.getOwnedGames()) + ". The transaction was" +
                " completed on " + convertDate(keyGameOrder.getDateOfOrder()) + " with key: " + keyGameOrder.getKeyValue() + ".", textFont));

        addEmptyLine(paragraph, 1);
        addParagraph(result, paragraph);
        addSignature(result);
        result.close();

        MailData data = createMailData(keyGameOrder, attachment);

        mailSenderService.sendMail(Arrays.asList(data));
    }

    @Override
    public void visit(GiftGameOrder giftGameOrder) {

        result = new Document(PageSize.A4);
        ByteArrayOutputStream attachmentToBuyer = createAttachment(result);
        result.open();
        addMetaData(result);

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Gift purchase confirmation.", titleFont));
        addEmptyLine(paragraph, 1);

        List<Game> gameList = giftGameOrder.getGameOfferList().stream().map(GameOffer::getGames).flatMap(games -> games.stream()).collect(Collectors.toList());

        paragraph.add(new Paragraph("You have successfully sent " + getGameNames(gameList) + " to " + giftGameOrder.getRecieverUser().getUserName() + ". The transaction was" +
                " completed on " + convertDate(giftGameOrder.getDateOfOrder()) + " with a value of " + giftGameOrder.getGiftValue() + "$.", textFont));

        addEmptyLine(paragraph, 1);
        addParagraph(result, paragraph);
        addSignature(result);
        result.close();

        MailData dataToBuyer = new MailData();
        dataToBuyer.setUserFrom(SERVICE_MAIL);
        dataToBuyer.setUserTo(giftGameOrder.getUser().getEmail());
        dataToBuyer.setSubject(MAIL_SUBJECT);
        dataToBuyer.setAttachment(attachmentToBuyer);

        result = new Document(PageSize.A4);
        ByteArrayOutputStream attachmentToReceiver = createAttachment(result);
        result.open();
        addMetaData(result);

        paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Gift receive confirmation.", titleFont));
        addEmptyLine(paragraph, 1);

        paragraph.add(new Paragraph("Congratulations! You have received a gift from " + giftGameOrder.getUser().getUserName() + " containing the following game(s): " + getGameNames(gameList), textFont));

        if (!giftGameOrder.getKeys().isEmpty()) {
            paragraph.add(new Paragraph("You have acquired the following key(s):\n", textFont));
        }

        addEmptyLine(paragraph, 1);
        addParagraph(result, paragraph);
        addKeyGameNameTable(giftGameOrder.getKeys());
        addSignature(result);
        result.close();

        MailData dataToReceiver = new MailData();
        dataToReceiver.setUserFrom(SERVICE_MAIL);
        dataToReceiver.setUserTo(giftGameOrder.getRecieverUser().getEmail());
        dataToReceiver.setSubject(MAIL_SUBJECT);
        dataToReceiver.setAttachment(attachmentToReceiver);

        mailSenderService.sendMail(Arrays.asList(dataToBuyer, dataToReceiver));
    }

    public Document getResult() {
        return result;
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void addMetaData(Document document) {

        document.addTitle("GameLabz purchase");
        document.addSubject("Game transaction");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("GameLabz");
        document.addCreator("GameLabz");
    }

    private String getGameNames(List<Game> gameList) {

        return gameList.stream()
                .distinct()
                .map(game -> game.getName())
                .collect(Collectors.joining(", "));
    }

    private void addParagraph(Document document, Paragraph paragraph) {

        try {
            document.add(paragraph);
        } catch (DocumentException e) {
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Pdf creation error!");
        }
    }

    private MailData createMailData(GameOrder gameOrder, ByteArrayOutputStream attachment) {

        MailData data = new MailData();
        data.setUserFrom(SERVICE_MAIL);
        data.setUserTo(gameOrder.getUser().getEmail());
        data.setSubject(MAIL_SUBJECT);
        data.setAttachment(attachment);

        return data;
    }

    private void addKeyGameNameTable(List<GeneratedKey> keys) {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);

        PdfPCell cell = new PdfPCell(new Paragraph("Game name"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Key"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        keys.stream()
                .forEach(key -> {
                    table.addCell(key.getGame().getName());
                    table.addCell(key.getGeneratedKey());
                });

        try {
            result.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addSignature(Document document) {

        Paragraph signature = new Paragraph(SIGNATURE, textFont);
        signature.setAlignment(Element.ALIGN_CENTER);
        addParagraph(document, signature);
    }

    public ByteArrayOutputStream createAttachment(Document document) {

        ByteArrayOutputStream attachment = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, attachment);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return attachment;
    }

    public String convertDate(Long time) {

        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
