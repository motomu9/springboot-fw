package com.example.util;

import com.example.util.function.ConsumerThrowable;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * CSVWriterのラッパークラス.
 *
 * <p>以下のパラメータのデフォルト値は以下の通り<br>
 * <li>Charset - [UTF-8]
 * <li>SeparatorChar - [,]
 * <li>QuoteChar - ["]
 * <li>EscapeChar - ["]
 * <li>LineEnd - [\r\n]
 */
public class CsvWriterWrapper {

  private final Writer writer;
  private char separator = CSVWriter.DEFAULT_SEPARATOR;
  private char quoteChar = CSVWriter.DEFAULT_QUOTE_CHARACTER;
  private char escapeChar = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
  private String lineEnd = CSVWriter.RFC4180_LINE_END;

  private CsvWriterWrapper(final Path filePath, CharsetType charsetType) throws IOException {
    writer = new FileWriter(filePath.toAbsolutePath().toString(), charsetType.charset);
  }

  private void close() throws IOException {
    writer.close();
  }

  public CsvWriterWrapper setSeparator(final char separator) {
    this.separator = separator;
    return this;
  }

  public CsvWriterWrapper setQuoteChar(final char quoteChar) {
    this.quoteChar = quoteChar;
    return this;
  }

  public CsvWriterWrapper setEscapeChar(final char escapeChar) {
    this.escapeChar = escapeChar;
    return this;
  }

  public CsvWriterWrapper setLineEnd(final String lineEnd) {
    if (!Objects.isNull(lineEnd)) {
      this.lineEnd = lineEnd;
    }
    return this;
  }

  public <T> void write(final Stream<T> beans, final Class<T> clazz)
      throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
    write(beans.iterator(), clazz);
  }

  public <T> void write(final List<T> beans, final Class<T> clazz)
      throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
    write(beans.iterator(), clazz);
  }

  public <T> void write(final Iterator<T> beans, final Class<T> clazz)
      throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

    var csvWriter = new CSVWriter(writer, separator, quoteChar, escapeChar, lineEnd);
    var strategy = new AddHeaderMappingStrategy<T>();
    //    var strategy = new HeaderColumnNameMappingStrategy<T>();
    strategy.setType(clazz);
    var toCsv = new StatefulBeanToCsvBuilder<T>(csvWriter).withMappingStrategy(strategy).build();
    toCsv.write(beans);
  }

  public static void execute(
      final Path filePath, final ConsumerThrowable<CsvWriterWrapper, Exception> consumer)
      throws Exception {
    execute(filePath, CharsetType.UTF_8, consumer);
  }

  public static void execute(
      final Path filePath,
      final CharsetType charsetType,
      final ConsumerThrowable<CsvWriterWrapper, Exception> consumer)
      throws Exception {

    if (Objects.isNull(filePath)) {
      throw new IllegalArgumentException("'filePath' is null.");
    }

    final var writerWrapper = new CsvWriterWrapper(filePath, charsetType);
    try {
      consumer.accept(writerWrapper);
    } finally {
      writerWrapper.close();
    }
  }

  enum CharsetType {
    UTF_8(StandardCharsets.UTF_8),
    S_JIS(Charset.forName("Shift-JIS"));
    final Charset charset;

    CharsetType(Charset charset) {
      this.charset = charset;
    }
  }

  private static class AddHeaderMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {

      super.setColumnMapping(new String[FieldUtils.getAllFields(bean.getClass()).length]);
      final var numColumns = headerIndex.findMaxIndex();
      if (numColumns == -1) {
        return super.generateHeader(bean);
      }

      var header = new String[numColumns + 1];
      Arrays.setAll(header, i -> extractHeaderName(findField(i)));
      return header;
    }

    private String extractHeaderName(BeanField<T, Integer> beanField) {
      if (beanField == null
          || beanField.getField() == null
          || beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class).length == 0) {
        return StringUtils.EMPTY;
      }

      final var bindByNameAnnotation =
          beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class)[0];
      return bindByNameAnnotation.column();
    }
  }
}
