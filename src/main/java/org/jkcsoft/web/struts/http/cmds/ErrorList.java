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
import java.util.List;
import java.util.Vector;

/**
 * Add type safe methods to java.util.List for .Errors.
 */
public class ErrorList extends Vector implements List, Serializable
{
  public ErrorIterator errorIterator() {
    return new ErrorIterator(this.iterator());
  }
  public void addError(CmdError error) {
    this.add(error);
  }
}