package io.linus.artemis.resource

import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import javax.ws.rs.core.MediaType

@RouteBase(path = "quarkus", produces = [MediaType.APPLICATION_JSON])
class QuarkusRouter {

    @Route(methods = [HttpMethod.GET], path = "/ping")
    fun healthCheck(rc: RoutingContext) {   /* RoutingContext contains the standard Vert.x HttpServerRequest and HttpServerResponse */
        rc.response().end("pong")
    }

}
