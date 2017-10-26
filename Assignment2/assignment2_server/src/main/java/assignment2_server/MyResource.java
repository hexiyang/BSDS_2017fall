package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
        return "Wed Oct 25 23:38";
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
        List<RFIDLiftData> cachedList = (List<RFIDLiftData>) context.getAttribute("cachedList");
        String result;
        int chunkSize = (int) context.getAttribute("chunkSize");
        cachedList.add(rfidLiftData);
        if (cachedList.size() >= chunkSize) {
            context.setAttribute("cachedList", new ArrayList<RFIDLiftData>());
            SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
            int successNum = skierDAO.loadRecords(cachedList);
            result =  "post added, successful loaded data, successNum is " + successNum;
        }
        result = "\tpost added, skierID is " + rfidLiftData.getSkierID();
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("endLoading")
    @Produces(MediaType.TEXT_PLAIN)
    public Response endLoading() {
        List<RFIDLiftData> cachedList = (List<RFIDLiftData>) context.getAttribute("cachedList");
        context.setAttribute("cachedList", new ArrayList<RFIDLiftData>());
        SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
        int successNum = skierDAO.loadRecords(cachedList);
        String result= "endLoding, finish the remaining " + cachedList.size() + " records, successNum is " + successNum;
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStatus() {
        List<RFIDLiftData> cachedList = (List<RFIDLiftData>) context.getAttribute("cachedList");
        int size = cachedList.size();
        int dayNum = (int) context.getAttribute("dayNum");
        int chunkSize = (int) context.getAttribute(("chunkSize"));
        String result =  "There are " + size + " records in the cachedList, dayNum is "+ dayNum +
                ", chunkSize is " +chunkSize;
        return Response.ok().entity(result).build();
    }

    @POST
    public Response postDayNum(@QueryParam("dayNum") int dayNum, @QueryParam("chunkSize") int chunkSize) {
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
