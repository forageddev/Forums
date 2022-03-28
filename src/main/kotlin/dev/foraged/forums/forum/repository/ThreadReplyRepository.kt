package dev.foraged.forums.forum.repository

import dev.foraged.forums.forum.ForumThreadReply
import dev.foraged.forums.forum.ForumThread
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ThreadReplyRepository : MongoRepository<ForumThreadReply?, String?>
{
    // find those?
    fun findForumRepliesByThread(thread: ForumThread) : List<ForumThreadReply>
}