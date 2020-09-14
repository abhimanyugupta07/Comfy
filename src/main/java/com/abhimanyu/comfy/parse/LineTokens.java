package com.abhimanyu.comfy.parse;

public class LineTokens {
  private final String sectionName;
  private final String key;
  private final String value;
  private final String override;

  public LineTokens(String sectionName, String key, String value, String override) {
    this.sectionName = sectionName;
    this.key = key;
    this.value = value;
    this.override = override;
  }

  public String getSectionName() {
    return sectionName;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public String getOverride() {
    return override;
  }

}
