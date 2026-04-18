package com.mycompany.schoolmanagementssytem_edp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SimplePdfExporter {

    private static final int MAX_LINES_PER_PAGE = 46;
    private static final int MAX_CHARACTERS_PER_LINE = 92;
    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    private static final int MARGIN_LEFT = 48;
    private static final int START_Y = 780;
    private static final int LEADING = 15;

    private SimplePdfExporter() {
    }

    public static void exportLines(Path outputPath, String title, List<String> lines) throws IOException {
        List<List<String>> pages = paginate(lines);
        if (pages.isEmpty()) {
            pages = List.of(List.of(title));
        }

        List<String> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");
        objects.add("<< /Type /Pages /Count 0 /Kids [] >>");
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>");

        StringBuilder pagesKids = new StringBuilder();
        for (int pageIndex = 0; pageIndex < pages.size(); pageIndex++) {
            int pageObjectNumber = 4 + (pageIndex * 2);
            int contentObjectNumber = pageObjectNumber + 1;
            pagesKids.append(pageObjectNumber).append(" 0 R ");
            objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT + "] "
                    + "/Resources << /Font << /F1 3 0 R >> >> /Contents " + contentObjectNumber + " 0 R >>");

            byte[] streamBytes = pageStream(pageIndex, title, pages.get(pageIndex)).getBytes(StandardCharsets.UTF_8);
            objects.add("<< /Length " + streamBytes.length + " >>\nstream\n"
                    + new String(streamBytes, StandardCharsets.UTF_8)
                    + "\nendstream");
        }

        objects.set(1, "<< /Type /Pages /Count " + pages.size() + " /Kids [" + pagesKids.toString().trim() + "] >>");

        writePdf(outputPath, objects);
    }

    private static List<List<String>> paginate(List<String> lines) {
        List<List<String>> pages = new ArrayList<>();
        List<String> currentPage = new ArrayList<>();

        for (String line : lines) {
            for (String wrappedLine : wrapLine(line == null ? "" : line, MAX_CHARACTERS_PER_LINE)) {
                if (currentPage.size() == MAX_LINES_PER_PAGE) {
                    pages.add(currentPage);
                    currentPage = new ArrayList<>();
                }
                currentPage.add(wrappedLine);
            }
        }

        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }

        return pages;
    }

    private static String pageStream(int pageIndex, String title, List<String> lines) {
        StringBuilder builder = new StringBuilder();
        builder.append("BT\n/F1 10 Tf\n").append(LEADING).append(" TL\n").append(MARGIN_LEFT).append(' ').append(START_Y).append(" Td\n");
        if (title != null && !title.isBlank()) {
            if (pageIndex == 0) {
                builder.append("(").append(escape(title)).append(") Tj\nT*\nT*\n");
            } else {
                builder.append("(").append(escape(title + " (Page " + (pageIndex + 1) + ")")).append(") Tj\nT*\nT*\n");
            }
        }

        for (String line : lines) {
            builder.append("(").append(escape(line)).append(") Tj\nT*\n");
        }
        builder.append("ET");
        return builder.toString();
    }

    private static void writePdf(Path outputPath, List<String> objects) throws IOException {
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);

        for (int index = 0; index < objects.size(); index++) {
            offsets.add(pdf.length());
            pdf.append(index + 1)
                    .append(" 0 obj\n")
                    .append(objects.get(index))
                    .append("\nendobj\n");
        }

        int xrefStart = pdf.length();
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n");
        pdf.append(String.format("%010d %05d f %n", 0, 65535));
        for (int index = 1; index < offsets.size(); index++) {
            pdf.append(String.format("%010d %05d n %n", offsets.get(index), 0));
        }
        pdf.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xrefStart).append("\n%%EOF");

        Files.writeString(outputPath, pdf.toString(), StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", "")
                .replace("\n", " ");
    }

    private static List<String> wrapLine(String value, int maxWidth) {
        String normalized = value == null ? "" : value.replace('\t', ' ').replaceAll("\\s+$", "");
        if (normalized.isBlank()) {
            return List.of("");
        }

        List<String> lines = new ArrayList<>();
        String remaining = normalized;
        while (remaining.length() > maxWidth) {
            int breakIndex = remaining.lastIndexOf(' ', maxWidth);
            if (breakIndex <= 0) {
                breakIndex = maxWidth;
            }
            lines.add(remaining.substring(0, breakIndex).stripTrailing());
            remaining = remaining.substring(Math.min(remaining.length(), breakIndex + 1)).stripLeading();
        }

        lines.add(remaining);
        return lines;
    }
}
