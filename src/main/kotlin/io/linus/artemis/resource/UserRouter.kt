package io.linus.artemis.resource

import arrow.core.None
import arrow.core.Some
import io.linus.artemis.persistence.entity.LoginReq
import io.linus.artemis.persistence.entity.RegisterReq
import io.linus.artemis.persistence.entity.UpdateReq
import io.linus.artemis.service.PosterService
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
/*
 * Notion: https://www.notion.so/CDI-Contexts-and-Dependency-Injection-for-Java-EE-JSR-365-96e86c93a84547c0bedbf0361e2e49fd
 * Ref: https://quarkus.io/guides/spring-di
 *
 * Issue: Found unrecommended usage of private members (use package-private instead) in application beans:
 * @Inject field io.linus.quarkus.resource.ReactiveUsersRouter#service
 */
class UsersRouter(val service: PosterService) {

    @POST
    @Path("/users")
    @PermitAll
    fun registerUser(@Valid register: RegisterReq): Response {
        return Response.ok(service.create(register)).status(Response.Status.CREATED).build()
    }

    @POST
    @Path("/users/login")
    @PermitAll
    fun loginUser(@Valid login: LoginReq): Response {
        return when (val result= service.login(login)) {
            is None -> Response.status(Response.Status.BAD_REQUEST).entity("Authentication fail...").build()
            is Some -> Response.ok(result.t).build()
        }
    }

    @PUT
    @Path("/users")
    @RolesAllowed("Admin", "User")
    fun updateUser(@Valid req: UpdateReq, @Context sec: SecurityContext): Response {
        return service.update(req).fold({
            Response.status(Response.Status.BAD_REQUEST).entity("Cannot find the user to update...").build()
        }, {
            Response.ok(it).build()
        })
    }

    @GET
    @Path("/users")
    @RolesAllowed("User")
    fun getUser(): Response {
        return service.currentUser().fold({
            Response.status(Response.Status.BAD_REQUEST).entity("Cannot find the user...").build()
        }, {
            Response.ok(it).build()
        })
    }

    @GET
    @Path("/profiles/{username}")
    @RolesAllowed("Admin", "User")
    fun getProfile(@PathParam("username") username: String): Response {
        return service.getProfile(username).fold({
            Response.status(Response.Status.BAD_REQUEST).entity("Cannot find the profile for specified user...").build()
        }, {
            Response.ok(it).build()
        })
    }

    @POST
    @Path("/profiles/:username/follow")
    @RolesAllowed("User")
    fun followUser(@PathParam("username") username: String): Response {
        return Response.ok().build()
    }

    @DELETE
    @Path("/profiles/:username/follow")
    @RolesAllowed("User")
    fun unFollowUser(@PathParam("username") username: String): Response {
        return Response.ok().build()
    }


}