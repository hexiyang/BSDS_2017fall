package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

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
        return "Mon Nov 21 1:47";
    }

    @GET
    @Path("myvert")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getVert(@QueryParam("skierID") int skierID, @QueryParam("dayNum") int dayNum) {
        long startTime = System.nanoTime();
        Queue<Long> responseQueue = (Queue<Long>) context.getAttribute("responseQueue");
        Response.Status status = Response.Status.ACCEPTED;
        int DAY_NUM = (int) context.getAttribute("dayNum");
        boolean caching = (boolean) context.getAttribute("caching");
        if (dayNum != DAY_NUM) {
            context.setAttribute("dayNum", dayNum);
            context.setAttribute("caching", true);
            SkierDAO skierDAO = (SkierDAO) context.getAttribute("skierDAO");
            try {
                skierDAO.createDayCache(context, dayNum);
                context.setAttribute("caching", false);
            } catch (SQLException e) {
                status = Response.Status.BAD_REQUEST;
                e.printStackTrace();
            }
        } else if (caching) {
            while ((boolean) context.getAttribute("caching")){ }
        }
        HashMap<Integer,Integer> liftMap = (HashMap<Integer, Integer>) context.getAttribute("liftMap");
        HashMap<Integer,Integer> verticalMap = (HashMap<Integer, Integer>) context.getAttribute("verticalMap");
        String result;
        if (liftMap.get(skierID) == null) {
            result = "No such skier with skierID: " + skierID;
        } else {
            int liftSum = liftMap.get(skierID);
            int verticalSum = verticalMap.get(skierID);
            result = "The liftSum for skierID " + skierID + " is " + liftSum +
                    ", its total vertical today is " + verticalSum + "m";
        }
        responseQueue.add(System.nanoTime() - startTime);
        return Response.status(status).entity(result).build();
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRecord(RFIDLiftData rfidLiftData) throws Exception{
        long startTime = System.nanoTime();
        Queue<Long> responseQueue = (Queue<Long>) context.getAttribute("responseQueue");
        /*
        Add the incoming reccords to the cached queue
         */
        Queue<RFIDLiftData> queue = (Queue<RFIDLiftData>) context.getAttribute("processQueue");
        queue.add(rfidLiftData);
        String result = "post added, skierID is " + rfidLiftData.getSkierID();
        responseQueue.add(System.nanoTime() - startTime);
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStatus() {
        Queue<Long> responseQueue = (Queue<Long>) context.getAttribute("responseQueue");
        Queue<List<RFIDLiftData>> queue = (Queue<List<RFIDLiftData>>) context.getAttribute("processQueue");
        int dayNum = (int) context.getAttribute("dayNum");
        int chunkSize = (int) context.getAttribute(("chunkSize"));
        String result =  "There are " + queue.size() + " records in the process queue.\n" +
                "dayNum is "+ dayNum + "\n" +
                "chunkSize is " + chunkSize + "\n" +
                "responseQueue size: " + responseQueue.size() + "\n";
        return Response.ok().entity(result).build();
    }


    @POST
    @Path("setAttributes")
    public Response setAttributes(@QueryParam("dayNum") int dayNum, @QueryParam("chunkSize") int chunkSize) {
        context.setAttribute("dayNum", dayNum);
        context.setAttribute("chunkSize", chunkSize);
        String result = "Successfully set the dayNum "+ dayNum + " and chunkSize " + chunkSize;
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("clearCache")
    @Produces(MediaType.TEXT_PLAIN)
    public Response clearCache() {
        Queue<Long> responseQueue = (Queue<Long>) context.getAttribute("responseQueue");
        Queue<List<RFIDLiftData>> processQueue = (Queue<List<RFIDLiftData>>) context.getAttribute("processQueue");
        File file = (File) context.getAttribute("file");
        responseQueue.clear();
        processQueue.clear();
        file.delete();
        context.setAttribute("dayNum", 0);
        String result = "Successfully clear the cache";
        return Response.ok().entity(result).build();
    }

    /*
    typeCode:
    0: mean
    1: median
    2: 95th
    3: 99th
     */
    @GET
    @Path("getResponseTimeStats")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResponseStats() {
        File file = (File) context.getAttribute("file");
        String result = getResponseStatsHelper(file);
        return Response.ok().entity(result).build();
    }

    private String getResponseStatsHelper(File file) {
        List<Long> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(Long.valueOf(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Long mean = meanResponse(list);
        Long median = percentileResponse(list, 0.5);
        Long p95 = percentileResponse(list, 0.95);
        Long p99 = percentileResponse(list, 0.99);

        return mean.toString() + "::" +
                median.toString() + "::" +
                p95.toString() + "::" +
                p99.toString();
    }

    // Helper functions
    private long meanResponse (List<Long> list) {
        if (list.size() > 0) {
            long sum = 0;
            for (Long latency: list) {
                sum += latency;
            }
            return sum/list.size();
        } else {
            return 0;
        }

    }

    private long percentileResponse (List<Long> list, double percentile) {
        if (list.size() > 0) {
            Collections.sort(list);
            int index = (int)(percentile * list.size());
            return list.get(Math.max(index-1, 0));
        } else {
            return 0;
        }
    }

    @GET
    @Path("test")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response test(@QueryParam("value") Long value) {
        long startTime = System.nanoTime();
        Queue<Long> responseQueue = (Queue<Long>) context.getAttribute("responseQueue");
        responseQueue.add(value);
        System.out.println("variance: " + (System.nanoTime() - startTime));
        return Response.ok().entity("Successfully Added!").build();
    }

}
