package com.ticket.ticketservice.notify;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Date;

@Service
public class PdfService {
    public byte[] ticketSummary(Long id, String title, String status,
                                String priority, String category,
                                String actorEmail, String comments,
                                Date createdAt, Date updatedAt) {
        try (var baos = new ByteArrayOutputStream()) {
            var doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("Ticket Summary"));
            doc.add(new Paragraph("ID: " + id));
            doc.add(new Paragraph("Title: " + nz(title)));
            doc.add(new Paragraph("Status: " + status));
            doc.add(new Paragraph("Priority: " + priority));
            doc.add(new Paragraph("Category: " + nz(category)));
            doc.add(new Paragraph("Actor: " + nz(actorEmail)));
            doc.add(new Paragraph("Comments: " + nz(comments)));
            doc.add(new Paragraph("Created: " + createdAt));
            doc.add(new Paragraph("Updated: " + updatedAt));
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String nz(String s){ return s==null? "" : s; }
}
