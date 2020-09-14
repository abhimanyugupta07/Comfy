package com.abhimanyu.comfy.value;

public class IntValue implements ConfigValue<Integer> {
  private final Integer value;

  public IntValue(Integer value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }
}
