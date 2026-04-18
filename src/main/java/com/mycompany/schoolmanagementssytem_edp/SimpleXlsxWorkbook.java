package com.mycompany.schoolmanagementssytem_edp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class SimpleXlsxWorkbook {

    private SimpleXlsxWorkbook() {
    }

    public static void writeWorkbook(Path outputPath, String sheetName, List<String> headers, List<List<String>> rows) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(outputPath))) {
            writeEntry(zipOutputStream, "[Content_Types].xml", contentTypes());
            writeEntry(zipOutputStream, "_rels/.rels", rootRelationships());
            writeEntry(zipOutputStream, "docProps/app.xml", appProperties());
            writeEntry(zipOutputStream, "docProps/core.xml", coreProperties());
            writeEntry(zipOutputStream, "xl/workbook.xml", workbook(sheetName));
            writeEntry(zipOutputStream, "xl/_rels/workbook.xml.rels", workbookRelationships());
            writeEntry(zipOutputStream, "xl/styles.xml", styles());
            writeEntry(zipOutputStream, "xl/worksheets/sheet1.xml", sheetXml(headers, rows));
        }
    }

    public static List<Map<String, String>> readRows(Path inputPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(inputPath.toFile())) {
            List<String> sharedStrings = readSharedStrings(zipFile);
            String sheetPath = locateFirstSheet(zipFile);
            Document sheetDocument = parseXml(readZipEntry(zipFile, sheetPath));
            NodeList rowNodes = sheetDocument.getElementsByTagName("row");
            if (rowNodes.getLength() == 0) {
                return List.of();
            }

            List<String> headers = readRowCells((Element) rowNodes.item(0), sharedStrings);
            List<Map<String, String>> rows = new ArrayList<>();
            for (int index = 1; index < rowNodes.getLength(); index++) {
                List<String> values = readRowCells((Element) rowNodes.item(index), sharedStrings);
                boolean nonBlank = values.stream().anyMatch(value -> value != null && !value.isBlank());
                if (!nonBlank) {
                    continue;
                }

                Map<String, String> mappedRow = new LinkedHashMap<>();
                for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
                    String header = headers.get(headerIndex);
                    if (header == null || header.isBlank()) {
                        continue;
                    }
                    mappedRow.put(header, headerIndex < values.size() ? values.get(headerIndex) : "");
                }
                rows.add(mappedRow);
            }
            return rows;
        }
    }

    private static String contentTypes() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                  <Default Extension="xml" ContentType="application/xml"/>
                  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
                  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
                </Types>
                """;
    }

    private static String rootRelationships() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
                  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
                </Relationships>
                """;
    }

    private static String workbook(String sheetName) {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
                          xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                  <sheets>
                    <sheet name="%s" sheetId="1" r:id="rId1"/>
                  </sheets>
                </workbook>
                """.formatted(escapeXml(sheetName));
    }

    private static String workbookRelationships() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
                </Relationships>
                """;
    }

    private static String styles() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <fonts count="1">
                    <font>
                      <sz val="11"/>
                      <name val="Calibri"/>
                    </font>
                  </fonts>
                  <fills count="1">
                    <fill><patternFill patternType="none"/></fill>
                  </fills>
                  <borders count="1">
                    <border><left/><right/><top/><bottom/><diagonal/></border>
                  </borders>
                  <cellStyleXfs count="1">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
                  </cellStyleXfs>
                  <cellXfs count="1">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
                  </cellXfs>
                  <cellStyles count="1">
                    <cellStyle name="Normal" xfId="0" builtinId="0"/>
                  </cellStyles>
                </styleSheet>
                """;
    }

    private static String appProperties() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
                            xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
                  <Application>Codex</Application>
                </Properties>
                """;
    }

    private static String coreProperties() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                                   xmlns:dc="http://purl.org/dc/elements/1.1/"
                                   xmlns:dcterms="http://purl.org/dc/terms/"
                                   xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                  <dc:creator>Codex</dc:creator>
                  <cp:lastModifiedBy>Codex</cp:lastModifiedBy>
                </cp:coreProperties>
                """;
    }

    private static String sheetXml(List<String> headers, List<List<String>> rows) {
        StringBuilder builder = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <sheetData>
                """);

        int rowNumber = 1;
        builder.append(row(rowNumber++, headers));
        for (List<String> row : rows) {
            builder.append(row(rowNumber++, row));
        }

        builder.append("""
                  </sheetData>
                </worksheet>
                """);
        return builder.toString();
    }

    private static String row(int rowNumber, List<String> values) {
        StringBuilder builder = new StringBuilder();
        builder.append("<row r=\"").append(rowNumber).append("\">");
        for (int columnIndex = 0; columnIndex < values.size(); columnIndex++) {
            String cellReference = columnName(columnIndex) + rowNumber;
            builder.append("<c r=\"").append(cellReference).append("\" t=\"inlineStr\"><is><t>")
                    .append(escapeXml(values.get(columnIndex) == null ? "" : values.get(columnIndex)))
                    .append("</t></is></c>");
        }
        builder.append("</row>");
        return builder.toString();
    }

    private static String columnName(int index) {
        StringBuilder name = new StringBuilder();
        int current = index;
        do {
            name.insert(0, (char) ('A' + (current % 26)));
            current = (current / 26) - 1;
        } while (current >= 0);
        return name.toString();
    }

    private static void writeEntry(ZipOutputStream zipOutputStream, String name, String contents) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(name));
        zipOutputStream.write(contents.getBytes(StandardCharsets.UTF_8));
        zipOutputStream.closeEntry();
    }

    private static String locateFirstSheet(ZipFile zipFile) {
        if (zipFile.getEntry("xl/worksheets/sheet1.xml") != null) {
            return "xl/worksheets/sheet1.xml";
        }

        return zipFile.stream()
                .map(ZipEntry::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith("xl/worksheets/") && name.endsWith(".xml"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No worksheet XML was found in the Excel file."));
    }

    private static List<String> readSharedStrings(ZipFile zipFile) throws IOException {
        ZipEntry sharedStringsEntry = zipFile.getEntry("xl/sharedStrings.xml");
        if (sharedStringsEntry == null) {
            return List.of();
        }

        Document document = parseXml(readZipEntry(zipFile, "xl/sharedStrings.xml"));
        NodeList textNodes = document.getElementsByTagName("t");
        List<String> values = new ArrayList<>();
        for (int index = 0; index < textNodes.getLength(); index++) {
            values.add(textNodes.item(index).getTextContent());
        }
        return values;
    }

    private static List<String> readRowCells(Element rowElement, List<String> sharedStrings) {
        NodeList cellNodes = rowElement.getElementsByTagName("c");
        Map<Integer, String> valuesByIndex = new HashMap<>();
        int maxIndex = -1;

        for (int index = 0; index < cellNodes.getLength(); index++) {
            Element cell = (Element) cellNodes.item(index);
            int columnIndex = columnIndex(cell.getAttribute("r"));
            maxIndex = Math.max(maxIndex, columnIndex);
            valuesByIndex.put(columnIndex, readCellValue(cell, sharedStrings));
        }

        List<String> values = new ArrayList<>();
        for (int index = 0; index <= maxIndex; index++) {
            values.add(valuesByIndex.getOrDefault(index, ""));
        }
        return values;
    }

    private static String readCellValue(Element cell, List<String> sharedStrings) {
        String cellType = cell.getAttribute("t");
        if ("inlineStr".equals(cellType)) {
            NodeList texts = cell.getElementsByTagName("t");
            return texts.getLength() == 0 ? "" : texts.item(0).getTextContent();
        }
        if ("s".equals(cellType)) {
            String indexText = firstChildText(cell, "v");
            if (indexText == null || indexText.isBlank()) {
                return "";
            }
            int sharedStringIndex = Integer.parseInt(indexText);
            return sharedStringIndex >= 0 && sharedStringIndex < sharedStrings.size()
                    ? sharedStrings.get(sharedStringIndex)
                    : "";
        }
        String value = firstChildText(cell, "v");
        return value == null ? "" : value;
    }

    private static String firstChildText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        Node node = nodes.item(0);
        return node == null ? null : node.getTextContent();
    }

    private static int columnIndex(String reference) {
        int index = 0;
        for (int i = 0; i < reference.length(); i++) {
            char character = reference.charAt(i);
            if (!Character.isLetter(character)) {
                break;
            }
            index = (index * 26) + (Character.toUpperCase(character) - 'A' + 1);
        }
        return Math.max(0, index - 1);
    }

    private static Document parseXml(byte[] contents) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(contents));
        } catch (Exception exception) {
            throw new IOException("Unable to parse Excel XML content.", exception);
        }
    }

    private static byte[] readZipEntry(ZipFile zipFile, String name) throws IOException {
        ZipEntry entry = zipFile.getEntry(name);
        if (entry == null) {
            throw new IOException("Missing Excel entry: " + name);
        }

        try (InputStream inputStream = zipFile.getInputStream(entry);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
