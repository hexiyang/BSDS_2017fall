package assignment2_server;

import Dependencies.SkierInfo;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {
    @Context ServletContext context;

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it test";
    }

    @GET
    @Path("myvert")
    @Produces(MediaType.TEXT_PLAIN)
    public String fetchBy(@QueryParam("skierID") String skierID, @QueryParam("dayNum") int dayNum) {
        SkierInfo skierInfo = new SkierInfo();
        skierInfo.setDayNum(dayNum);
        skierInfo.setSkierID(skierID.toString());
        return "\tgot myvert";
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addRecord(SkierInfo skierInfo) throws Exception{
        List<SkierInfo> cachedList = (List<SkierInfo>) context.getAttribute("cachedList");
        int chunkSize = (int) context.getAttribute("chunkSize");
        cachedList.add(skierInfo);
        if (cachedList.size() >= chunkSize) {
            context.setAttribute("cachedList", new ArrayList<SkierInfo>());
            SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
            int successNum = skierDAO.loadRecords(cachedList);
            return "post added, successful loaded data, successNum is " + successNum;
        }
        return "\tpost added, skierID is " + skierInfo.getSkierID();
    }

    @GET
    @Path("endLoading")
    @Produces(MediaType.TEXT_PLAIN)
    public String endLoading() {
        List<SkierInfo> cachedList = (List<SkierInfo>) context.getAttribute("cachedList");
        context.setAttribute("cachedList", new ArrayList<SkierInfo>());
        SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
        int successNum = skierDAO.loadRecords(cachedList);
        return "endLoding, finish the remaining " + cachedList.size() + " records, successNum is " + successNum;
    }

    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        List<SkierInfo> cachedList = (List<SkierInfo>) context.getAttribute("cachedList");
        int size = cachedList.size();
        return "There are " + size + " records in the cachedList";
    }

    @POST
    public String postDayNum(@QueryParam("dayNum") int dayNum, @QueryParam("chunkSize") int chunkSize) {
        context.setAttribute("dayNum", dayNum);
        context.setAttribute("chunkSize", chunkSize);
        return "Successfully set the dayNum "+ dayNum + " and chunkSize " + chunkSize;
    }

    @POST
    @Path("test")
    @Consumes(MediaType.APPLICATION_JSON)
    public String testGet(SkierInfo skierInfo) {
        List<SkierInfo> processList = new ArrayList<>();
        SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
        processList.add(skierInfo);
        skierDAO.loadRecords(processList);
        return "Successfully added!";
    }
}
