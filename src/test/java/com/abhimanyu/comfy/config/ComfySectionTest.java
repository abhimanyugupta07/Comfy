package com.abhimanyu.comfy.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.abhimanyu.comfy.value.ArrayValue;
import com.abhimanyu.comfy.value.BooleanValue;
import com.abhimanyu.comfy.value.IntValue;
import com.abhimanyu.comfy.value.StringValue;

@TestInstance(Lifecycle.PER_CLASS)
public class ComfySectionTest {

  private ComfySection comfySection;

  @BeforeAll
  public void init() {
    comfySection = new ComfySection();
    comfySection.put("key1", new StringValue("test_string"));
    comfySection.put("key2", new BooleanValue(true));
    comfySection.put("key3", new ArrayValue(new String[] { "a", "b", "c" }));
    comfySection.put("key4", new IntValue(10));
  }

  @Test
  public void stringValue() {
    assertThat(comfySection.getStringValue("key1")).isEqualTo("test_string");
  }

  @Test
  public void stringValueDoesNotExist() {
    assertThat(comfySection.getStringValue("non-existent-key")).isNull();
  }

  @Test
  public void tryToGetStringAsBoolean() {
    ClassCastException exception = assertThrows(ClassCastException.class, () -> {
      comfySection.getBooleanValue("key1");
    });

    assertTrue(exception
        .getMessage()
        .contains(String.format("Trying to convert an object of %s to a BooleanValue.", StringValue.class)));
  }

  @Test
  public void booleanValue() {
    assertThat(comfySection.getBooleanValue("key2")).isEqualTo(true);
  }

  @Test
  public void booleanValueDoesNotExist() {
    assertThat(comfySection.getBooleanValue("non-existent-key")).isNull();
  }

  @Test
  public void tryToGetBooleanAsStringValue() {
    ClassCastException exception = assertThrows(ClassCastException.class, () -> {
      comfySection.getStringValue("key2");
    });

    assertTrue(exception
        .getMessage()
        .contains(String.format("Trying to convert an object of %s to a StringValue.", BooleanValue.class)));
  }

  @Test
  public void arrayValue() {
    assertThat(comfySection.getArrayValue("key3")).isEqualTo(new String[] { "a", "b", "c" });
  }

  @Test
  public void arrayValueDoesNotExist() {
    assertThat(comfySection.getArrayValue("non-existent-key")).isNull();
  }

  @Test
  public void tryToGetArrayAsIntValue() {
    ClassCastException exception = assertThrows(ClassCastException.class, () -> {
      comfySection.getIntValue("key3");
    });

    assertTrue(exception
        .getMessage()
        .contains(String.format("Trying to convert an object of %s to a IntValue.", ArrayValue.class)));
  }

  @Test
  public void intValue() {
    assertThat(comfySection.getIntValue("key4")).isEqualTo(10);
  }

  @Test
  public void intValueDoesNotExist() {
    assertThat(comfySection.getIntValue("non-existent-key")).isNull();
  }

  @Test
  public void tryToGetIntAsArrayValue() {
    ClassCastException exception = assertThrows(ClassCastException.class, () -> {
      comfySection.getArrayValue("key4");
    });

    assertTrue(exception
        .getMessage()
        .contains(String.format("Trying to convert an object of %s to a ArrayValue.", IntValue.class)));
  }

  @Test
  public void size() {
    assertThat(comfySection.size()).isEqualTo(4);
  }

}
