package com.example.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ConfigurationProperties(prefix = "foo")
@ConstructorBinding
@AllArgsConstructor
@Getter
@ToString
public class PropertyValues {
  private final String data;
  private final List<String> names;

  public static Path getCurrentPath() {
    return Paths.get(System.getProperty("user.dir"));
  }
}
