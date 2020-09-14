package com.abhimanyu.comfy.config;

import java.util.HashMap;
import java.util.Map;

import com.abhimanyu.comfy.value.ConfigValue;

public class ComfyConfig {
  private final Map<String, ComfySection> map;

  public ComfyConfig() {
    map = new HashMap<>();
  }

  public ComfySection get(String s) {
    return map.get(s);
  }

  public void put(String sectionName, ComfySection section) {
    map.put(sectionName, section);
  }

  public void put(String sectionName, String key, ConfigValue value) {
    ComfySection section = getOrDefault(sectionName);
    section.put(key, value);
    put(sectionName, section);
  }

  public ComfySection getOrDefault(String s) {
    return map.getOrDefault(s, new ComfySection());
  }

  public int size() {
    return map.size();
  }

}
