/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.on36.haetae.rpc.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum ResultType implements org.apache.thrift.TEnum {
  SUCCESS(1),
  FAILURE(0);

  private final int value;

  private ResultType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static ResultType findByValue(int value) { 
    switch (value) {
      case 1:
        return SUCCESS;
      case 0:
        return FAILURE;
      default:
        return null;
    }
  }
}