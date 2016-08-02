/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */
package org.jkcsoft.web.struts.http.cmds;

import java.io.Serializable;

/**
 * Error message structure returned within the .Response returned by a call
 * to an remote method.
 */
public class CmdError implements Serializable
{
  private String _code = null;
  private String _message = null;

  public CmdError(String code, String message) {
    _code = code;
    _message = message;
  }

  public String getCode() {
    return _code;
  }

  public String getMessage() {
    return _message;
  }
}