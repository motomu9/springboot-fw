package com.example.util;

import static org.mozilla.universalchardet.Constants.CHARSET_SHIFT_JIS;
import static org.mozilla.universalchardet.Constants.CHARSET_UTF_8;

import com.example.util.function.FunctionThrowable;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.input.BOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;

public class CsvReaderWrapper {

  private final InputStreamReader reader;

  private Charset charset = null;
  private char separator = SeparatorChar.COMMA.character;

  private CsvReaderWrapper(final Path filePath, final CharsetType charsetType) throws IOException {

    this.charset =
        (charsetType == CharsetType.UNKNOWN) ? getCharset(filePath) : charsetType.charset;

    reader =
        new InputStreamReader(new BOMInputStream(new FileInputStream(filePath.toFile())), charset);
  }

  private void close() throws IOException {
    reader.close();
  }

  public CsvReaderWrapper setSeparator(final SeparatorChar separator) {
    this.separator = separator.character;
    return this;
  }

  public <T> List<T> read(final Class<T> clazz) {

    var strategy = new HeaderColumnNameMappingStrategy<T>();
    strategy.setType(clazz);
    CsvToBean<T> toBean =
        new CsvToBeanBuilder<T>(reader)
            .withSeparator(separator)
            .withMappingStrategy(strategy)
            .build();
    return toBean.parse();
  }

  public <T> Stream<T> readToStream(final Class<T> clazz) {
    var strategy = new HeaderColumnNameMappingStrategy<T>();
    strategy.setType(clazz);
    CsvToBean<T> toBean =
        new CsvToBeanBuilder<T>(reader)
            .withSeparator(separator)
            .withMappingStrategy(strategy)
            .build();
    return toBean.stream();
  }

  public static <T> List<T> execute(
      final Path filePath, final FunctionThrowable<CsvReaderWrapper, List<T>, Exception> function)
      throws Exception {
    return execute(filePath, CharsetType.UNKNOWN, function);
  }

  public static <T> List<T> execute(
      final Path filePath,
      final CharsetType charsetType,
      final FunctionThrowable<CsvReaderWrapper, List<T>, Exception> function)
      throws Exception {
    final var readerWrapper = new CsvReaderWrapper(filePath, charsetType);
    try {
      return function.apply(readerWrapper);
    } finally {
      readerWrapper.close();
    }
  }

  public static <T> Stream<T> executeStream(
      final Path filePath, final FunctionThrowable<CsvReaderWrapper, Stream<T>, Exception> function)
      throws Exception {
    return executeStream(filePath, CharsetType.UNKNOWN, function);
  }

  public static <T> Stream<T> executeStream(
      final Path filePath,
      final CharsetType charsetType,
      final FunctionThrowable<CsvReaderWrapper, Stream<T>, Exception> function)
      throws Exception {
    final var readerWrapper = new CsvReaderWrapper(filePath, charsetType);
    try {
      return function.apply(readerWrapper);
    } finally {
      readerWrapper.close();
    }
  }

  private Charset getCharset(Path filePath) throws IOException {
    final var encode = UniversalDetector.detectCharset(filePath.toFile());
    final var charType =
        Arrays.stream(CharsetType.values())
            .filter(charsetType -> charsetType.nameOf(encode))
            .findFirst();
    return charType.isPresent() ? charType.get().charset : CharsetType.UTF_8.charset;
  }

  enum CharsetType {
    UTF_8(StandardCharsets.UTF_8, CHARSET_UTF_8),
    S_JIS(Charset.forName("Shift-JIS"), CHARSET_SHIFT_JIS),
    UNKNOWN(null, "");

    final Charset charset;
    final String charsetName;

    CharsetType(Charset charset, String charsetName) {
      this.charset = charset;
      this.charsetName = charsetName;
    }

    public boolean nameOf(String name) {
      return this.charsetName.equals(name);
    }
  }

  enum SeparatorChar {
    COMMA(','),
    TAB('\t');

    final char character;

    SeparatorChar(char character) {
      this.character = character;
    }
  }
}
