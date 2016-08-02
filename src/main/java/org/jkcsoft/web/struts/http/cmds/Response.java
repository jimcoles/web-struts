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
 * A simple, generic response object that can hold a list of .Error objects
 * and the return object of a command call.  Good to make this the return type
 * of a command get( ) or put( ) method.  Makes up for the fact that with Java
 * you can't have a called method create and set the value of a call parameter.
 *
 * @author Jim Coles
 * @version 1.0
 */
public class Response implements Serializable
{
  private ErrorList _errors = null;
  private Object    _data = null;

  public Response() {
  }

  // <getters>
  public boolean hasErrors() {
    return (_errors != null);
  }
  public ErrorList getErrors() {
    return _errors;
  }
  public Object getData() {
    return _data;
  }
  // </getters>

  // <setters>
  public void setData(Object data) {
    _data = data;
  }
  public void addError(CmdError error) {
    if (_errors == null) { _errors = new ErrorList(); }
    _errors.addError(error);
  }
  // </setters>
  //
}