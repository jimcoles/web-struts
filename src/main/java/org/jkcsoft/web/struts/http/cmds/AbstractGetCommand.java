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
import com.jkc.jdbc.JdbcUtil;
import org.w3c.dom.Document;

import java.sql.Connection;

/**
 * Good base class for simple (non-EJB) 'get' commands.
 *
 * @author Jim Coles
 * @version 1.0
 */
public abstract class AbstractGetCommand extends AbstractCommand
{
  public AbstractGetCommand(WebApp app)
  {
    super(app);
  }

  public Document getDocFromSQL(Connection conn, String strSql, String name)
    throws Exception
  {
    return JdbcUtil.getDocFromSQL(conn, strSql, name, getApp().getDbf());
  }
}