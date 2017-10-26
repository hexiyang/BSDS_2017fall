package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.HashMap;

@WebListener()
public class SkierListener implements ServletContextListener, ServletContextAttributeListener {

    SkierDAO skierDAO;
    ServletContext context;
    // Public constructor is required by servlet spec
    public SkierListener() {
        skierDAO = new SkierDAO();
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        context.setAttribute("skierDAO", skierDAO);
        context.setAttribute("cachedList", new ArrayList<RFIDLiftData>());
        context.setAttribute("cachedLift", new HashMap<Integer, Integer>());
        context.setAttribute("cachedVertical", new HashMap<Integer, Integer>());
        context.setAttribute("dayNum", 0);
        context.setAttribute("chunkSize", 1000);
        System.out.println("cacheList has been initialized!");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        skierDAO.destroyConnection();
    }

    public void attributeAdded(ServletContextAttributeEvent sce) {
        System.out.println(sce.getName() + " is added");
    }

    public void attributeRemoved(ServletContextAttributeEvent sce) {
        System.out.println(sce.getName() + " is removed");
    }

    public void attributeReplaced(ServletContextAttributeEvent sce) {
        System.out.println(sce.getName() + " is replaced");
    }

}
