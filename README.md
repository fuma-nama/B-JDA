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

### Creating a select app
```kotlin
class ResultPanelProps : IProps() {
  lateinit var answer: Answer
  lateinit var onConfirm: () -> Unit
  var score: Int = 0
  var isCorrect = false
}
val ResultPanel = FComponent.noState(::ResultPanelProps) {
  val onConfirm = ButtonClick {event ->
    ui.switchTo(WaitingPlayersPanel(), false)

    ui.edit(event) {
      props.onConfirm()
    }
  };

  {
    + Embed()..{
      with (props) {
        title = "The Answer is ${answer.name} with ${answer.votes} Votes"
        description = "Now you have ${props.score} Scores"

        color = if (isCorrect) Color.GREEN else Color.RED
      }
    }

    + Row() -{
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

### Update Message after update
It is painful to real-time update messages in multiplayer game

Now you can write it clearly with hooks or manually update
#### You have two ways:
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
  updateState {
      player.score++
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


## Coming soon

We will move to Kcord soon which is a better discord api written in kotlin
