package com.example.constants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "foo")
@Data
public class PropertyValues {
  private final String data;
  private final List<String> names;
}
