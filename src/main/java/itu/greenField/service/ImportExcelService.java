package itu.greenField.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

@Service
public class ImportExcelService {
    public static LocalDateTime localDateTimeValidator(String localDateTimeStr) {
        localDateTimeStr = localDateTimeStr.trim();
        try {
            String[] split = localDateTimeStr.split(" ");
            String datePart = split[0];
            String hourPart = split[1];
            LocalDate date = localDateValidator(datePart);
            LocalTime time = localTimeValidator(hourPart);

            if (date == null || time == null)
                throw new Exception();

            // combine date & time in a variable dateTime of type LocalDateTime
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            return dateTime;
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate localDateValidator(String localDateStr) {
        localDateStr = localDateStr.trim();
        try {
            // try format "dd/MM/yyyy"
            LocalDate date = LocalDate.parse(localDateStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return date;
        } catch (Exception e0) {
            try {
                // try exel format "dd/MM/yy"
                LocalDate date = LocalDate.parse(localDateStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
                return date;
            } catch (Exception e) {
                try {
                    // try format "yyyy-MM-dd"
                    LocalDate date = LocalDate.parse(localDateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    return date;
                } catch (Exception e1) {
                    try {
                        // try format "MM/dd/yyyy"
                        LocalDate date = LocalDate.parse(localDateStr, java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        return date;
                    } catch (Exception e2) {
                        LocalDate date = null;
                        return date;
                    }
                }
            }
        }
    }

    public static LocalTime localTimeValidator(String localTimeStr) {
        localTimeStr = localTimeStr.trim();
        try {
            LocalTime time = LocalTime.parse(localTimeStr, java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            return time;
        } catch (Exception e) {
            return null;
        }
    }
}
