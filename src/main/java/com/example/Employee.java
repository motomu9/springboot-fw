package com.example;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Employee {
  @CsvBindByName(column = "社員番号")
  @CsvBindByPosition(position = 3)
  private String id;

  @CsvBindByName(column = "名前")
  @CsvBindByPosition(position = 1)
  private String name;

  @CsvBindByName(column = "給与")
  @CsvBindByPosition(position = 2)
  private Integer salary;
}
