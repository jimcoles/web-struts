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

import java.util.Vector;
import java.util.List;
import java.util.Hashtable;

public abstract class FieldValidator
  {

  /* ************************ */
  /* initialize default error codes and default error messages */

  public static final String BLANK = "blank";
  public static final String MAX_LENGTH_OUT_OF_BOUNDS = "max";
  public static final String MIN_LENGTH_OUT_OF_BOUNDS = "min";

  private static final Hashtable ERRMSGHASH = new Hashtable();

    // use to retrieve default error msgs if don't define your own
  static
    {
    ERRMSGHASH.put(BLANK, "field is blank");
    ERRMSGHASH.put(MAX_LENGTH_OUT_OF_BOUNDS, "field exceeds maximum length");
    ERRMSGHASH.put(MIN_LENGTH_OUT_OF_BOUNDS,
                                        "field is less than minimum length");
    }


  boolean valid = false;

  String fieldCaption = "";

  String errorCode = "";

  List errorMessages = new Vector();

  //*************************

  public boolean isValid()
    {
    return valid;
    }

  //*************************

  public String getErrorCode()
    {
    return errorCode;
    }

  //*************************

  public static String getMessage(String msgKey)
    {
    return (String) ERRMSGHASH.get(msgKey);
    }

  //*************************

  /**
    * setFieldCaption is used for building error messages.
    * it is the caption for the field that the end user sees
    *
    * @param fieldCaption name of the field caption
    *
    */

  public void setFieldCaption(String fieldCaption)
    {
    this.fieldCaption = fieldCaption;
    }

  //*************************

  /**
    * get the field caption name (used for building error messages)
    *
    * @return the field's caption
    */

  public String getFieldCaption()
    {
    return this.fieldCaption;
    }

  //*************************

  void setValid(boolean ok)
    {
    this.valid = ok;
    }
  }
