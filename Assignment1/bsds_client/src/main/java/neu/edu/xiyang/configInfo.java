package neu.edu.xiyang;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class configInfo {
    public int threadNum = 100;
    public int iterationNum = 100;
    public String ipAddress = "34.215.58.78";
    public String port = "8080";

    public WebTarget getWebTarget(String path) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + ipAddress + ":" + port).path("bsds-server/webapi/" + path);
        return webTarget;
    }
}
