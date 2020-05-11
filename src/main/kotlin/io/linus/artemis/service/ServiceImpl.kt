package io.linus.artemis.service

import arrow.core.Option
import io.linus.artemis.authorisation.JWTClaims
import io.linus.artemis.authorisation.JWTService
import io.linus.artemis.persistence.dao.PanacheFollowerDAO
import io.linus.artemis.persistence.dao.PanachePosterDAO
import io.linus.artemis.persistence.entity.*
import kotlinx.coroutines.*
import org.eclipse.microprofile.jwt.JsonWebToken
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import kotlin.coroutines.CoroutineContext

@ApplicationScoped
class PosterServiceImpl(
        override val posterDAO: PanachePosterDAO,
        override val followerDAO: PanacheFollowerDAO,
        val jwt: JsonWebToken,
        val jwtService: JWTService
) : PosterService, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    override fun login(login: LoginReq): Option<String> {
        return posterDAO.login(login).fold({ Option.empty() }, {
            Option.just(jwtService.fetchJWT(JWTClaims(it.username, it.id, it.email)))
        })
    }

    @Transactional
    override fun create(register: RegisterReq): Poster {
        val p = Poster(register.email, register.username, register.password)
        posterDAO.persist(p)
        return p
    }

    @Transactional
    override fun update(info: UpdateReq): Option<Poster> {
        val result= posterDAO.findByIdOptional(jwt.name.toLong())
        return Option.fromNullable(result.orElse(null))
                .fold({ Option.empty() }, { p ->
                    p.run {
                        if (info.email.isNotBlank()) {
                            email = info.email
                        }
                        if (info.username.isNotBlank()) {
                            username = info.username
                        }
                        if (info.password.isNotBlank()) {
                            password = info.password
                        }
                        if (info.image.isNotBlank()) {
                            image = info.image
                        }
                        if (info.bio.isNotBlank()) {
                            bio = info.bio
                        }
                        Option.just(this)
                    }
                })
    }

    override fun currentUser(): Option<Poster> {
        val result= posterDAO.findByIdOptional(jwt.name.toLong())
        return Option.fromNullable(result.orElse(null)).fold({ Option.empty() }, { Option.just(it) })
    }

    override fun getProfile(username: String): Option<Profile> {
        var result= Option.empty<Profile>()

        launch {
            posterDAO.findByUsername(username)?.let { poster ->
                val isFollower= followerDAO.findFollowerById(poster.id).any { it.follower == jwt.subject }
                result = Option.just(Profile(poster.username, poster.bio, poster.image, isFollower))
            }
        }

        return result
    }

    override fun followUser(username: String): Option<Profile> {
        TODO("Not yet implemented")
    }

    override fun unFollowUser(username: String): Option<Profile> {
        TODO("Not yet implemented")
    }

}