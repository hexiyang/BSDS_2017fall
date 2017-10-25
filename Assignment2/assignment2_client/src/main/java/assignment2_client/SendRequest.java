package assignment2_client;

import Dependencies.SkierInfo;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class SendRequest {
    private String ipAddress = "34.215.58.78";
    private String port = "8080";
    public WebTarget postTarget;
    public WebTarget getTarget;

    public SendRequest() {
        getTarget = getWebTarget("myvert");
        postTarget = getWebTarget("add");
    }

    public void getMyVert(String skierID, int dayNum) throws Exception{
        String response = getTarget.queryParam("skierID", "123")
                .queryParam("dayNum", 1).request(MediaType.TEXT_PLAIN).get(String.class);
        System.out.println("getRequest is returning " + response);
    }

    public void postRecord(SkierInfo skierInfo) throws Exception{
        String response = postTarget.request(MediaType.APPLICATION_JSON)
                .post(Entity.json(skierInfo), String.class);
        System.out.println(response);
    }

    public void endLoading() {
        WebTarget target = getWebTarget("endLoading");
        String response = target.request(MediaType.TEXT_PLAIN).get(String.class);
        System.out.println(response);
    }

    public void setAttributes(int dayNum, int chunkSize) {
        WebTarget target = getWebTarget("");
        String response = target.queryParam("dayNum", dayNum).queryParam("chunkSize", chunkSize)
                .request(MediaType.TEXT_PLAIN).post(Entity.entity(null, MediaType.TEXT_PLAIN), String.class);
        System.out.println(response);
    }

    private WebTarget getWebTarget(String path) {
        //"http://localhost:8080/webapi/myresource"
        //.path("assignment2_server/webapi/myresource")
        //"http://" + ipAddress + ":" + port
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client
                .target("http://" + ipAddress + ":" + port).path("assignment2_server/webapi/myresource")
                .path(path);
        return webTarget;
    }

}
