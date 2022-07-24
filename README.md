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
  <version>4.2.1</version>
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
The coolest thing is you can even create a text command with Clikt
<br>
Notice that Discord is going to replace text commands with slash commands, avoid to use it in production 
```kotlin
class Hello : TextCommand(name = "apps") { //TextCommand is based on Clikt
  override fun run() {
    UI(App()).reply(event.message)
  }
}
```

## Getting Started
### Demo
Full Demo of a Todo App: https://github.com/SonMooSans/bjda-example

### Creating an App
```kotlin
val Panel = FComponent.component {
  val onConfirm by onClick {event ->
    println("Confirmed")
    ui.edit(event)
  };

  {
    + Embed()..{
      title = "Hello World"
    }

    + Row()-{
      + Button(onConfirm) {
        label = "Confirm"
        style = ButtonStyle.SUCCESS
      }
    }
  }
}
```
Declare `val state = useState()` variable
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

  You should use `state.update(event)` so that hooks will reply to the event instead of updating the message 

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
  Notice that if you directly call `state.update` without the `event` parameter, ui will not be updated

  You must reply to the ui manually, it is called **Half-Auto Update**
- #### Half-Auto update:
  To half-auto update state, use `state.update {..}` instead of `state.update(event) {..}`
  
  You can call `ui.updateHooks` or `ui.editAndUpdate` manually to update hooks
  <br>
  <br>
  Make sure you are calling `ui.editAndUpdate(event)` when you are replying to an event,
  
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
  
  ui.editAndUpdate(event)
  ```
- #### Manually Update (For event handlers)

  When you wanted to update state only without updating hooks
  
  you can easily use `ui.edit` or `ui.reply` from the component

  ```kotlin
  private val onAddItem = ButtonClick { event ->
    state update "Hello World" //don't pass the event parameter
  
    ui.edit(event)
  }
  ```

## Performance

The UI API is similar to React.js, unless it is removed from view
<br>
Otherwise, components is always reused

**Important**:
<br>
You should dispose the UI by using `ui.destroy()` when it is never to be used again
<br>
Otherwise, it will cause memory leak

### For Dynamic List
To render a collection of components, convert it to a Fragment by using `Fragment(components)`

Since 4.0.0, collections of components will automatically convert to a Fragment 
<br>
Give component a `key` prop to help the Scanner knows which component is new or removed
<br>
It can improve the performance of the Tree Scanner

## What's New Since 4.0.0

### UI Update Queue (since 4.2.0)
Before 4.2.0, when you update the ui by using `ui.edit(event)` or `state.update(event)`
<br>
It will call `event.editMessage` asynchronously.

if you are calling it twice at same time, The previous task might be finish later than the current one
<br>
which will make the ui displayed in message is not the current one

### Await updating Unsupported
`ui.updateHooks()` is not unsupported since 4.2.0

### Form API supports Class version
Example: 
```kotlin
class AddForm : FormFactory() {
    override val title = "Add Todo"

    override fun render(): LambdaList<Row> {
        return {
            + Row()-{
                + TextField("todo") {
                    label = "TODO"
                    style = TextInputStyle.PARAGRAPH
                }
            }
        }
    }

    override fun onSubmit(event: ModalInteractionEvent) {
        //Do something...
    }
}
```
### Reaction Module Updated
Notice: We still recommend you to use button instead of reaction
<br>
You can enable it by `ui.enableReaction(message)`

### Localization
It is easier to support multi languages with new `Translation` util
<br>
example: 
```kotlin
import commands.context.Translation.Companion.group

val ch = group(
    "todo" to "待辦事項",
    "title" to "待辦事項面板",
    "add" to "添加待辦事項",
    "edit" to "編輯待辦事項",
    "delete" to "刪除待辦事項",
    "placeholder" to "還沒有待辦事項",
    "close" to "關閉面板"
)(
    "menu" to group(
        "placeholder" to "選擇一個待辦事項"
    ),
    "form" to group(
        "new_content" to "新內容"
    ),
)
```

## Coming soon

We will move to Kord soon which is a better discord api written in kotlin
