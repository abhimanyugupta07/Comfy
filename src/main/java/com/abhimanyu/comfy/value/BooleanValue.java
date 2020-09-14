package com.abhimanyu.comfy.value;

public class BooleanValue implements ConfigValue<Boolean> {
  private final Boolean value;

  public BooleanValue(Boolean value) {
    this.value = value;
  }

  @Override
  public Boolean getValue() {
    return value;
  }
}
