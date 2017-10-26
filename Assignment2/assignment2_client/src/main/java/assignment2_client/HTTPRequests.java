package assignment2_client;


import bsdsass2testdata.RFIDLiftData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HTTPRequests {
    private String ipAddress = "34.215.58.78";
    private String port = "8080";
    public WebTarget webTarget;

    public HTTPRequests() {
        webTarget = getWebTarget();
    }

    public int getMyVert(int skierID, int dayNum){
        Response response = webTarget.path("myvert").queryParam("skierID", skierID)
                .queryParam("dayNum", dayNum).request(MediaType.TEXT_PLAIN).get(Response.class);
        System.out.println(response.readEntity(String.class));
        return response.getStatus();
    }

    public void postRecord(RFIDLiftData rfidLiftData){
        String response = webTarget.path("add").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(rfidLiftData, MediaType.APPLICATION_JSON), String.class);
        System.out.println(response);
    }

    public void endLoading() {
        String response = webTarget.path("endLoading").request().get(String.class);
        //System.out.println(response);
    }

    public void setAttributes(int dayNum, int chunkSize) {
        String response = webTarget.queryParam("dayNum", dayNum).queryParam("chunkSize", chunkSize)
                .request(MediaType.TEXT_PLAIN).post(Entity.entity(null, MediaType.TEXT_PLAIN), String.class);
        System.out.println(response);
    }

    private WebTarget getWebTarget() {
        String localPath = "http://localhost:8383/webapi/myresource";
        String awsPath = "http://" + ipAddress + ":" + port + "/assignment2_server/webapi/myresource";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client
                .target(awsPath);
        return webTarget;
    }

}
