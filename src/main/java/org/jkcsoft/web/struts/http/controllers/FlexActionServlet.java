/*
 * Copyright (c) Jim Coles (jameskcoles@gmail.com) 2016. through present.
 *
 * Licensed under the following license agreement:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Also see the LICENSE file in the repository root directory.
 */

package org.jkcsoft.web.struts.http.controllers;

import org.apache.struts.action.ActionServlet;
import org.jkcsoft.java.util.LogHelper;

import javax.servlet.ServletException;

/**
 * An SBC TSIT extension to the Struts ActionServlet.
 */
public class FlexActionServlet extends ActionServlet {
    public void init() throws ServletException {
        super.init();
        LogHelper.initForWeb();
        LogHelper.info(this, "Initialized app.");

//        try {
//            
//        } catch (Exception e) {
//            Log.error(this, "Initializing persistence layer", e);
//        }

    }
}
