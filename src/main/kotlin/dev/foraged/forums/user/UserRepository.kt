package dev.foraged.forums.user

import com.mongodb.client.model.Filters
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

object UserRepository {

    val controller by lazy {
        DataStoreObjectControllerCache.create<User>()
    }

    fun findByIdentifier(identifier: UUID) : User? {
        return controller.load(identifier, DataStoreStorageType.CACHE).join() ?: controller.load(identifier, DataStoreStorageType.MONGO).join()
    }

    fun findByUsername(username: String): User? {
        return ScalaStoreUuidCache.uniqueId(username)?.let { controller.load(it, DataStoreStorageType.MONGO).join() }
    }

    fun findByUsernameIgnoreCase(username: String): User? {
        return findByUsername(username)
    }

    fun findByEmail(email: String): User? {
        return controller.useLayerWithReturn<MongoDataStoreStorageLayer<User>, User>(DataStoreStorageType.MONGO) {
            return this.loadWithFilter(Filters.eq("email", email)).join()
        }
    }

    fun findByRegisterSecret(secret: String) : User? {
        return controller.useLayerWithReturn<MongoDataStoreStorageLayer<User>, User>(DataStoreStorageType.MONGO) {
            return this.loadWithFilter(Filters.eq("registerSecret", secret)).join()
        }
    }
}