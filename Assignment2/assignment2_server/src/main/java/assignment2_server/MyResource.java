package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

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
        return "Sun Nov 19 22:57";
    }

    @GET
    @Path("myvert")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fetchBy(@QueryParam("skierID") int skierID, @QueryParam("dayNum") int dayNum) {
        Response.Status status = Response.Status.ACCEPTED;
        if (context.getAttribute("liftMap") == null || context.getAttribute("verticalMap") == null) {
            SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
            try {
                skierDAO.createDay1Cache(context);
            } catch (SQLException e) {
                status = Response.Status.BAD_REQUEST;
                e.printStackTrace();
            }
        }
        HashMap<Integer,Integer> liftMap = (HashMap<Integer, Integer>) context.getAttribute("liftMap");
        HashMap<Integer,Integer> verticalMap = (HashMap<Integer, Integer>) context.getAttribute("verticalMap");
        int liftSum = liftMap.get(skierID);
        int verticalSum = verticalMap.get(skierID);
        String result = "\tThe liftSum for skierID " + skierID + " is " + liftSum +
                ", its total vertical today is " + verticalSum + "m";
        return Response.status(status).entity(result).build();
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRecord(RFIDLiftData rfidLiftData) throws Exception{
        Queue<RFIDLiftData> queue = (Queue<RFIDLiftData>) context.getAttribute("processQueue");
        queue.add(rfidLiftData);
        String result = "post added, skierID is " + rfidLiftData.getSkierID();
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStatus() {
        List<RFIDLiftData> cachedList = (List<RFIDLiftData>) context.getAttribute("cachedList");
        Queue<List<RFIDLiftData>> queue = (Queue<List<RFIDLiftData>>) context.getAttribute("processQueue");
        int size = cachedList.size();
        int dayNum = (int) context.getAttribute("dayNum");
        int chunkSize = (int) context.getAttribute(("chunkSize"));
        String result =  "There are " + size + " records in the cachedList, dayNum is "+ dayNum +
                ", chunkSize is " +chunkSize + ", queue size is " + queue.size();
        return Response.ok().entity(result).build();
    }


    @POST
    @Path("setAttributes")
    public Response setAttributes(@QueryParam("dayNum") int dayNum, @QueryParam("chunkSize") int chunkSize) {
        context.setAttribute("cachedList", new ArrayList<RFIDLiftData>());
        context.setAttribute("dayNum", dayNum);
        context.setAttribute("chunkSize", chunkSize);
        String result = "Successfully set the dayNum "+ dayNum + " and chunkSize " + chunkSize;
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("test")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testGet(RFIDLiftData rfidLiftData) {
        List<RFIDLiftData> processList = new ArrayList<>();
        SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
        processList.add(rfidLiftData);
        skierDAO.loadRecords(processList);
        return Response.ok().entity("Successfully Added!").build();
    }

}
