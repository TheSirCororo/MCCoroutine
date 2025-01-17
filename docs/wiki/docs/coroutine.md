# Kotlin Coroutines and Minecraft Plugins

When starting with [Coroutines in Kotlin](https://kotlinlang.org/docs/coroutines-basics.html), it is interesting
how this can be translated to the world of minecraft plugins. It is recommended to learn how Kotlin Coroutines work before you continue here.

!!! note "Important"
    Make sure you have already installed MCCoroutine. See [Installation](/gettingstarted) for details.

### Starting a coroutine

For beginners, it is often confusing how to enter a coroutine. The examples in the official guide mostly use ``runBlocking``
because it makes sense for testing. However, keep in mind to **never** use ``runblocking`` in any of your plugins.

* To enter a coroutine **anywhere** in your code at any time:

```kotlin
fun foo() {
    plugin.launch {
        // This will always be on the minecraft main thread.
    }
}
```

### Switching coroutine context
    
Later in the [Coroutines in Kotlin](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html) guide, the terms coroutine-context and dispatchers are explained.
A dispatcher determines what thread or threads the corresponding coroutine uses for its execution. Therefore, MCCoroutine offers 2 custom dispatchers:

* minecraftDispatcher (Allows to execute coroutines on the main minecraft thread)
* asyncDispatcher (Allows to execute coroutines on the async minecraft threadpool)

However, it is recommend to use ``Dispatchers.IO`` instead of asyncDispatcher because it is more optimized.

* An example how this works is shown below:

```kotlin
fun foo() {
    plugin.launch {
        // This will always be on the minecraft main thread.

        val result1 = withContext(plugin.minecraftDispatcher) {
            // Perform operations on the minecraft main thread.
            "Player is " // Optionally, return a result.
        }

        // Here we are automatically back on the main thread again.

        val result2 = withContext(plugin.asyncDispatcher) {
            // Perform operations asynchronously.
            " Max"
        }

        // Here we are automatically back on the main thread again.

        println(result1 + result2) // Prints 'Player is Max'
    }
}
```

Normally, you do not need to call ``plugin.minecraftDispatcher`` in your code. Instead, you are guaranteed to be always on the minecraft main thread
in the ``plugin.launch{}`` scope and use sub coroutines (e.g. withContext) to perform asynchronous operations. Such a case can be found below:


```kotlin
@EventHandler
fun onPlayerJoinEvent(event: PlayerJoinEvent) {
    plugin.launch {
        // Main Thread
        val name = event.player.name
        val listOfFriends = withContext(Dispatchers.IO) {
            // IO Thread
            val friendNames = Files.readAllLines(Paths.get("$name.json"))
            friendNames
        }
        
        // Main Thread
        val friendText = listOfFriends.joinToString(", ")
        event.player.sendMessage("My friends are: $friendText")
    }
}

```

###  Coroutines everywhere

Using ``plugin.launch{}``is valuable if you migrate existing plugins to use coroutines. However, if you write a new plugin from scratch, you may consider using
convenience integrations provided by MCCoroutine such as:

* Suspending Plugin
* Suspending Listeners
* Suspending CommandExecutors










