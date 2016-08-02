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

public class DateFieldValidator extends FieldValidator
  {

  private String fieldName;

  public DateFieldValidator(String field, int minLen, int maxLen)
    throws Exception
    {

    if (minLen > maxLen)
      {
      throw new Exception("minLength param must be <= maxLength param");
      }

    //validate(field, minLen, maxLen);
    }

  //*************************

  public String getFieldName()
    {
    return this.fieldName;
    }

  //*************************

  private void validate(String field, int minLen, int maxLen)
    {
    boolean ok = true;
    StringBuffer msgBuf = new StringBuffer();

    if (JspUtil.isBlank(field))
      {
      ok = false;

      errorMessages.add(BLANK);

      return;
      }

    if (field.length() < minLen)
      {
      ok = false;

      errorMessages.add(MIN_LENGTH_OUT_OF_BOUNDS);
      }

    else if (field.length() > maxLen)
      {
      ok = false;

      errorMessages.add(MAX_LENGTH_OUT_OF_BOUNDS);
      }

    valid = ok;

    } // end validate()

  }
