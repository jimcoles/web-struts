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

public class EmailFieldValidator extends FieldValidator
{
  private String fieldName;

  /**
   *
   */
  public EmailFieldValidator(String field, String fieldCaption)
    throws Exception
  {
    this.fieldName = field;
    // set the caption
    setFieldCaption(fieldCaption);
    validate(field);
  }

  /**
   *
   */
  public String getFieldName()
  {
    return this.fieldName;
  }

  /**
   *
   */
  private void validate(String field)
  {
    boolean ok = false;
    if (JspUtil.isBlank(field))
    {
      errorCode = BLANK;
    }
    setValid(ok);
    return;
  }
}