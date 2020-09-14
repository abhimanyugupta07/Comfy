package com.abhimanyu.comfy.exception;

public class ComfyException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ComfyException(String message, Exception e) {
    super(message, e);
  }

  public ComfyException(String message, Throwable e) {
    super(message, e);
  }

  public ComfyException(String message) {
    super(message);
  }
}
