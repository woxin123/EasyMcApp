package top.mcwebsite.crash

interface EventListener {
    fun onLaunchErrorActivity()

    fun onRestartAppFromErrorActivity()

    fun onCloseAppFromErrorActivity()
}