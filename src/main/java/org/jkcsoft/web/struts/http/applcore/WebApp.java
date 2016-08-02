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

import com.jkc.xml.BuilderPool;
import com.jkc.util.Strings;

import javax.sql.DataSource;
import java.sql.Connection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.servlet.ServletContext;

//import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import java.util.*; // Properties, List, etc.

/**
 * An abstract base class for a TRI application including web applications.
 * Each application should create a sub-class of this and probably singleton
 * it.  A <code>WebApp</code> provides typical app resources such as database
 * connection and app configuration properties.  Also sets values for the
 * application Java package and page contoller
 * package used for dynamic page controller instantiation.  Default
 * page controller package = 'package of the WebApp' + ".controllers".
 */
public abstract class WebApp
{
  //---------------------------------------------------------------------------
  // Static vars and methods
  //---------------------------------------------------------------------------
  public static final String PROPKEY_DB_SERVERNAME = "www-db-servername";
  public static final String PROPKEY_DB_INSTNAME   = "www-db-instname";
  public static final String PROPKEY_DB_PORTNUM    = "www-db-port";
  public static final String PROPKEY_DB_USER       = "www-db-user";
  public static final String PROPKEY_DB_PASSWORD   = "www-db-pass";

//  public static final String PROPKEY_APP_PATH   = "www-app-path";

  private static final String PROPKEY_DBF = "javax.xml.parsers.DocumentBuilderFactory";
  private static final String PROPVAL_DBF = "org.apache.crimson.jaxp.DocumentBuilderFactoryImpl";

  //---------------------------------------------------------------------------
  // Instance vars
  //---------------------------------------------------------------------------
  private AppProperties           _props = null;
  private BuilderPool             _builderPool    = null;
  private DocumentBuilderFactory  _dbf = null;
  private DataSource              _dataSource      = null;
  private String                  _appPackageName = null;
  private String                  _controllerPackageName = null;
  private boolean                 _bInit = false;
  private ServletContext          _servletContext = null;
  private List                    _propInfos = null;

  //---------------------------------------------------------------------------
  // Constructors
  //---------------------------------------------------------------------------

  /**
   * Constructor.  Sets values for application package and page contoller
   * package used for dynamic page controller instantiation.  Default
   * page controller package is 'package of WebApp' + ".controllers".
   */
  public WebApp()
  {
    super();
    // set info on required properties to use for init validation.

    //
    if (getAppPackageName() == null) {
      Class cls = this.getClass();
      // Tried to use the following but it returns null for some reason -->
      // Package pkg = cls.getPackage();
      String clsname = cls.getName();
      _appPackageName = Strings.trimRightSide(clsname, ".");
    }
    if (getControllerPackageName() == null) {
      _controllerPackageName = getAppPackageName() + ".controllers";
    }
  }

  //---------------------------------------------------------------------------
  // Public methods
  //---------------------------------------------------------------------------

  /** Sub-class should override if it wants framework to validate config params. */
  public List getPropInfos() { return _propInfos; }

  public void init(ServletContext sc)
    throws Exception
  {
    _props = new AppProperties(sc);
    _servletContext = sc;
    this._init();
  }

  public void init(Properties props)
    throws Exception
  {
    _props = new AppProperties(props);
    this._init();
  }

  /**
   *  Returns the JDBC DataSource to use for this app. Consuming objects can
   *  call this method and not worry about HOW the datasource is created or
   *  even whether it is poolable or not.
   * <br><br>
   *  NOTE: Consuming objects must call Connection.close() after getting
   *  the connection, e.g., <br>
   *     <code>
   *     DataSource ds = JobsApp.getDataSource();
   *     Connection conn = ds.getConnection();
   *     try {
   *       (use connection)
   *     }
   *     finally {
   *       conn.close();
   *     }
   *     </code>
   */
  public DataSource getDataSource()
    throws Exception
  {
    if (_dataSource == null) {
      throw new Exception("Application data source is null.  Make sure WepApp initialization contains database connection parameters.");
    }
    return _dataSource;
  }

  /**
   *  Returns a JDBC Connection to use for this app.  This methods sets connection
   *  properties we want enforced such as setAutoCommit(false).  Consuming objects
   *  can call this method and not worry about HOW the connection is created or
   *  whether it is poolable or not.
   *
   *  NOTE: Consuming objects must call Connection.close() after using
   *  the connection, e.g.,
   *     Connection conn = WebApp.getInstance().getDBConnection();
   *     try {
   *       (use connection)
   *     }
   *     finally {
   *       conn.close();
   *     }
   */
  public Connection getDSConnection()
    throws Exception
  {
    Connection conn = null;
    if (_dataSource == null) {
      throw new Exception("Application data source is null.  Make sure WepApp initialization contains database connection parameters.");
    }
    conn = _dataSource.getConnection();
    conn.setAutoCommit(false);
    return conn;
  }

  /** Returns the XML DocumentBuilder pool to use for this app. */
  public BuilderPool getBuilderPool()
  {
    return _builderPool;
  }
  
  public DocumentBuilderFactory getDbf() { return _dbf; }

  /** Return the application properties object.  Typically contains config parameters. */
  public AppProperties getAppProps()
  {
    return _props;
  }

  public String getAppPath() {
    log("WARNING: Use of getAppPath() is bad.");
    return "";
  }
  public String getAppPackageName() { return _appPackageName; }
  public String getControllerPackageName() { return _controllerPackageName; }
  public boolean isInit() { return _bInit; }

  /** Sends message to appropriate logging system, usually the web server log. */
  public void log(String message) {
    if (_servletContext != null) {
      _servletContext.log(message);
    }
    else {
      System.out.println("Psuedo-log message: " + message);
    }
  }

  /** */
  public void log(String message, Throwable thr) {
    if (_servletContext != null) {
      _servletContext.log(message, thr);
    }
    else {
      System.out.println("Psuedo-log message: " + message);
      System.out.print("Psuedo-log stack trace: ");
      thr.printStackTrace();
    }
  }

  //--------------------------------------------------------------------------
  // Private methods
  //--------------------------------------------------------------------------

  private void _init() throws Exception
  {
    _propInfos = getPropInfos();

    // Validate app config properties
    if (_propInfos != null) {
      Iterator ipropinfos = _propInfos.iterator();
      if (ipropinfos != null) {
        boolean bErrs = false;
        while (ipropinfos.hasNext()) {
          PropertyInfo pi = (PropertyInfo) ipropinfos.next();
          String value = this.getAppProps().getProperty(pi.getProgId());
          if ((pi.isNonEmpty() | pi.isRequired()) & value == null) {
            bErrs = true;
            log("WARNING: Required application configuration parameter not specified.  Key='"+pi.getProgId()+"'.");
          }
          if (pi.isNonEmpty() & (value != null && value.length() == 0)) {
            bErrs = true;
            log("WARNING: Required configuration parameter exists but has an empty value.  Key='"+pi.getProgId()+"'.");
          }
        }
        if (bErrs) {
          throw new Exception("ERROR: Previous validation errors in application config parameters.  Ensure values are set in web.xml.");
        }
        else {
          log("INFO: Validated "+_propInfos.size()+" application configuration parameters.");
        }
      }
    }

    // init the XML builder factory for use in this app.
    DocumentBuilderFactory dbf = null;
    try {
      dbf = DocumentBuilderFactory.newInstance();
//      System.out.println("DBF Class: " + dbf.getClass().getName());
    }
    catch(javax.xml.parsers.FactoryConfigurationError ex) {
      System.out.println("Exception initializing JAXP document builder factory -->");
      ex.printStackTrace();
      System.out.println("\nInternal Exception -->");
      Exception ex1 = ex.getException();
      if( ex1 != null) {
        ex1.printStackTrace();
      }
    }
// <debug>
//    System.out.println("System property name='"+PROPKEY_DBF + "' value='" + System.getProperty(PROPKEY_DBF)+"'");
// </debug>
    dbf.setValidating(false);
    dbf.setIgnoringComments(true);
    // next three lines are new to jaxp 1.1
    dbf.setIgnoringElementContentWhitespace(true);
    dbf.setCoalescing(true);
    dbf.setExpandEntityReferences(false);
    _dbf = dbf;
    _builderPool = new BuilderPool(dbf);

    // This logic is specific to Oracle.
    // TODO: Make logic vendor independent; consider using JNDI.
    if (_dataSource == null) {

//      OracleConnectionPoolDataSource ods = new OracleConnectionPoolDataSource();
//      ods.setDriverType("thin");
//      ods.setNetworkProtocol("tcp");
      //
      AppProperties props = getAppProps();
      if(   props.containsKey(PROPKEY_DB_SERVERNAME)
         && props.containsKey(PROPKEY_DB_INSTNAME)
         && props.containsKey(PROPKEY_DB_PORTNUM)
         && props.containsKey(PROPKEY_DB_USER)
         && props.containsKey(PROPKEY_DB_PASSWORD))
      {
//        ods.setServerName(getAppProps().getProperty(PROPKEY_DB_SERVERNAME));
//        ods.setDatabaseName(getAppProps().getProperty(PROPKEY_DB_INSTNAME));
//        ods.setPortNumber(Integer.parseInt(getAppProps().getProperty(PROPKEY_DB_PORTNUM)));
//        ods.setUser(getAppProps().getProperty(PROPKEY_DB_USER));
//        ods.setPassword(getAppProps().getProperty(PROPKEY_DB_PASSWORD));
//        _dataSource = ods;
      }
      else {
        log("One or more database connection parameters not set.");
      }
    }
    // and finally...
    _bInit = true;
  }
}