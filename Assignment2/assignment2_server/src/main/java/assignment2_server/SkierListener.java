package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.*;
import java.util.concurrent.*;

@WebListener()
public class SkierListener implements ServletContextListener, ServletContextAttributeListener {
    private ScheduledExecutorService scheduler;
    private SkierDAO skierDAO;
    private Queue<RFIDLiftData> processQueue;

    // Public constructor is required by servlet spec
    public SkierListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        skierDAO = new SkierDAO();
        int chunckSize = 5000;
        context.setAttribute("skierDAO", skierDAO);
        context.setAttribute("caching", false);
        processQueue = new LinkedBlockingQueue<RFIDLiftData>();
        context.setAttribute("processQueue", processQueue);
        System.out.println("processQueue has been initialized!");
        context.setAttribute("dayNum", 0);
        context.setAttribute("chunkSize", chunckSize);
        context.setAttribute("liftMap", new HashMap<>());
        context.setAttribute("verticalMap", new HashMap<>());
        System.out.println("maps have been initialized!");

        scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable loadTask = new ScanTimerTask(processQueue, skierDAO, chunckSize);
        scheduler.scheduleAtFixedRate(loadTask, 10, 3, TimeUnit.SECONDS);
        System.out.println("Task has been scheduled!");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        skierDAO.destroyConnection();
        scheduler.shutdown();
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
