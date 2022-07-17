# BJDA - Better Java Discord API

A discord library based on jda for kotlin
<br>
Added a flexible UI API which inspired by React.js
<br>
Used for my own bots only, might be out of maintenance

## Installation
### Maven:
```xml
<dependency>
  <groupId>io.github.sonmoosans</groupId>
  <artifactId>bjda</artifactId>
  <version>3.0.0</version>
</dependency>
```

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
Declare `val state = useCominedState()` variable
<br>
and Invoke `state.update` to update state and render the component again

Normally it should be synchronous but in some cases it is async.

### Creating the Command
```kotlin
class MessageHelloCommand : SuperCommand(name = "hello", description = "Hello World") {
    override fun run() {
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
  <br>
  If you are replying to an event
  <br>
  You should use `state.update(event)` so that hooks will reply to the event instead of updating the message 
  <br>
  Otherwise, the message will be edited twice times
  ```kotlin
  //reply and listen
  ui.reply(event) {
    ui.listen(it)
  }
  
  //update state
  state.update(event) {
    name = "Hello World"
  }
  ```
- #### Half-Auto update:
  You can call `ui.updateHooks` or `ui.editAndUpdate` manually to update hooks
  
  Make sure you are calling `ui.editAndUpdate(event)` when you are replaying to an event,
  
  it is equal to `state.update(event)`
  ```kotlin
  //reply and listen
  ui.reply(event) {
    ui.listen(it)
  }
  
  //update state
  player update {
      score++
  }
  ui.editAndUpdate(event, Await())
  //or updating hooks sync
  ui.updateHooks(Await())
  ```
- #### Manually Update (For event handlers)

  When you wanted to update state without update hooks
  
  you can easily use `ui.edit` or `ui.reply` from the component

  It is more flexible and allow you to write a state manager yourself
  ```kotlin
  private val onAddItem = ButtonClick { event ->
    ui.edit(event)
  }
  ```

## Performance

The UI API is similar to React.js, unless it is removed from view
<br>
Otherwise, components is always reused

### For Dynamic List
To render a collection of components, convert it to a Fragment by using `Fragment(components)` or `!components`
<br>
Give component a `key` prop to help the Scanner knows which component is new or removed
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

## Known Issues

If some messages are deleted, related hooks will be failed to updated 

## Coming soon

We will move to Kord soon which is a better discord api written in kotlin
