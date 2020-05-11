package io.linus.artemis.persistence.repository

import io.linus.artemis.persistence.entity.Poster
import io.linus.artemis.service.repository.PosterRepository
import io.quarkus.hibernate.orm.panache.PanacheRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PanachePosterRepoImpl : PanacheRepository<Poster>, PosterRepository {
    override fun listTopPoster(): List<Poster> {
        TODO("Not yet implemented")
    }
}
