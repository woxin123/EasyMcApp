package top.mcwebsite.easymcapp.todo.todoData.mapper

fun interface Mapper<F, T> {
    fun mapper(from: F): T
}

fun interface IndexedMapper<F, T> {
    fun mapper(index: Int, from: T): T
}
