package com.abhimanyu.comfy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.abhimanyu.comfy.config.ComfyConfig;
import com.abhimanyu.comfy.exception.ComfyException;
import com.abhimanyu.comfy.io.InputReader;
import com.abhimanyu.comfy.io.ini.ConfigFileReader;
import com.abhimanyu.comfy.parse.FilteringLineParser;
import com.abhimanyu.comfy.parse.INIPropertyProcessor;
import com.abhimanyu.comfy.parse.LineParser;
import com.abhimanyu.comfy.parse.PropertyProcessor;

public class Comfy {

  public static ComfyConfig loadConfig(String file, String[] overrides) {

    Path filePath = Paths.get(file);
    LineParser lineParser = new FilteringLineParser();
    PropertyProcessor propertyProcessor = new INIPropertyProcessor();
    InputReader configFileReader = new ConfigFileReader(lineParser, propertyProcessor, overrides);
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(filePath.toFile());
    } catch (FileNotFoundException e) {
      throw new ComfyException(String.format("Failed to find file on path: %s", filePath.toAbsolutePath()), e);
    }

    return configFileReader.read(inputStream);
  }

  public static void main(String[] args) {
    ComfyConfig config = Comfy.loadConfig(args[0], new String[] { "production", "ubuntu" });

    System.out
        .println("config.get(\"common\").getIntValue(\"paid_users_size_limit\") = "
            + config.get("common").getIntValue("paid_users_size_limit"));
    System.out.println("config.get(\"ftp\").getStringValue(\"name\") = " + config.get("ftp").getStringValue("name"));
    System.out
        .println("config.get(\"ftp\").getStringValue(\"lastname\") = " + config.get("ftp").getStringValue("lastname"));
    System.out
        .println("config.get(\"http\").getArrayValue(\"params\") = " + config.get("http").getArrayValue("params"));
    System.out
        .println("config.get(\"ftp\").getBooleanValue(\"enabled\") = " + config.get("ftp").getBooleanValue("enabled"));
    System.out.println("config.get(\"ftp\").getStringValue(\"path\") = " + config.get("ftp").getStringValue("path"));
    System.out.println("config.get(\"ftp\") = " + config.get("ftp"));
  }
}
