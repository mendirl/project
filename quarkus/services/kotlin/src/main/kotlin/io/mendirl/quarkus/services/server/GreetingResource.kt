package io.mendirl.quarkus.services.server

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/greeting")
@Produces(MediaType.TEXT_PLAIN)
class GreetingResource {

    @GET
    fun greeting() = Response.ok("HELLO FROM QUARKUS").build()
}
