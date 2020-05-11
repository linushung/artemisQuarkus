package io.linus.artemis.resource

import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
@RouteBase(path = "/api")
class UsersValidator() {
    @Route(path = "/users", methods = [HttpMethod.POST], order = -1)
    fun registerValidator(rc: RoutingContext) {
        /* TODO("Ref(Vert.x Web API Contract): https://vertx.io/docs/vertx-web-api-contract/kotlin") */
        rc.next()
    }
}
