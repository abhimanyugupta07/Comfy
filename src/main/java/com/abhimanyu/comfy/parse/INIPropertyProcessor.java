package com.abhimanyu.comfy.parse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abhimanyu.comfy.exception.ComfyException;
import com.abhimanyu.comfy.value.ArrayValue;
import com.abhimanyu.comfy.value.BooleanValue;
import com.abhimanyu.comfy.value.ConfigValue;
import com.abhimanyu.comfy.value.IntValue;
import com.abhimanyu.comfy.value.StringValue;
import com.abhimanyu.comfy.value.ValueType;

public class INIPropertyProcessor implements PropertyProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(INIPropertyProcessor.class);

  private static final Set<String> BOOLEAN_TRUE_VALUES = Stream
      .of("yes", "true", "1")
      .collect(Collectors.toCollection(HashSet::new));

  private static final Set<String> BOOLEAN_FALSE_VALUES = Stream
      .of("no", "false", "0")
      .collect(Collectors.toCollection(HashSet::new));

  private static final String STRING_REGEX = "\"(.+)*\"";
  private static final String TEXT_REGEX = "^\\S+$";
  private static final String NUMBER_REGEX = "^-?\\d+$";
  private static final String ARRAY_REGEX = "^[^,]+(,[^,]+)+$";

  private static final Pattern STRING_PATTERN = Pattern.compile(STRING_REGEX);
  private static final Pattern TEXT_PATTERN = Pattern.compile(TEXT_REGEX);
  private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);
  private static final Pattern ARRAY_PATTERN = Pattern.compile(ARRAY_REGEX);

  @Override
  public ConfigValue process(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new ComfyException(String.format("Found null or empty value: %s", value));
    }
    value = value.trim();
    ValueType valueType = detectValueType(value);

    switch (valueType) {
    case INT:
      return new IntValue(Integer.valueOf(value));
    case STRING:
      String actualString = value.substring(1, value.length() - 1);
      return new StringValue(actualString);
    case TEXT:
      if (BOOLEAN_TRUE_VALUES.contains(value.toLowerCase())) {
        return new BooleanValue(true);
      } else if (BOOLEAN_FALSE_VALUES.contains(value.toLowerCase())) {
        return new BooleanValue(false);
      } else {
        return new StringValue(value);
      }
    case ARRAY:
      String[] values = value.split(",");
      List<String> trimmedValues = Arrays.stream(values).map(x -> x.trim()).collect(Collectors.toList());
      return new ArrayValue(trimmedValues.toArray(new String[trimmedValues.size()]));
    default:
      throw new ComfyException(String.format("Value type not supported: %s", value));
    }
  }

  private ValueType detectValueType(String value) {
    Matcher stringMatcher = STRING_PATTERN.matcher(value);
    Matcher numberMatcher = NUMBER_PATTERN.matcher(value);
    Matcher arrayMatcher = ARRAY_PATTERN.matcher(value);
    Matcher textMatcher = TEXT_PATTERN.matcher(value);

    if (numberMatcher.matches()) {
      return ValueType.INT;
    } else if (stringMatcher.matches()) {
      return ValueType.STRING;
    } else if (arrayMatcher.matches()) {
      return ValueType.ARRAY;
    } else if (textMatcher.matches()) {
      return ValueType.TEXT;
    }

    return ValueType.NOT_SUPPORTED;
  }

}
