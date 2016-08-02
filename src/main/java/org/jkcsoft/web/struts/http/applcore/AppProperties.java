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

import java.util.*;  // Properties, etc.
import javax.servlet.ServletContext;
import java.io.*;

/**
 * Essentially just wraps a java.util.Properties object.  Allows initialization
 * from a javax.servlet.ServletContext or a java.util.Properties object.
 * Adds some methods over-and-above .Properties to get properties for a given
 * class with naming convention that the class name is the prefix for the
 * properties.
 */
public class AppProperties implements Serializable
{
  //--------------------------------------------------------------------
  // Privates instance vars
  //--------------------------------------------------------------------
  private Properties _props = null;

  //--------------------------------------------------------------------
  // Constructor(s)
  //--------------------------------------------------------------------
  public AppProperties(ServletContext app) throws Exception
  {
    _props = new Properties();
    Enumeration enames = app.getInitParameterNames();
    if (enames != null) {
      while (enames.hasMoreElements()) {
        Object name = enames.nextElement();
        Object value = app.getInitParameter(name.toString());
        _props.put(name, value);
      }
    }
    _debug();
  }

  public AppProperties(Properties props) throws Exception
  {
    _props = props;
    _debug();
  }

  private void _debug()
  {
// <debug>
//    Enumeration eprops = _props.propertyNames();
//    while(eprops.hasMoreElements()) {
//      String name = (String) eprops.nextElement();
//      System.out.println("App property name='"+name+ "' value='" + _props.getProperty(name)+"'");
//    }
// </debug>
  }
  //--------------------------------------------------------------------
  // Public instance methods
  //--------------------------------------------------------------------

  /** Simple pass-thru to Properties object */
  public String getProperty(String parm)
  {
    return _props.getProperty(parm);
  }

  /**
   * Convenience method for getting properties of a specific class.  Useful
   * for service oriented consuming classes wanting to set configuration defaults.
   */
  public String getProperty(Class cls, String parm)
  {
    return getProperty(cls.getName() + "." + parm);
  }

  /**
   * Nice classes will only call this once and then cache the Properties.
   * Motivated by the javamail framework which expects a set of Properties
   * at initialization.
   */
  public Properties getPropertiesByClassname(Class cls) throws Exception
  {
    String clsname = cls.getName();
    Properties retVal = new Properties();
    Iterator iprops = _props.keySet().iterator();
    while (iprops.hasNext()) {
      String propname = (String) iprops.next();
      if (propname.startsWith(clsname)) {
        retVal.put(propname.substring(clsname.length()+1), _props.get(propname));
      }
    }
    return retVal;
  }

  public boolean containsKey(String key) {
    return _props.containsKey(key);
  }
}
