package com.example;

import com.example.util.CsvReaderUtil;
import com.example.util.CsvWriterUtil;
import com.example.constants.PropertyValues;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@Slf4j
public class SchedulingExam {

  @Autowired PropertyValues prop;

  @Scheduled(cron = "*/10 * * * * *")
  public void execute() throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
    log.info(prop.getData());
    log.info(prop.getNames().toString());
    String path = System.getProperty("user.dir");
    log.info(path);
    var fileData =
        CsvReaderUtil.getFileData(
            Paths.get(System.getProperty("user.dir"), "testFiles", "input", "Input.CSV"),
            Employee.class,
            ',',
            StandardCharsets.UTF_8);
    log.info(fileData.toString());
    Function<Employee, Employee> function = e -> e;
    //    function.

    Predicate<Employee> predicate = e -> true;

    var fileData3 =
        CsvReaderUtil.getFileData3(
            Paths.get(System.getProperty("user.dir"), "testFiles", "input", "Input.CSV"),
            Employee.class,
            ',',
            StandardCharsets.UTF_8,
            predicate);
    log.info(fileData3.toString());

    CsvWriterUtil.writeCsv(Paths.get(System.getProperty("user.dir"), "testFiles", "output", "Output.CSV"),
            fileData,Employee.class);
  }
}
