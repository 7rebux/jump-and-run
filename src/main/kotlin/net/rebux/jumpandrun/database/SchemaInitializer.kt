package net.rebux.jumpandrun.database

import net.rebux.jumpandrun.database.models.Locations
import net.rebux.jumpandrun.database.models.Parkours
import net.rebux.jumpandrun.database.models.Times
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection.TRANSACTION_SERIALIZABLE

class SchemaInitializer {

    init {
        TransactionManager.manager.defaultIsolationLevel = TRANSACTION_SERIALIZABLE
    }

    fun initialize() = transaction {
        SchemaUtils.create(
            Parkours,
            Locations,
            Times,
        )
    }
}
