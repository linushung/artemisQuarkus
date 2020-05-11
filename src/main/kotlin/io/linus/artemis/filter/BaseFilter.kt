package io.linus.artemis.filter

import io.quarkus.vertx.web.RouteFilter
import io.vertx.ext.web.RoutingContext
import javax.ws.rs.core.MediaType

class MyFilters {
    @RouteFilter(100)
    fun myFilter(rc: RoutingContext) {
        rc.response().putHeader("X-Header", "Interception")
        rc.response().putHeader("Content-Type", MediaType.APPLICATION_JSON)
        rc.next()
    }
}
