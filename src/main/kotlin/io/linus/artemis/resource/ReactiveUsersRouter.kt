package io.linus.artemis.resource

import io.linus.artemis.persistence.entity.RegisterReq
import io.linus.artemis.service.PosterService
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.validation.ValidationException
import javax.ws.rs.core.MediaType


/*
* Ref(Reactive Routes): https://quarkus.io/guides/reactive-routes
* Ref(Vert.x-Web): https://vertx.io/docs/vertx-web/kotlin/
*/
@ApplicationScoped
@RouteBase(path = "/reactive/api", produces = [MediaType.APPLICATION_JSON])
class ReactiveUsersRouter(@Inject val service: PosterService) {

    @PostConstruct
    fun init(@Observes router: Router) {
        /* BodyHandler is required to manage body parameters like forms or json body */
        router.route().handler(BodyHandler.create())
        /* Manage the validation failure for all routes in the router */
        router.errorHandler(400) {
            if (it.failure() is ValidationException) {
                it.response().end(it.failure().message)
            }
        }
    }

    @Route(path = "/users", methods = [HttpMethod.POST], type = Route.HandlerType.BLOCKING)
    fun registerUser(rc: RoutingContext) {
        rc.bodyAsJson.mapTo(RegisterReq::class.java)?.let {
            val poster= service.create(it)
            rc.response().setStatusCode(201).end(Json.encodePrettily(poster))
        }
    }

    @Route(path = "/users/login", methods = [HttpMethod.POST])
    fun loginUser(rc: RoutingContext) {
        rc.response().end("loginUser")

    }

    @Route(path = "/users", methods = [HttpMethod.GET])
    fun getUser(rc: RoutingContext) {
        rc.response().end("getUser")
    }

    @Route(path = "/users", methods = [HttpMethod.PUT])
    fun updateUser(rc: RoutingContext) {
        rc.response().end("updateUser")
    }

}