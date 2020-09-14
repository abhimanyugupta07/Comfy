package com.abhimanyu.comfy.config;

import java.util.HashMap;
import java.util.Map;

import com.abhimanyu.comfy.value.ArrayValue;
import com.abhimanyu.comfy.value.BooleanValue;
import com.abhimanyu.comfy.value.ConfigValue;
import com.abhimanyu.comfy.value.IntValue;
import com.abhimanyu.comfy.value.StringValue;

public class ComfySection {
  private final Map<String, ConfigValue> map;

  public ComfySection() {
    map = new HashMap<>();
  }

  public void put(String key, ConfigValue value) {
    map.put(key, value);
  }

  public int size() {
    return map.size();
  }

  public ConfigValue get(String s) {
    return map.get(s);
  }

  public String getStringValue(String s) {
    ConfigValue configValue = get(s);

    if (configValue == null) {
      return null;
    }

    if (configValue instanceof StringValue) {
      return (String) configValue.getValue();
    } else {
      throw new ClassCastException(
          String.format("Trying to convert an object of %s to a StringValue.", configValue.getClass()));
    }
  }

  public Integer getIntValue(String s) {
    ConfigValue configValue = get(s);

    if (configValue == null) {
      return null;
    }

    if (configValue instanceof IntValue) {
      return (Integer) configValue.getValue();
    } else {
      throw new ClassCastException(
          String.format("Trying to convert an object of %s to a IntValue.", configValue.getClass()));
    }
  }

  public Boolean getBooleanValue(String s) {
    ConfigValue configValue = get(s);

    if (configValue == null) {
      return null;
    }

    if (configValue instanceof BooleanValue) {
      return (Boolean) configValue.getValue();
    } else {
      throw new ClassCastException(
          String.format("Trying to convert an object of %s to a BooleanValue.", configValue.getClass()));
    }
  }

  public String[] getArrayValue(String s) {
    ConfigValue configValue = get(s);

    if (configValue == null) {
      return null;
    }

    if (configValue instanceof ArrayValue) {
      return (String[]) configValue.getValue();
    } else {
      throw new ClassCastException(
          String.format("Trying to convert an object of %s to a ArrayValue.", configValue.getClass()));
    }
  }

}
