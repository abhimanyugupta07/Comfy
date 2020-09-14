package com.abhimanyu.comfy.io;

import java.io.InputStream;

import com.abhimanyu.comfy.config.ComfyConfig;

public interface InputReader {
  public ComfyConfig read(InputStream inputStream);
}
