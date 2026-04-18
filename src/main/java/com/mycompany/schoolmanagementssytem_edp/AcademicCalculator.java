package com.mycompany.schoolmanagementssytem_edp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class AcademicCalculator {

    private static final BigDecimal PERFORMANCE_WEIGHT = new BigDecimal("0.40");
    private static final BigDecimal ATTENDANCE_WEIGHT = new BigDecimal("0.10");
    private static final BigDecimal WRITTEN_WORKS_WEIGHT = new BigDecimal("0.20");
    private static final BigDecimal EXAM_WEIGHT = new BigDecimal("0.30");

    private AcademicCalculator() {
    }

    public static BigDecimal computeTermRawScore(
            BigDecimal performance,
            BigDecimal attendance,
            BigDecimal writtenWorks,
            BigDecimal exam
    ) {
        if (performance == null || attendance == null || writtenWorks == null || exam == null) {
            return null;
        }

        return performance.multiply(PERFORMANCE_WEIGHT)
                .add(attendance.multiply(ATTENDANCE_WEIGHT))
                .add(writtenWorks.multiply(WRITTEN_WORKS_WEIGHT))
                .add(exam.multiply(EXAM_WEIGHT))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal computeFinalRawGrade(BigDecimal midtermRawScore, BigDecimal finalsRawScore) {
        if (midtermRawScore == null && finalsRawScore == null) {
            return null;
        }
        if (midtermRawScore == null) {
            return finalsRawScore.setScale(2, RoundingMode.HALF_UP);
        }
        if (finalsRawScore == null) {
            return midtermRawScore.setScale(2, RoundingMode.HALF_UP);
        }

        return midtermRawScore.add(finalsRawScore)
                .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal toUniversityGrade(BigDecimal rawGrade) {
        if (rawGrade == null) {
            return null;
        }

        double score = rawGrade.doubleValue();
        if (score >= 96) {
            return new BigDecimal("1.00");
        }
        if (score >= 93) {
            return new BigDecimal("1.25");
        }
        if (score >= 90) {
            return new BigDecimal("1.50");
        }
        if (score >= 87) {
            return new BigDecimal("1.75");
        }
        if (score >= 84) {
            return new BigDecimal("2.00");
        }
        if (score >= 81) {
            return new BigDecimal("2.25");
        }
        if (score >= 78) {
            return new BigDecimal("2.50");
        }
        if (score >= 75) {
            return new BigDecimal("2.75");
        }
        if (score >= 72) {
            return new BigDecimal("3.00");
        }
        if (score >= 69) {
            return new BigDecimal("4.00");
        }
        return new BigDecimal("5.00");
    }

    public static String remarksFor(BigDecimal universityGrade, BigDecimal rawGrade) {
        if (universityGrade == null || rawGrade == null) {
            return "In Progress";
        }
        return rawGrade.compareTo(new BigDecimal("75")) >= 0 && universityGrade.compareTo(new BigDecimal("3.00")) <= 0
                ? "Passed"
                : "Failed";
    }

    public static BigDecimal computeGwa(List<WeightedGrade> grades) {
        BigDecimal weightedTotal = BigDecimal.ZERO;
        BigDecimal totalUnits = BigDecimal.ZERO;

        for (WeightedGrade grade : grades) {
            if (grade == null || grade.grade() == null || grade.units() == null) {
                continue;
            }
            weightedTotal = weightedTotal.add(grade.grade().multiply(grade.units()));
            totalUnits = totalUnits.add(grade.units());
        }

        if (totalUnits.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return weightedTotal.divide(totalUnits, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal parseScore(String value, String label) {
        if (value == null || value.isBlank()) {
            return null;
        }

        BigDecimal parsed;
        try {
            parsed = new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be numeric.");
        }

        if (parsed.compareTo(BigDecimal.ZERO) < 0 || parsed.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(label + " must be between 0 and 100.");
        }

        return parsed.setScale(2, RoundingMode.HALF_UP);
    }

    public record WeightedGrade(BigDecimal grade, BigDecimal units) {
    }
}
