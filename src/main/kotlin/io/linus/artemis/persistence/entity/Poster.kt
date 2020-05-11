package io.linus.artemis.persistence.entity

import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.hibernate.orm.panache.PanacheEntity
import io.quarkus.runtime.annotations.RegisterForReflection
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

const val EMAIL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"

/*
* Ref: https://quarkus.io/guides/hibernate-orm-panache
* Ref: https://in.relation.to/2019/11/19/hibernate-orm-with-panache-in-quarkus/
*/
@Entity
@Table(name = "poster", indexes = [ Index(name = "username_index", columnList = "username") ])
@JsonRootName("user")
/* Ref: https://quarkus.io/guides/rest-json#using-response */
@RegisterForReflection
class Poster() : PanacheEntity() {
//        @Column(unique = true, updatable = false)
        var email: String = ""
        var username: String = ""
        var password: String = ""
        var image: String = ""
        var bio: String = ""
        @Column(length = 500)
        var token: String? = ""

        constructor(email: String, username: String, password: String) : this() {
                this.email = email
                this.username = username
                this.password = password
        }

        /* Ref: https://stackoverflow.com/questions/51266313/is-it-possible-to-override-static-method-in-kotlin
        *  Issue: JSON Binding serialization error javax.json.bind.JsonbException: Unable to serialize property 'panache'
        */
        /*companion object Panache {
                fun findById(id: Long) : Optional<Poster> = findByIdOptional<Poster>(id)
                fun findByIdOptional(id: Long) : Optional<Poster> = findByIdOptional<Poster>(id)
                fun findByEmailOptional(login: LoginReq) : Optional<Poster> = find<Poster>("email = ?1 and password = ?2", login.email, login.password).firstResultOptional()
        }*/
}

@Entity
class Follower : PanacheEntity() {
        @ManyToOne
        @JoinColumn(name = "poster_id", foreignKey = ForeignKey(name = "poster_fkey"))
        lateinit var poster: Poster
        lateinit var follower: String
}

data class Profile(val username: String, val bio: String = "", val image: String = "", val following: Boolean = false)

@JsonRootName("user")
sealed class Request {
        /* Properties that can be declared with val when using Reactive Route might because of using Reflection to map Request Body */
        @NotBlank(message = "email should not be blank")
        @Email(message = "email format is not valid", regexp = EMAIL_REGEX)
        var email = ""
        @NotBlank(message = "password should not be blank")
        @Size(message = "password should not less than 6 character", min = 6)
        var password = ""
}

class LoginReq : Request()

class RegisterReq : Request() {
        @NotBlank(message = "username should not be blank")
        @Size(message = "username should not less than 3 character", min = 3)
        @Pattern(message = "must be alphanumeric", regexp="^\\w+$")
        var username = ""
}

class UpdateReq {
        var email: String = ""
        var password: String = ""
        var username: String = ""
        var image: String = ""
        var bio: String = ""
}
