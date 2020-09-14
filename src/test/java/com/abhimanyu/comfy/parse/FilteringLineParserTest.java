package com.abhimanyu.comfy.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.abhimanyu.comfy.exception.ComfyException;

public class FilteringLineParserTest {

  private final LineParser filteringLineParser = new FilteringLineParser();

  @Test
  public void typicalStringLine() {
    String line = "name = \"hello there, ftp uploading\"";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens.getSectionName()).isNull();
    assertThat(lineTokens.getKey()).isEqualTo("name");
    assertThat(lineTokens.getValue()).isEqualTo("\"hello there, ftp uploading\"");
    assertThat(lineTokens.getOverride()).isNull();
  }

  @Test
  public void testThatAllTokensAreTrimmed() {
    String line = "    name    =    \"hello there, ftp uploading\"    ";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens.getSectionName()).isNull();
    assertThat(lineTokens.getKey()).isEqualTo("name");
    assertThat(lineTokens.getValue()).isEqualTo("\"hello there, ftp uploading\"");
    assertThat(lineTokens.getOverride()).isNull();
  }

  @Test
  public void typicalCommentLine() {
    String line = "; this is the config file format your code should accept.";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens).isNull();
  }

  @Test
  public void typicalOverrideLine() {
    String line = "path<production>       = /srv/var/tmp/";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens.getSectionName()).isNull();
    assertThat(lineTokens.getKey()).isEqualTo("path");
    assertThat(lineTokens.getValue()).isEqualTo("/srv/var/tmp/");
    assertThat(lineTokens.getOverride()).isEqualTo("production");
  }

  @Test
  public void overrideLineWithComment() {
    String line = "path<staging> = /srv/uploads/; This is another comment";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens.getSectionName()).isNull();
    assertThat(lineTokens.getKey()).isEqualTo("path");
    assertThat(lineTokens.getValue()).isEqualTo("/srv/uploads/");
    assertThat(lineTokens.getOverride()).isEqualTo("staging");
  }

  @Test
  public void overrideLineWithStringValue() {
    String line = "key<production> = \"test\"";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens.getSectionName()).isNull();
    assertThat(lineTokens.getKey()).isEqualTo("key");
    assertThat(lineTokens.getValue()).isEqualTo("\"test\"");
    assertThat(lineTokens.getOverride()).isEqualTo("production");
  }

  @Test
  public void typicalSectionLine() {
    String line = "[ftp]";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens.getSectionName()).isEqualTo("ftp");
    assertThat(lineTokens.getKey()).isNull();
    assertThat(lineTokens.getValue()).isNull();
    assertThat(lineTokens.getOverride()).isNull();
  }

  @Test
  public void typicalLineWithArrayValues() {
    String line = "params   =      array,  of,  values";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens.getSectionName()).isNull();
    assertThat(lineTokens.getKey()).isEqualTo("params");
    assertThat(lineTokens.getValue()).isEqualTo("array,  of,  values");
    assertThat(lineTokens.getOverride()).isNull();
  }

  @Test
  public void malformedLineWithOverrideAndMissingKey() {
    String line = "<production>       = /srv/var/tmp/";

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      filteringLineParser.parse(line);
    });

    assertTrue(exception.getMessage().contains("Encountered an invalid line: "));
  }

  @Test
  public void malformedLineWithInvalidKey() {
    String line = "name<  = /srv/var/tmp/";

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      filteringLineParser.parse(line);
    });

    assertTrue(exception.getMessage().contains("Encountered an invalid line: "));
  }

  @Test
  public void malformedLineWithInvalidSectionToken() {
    String line = "[section";

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      filteringLineParser.parse(line);
    });

    assertTrue(exception.getMessage().contains("Encountered an invalid line: "));
  }

  @Test
  public void malformedLineWithJustAWord() {
    String line = "section";

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      filteringLineParser.parse(line);
    });

    assertTrue(exception.getMessage().contains("Encountered an invalid line: "));
  }

  @Test
  public void malformedLineWithMissingValue() {
    String line = "name = ";

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      filteringLineParser.parse(line);
    });

    assertTrue(exception.getMessage().contains("Encountered an invalid line: "));
  }

  @Test
  public void malformedLineWithMissingKey() {
    String line = " = value";

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      filteringLineParser.parse(line);
    });

    assertTrue(exception.getMessage().contains("Encountered an invalid line: "));
  }

  @Test
  public void malformedLineWithBothKeyAndValueMissing() {
    String line = " =     ";

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      filteringLineParser.parse(line);
    });

    assertTrue(exception.getMessage().contains("Encountered an invalid line: "));
  }

  @Test
  public void emptyLine() {
    String line = "          ";
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens).isNull();
  }

  @Test
  public void nullLine() {
    String line = null;
    LineTokens lineTokens = filteringLineParser.parse(line);

    assertThat(lineTokens).isNull();
  }
}
