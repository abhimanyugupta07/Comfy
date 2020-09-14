package com.abhimanyu.comfy.value;

public class ArrayValue implements ConfigValue<String[]> {

  private final String[] value;

  public ArrayValue(String[] value) {
    this.value = value;
  }

  @Override
  public String[] getValue() {
    return value;
  }
}
