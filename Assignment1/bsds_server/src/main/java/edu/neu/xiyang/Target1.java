package edu.neu.xiyang;

import javax.print.attribute.standard.Media;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("target1")
public class Target1 {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it from target1!";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String postIt() {
        return "Posted it to target1!";
    }
}


