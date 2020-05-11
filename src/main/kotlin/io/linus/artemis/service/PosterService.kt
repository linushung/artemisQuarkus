package io.linus.artemis.service

import arrow.core.Option
import io.linus.artemis.persistence.dao.PanacheFollowerDAO
import io.linus.artemis.persistence.dao.PanachePosterDAO
import io.linus.artemis.persistence.entity.*
import io.linus.artemis.service.repository.PosterRepository
import java.security.Principal

interface PosterService {
    val posterDAO: PanachePosterDAO

    val followerDAO: PanacheFollowerDAO

    fun create(register: RegisterReq): Poster

    fun login(login: LoginReq): Option<String>

    fun update(info: UpdateReq): Option<Poster>

    fun currentUser(): Option<Poster>

    fun getProfile(username: String): Option<Profile>

    fun followUser(username: String): Option<Profile>

    fun unFollowUser(username: String): Option<Profile>
}
