package test.edu.xiyang;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class ConfigInfo {
    public int threadNum = 10;
    public int iterationNum = 100;
    private String ipAddress = "34.215.58.78";
    private String port = "8080";
    public WebTarget webTarget;

    public ConfigInfo() {
        webTarget = getWebTarget();
    }

    private WebTarget getWebTarget() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + ipAddress + ":" + port).path("bsds-server/webapi/target1");
        return webTarget;
    }
}
