package com.abhimanyu.comfy.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.abhimanyu.comfy.exception.ComfyException;
import com.abhimanyu.comfy.value.ArrayValue;
import com.abhimanyu.comfy.value.BooleanValue;
import com.abhimanyu.comfy.value.ConfigValue;
import com.abhimanyu.comfy.value.IntValue;
import com.abhimanyu.comfy.value.StringValue;

public class INIPropertyProcessorTest {
  PropertyProcessor propertyProcessor = new INIPropertyProcessor();

  @Test
  public void typicalStringValue() {
    ConfigValue<?> configValue = propertyProcessor.process("\"hello there, ftp uploading\"");

    assertThat(configValue).isInstanceOf(StringValue.class);
    assertThat(configValue.getValue()).isEqualTo("hello there, ftp uploading");
  }

  @Test
  public void typicalStringValueWithWhiteSpaces() {
    ConfigValue<?> configValue = propertyProcessor.process("     \"hello there, ftp uploading\"");

    assertThat(configValue).isInstanceOf(StringValue.class);
    assertThat(configValue.getValue()).isEqualTo("hello there, ftp uploading");
  }

  @Test
  public void typicalTextValue() {
    ConfigValue<?> configValue = propertyProcessor.process("/etc/var/uploads");

    assertThat(configValue).isInstanceOf(StringValue.class);
    assertThat(configValue.getValue()).isEqualTo("/etc/var/uploads");
  }

  @Test
  public void textValueWithSpecialChars() {
    ConfigValue<?> configValue = propertyProcessor.process("/etc*****var*(@uploads");

    assertThat(configValue).isInstanceOf(StringValue.class);
    assertThat(configValue.getValue()).isEqualTo("/etc*****var*(@uploads");
  }

  @Test
  public void typicalIntegerValue() {
    ConfigValue<?> configValue = propertyProcessor.process("  52428800");

    assertThat(configValue).isInstanceOf(IntValue.class);
    assertThat(configValue.getValue()).isEqualTo(52428800);
  }

  @Test
  public void negativeIntegerValue() {
    ConfigValue<?> configValue = propertyProcessor.process("  -52428800");

    assertThat(configValue).isInstanceOf(IntValue.class);
    assertThat(configValue.getValue()).isEqualTo(-52428800);
  }

  @Test
  public void typicalBooleanValue() {
    ConfigValue<?> yes = propertyProcessor.process("yes");
    ConfigValue<?> no = propertyProcessor.process("no");
    ConfigValue<?> trueVal = propertyProcessor.process("true");
    ConfigValue<?> falseVal = propertyProcessor.process("false");

    // This is ambiguous requirement. So, I am not treating 0 and 1 as booleans but as integers.
    ConfigValue<?> one = propertyProcessor.process("1");
    ConfigValue<?> zero = propertyProcessor.process("0");

    assertThat(yes).isInstanceOf(BooleanValue.class);
    assertThat(no).isInstanceOf(BooleanValue.class);
    assertThat(trueVal).isInstanceOf(BooleanValue.class);
    assertThat(falseVal).isInstanceOf(BooleanValue.class);

    assertThat(one).isInstanceOf(IntValue.class);
    assertThat(zero).isInstanceOf(IntValue.class);

    assertThat(yes.getValue()).isEqualTo(true);
    assertThat(no.getValue()).isEqualTo(false);
    assertThat(trueVal.getValue()).isEqualTo(true);
    assertThat(falseVal.getValue()).isEqualTo(false);

    assertThat(one.getValue()).isEqualTo(1);
    assertThat(zero.getValue()).isEqualTo(0);
  }

  @Test
  public void returnsStringWhenBooleanIsPassedAsString() {
    ConfigValue<?> configValue = propertyProcessor.process("\"no\"");

    assertThat(configValue).isInstanceOf(StringValue.class);
    assertThat(configValue.getValue()).isEqualTo("no");
  }

  @Test
  public void typicalArrayValue() {
    ConfigValue<?> configValue = propertyProcessor.process("array,  of,values");

    assertThat(configValue).isInstanceOf(ArrayValue.class);
    assertThat(configValue.getValue()).isEqualTo(new String[] { "array", "of", "values" });
  }

  @Test
  public void arrayValueWithOpenCommas() {
    ComfyException exception = assertThrows(ComfyException.class, () -> {
      propertyProcessor.process("array, of,");
    });

    assertTrue(exception.getMessage().contains("Value type not supported: "));
  }

  @Test
  public void stringValueWithOpenCommasAfterFirstWordAndNoSpaceInBetween() {
    ConfigValue<?> configValue = propertyProcessor.process("array, ");

    assertThat(configValue).isInstanceOf(StringValue.class);
    assertThat(configValue.getValue()).isEqualTo("array,");
  }

  @Test
  public void malformedValue() {
    ComfyException exception = assertThrows(ComfyException.class, () -> {
      propertyProcessor.process("hello there");
    });

    assertTrue(exception.getMessage().contains("Value type not supported: "));
  }

  @Test
  public void emptyValue() {
    ComfyException exception = assertThrows(ComfyException.class, () -> {
      propertyProcessor.process("   ");
    });

    assertTrue(exception.getMessage().contains("Found null or empty value: "));
  }

  @Test
  public void nullValue() {
    ComfyException exception = assertThrows(ComfyException.class, () -> {
      propertyProcessor.process(null);
    });

    assertTrue(exception.getMessage().contains("Found null or empty value: "));
  }
}
