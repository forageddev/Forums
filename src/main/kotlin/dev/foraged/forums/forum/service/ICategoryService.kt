package dev.foraged.forums.forum.service

import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.ForumThread

interface ICategoryService
{
    /** List all categories  */
    fun findAll(): List<ForumCategory>?

    /** Retrieves a category by it's name, with it outputting  */
    fun getCategory(name: String?): ForumCategory?

    /**
     * Updates a category data for mass changes, it
     * supports modifying description, weight, display name
     *
     * @return Category updated
     */
    fun updateCategory(name: String?, newCategory: ForumCategory): Boolean

    /** Returns updated display name of a category  */
    fun updateDisplayName(category: String?, name: String?): Boolean

    /** Returns updated description of a category  */
    fun updateDescription(category: String?, description: String?): Boolean

    /** Returns updated weight of a category  */
    fun updateWeight(category: String?, weight: Int): Boolean

    /** Returns updated permission of a category  */
    fun updatePermission(category: String?, permission: String?): Boolean

    /**
     * Remove a category by it's identifier
     *
     * @param name Unique identifier
     * @return Category that was removed
     */
    fun removeCategory(name: String?): ForumCategory
    fun addThread(category: ForumCategory?, thread: ForumThread?): ForumThread?
}