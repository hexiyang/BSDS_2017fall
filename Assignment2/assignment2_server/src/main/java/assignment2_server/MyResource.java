package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
        return "Got it!";
    }

    @GET
    @Path("myvert")
    @Produces(MediaType.TEXT_PLAIN)
    public String fetchBy(@QueryParam("skierID") int skierID, @QueryParam("dayNum") int dayNum) {

        HashMap<Integer,Integer> cachedLift = (HashMap<Integer, Integer>) context.getAttribute("cachedLift");
        HashMap<Integer,Integer> cachedVertical = (HashMap<Integer, Integer>) context.getAttribute("cachedVertical");
        return "\tThe liftSum for skierID " + skierID + " is " + cachedLift.get(skierID) +
                ", its total vertical today is " + cachedVertical.get(skierID) + "m";
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addRecord(RFIDLiftData rfidLiftData) throws Exception{
        HashMap<Integer,Integer> cachedLift = (HashMap<Integer, Integer>) context.getAttribute("cachedLift");
        HashMap<Integer,Integer> cachedVertical = (HashMap<Integer, Integer>) context.getAttribute("cachedVertical");
        int skierID = rfidLiftData.getSkierID();
        int liftID = rfidLiftData.getLiftID();
        int vertical = 0;
        if (liftID < 11) {
            vertical=200;
        } else if (liftID < 21) {
            vertical = 300;
        } else if (liftID < 31) {
            vertical = 400;
        } else {
            vertical = 500;
        }
        if (cachedLift.containsKey(rfidLiftData.getSkierID())) {
            int temp = cachedLift.get(skierID);
            cachedLift.put(skierID, liftID+temp);
        } else {
            cachedLift.put(skierID, liftID);
        }
        if (cachedVertical.containsKey(rfidLiftData.getSkierID())) {
            int temp = cachedVertical.get(skierID);
            cachedVertical.put(skierID, vertical+temp);
        } else {
            cachedVertical.put(skierID, vertical);
        }
        List<RFIDLiftData> cachedList = (List<RFIDLiftData>) context.getAttribute("cachedList");
        int chunkSize = (int) context.getAttribute("chunkSize");
        cachedList.add(rfidLiftData);
        if (cachedList.size() >= chunkSize) {
            context.setAttribute("cachedList", new ArrayList<RFIDLiftData>());
            SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
            int successNum = skierDAO.loadRecords(cachedList);
            return "post added, successful loaded data, successNum is " + successNum;
        }
        return "\tpost added, skierID is " + rfidLiftData.getSkierID();
    }

    @GET
    @Path("endLoading")
    @Produces(MediaType.TEXT_PLAIN)
    public String endLoading() {
        List<RFIDLiftData> cachedList = (List<RFIDLiftData>) context.getAttribute("cachedList");
        context.setAttribute("cachedList", new ArrayList<RFIDLiftData>());
        SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
        int successNum = skierDAO.loadRecords(cachedList);
        return "endLoding, finish the remaining " + cachedList.size() + " records, successNum is " + successNum;
    }

    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        List<RFIDLiftData> cachedList = (List<RFIDLiftData>) context.getAttribute("cachedList");
        int size = cachedList.size();
        int dayNum = (int) context.getAttribute("dayNum");
        int chunkSize = (int) context.getAttribute(("chunkSize"));
        return "There are " + size + " records in the cachedList, dayNum is "+ dayNum +
                ", chunkSize is " +chunkSize;
    }

    @POST
    public String postDayNum(@QueryParam("dayNum") int dayNum, @QueryParam("chunkSize") int chunkSize) {
        context.setAttribute("cachedList", new ArrayList<RFIDLiftData>());
        context.setAttribute("dayNum", dayNum);
        context.setAttribute("chunkSize", chunkSize);
        return "Successfully set the dayNum "+ dayNum + " and chunkSize " + chunkSize;
    }

    @POST
    @Path("test")
    @Consumes(MediaType.APPLICATION_JSON)
    public String testGet(RFIDLiftData rfidLiftData) {
        List<RFIDLiftData> processList = new ArrayList<>();
        SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
        processList.add(rfidLiftData);
        skierDAO.loadRecords(processList);
        return "Successfully added!";
    }

}
