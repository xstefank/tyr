package org.jboss.tyr.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/fakeGithub")
public class FakeGitHub {

    @GET
    @Path("/ReturnStatusUnauthorized")
    public Response returnStatusUnauthorized() {
        return Response.status(Response.Status.UNAUTHORIZED).entity(false).build();
    }

    @GET
    @Path("/ReturnStatusInternalServerError")
    public Response returnStatusInternalServerError() {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(false).build();
    }
}
