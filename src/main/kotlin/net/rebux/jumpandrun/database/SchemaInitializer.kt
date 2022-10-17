package net.rebux.jumpandrun.database

import com.mysql.jdbc.Connection
import net.rebux.jumpandrun.database.models.Parkours
import net.rebux.jumpandrun.database.models.Times
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class SchemaInitializer {

    init {
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
    }

    fun initialize() = transaction {
        SchemaUtils.create(
            Parkours,
            Times
        )
    }
}
