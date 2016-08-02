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

import org.jkcsoft.java.util.LogHelper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Responsibilities:
 * - Do a smooth shutdown of sub-systems running in this context
 *
 * @author coles
 */
public class ShutdownServletContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
//        Log.info(this, "Received context init event for context ["+
//                event.getServletContext().getServletContextName()+"]");
        System.out.print("Received context init event for ["+event.getServletContext().getServletContextName()+"]");
    }

    public void contextDestroyed(ServletContextEvent event) {
        LogHelper.info(this, "Received context destroyed event for context ["+
                event.getServletContext().getServletContextName()+"];" +
                        "will try to shutdown smoothly");

        // TODO: shutdown sub-system nodes smoothly such as
        //       database connections and remote connections
        
//        TsessUpsGateway.getInstance().shutdownServer();
    }
    

}
