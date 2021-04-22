package com.laces.core.database

import com.laces.core.database.MasterReplicaRoutingDataSource.Type.MASTER
import com.laces.core.database.MasterReplicaRoutingDataSource.Type.REPLICA
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import javax.sql.DataSource

class MasterReplicaRoutingDataSource(master: DataSource, slave: DataSource) : AbstractRoutingDataSource() {

    companion object {
        private val currentDataSource = ThreadLocal<Type>()

        fun setReadonlyDataSource(isReadonly: Boolean) {
            currentDataSource.set(if (isReadonly) REPLICA else MASTER)
        }

        fun isCurrentlyReadonly(): Boolean {
            return currentDataSource.get() == REPLICA
        }
    }

    init {
        val dataSources: MutableMap<Any, Any> = mutableMapOf(MASTER to master, REPLICA to slave)
        super.setTargetDataSources(dataSources)
        super.setDefaultTargetDataSource(master)
    }

    private enum class Type {
        MASTER, REPLICA
    }

    override fun determineCurrentLookupKey(): Any? {
        return currentDataSource.get()
    }
}