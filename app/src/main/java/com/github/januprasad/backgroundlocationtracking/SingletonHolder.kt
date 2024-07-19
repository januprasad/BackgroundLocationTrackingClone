package com.github.januprasad.backgroundlocationtracking

open class SingletonHolder<out T, in A>(
    creator: (A) -> T,
) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var singletonInstance: T? = null

    fun getInstance(arg: A): T {
        val copyInstance = singletonInstance
        if (copyInstance != null) {
            return copyInstance
        }

        return synchronized(this) {
            val syncInstance = singletonInstance
            if (syncInstance != null) {
                syncInstance
            } else {
                val created = creator!!(arg)
                singletonInstance = created
                creator = null
                created
            }
        }
    }
}
