package io.linus.artemis.persistence.entity

import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.hibernate.orm.panache.PanacheEntity
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne

@JsonRootName("tag")
data class Tag(val name: String): PanacheEntity()

@JsonRootName("article")
data class Article(
        var slug: String,
        var title: String,
        var description: String,
        var body: String,
        @ManyToMany
        val tagList: MutableList<Tag> = mutableListOf(),
        @CreationTimestamp
        var createdAt: LocalDateTime = LocalDateTime.now(),
//        var createdAt: OffsetDateTime = OffsetDateTime.now(),
        @UpdateTimestamp
        var updatedAt: LocalDateTime = LocalDateTime.now(),
        @ManyToMany
        var favorited: MutableList<Poster> = mutableListOf(),
        var favoritesCount: Int,
        @ManyToOne
        var author: Poster
): PanacheEntity()

