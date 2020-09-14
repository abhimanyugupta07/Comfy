package com.abhimanyu.comfy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.abhimanyu.comfy.config.ComfyConfig;
import com.abhimanyu.comfy.exception.ComfyException;

/*** Integration Test for Comfy */

public class ComfyTest {

  @TempDir
  File tempDirectory;

  private File configFile;

  @BeforeEach
  public void init() throws IOException {

    configFile = new File(tempDirectory, "test.ini");
    List<String> rows = Arrays
        .asList("; this is the config file format your code should accept.", "[common]", "basic_size_limit= 26214400",
            "student_size_limit = 52428800", " paid_users_size_limit =  2147483647", "path = /srv/var/tmp/",
            "path<itscript> = /srv/tmp/", "\n",

            "[ftp]", "name = \"hello there, ftp uploading\"", "path = /tmp/", "path<production> = /srv/var/tmp/",
            "path<staging> = /srv/uploads/", "path<ubuntu> = /etc/var/uploads", "enabled = no", "; This is a comment",
            "\n",

            "[http]", "name =  \"http uploading\"", "path =   /tmp/", "path<production> = /srv/var/tmp/",
            "path<staging> = /srv/uploads/; This is another comment", "params = array,  of,values");
    Files.write(configFile.toPath(), rows);
  }

  @Test
  public void typical() {
    ComfyConfig config = Comfy.loadConfig(configFile.getAbsolutePath(), new String[] { "production", "ubuntu" });

    assertThat(config.get("common").getIntValue("paid_users_size_limit")).isEqualTo(2147483647);
    assertThat(config.get("ftp").getStringValue("name")).isEqualTo("hello there, ftp uploading");
    assertThat(config.get("ftp").getStringValue("lastname")).isNull();
    assertThat(config.get("http").getArrayValue("params")).isEqualTo(new String[] { "array", "of", "values" });
    assertThat(config.get("ftp").getBooleanValue("enabled")).isEqualTo(false);
    assertThat(config.get("ftp").getStringValue("path")).isEqualTo("/etc/var/uploads");
  }

  @Test
  public void fileDoesNotExist() {
    ComfyException exception = assertThrows(ComfyException.class, () -> {
      Comfy.loadConfig("/path/does/not/exist/file.ini", new String[] { "production", "ubuntu" });
    });

    assertTrue(exception.getMessage().contains("Failed to find file on path: /path/does/not/exist/file.ini"));
  }

}
