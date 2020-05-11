package io.linus.artemis.persistence.dao

import io.linus.artemis.persistence.entity.Follower
import io.quarkus.hibernate.orm.panache.PanacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PanacheFollowerDAO: PanacheRepository<Follower> {

    suspend fun findFollowerById(id: Long): List<Follower> {
        return withContext(Dispatchers.IO) { list("poster_id", id) }
    }
}