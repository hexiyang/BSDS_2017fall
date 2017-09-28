package neu.edu.xiyang;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class webTargetBuilder {
    public WebTarget getWebTarget(String ipAddress, String port, String path) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + ipAddress + ":" + port).path("bsds-server/webapi/" + path);
        return webTarget;
    }
}
