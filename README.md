# BJDA - Better Java Discord API

A discord library based on jda for kotlin
<br>
Added a flexible UI API which inspired by React.js
<br>
Used for my own bots only, might be out of maintenance

## Why BJDA

### Fully Modularized Design

You can manage all modules easily

### Beautiful, Readable, Flexible

Create an interactive UI easily in few lines of code
```kotlin
val app = UI(
    Pager()-{
        + Text()..{
            content = "Hello"
        }
        + Embed()..{
            title = "Hello World"
        }
    }
)

app.reply(event)
```
Declare slash command 
```kotlin
class SuperHello : SuperCommand(name = "hello", description = "Say Hello") {
  private val size: String by option(OptionType.STRING, "size").choices(
    "Small" to "2xl",
    "Medium" to "4xl",
    "Large" to "6xl"
  ).required(true)

  override fun run() {
    event.reply("size: $size").queue()
  }
}
```
Application Command is also supported
```kotlin
class UserHelloCommand : SuperContext(name = "hello", type = Command.Type.USER) {
    override fun run(event: UserContextInteractionEvent) {
        event.reply("Hello").queue()
    }
}
```
The coolest thing is you can even create a normal command with Clikt
```kotlin
class Hello : BJDACommand(name = "apps") { //BJDACommand is based on Clikt
  override fun run() {
    UI(App()).reply(event.message)
  }
}
```

## Getting Started
### Demo
See the full Demo and TODO APP implementation in [here](./src/test/kotlin)

### Creating an App
```kotlin
val Panel = FComponent.create(::IProps) {
  val onConfirm = ButtonClick {event ->
    println("Confirmed")
    ui.edit(event)
  };

  {
    + Embed()..{
      title = "Hello World"
    }

    + Row()-{
      + Button(id = use(onConfirm)) {
        label = "Confirm"
        style = ButtonStyle.SUCCESS
      }
    }
  }
}
```
Invoke `updateState` to update state and render the component again
<br>
Normally it should be synchronous but in some cases it is async.

### Creating the Command
```kotlin
class MessageHelloCommand : SuperCommand(name = "hello", description = "Hello World") {
    override fun run() {
        UI()
        event.reply("Hello").queue()
    }
}
```

## Update Message after update
It is painful to real-time update messages in multiplayer game

Now you can write it clearly with hooks or manually update
### You have two ways:
- #### Auto update (For updating multi messages realtime)

  it will update listened hooks when ui is updated
  <br>
  However, the message will update multi times when replying to an interaction event
  <br>
  which is listened as it won't detect if the message is updated manually
  ```kotlin
  ui.reply(event) {
    ui.listen(it)
  }
  ```
- #### Half-Auto update:
  You may disable the `updateHooks` option to avoid updating hooks after components updated
  
  which includes calling the `updateState` from components. 
  
  You must call `ui.updateHooks` manually to update hooks

  ```kotlin
  player update {
      score++
  }
  ui.updateHooks()
  ```
- #### Manually Update (For event handlers)
  Calling `ui.edit` or `ui.reply` from component

  It is more flexible and won't cause multi-edit issue
  ```kotlin
  private val onAddItem = ButtonClick { event ->
    event.replyModal(
      addTodoForm.create()
    ).queue()
    //or
    ui.edit(event)
  }
  ```

## Performance

The UI API is similar to React.js, unless it is removed from view
<br>
Otherwise, components is always reused

### For List
To create a list of components, use the `key` prop to help the Scanner knows which component is new or removed
<br>
It can improve the performance of the Tree Scanner

## What's New
### SlashCommand Module is removed
You can use SuperCommand instead.
It supports to create slash commands with the similar usage of BJDACommand 


### Reactions Component are supported in 2.0
```kotlin
Reactions()/{
  onAdd = {event ->
    event.channel
      .sendMessage("You chosen ${event.reaction.emoji}")
      .queue()
  }

  onRemove = {event ->
    event.channel
      .sendMessage("You removed ${event.reaction.emoji}")
      .queue()
  }

  reactions(
    Emoji.fromUnicode("U+1F601"),
    Emoji.fromUnicode("U+1F602")
  )
}
```
The design pattern of UIHooks is also updated,
<br>
You can create a provider and receiver UIHook to access the message after sending

### The `useState` and `useCombinedState` hooks
you can use it by declaring it on the component level with `var score by useState(0)`
<br>
To updating multi states at same time, use `useCombinedState` like:
```kotlin
val player = useCominedState(Player())

//get
val (score, win) = player.get()

//update
player update {
  win = true
  score++
}
```

## Coming soon

We will move to Kord soon which is a better discord api written in kotlin
