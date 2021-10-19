package com.example;

import com.example.constants.PropertyValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SchedulingExam {

  @Autowired PropertyValues prop;

  @Scheduled(cron = "*/1 * * * * *")
  public void execute() {
    log.info(prop.getData());
    log.info(prop.getNames().toString());
  }
}
