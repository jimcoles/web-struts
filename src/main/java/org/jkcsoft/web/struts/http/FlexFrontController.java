/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.struts.http;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.jkcsoft.java.util.LogHelper;
import org.jkcsoft.java.systems.components.Application;


/**
 * A FLEX extension to the struts front controller
 * 
 * Responsibilities upon startup:
 * 
 * 1. Initialize the logging sub-system.
 * 3. Init app sub-systems
 */
public class FlexFrontController extends ActionServlet
{
    public Application theApp;

    public void init() throws ServletException
    {
        // init logging...
        LogHelper.initForWeb();

        // call super core Struts controller...
        super.init();

        // Start TSESS gateway thread
        try {
            LogHelper.info(this, "Starting init process for "+theApp.getDefaultProductName()
                +" ["+
                theApp.getVersion().getMajor() + "." +
                theApp.getVersion().getMinor()+ "; build " +
                theApp.getVersion().getBuild()+"]");
            //
            // TODO: init app subsystems...
        } catch (Exception e) {
            LogHelper.error(this, "While staring TSESS gateway instance", e);
        }

    }
}
