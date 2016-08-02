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

import com.jkc.applcore.*;

/**
 *
 * @author Jim Coles
 * @version 1.0
 */
public abstract class AbstractCommand
{
  private WebApp _app = null;

  public AbstractCommand(WebApp app)
  {
    _app = app;
  }

  /** Limit to sub-classes */
  protected WebApp getApp() { return _app; }
}