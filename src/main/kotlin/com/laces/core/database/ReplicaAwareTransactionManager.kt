package com.laces.core.database

import org.springframework.stereotype.Component
import org.springframework.transaction.*
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class ReplicaAwareTransactionManager(private val wrapped: PlatformTransactionManager) : PlatformTransactionManager {

    @Throws(TransactionException::class)
    override fun getTransaction(definition: TransactionDefinition): TransactionStatus {
        val isTxActive: Boolean = TransactionSynchronizationManager.isActualTransactionActive()

        if (isTxActive && MasterReplicaRoutingDataSource.isCurrentlyReadonly() && !definition.isReadOnly) {
            throw CannotCreateTransactionException("Can not request read-write transaction from initialized readonly transaction")
        }

        // Prevents swapping Datasources when nested transactions are called.
        if (!isTxActive) {
            MasterReplicaRoutingDataSource.setReadonlyDataSource(definition.isReadOnly);
        }

        return wrapped.getTransaction(definition)
    }

    @Throws(TransactionException::class)
    override fun commit(status: TransactionStatus) {
        wrapped.commit(status)
    }

    @Throws(TransactionException::class)
    override fun rollback(status: TransactionStatus) {
        wrapped.rollback(status)
    }
}