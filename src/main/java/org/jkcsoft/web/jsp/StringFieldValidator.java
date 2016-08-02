/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.jsp;

public class StringFieldValidator extends FieldValidator
  {

  private String fieldName;
  private int maxLength;
  private int minLength;

  public StringFieldValidator(String field, String fieldCaption,
                              int minLen, int maxLen) throws Exception
    {

    if (minLen > maxLen)
      {
      throw new Exception("minLength param must be <= maxLength param");
      }

    this.fieldName = field;
      // set the caption
    setFieldCaption(fieldCaption);
    this.minLength = minLen;
    this.maxLength = maxLen;

    validate(field, minLen, maxLen);
    }

  //*************************

  public String getFieldName()
    {
    return this.fieldName;
    }

  //*************************

  public int getMinLength()
    {
    return this.minLength;
    }

  //*************************

  public int getMaxLength()
    {
    return this.maxLength;
    }

  //*************************

  private void validate(String field, int minLen, int maxLen)
    {
    boolean ok = false;

    if (JspUtil.isBlank(field))
      {
      errorCode = BLANK;

      return;
      }

    if (field.length() < minLen)
      {
      errorCode = MIN_LENGTH_OUT_OF_BOUNDS;

      return;
      }

    else if (field.length() > maxLen)
      {
      errorCode = MAX_LENGTH_OUT_OF_BOUNDS;

      return;
      }

      // set valid
    //valid = true;
    setValid(true);

    } // end validate()

  }
