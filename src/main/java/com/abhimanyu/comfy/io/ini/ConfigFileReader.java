package com.abhimanyu.comfy.io.ini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abhimanyu.comfy.config.ComfyConfig;
import com.abhimanyu.comfy.exception.ComfyException;
import com.abhimanyu.comfy.io.InputReader;
import com.abhimanyu.comfy.parse.LineParser;
import com.abhimanyu.comfy.parse.LineTokens;
import com.abhimanyu.comfy.parse.PropertyProcessor;
import com.abhimanyu.comfy.value.ConfigValue;

public class ConfigFileReader implements InputReader {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigFileReader.class);

  private final ComfyConfig comfyConfig;
  private final Set<String> overrides;
  private final LineParser lineParser;
  private final PropertyProcessor propertyProcessor;

  public ConfigFileReader(LineParser lineParser, PropertyProcessor propertyProcessor, String[] overridesInput) {
    comfyConfig = new ComfyConfig();
    overrides = new HashSet<>(Arrays.asList(overridesInput));
    this.lineParser = lineParser;
    this.propertyProcessor = propertyProcessor;
  }

  @Override
  public ComfyConfig read(InputStream inputStream) {
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));
      String line = null;
      String currentSection = null;
      while ((line = bufferedReader.readLine()) != null) {
        LineTokens tokens = lineParser.parse(line);
        if (tokens == null) {
          LOG.debug("An empty or comment line was detected, it will be ignored.");
          continue;
        }

        String sectionName = tokens.getSectionName();
        String override = tokens.getOverride();

        if (sectionName != null) {
          currentSection = sectionName;
          continue;
        } else if (currentSection == null) {
          throw new ComfyException(String
              .format("Line found that is not under any section. Please move this line under a section: {%s}", line));
        }

        if (override != null && !overrides.contains(override)) {
          LOG.debug("The override: {} is not selected. So, this line will be skipped.", override);
          continue;
        }

        String key = tokens.getKey();
        String value = tokens.getValue();

        ConfigValue<?> configValue = propertyProcessor.process(value);
        comfyConfig.put(currentSection, key, configValue);
      }
      return comfyConfig;
    } catch (IOException e) {
      throw new ComfyException(String.format("Failed to read from inputStream."), e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          LOG.error("Failed to close inputstream.", e);
        }
      }
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException e) {
          LOG.error("Failed to close bufferedReader.", e);
        }
      }
    }

  }

}
