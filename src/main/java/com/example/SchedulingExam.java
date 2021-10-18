package com.example;

import com.example.constants.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulingExam {

  @Autowired PropertyValues prop;

  @Scheduled(cron = "*/1 * * * * *")
  public void execute() {
    System.out.println(prop.getNames());
  }
}
