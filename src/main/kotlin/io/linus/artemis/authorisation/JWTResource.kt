package io.linus.artemis.authorisation

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/microprofile")
@Produces(MediaType.APPLICATION_JSON)
class JWTResource(val jwtService: JWTService) {

    @GET
    @Path("/jwt")
    fun getJWT(): Response {
        return Response.ok(jwtService.fetchJWT(JWTClaims("linushung@gmail.com", 1, "linushung@gmail.com"))).build()
    }
}
