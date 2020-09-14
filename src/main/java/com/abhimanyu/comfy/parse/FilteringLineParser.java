package com.abhimanyu.comfy.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abhimanyu.comfy.exception.ComfyException;

public class FilteringLineParser implements LineParser {
  private static final String GROUP_REGEX = "(\\[)([a-zA-Z0-9]+)(\\])";
  private static final String LINE_REGEX = "([^<>]+)\\s*=\\s*(.+)";
  private static final String LINE_OVERRIDE_REGEX = "([^<>]+)[<](.+)[>]\\s*[=](.+)";

  private static final Pattern SECTION_PATTERN = Pattern.compile(GROUP_REGEX);
  private static final Pattern LINE_PATTERN = Pattern.compile(LINE_REGEX);
  private static final Pattern LINE_OVERRIDE_PATTERN = Pattern.compile(LINE_OVERRIDE_REGEX);

  @Override
  public LineTokens parse(String line) {
    if (line == null || line.trim().isEmpty()) {
      return null;
    }

    line = line.trim();

    String filteredLine = removeComments(line);
    if (filteredLine.isEmpty()) {
      return null;
    }
    Matcher sectionMatcher = SECTION_PATTERN.matcher(filteredLine);
    Matcher lineMatcher = LINE_PATTERN.matcher(filteredLine);
    Matcher lineWithOverrideMatcher = LINE_OVERRIDE_PATTERN.matcher(filteredLine);

    String sectionName = null;
    String key = null;
    String value = null;
    String override = null;

    // most of the lines will be of this format. So testing line regex first and then lineWithOverride and finally
    // section.
    if (lineMatcher.matches()) {
      key = lineMatcher.group(1).trim();
      value = lineMatcher.group(2).trim();
    } else if (lineWithOverrideMatcher.matches()) {
      key = lineWithOverrideMatcher.group(1).trim();
      override = lineWithOverrideMatcher.group(2).trim();
      value = lineWithOverrideMatcher.group(3).trim();
    } else if (sectionMatcher.matches()) {
      sectionName = sectionMatcher.group(2).trim();
    } else {
      throw new ComfyException(String.format("Encountered an invalid line: %s", filteredLine));
    }

    return new LineTokens(sectionName, key, value, override);
  }

  private String removeComments(String s) {
    return s.split(";")[0];
  }
}
