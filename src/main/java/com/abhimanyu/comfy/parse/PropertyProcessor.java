package com.abhimanyu.comfy.parse;

import com.abhimanyu.comfy.value.ConfigValue;

public interface PropertyProcessor {
  ConfigValue process(String property);
}
