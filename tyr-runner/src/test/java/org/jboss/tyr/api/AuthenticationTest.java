package org.jboss.tyr.api;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.tyr.JaxRsApplication;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URL;

//@RunWith(Arquillian.class)
//@RunAsClient
public class AuthenticationTest {

    @Inject
    GitHubAPI git;

    private static Client restClient;
    private static final String RETURN_STATUS_UNAUTHORIZED_PATH = "/fakeGithub/ReturnStatusUnauthorized";
    private static final String RETURN_STATUS_INTERNAL_SERVER_ERROR_PATH = "/fakeGithub/ReturnStatusInternalServerError";

//    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ShrinkWrap
                .create(JavaArchive.class, AuthenticationTest.class.getSimpleName() + ".jar")
                .addClasses(FakeGitHub.class, JaxRsApplication.class);
    }

//    @ArquillianResource
    private URL deploymentUrl;

    @BeforeClass
    public static void setUp() {
        restClient = ClientBuilder.newClient();
    }

//    @Test (expected=IllegalArgumentException.class)
    public void testAuthenticationStatusUnauthorized() {
        Assert.assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), getEndPointResponse(RETURN_STATUS_UNAUTHORIZED_PATH).getStatus());
        testAuthentication(RETURN_STATUS_UNAUTHORIZED_PATH);
    }

//    @Test (expected=IllegalArgumentException.class)
    public void testAuthenticationStatusInternalServerError() {
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), getEndPointResponse(RETURN_STATUS_INTERNAL_SERVER_ERROR_PATH).getStatus());
        testAuthentication(RETURN_STATUS_INTERNAL_SERVER_ERROR_PATH);
    }

    private Response getEndPointResponse(String endpointPath) {
        WebTarget target = restClient.target(UriBuilder.fromPath(deploymentUrl.toString()).path(endpointPath).build());

        return target.request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "token ")
                .get();
    }

    private void testAuthentication(String endpointPath) {
        git.getJSONReader(UriBuilder.fromPath(deploymentUrl.toString()).path(endpointPath).build());
    }

    @AfterClass
    public static void afterClass() {
        if (restClient != null) {
            restClient.close();
        }
    }
}
