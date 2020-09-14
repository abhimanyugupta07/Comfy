package com.abhimanyu.comfy.io.ini;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abhimanyu.comfy.config.ComfyConfig;
import com.abhimanyu.comfy.exception.ComfyException;
import com.abhimanyu.comfy.io.InputReader;
import com.abhimanyu.comfy.parse.LineParser;
import com.abhimanyu.comfy.parse.LineTokens;
import com.abhimanyu.comfy.parse.PropertyProcessor;
import com.abhimanyu.comfy.value.BooleanValue;
import com.abhimanyu.comfy.value.IntValue;
import com.abhimanyu.comfy.value.StringValue;

@ExtendWith(MockitoExtension.class)
public class ConfigFileReaderTest {
  @TempDir
  File tempDirectory;

  private File configFile;

  private @Mock PropertyProcessor propertyProcessor;
  private @Mock LineParser lineParser;

  private InputReader configFileReader;

  @Test
  public void typical() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays.asList("[common]", "basic_size_limit= 26214400", "[ ftp]", "enabled =  no");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] {});

    InputStream inputStream = new FileInputStream(configFile);

    when(lineParser.parse(any()))
        .thenReturn(new LineTokens("common", null, null, null))
        .thenReturn(new LineTokens(null, "basic_size_limit", "26214400", null))
        .thenReturn(new LineTokens("ftp", null, null, null))
        .thenReturn(new LineTokens(null, "enabled", "no", null));

    when(propertyProcessor.process(any())).thenReturn(new IntValue(26214400)).thenReturn(new BooleanValue(false));

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser, times(4)).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0)).isEqualTo("[common]");
    assertThat(lineParserCaptor.getAllValues().get(1)).isEqualTo("basic_size_limit= 26214400");
    assertThat(lineParserCaptor.getAllValues().get(2)).isEqualTo("[ ftp]");
    assertThat(lineParserCaptor.getAllValues().get(3)).isEqualTo("enabled =  no");

    ArgumentCaptor<String> propertyProcessorCaptor = ArgumentCaptor.forClass(String.class);
    verify(propertyProcessor, times(2)).process(propertyProcessorCaptor.capture());

    assertThat(propertyProcessorCaptor.getAllValues().get(0)).isEqualTo("26214400");
    assertThat(propertyProcessorCaptor.getAllValues().get(1)).isEqualTo("no");

    assertThat(comfyConfig.size()).isEqualTo(2);
    assertThat(comfyConfig.get("common").getIntValue("basic_size_limit")).isEqualTo(26214400);
    assertThat(comfyConfig.get("ftp").getBooleanValue("enabled")).isEqualTo(false);
  }

  @Test
  public void tryToGetValueAsWrongType() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays.asList("[common]", "basic_size_limit= 26214400", "[ ftp]", "enabled =  no");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] {});

    InputStream inputStream = new FileInputStream(configFile);

    when(lineParser.parse(any()))
        .thenReturn(new LineTokens("common", null, null, null))
        .thenReturn(new LineTokens(null, "basic_size_limit", "26214400", null))
        .thenReturn(new LineTokens("ftp", null, null, null))
        .thenReturn(new LineTokens(null, "enabled", "no", null));

    when(propertyProcessor.process(any())).thenReturn(new IntValue(26214400)).thenReturn(new BooleanValue(false));

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser, times(4)).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0)).isEqualTo("[common]");
    assertThat(lineParserCaptor.getAllValues().get(1)).isEqualTo("basic_size_limit= 26214400");
    assertThat(lineParserCaptor.getAllValues().get(2)).isEqualTo("[ ftp]");
    assertThat(lineParserCaptor.getAllValues().get(3)).isEqualTo("enabled =  no");

    ArgumentCaptor<String> propertyProcessorCaptor = ArgumentCaptor.forClass(String.class);
    verify(propertyProcessor, times(2)).process(propertyProcessorCaptor.capture());

    assertThat(propertyProcessorCaptor.getAllValues().get(0)).isEqualTo("26214400");
    assertThat(propertyProcessorCaptor.getAllValues().get(1)).isEqualTo("no");

    assertThat(comfyConfig.size()).isEqualTo(2);
    assertThat(comfyConfig.get("common").getIntValue("basic_size_limit")).isEqualTo(26214400);
    assertThat(comfyConfig.get("ftp").getBooleanValue("enabled")).isEqualTo(false);

    ClassCastException exception = assertThrows(ClassCastException.class, () -> {
      comfyConfig.get("ftp").getStringValue("enabled");
    });

    assertTrue(exception
        .getMessage()
        .contains(String.format("Trying to convert an object of %s to a StringValue.", BooleanValue.class)));
  }

  @Test
  public void commentLineIsIgnored() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays
        .asList("; this is the config file format your code should accept.", "[common]", "basic_size_limit= 26214400");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] {});

    InputStream inputStream = new FileInputStream(configFile);

    when(lineParser.parse(any()))
        .thenReturn(null)
        .thenReturn(new LineTokens("common", null, null, null))
        .thenReturn(new LineTokens(null, "basic_size_limit", "26214400", null));

    when(propertyProcessor.process(any())).thenReturn(new IntValue(26214400));

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser, times(3)).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0))
        .isEqualTo("; this is the config file format your code should accept.");
    assertThat(lineParserCaptor.getAllValues().get(1)).isEqualTo("[common]");
    assertThat(lineParserCaptor.getAllValues().get(2)).isEqualTo("basic_size_limit= 26214400");

    ArgumentCaptor<String> propertyProcessorCaptor = ArgumentCaptor.forClass(String.class);
    verify(propertyProcessor, times(1)).process(propertyProcessorCaptor.capture());

    assertThat(propertyProcessorCaptor.getAllValues().get(0)).isEqualTo("26214400");

    assertThat(comfyConfig.size()).isEqualTo(1);
    assertThat(comfyConfig.get("common").getIntValue("basic_size_limit")).isEqualTo(26214400);
  }

  @Test
  public void rowWithoutSection() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays.asList("basic_size_limit= 26214400", "[ ftp]", "enabled =  no");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] {});

    InputStream inputStream = new FileInputStream(configFile);

    when(lineParser.parse(any())).thenReturn(new LineTokens(null, "basic_size_limit", "26214400", null));

    ComfyException exception = assertThrows(ComfyException.class, () -> {
      configFileReader.read(inputStream);
    });

    assertTrue(exception
        .getMessage()
        .contains("Line found that is not under any section. Please move this line under a section: "));

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0)).isEqualTo("basic_size_limit= 26214400");
  }

  @Test
  public void overrideNotSelected() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays
        .asList("[common]", "basic_size_limit= 26214400", "[ ftp]", "path<production> = /srv/var/tmp/",
            "path<ubuntu> = /etc/var/uploads");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] { "ubuntu" });

    InputStream inputStream = new FileInputStream(configFile);

    // Putting in path values for "production" and "ubuntu". We expect the value for "production" to be ignored.
    when(lineParser.parse(any()))
        .thenReturn(new LineTokens("common", null, null, null))
        .thenReturn(new LineTokens(null, "basic_size_limit", "26214400", null))
        .thenReturn(new LineTokens("ftp", null, null, null))
        .thenReturn(new LineTokens(null, "path", "/srv/var/tmp/", "production"))
        .thenReturn(new LineTokens(null, "path", "/etc/var/uploads", "ubuntu"));

    when(propertyProcessor.process(any()))
        .thenReturn(new IntValue(26214400))
        .thenReturn(new StringValue("/etc/var/uploads"));

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser, times(5)).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0)).isEqualTo("[common]");
    assertThat(lineParserCaptor.getAllValues().get(1)).isEqualTo("basic_size_limit= 26214400");
    assertThat(lineParserCaptor.getAllValues().get(2)).isEqualTo("[ ftp]");
    assertThat(lineParserCaptor.getAllValues().get(3)).isEqualTo("path<production> = /srv/var/tmp/");
    assertThat(lineParserCaptor.getAllValues().get(4)).isEqualTo("path<ubuntu> = /etc/var/uploads");

    ArgumentCaptor<String> propertyProcessorCaptor = ArgumentCaptor.forClass(String.class);
    verify(propertyProcessor, times(2)).process(propertyProcessorCaptor.capture());

    assertThat(propertyProcessorCaptor.getAllValues().get(0)).isEqualTo("26214400");
    assertThat(propertyProcessorCaptor.getAllValues().get(1)).isEqualTo("/etc/var/uploads");

    assertThat(comfyConfig.size()).isEqualTo(2);
    assertThat(comfyConfig.get("common").getIntValue("basic_size_limit")).isEqualTo(26214400);
    assertThat(comfyConfig.get("ftp").getStringValue("path")).isEqualTo("/etc/var/uploads");
  }

  @Test
  public void lastOverridenValueIsSelected() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays
        .asList("[common]", "basic_size_limit= 26214400", "[ ftp]", "path<production> = /srv/var/tmp/",
            "path<ubuntu> = first_value", "path<ubuntu> = second_value");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] { "ubuntu" });

    InputStream inputStream = new FileInputStream(configFile);

    // Putting in one path value for "production" and two for "ubuntu". We expect the value for "production" to be
    // ignored and for "ubuntu", second one to be selected.
    when(lineParser.parse(any()))
        .thenReturn(new LineTokens("common", null, null, null))
        .thenReturn(new LineTokens(null, "basic_size_limit", "26214400", null))
        .thenReturn(new LineTokens("ftp", null, null, null))
        .thenReturn(new LineTokens(null, "path", "/srv/var/tmp/", "production"))
        .thenReturn(new LineTokens(null, "path", "first_value", "ubuntu"))
        .thenReturn(new LineTokens(null, "path", "second_value", "ubuntu"));

    when(propertyProcessor.process(any()))
        .thenReturn(new IntValue(26214400))
        .thenReturn(new StringValue("first_value"))
        .thenReturn(new StringValue("second_value"));

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser, times(6)).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0)).isEqualTo("[common]");
    assertThat(lineParserCaptor.getAllValues().get(1)).isEqualTo("basic_size_limit= 26214400");
    assertThat(lineParserCaptor.getAllValues().get(2)).isEqualTo("[ ftp]");
    assertThat(lineParserCaptor.getAllValues().get(3)).isEqualTo("path<production> = /srv/var/tmp/");
    assertThat(lineParserCaptor.getAllValues().get(4)).isEqualTo("path<ubuntu> = first_value");
    assertThat(lineParserCaptor.getAllValues().get(5)).isEqualTo("path<ubuntu> = second_value");

    ArgumentCaptor<String> propertyProcessorCaptor = ArgumentCaptor.forClass(String.class);
    verify(propertyProcessor, times(3)).process(propertyProcessorCaptor.capture());

    assertThat(propertyProcessorCaptor.getAllValues().get(0)).isEqualTo("26214400");
    assertThat(propertyProcessorCaptor.getAllValues().get(1)).isEqualTo("first_value");
    assertThat(propertyProcessorCaptor.getAllValues().get(2)).isEqualTo("second_value");

    assertThat(comfyConfig.size()).isEqualTo(2);
    assertThat(comfyConfig.get("common").getIntValue("basic_size_limit")).isEqualTo(26214400);
    assertThat(comfyConfig.get("ftp").getStringValue("path")).isEqualTo("second_value");
  }

  @Test
  public void emptySection() throws IOException {
    // Test that an empty section is not created in ComfyConfig when section is empty.
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays.asList("[ftp]");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] {});

    InputStream inputStream = new FileInputStream(configFile);

    when(lineParser.parse(any())).thenReturn(new LineTokens("ftp", null, null, null));

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0)).isEqualTo("[ftp]");

    assertThat(comfyConfig).isNotNull();
    assertThat(comfyConfig.size()).isZero();
  }

  @Test
  public void onlyCommentsInFile() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays.asList("; this is the config file format your code should accept.", "test-comment");
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] {});

    InputStream inputStream = new FileInputStream(configFile);

    when(lineParser.parse(any())).thenReturn(null).thenReturn(null);

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    ArgumentCaptor<String> lineParserCaptor = ArgumentCaptor.forClass(String.class);
    verify(lineParser, times(2)).parse(lineParserCaptor.capture());

    assertThat(lineParserCaptor.getAllValues().get(0))
        .isEqualTo("; this is the config file format your code should accept.");
    assertThat(lineParserCaptor.getAllValues().get(1)).isEqualTo("test-comment");

    assertThat(comfyConfig).isNotNull();
    assertThat(comfyConfig.size()).isZero();
  }

  @Test
  public void emptyFile() throws IOException {
    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays.asList();
    Files.write(configFile.toPath(), rows);

    configFileReader = new ConfigFileReader(lineParser, propertyProcessor, new String[] {});

    InputStream inputStream = new FileInputStream(configFile);

    ComfyConfig comfyConfig = configFileReader.read(inputStream);

    assertThat(comfyConfig).isNotNull();
    assertThat(comfyConfig.get("test")).isNull();
  }

}
