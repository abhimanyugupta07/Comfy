package com.abhimanyu.comfy.value;

public class StringValue implements ConfigValue<String> {
  private final String value;

  public StringValue(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
}
