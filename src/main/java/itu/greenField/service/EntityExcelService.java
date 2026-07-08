package itu.greenField.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Service Excel générique réutilisable pour les entités simples
 * (produit, vehicule, employe, categorie).
 *
 * - export()   : génère un .xlsx contenant les données existantes
 * - template() : génère un .xlsx modèle (en-têtes + listes déroulantes)
 * - read()     : lit les lignes de données d'un .xlsx uploadé (ignore l'en-tête)
 *
 * S'inspire du modèle déjà présent dans CommandesService.
 */
@Service
public class EntityExcelService {

    /** Décrit une liste déroulante à appliquer sur une colonne d'un modèle. */
    public static class Dropdown {
        public final int colIndex;
        public final String[] values;

        public Dropdown(int colIndex, String[] values) {
            this.colIndex = colIndex;
            this.values = values;
        }
    }

    public byte[] export(String sheetName, String[] headers, List<Object[]> rows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            writeHeader(workbook, sheet, headers);

            int rowIndex = 1;
            for (Object[] row : rows) {
                Row line = sheet.createRow(rowIndex++);
                for (int c = 0; c < row.length; c++) {
                    setCell(line.createCell(c), row[c]);
                }
            }

            autoSize(sheet, headers.length);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] template(String sheetName, String[] headers, List<Dropdown> dropdowns) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            writeHeader(workbook, sheet, headers);

            if (dropdowns != null) {
                XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
                for (Dropdown d : dropdowns) {
                    if (d.values == null || d.values.length == 0) {
                        continue;
                    }
                    CellRangeAddressList addressList = new CellRangeAddressList(1, 5000, d.colIndex, d.colIndex);
                    DataValidationConstraint constraint = helper.createExplicitListConstraint(d.values);
                    DataValidation validation = helper.createValidation(constraint, addressList);
                    validation.setShowErrorBox(true);
                    validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                    validation.createErrorBox("Valeur invalide",
                            "Veuillez sélectionner une valeur dans la liste déroulante.");
                    sheet.addValidationData(validation);
                }
            }

            autoSize(sheet, headers.length);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    /** Lit les lignes de données (hors en-tête) d'un fichier .xlsx. */
    public List<String[]> read(InputStream inputStream, int nbCols) throws Exception {
        List<String[]> result = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // en-tête
                }
                String[] cells = new String[nbCols];
                boolean empty = true;
                for (int c = 0; c < nbCols; c++) {
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = cellToString(cell);
                    cells[c] = value;
                    if (!value.isEmpty()) {
                        empty = false;
                    }
                }
                if (!empty) {
                    result.add(cells);
                }
            }
        }
        return result;
    }

    private void writeHeader(XSSFWorkbook workbook, XSSFSheet sheet, String[] headers) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void autoSize(XSSFSheet sheet, int nbCols) {
        for (int i = 0; i < nbCols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void setCell(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private String cellToString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d) && !Double.isInfinite(d)) {
                    return String.valueOf((long) d);
                }
                return String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    try {
                        double dd = cell.getNumericCellValue();
                        if (dd == Math.floor(dd)) {
                            return String.valueOf((long) dd);
                        }
                        return String.valueOf(dd);
                    } catch (Exception e2) {
                        return "";
                    }
                }
            default:
                return "";
        }
    }
}
