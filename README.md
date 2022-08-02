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
  <version>4.3.6</version>
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

  override val run: CommandHandler = {
    //After 4.2.1, we must call option() or option.value(...)
    //To get option value from event
    event.reply("size: ${ size() }").queue()
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

    + Row(
      Button.success(
        id = onConfirm,
        label = "Confirm"
      )
    )
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
    override val run: CommandHandler = {
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

## What's New Since 4.2.0

### Form API 2.0
> You must unmount the component to avoid memory leaks

For modals that is attached to component lifecycle, use Form API.
```kotlin
val AddForm = Form {
    //set properties
}

class AddForm : FormFactory() {
    override val title = "..."

    override fun render(): LambdaList<Row> {
        return {}
    }

    override fun onSubmit(event: ModalInteractionEvent) {
        //Do something...
    }
}
```
**Modal Pool**
Modal Pool used to Manage modal listeners.

Creating a Modal Pool:
```kotlin
val AddTodoPool = ModalPool.multi(
    modal("Add Todo") {
        + Row(
            input(
                id = "todo",
                label = "Todo",
                style = TextInputStyle.PARAGRAPH
            )
        )
    }
)
```
Create a Listener with random id
```kotlin
val onSubmitTodo = AddTodoPool.listen { event ->
  state.update(event) {
    todos += event.value("todo")
  }
}
```
Getting Modal instance
```kotlin
val onAddTodo by onClick {
  val modal = AddTodoPool.next(onSubmitTodo)
  it.replyModal(modal).queue()
}
```

### Convert Interface
By extending the Convert interface, you can simply your code on LambdaList

**Before:**
```kotlin
{
    //Create a MessageEmbed and convert it to Component
    + embed(title = "Hello World").convert() 
}
```
**Now:**
```kotlin
{
    //MessageEmbed that extended the Convert<Component> interface
    //doesn't need to call convert manually
    + embed(title = "Hello World")
}
```

### UI Once and `component.buildMessage()`
UIOnce is used for components that rendered once only
<br>
However, it will throw an exception if you try to access the `ui` property

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

//getting localized text
ch("todo")
ch["form"]("new_content")
```

## Coming soon

We will move to Kord soon which is a better discord api written in kotlin
