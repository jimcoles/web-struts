/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.struts.http.applcore;

/**
 * Meta data for a configurable property.
 *
 * @author Jim Coles
 * @version 1.0
 */
public class PropertyInfo
{
  private String _progId = null;
  private boolean _isRequired = true;
  private boolean _isNonEmpty = true;

  public PropertyInfo(String progId, boolean isRequired, boolean isNonEmpty)
  {
    _progId = progId;
    _isRequired = isRequired;
    _isNonEmpty = isNonEmpty;
  }

  public String getProgId() { return _progId; }
  public boolean isRequired() { return _isRequired; }
  public boolean isNonEmpty() { return _isNonEmpty; }
}