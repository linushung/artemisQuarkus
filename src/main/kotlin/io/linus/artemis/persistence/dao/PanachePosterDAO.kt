package io.linus.artemis.persistence.dao

import arrow.core.Option
import io.linus.artemis.persistence.entity.*
import io.quarkus.hibernate.orm.panache.PanacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.enterprise.context.ApplicationScoped

/* Hibernate ORM with Panache */
@ApplicationScoped
class PanachePosterDAO: PanacheRepository<Poster> {

    fun login(login: LoginReq): Option<Poster> {
        val result= find("email = ?1 and password = ?2", login.email, login.password).firstResultOptional<Poster>()
        return Option.fromNullable(result.orElse(null))
    }

    suspend fun findByUsername(username: String): Poster? {
        return withContext(Dispatchers.IO) { find("username", username).firstResult<Poster>() }
    }

    suspend fun findByUsernameOption(username: String): Option<Poster> {
        return withContext(Dispatchers.IO) {
            val result= find("username", username).firstResultOptional<Poster>()
            Option.fromNullable(result.orElse(null))
        }
    }
}

/* Ref: https://in.relation.to/2019/11/19/hibernate-orm-with-panache-in-quarkus/ */
/**
 * Both Hibernate Panache & Spring Data JPA use `Repository` word, but they actually implement DAO layer in essence.
 *
 * Getting rid of the DAO:
 * In Hibernate ORM with Panache, users are advised to skip DAOs entirely and put the model methods in the entity class
 * as static methods
 *
 * Data Access Objects are mostly useful when you have one or more of the following situations:
 * - The entity type is shared between projects written for different stacks. One project will use DAOs written for
 *   WildFly, another for Spring.
 * - The entity type is shared between projects written for different use-cases. One project will handle the entity in
 *   one way, while another will differ entirely.
 * - You need to mock your DAOs in tests.
 * - Your entity type is crammed so full of getters and setters that adding any model method will exceed the maximum
 *   method count.
 *
 * If you don’t absolutely need DAOs, they come with drawbacks:
 * - You need to have one extra class per entity.
 * - You need to inject DAOs everywhere you use them.
 * - You cannot inject DAOs in methods without going out and adding a field, making this quite costly in terms of
 *   editing flow.
 * - You cannot discover a DAO methods without injecting it and trying completion. If this is not the DAO you’re looking
 *   for, you need to go back to the injected field and change its type and name to try again.
 * - Your IDEs are not helping you with any of these drawbacks.
 * - Any model refactoring requires you to examine queries in the DAO that corresponds to the entity you modified,
 *   making this poorly encapsulated.
 * */
