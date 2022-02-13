package top.mcwebsite.easymcapp.todo.todoData.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import top.mcwebsite.easymcapp.todo.todoData.entity.ToDoEntity

abstract class EntityDao<in E : ToDoEntity> {
    @Insert
    abstract suspend fun insert(entity: E): Long

    @Insert
    abstract suspend fun insertAll(vararg entity: E)

    @Insert
    abstract suspend fun insertAll(entities: List<E>)

    @Update
    abstract suspend fun update(entity: E)

    @Delete
    abstract suspend fun deleteEntity(entity: E): Int

    @Transaction
    open suspend fun withTransaction(tx: suspend () -> Unit) = tx()

    private suspend fun insertOrUpdate(entity: E): Long {
        return if (entity.id == 0L) {
            insert(entity)
        } else {
            update(entity)
            entity.id
        }
    }

    @Transaction
    open suspend fun insertOrUpdate(entities: List<E>) {
        entities.forEach {
            insertOrUpdate(it)
        }
    }
}
