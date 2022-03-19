package dev.foraged.forums.forum.service

import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory

interface IForumService
{
    /** List all forums  */
    fun findAll(): List<Forum>

    /** Retrieves a forum by it's name, with it outputting  */
    fun getForum(name: String?): Forum
    val subForums: List<ForumCategory>

    /**
     * Add a new forum by a display name which automatically
     * converts into a unique identifier (replaces the spaces to dashes and is lowercase)
     *
     * @param forum Forum to be added
     * @return Forum created
     */
    fun addForum(forum: Forum?): Forum?
}