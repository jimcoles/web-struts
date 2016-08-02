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
import java.util.Iterator;

public class ErrorIterator implements Iterator, Serializable
{
  // private impl delegatee
  private Iterator _iter = null;

  // Constructor
  public ErrorIterator(Iterator iter) {
    _iter = iter;
  }

  /** Typesafe next() */
  public CmdError nextError(){
    return (CmdError) _iter.next();
  }

  // <java.util.Iterator>
  public boolean hasNext() {
    return _iter.hasNext();
  }
  public Object next() {
    return _iter.next();
  }
  public void remove() {
    _iter.remove();
  }
  // </java.util.Iterator>
}