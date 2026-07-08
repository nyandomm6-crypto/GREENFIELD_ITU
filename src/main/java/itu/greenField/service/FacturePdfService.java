package itu.greenField.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import itu.greenField.model.Commandes;
import itu.greenField.model.DetailsCommande;

/**
 * Génère une facture PDF (style ticket de grande surface, moderne)
 * avec OpenPDF. Les informations du client ne sont pas affichées.
 */
@Service
public class FacturePdfService {

    private static final Color VERT = new Color(11, 122, 67);
    private static final Color ENCRE = new Color(23, 32, 27);
    private static final Color GRIS = new Color(107, 117, 110);
    private static final Color VERT_FOND = new Color(233, 244, 238);
    private static final Color LIGNE = new Color(217, 221, 217);

    private final DecimalFormat montantFormat;

    public FacturePdfService() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        this.montantFormat = new DecimalFormat("#,##0.00", symbols);
    }

    public byte[] genererFacture(Commandes commande) throws Exception {
        // Page étroite façon ticket : 80mm ≈ 226pt de large
        Rectangle pageSize = new Rectangle(300, 720);
        Document document = new Document(pageSize, 26, 26, 28, 28);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // ===== EN-TÊTE =====
        Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, VERT);
        Paragraph marque = new Paragraph("greenField", titre);
        marque.setAlignment(Element.ALIGN_CENTER);
        document.add(marque);

        Font sousMarque = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, GRIS);
        Paragraph sous = new Paragraph("Marché des producteurs", sousMarque);
        sous.setAlignment(Element.ALIGN_CENTER);
        sous.setSpacingBefore(2);
        document.add(sous);

        Font coordsFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, GRIS);
        Paragraph coords = new Paragraph("Antananarivo, Madagascar\nNIF 0000000000 · STAT 00000", coordsFont);
        coords.setAlignment(Element.ALIGN_CENTER);
        coords.setSpacingBefore(3);
        coords.setSpacingAfter(8);
        document.add(coords);

        document.add(ligneSeparation());

        // ===== META (n° facture / date) =====
        Font metaLabel = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, GRIS);
        Font metaValue = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, ENCRE);

        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);
        meta.setSpacingBefore(8);
        meta.setSpacingAfter(6);

        meta.addCell(metaCell("FACTURE", metaLabel, Element.ALIGN_LEFT));
        meta.addCell(metaCell("DATE", metaLabel, Element.ALIGN_RIGHT));
        meta.addCell(metaCell("N° " + commande.getId(), metaValue, Element.ALIGN_LEFT));
        String dateStr = "";
        if (commande.getDatecommande() != null) {
            dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(commande.getDatecommande());
        }
        meta.addCell(metaCell(dateStr, metaValue, Element.ALIGN_RIGHT));
        document.add(meta);

        // ===== TABLEAU DES ARTICLES =====
        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, GRIS);
        Font desFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, ENCRE);
        Font qteFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, GRIS);
        Font montantFont = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, ENCRE);

        PdfPTable table = new PdfPTable(new float[] { 3.2f, 1.6f, 1.6f });
        table.setWidthPercentage(100);
        table.addCell(headerCell("DÉSIGNATION", thFont, Element.ALIGN_LEFT));
        table.addCell(headerCell("PU", thFont, Element.ALIGN_RIGHT));
        table.addCell(headerCell("MONTANT", thFont, Element.ALIGN_RIGHT));

        BigDecimal sousTotal = BigDecimal.ZERO;
        if (commande.getDetailsCommande() != null) {
            for (DetailsCommande d : commande.getDetailsCommande()) {
                BigDecimal pu = d.getPuAuMomentAchat() != null ? d.getPuAuMomentAchat() : BigDecimal.ZERO;
                int qte = d.getQuantite() != null ? d.getQuantite() : 0;
                BigDecimal montant = pu.multiply(BigDecimal.valueOf(qte));
                sousTotal = sousTotal.add(montant);

                String nom = d.getProduit() != null ? d.getProduit().getNom() : "Produit";
                Phrase des = new Phrase();
                des.add(new Phrase(nom + "\n", desFont));
                des.add(new Phrase(qte + " × " + montantFormat.format(pu) + " Ar", qteFont));
                PdfPCell cDes = new PdfPCell(des);
                styleBodyCell(cDes, Element.ALIGN_LEFT);
                table.addCell(cDes);

                table.addCell(bodyCell(montantFormat.format(pu), montantFont, Element.ALIGN_RIGHT));
                table.addCell(bodyCell(montantFormat.format(montant), montantFont, Element.ALIGN_RIGHT));
            }
        }
        document.add(table);

        // ===== TOTAUX =====
        BigDecimal frais = commande.getFraisLivraison() != null ? commande.getFraisLivraison() : BigDecimal.ZERO;
        BigDecimal total = sousTotal.add(frais);

        Font totLabel = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, GRIS);
        Font totValue = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, ENCRE);

        PdfPTable totaux = new PdfPTable(2);
        totaux.setWidthPercentage(100);
        totaux.setSpacingBefore(10);

        int nbArticles = commande.getTotalProduits() != null ? commande.getTotalProduits() : 0;
        totaux.addCell(totalCell("Sous-total (" + nbArticles + " article(s))", totLabel, Element.ALIGN_LEFT));
        totaux.addCell(totalCell(montantFormat.format(sousTotal) + " Ar", totValue, Element.ALIGN_RIGHT));

        if (frais.signum() > 0) {
            totaux.addCell(totalCell("Frais de livraison", totLabel, Element.ALIGN_LEFT));
            totaux.addCell(totalCell(montantFormat.format(frais) + " Ar", totValue, Element.ALIGN_RIGHT));
        }
        document.add(totaux);

        // Bloc TOTAL mis en avant
        Font grandLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, VERT);
        Font grandValue = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, VERT);
        PdfPTable grand = new PdfPTable(2);
        grand.setWidthPercentage(100);
        grand.setSpacingBefore(6);

        PdfPCell gl = new PdfPCell(new Phrase("TOTAL À PAYER", grandLabel));
        gl.setBackgroundColor(VERT_FOND);
        gl.setBorder(Rectangle.NO_BORDER);
        gl.setPadding(10);
        gl.setHorizontalAlignment(Element.ALIGN_LEFT);
        gl.setVerticalAlignment(Element.ALIGN_MIDDLE);
        grand.addCell(gl);

        PdfPCell gv = new PdfPCell(new Phrase(montantFormat.format(total) + " Ar", grandValue));
        gv.setBackgroundColor(VERT_FOND);
        gv.setBorder(Rectangle.NO_BORDER);
        gv.setPadding(10);
        gv.setHorizontalAlignment(Element.ALIGN_RIGHT);
        gv.setVerticalAlignment(Element.ALIGN_MIDDLE);
        grand.addCell(gv);
        document.add(grand);

        // ===== CODE-BARRES =====
        PdfContentByte cb = writer.getDirectContent();
        Barcode128 barcode = new Barcode128();
        barcode.setCode("GF" + String.format("%07d", commande.getId()));
        barcode.setBarHeight(34);
        barcode.setX(0.9f);
        Image barImg = barcode.createImageWithBarcode(cb, ENCRE, ENCRE);
        barImg.setAlignment(Element.ALIGN_CENTER);
        barImg.setSpacingBefore(18);
        document.add(barImg);

        // ===== REMERCIEMENT =====
        Font merciFont = FontFactory.getFont(FontFactory.HELVETICA, 9, GRIS);
        Paragraph merci = new Paragraph("Merci de votre visite & à bientôt !", merciFont);
        merci.setAlignment(Element.ALIGN_CENTER);
        merci.setSpacingBefore(12);
        document.add(merci);

        Font petitFont = FontFactory.getFont(FontFactory.HELVETICA, 7, GRIS);
        Paragraph petit = new Paragraph("Facture générée électroniquement — greenField", petitFont);
        petit.setAlignment(Element.ALIGN_CENTER);
        petit.setSpacingBefore(2);
        document.add(petit);

        document.close();
        return out.toByteArray();
    }

    private PdfPTable ligneSeparation() {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColorBottom(LIGNE);
        c.setBorderWidthBottom(1.2f);
        c.setFixedHeight(1);
        t.addCell(c);
        return t;
    }

    private PdfPCell metaCell(String texte, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, font));
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(align);
        c.setPaddingBottom(2);
        return c;
    }

    private PdfPCell headerCell(String texte, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, font));
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColorBottom(LIGNE);
        c.setHorizontalAlignment(align);
        c.setPadding(5);
        c.setPaddingLeft(0);
        c.setPaddingRight(0);
        return c;
    }

    private PdfPCell bodyCell(String texte, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, font));
        styleBodyCell(c, align);
        return c;
    }

    private void styleBodyCell(PdfPCell c, int align) {
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColorBottom(LIGNE);
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_TOP);
        c.setPadding(6);
        c.setPaddingLeft(0);
        c.setPaddingRight(0);
    }

    private PdfPCell totalCell(String texte, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texte, font));
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(align);
        c.setPaddingBottom(3);
        return c;
    }
}
