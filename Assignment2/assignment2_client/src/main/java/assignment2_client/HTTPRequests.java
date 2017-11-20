package assignment2_client;


import bsdsass2testdata.RFIDLiftData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HTTPRequests {
    private String ec2 = "ec2-54-149-250-99.us-west-2.compute.amazonaws.com";
    private String ec2_1 = "ec2-54-191-27-28.us-west-2.compute.amazonaws.com";
    private String ec2_2 = "ec2-54-218-49-20.us-west-2.compute.amazonaws.com";
    private String load_balancer = "LB-1-26347b388ec587e9.elb.us-west-2.amazonaws.com";
    private String ipAddress = load_balancer;
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
        String localPath = "http://localhost:8484/webapi/myresource";
        String awsPath = "http://" + ipAddress + ":" + port + "/assignment2_server/webapi/myresource";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client
                .target(localPath);
        return webTarget;
    }

}
