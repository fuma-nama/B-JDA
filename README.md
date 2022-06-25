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
@CommandGroup(name = "todo", description = "TODO List")
class MainController {
    @Command(name = "create", description = "Create TODO List")
    fun create(
        @Event event: SlashCommandInteractionEvent,
    ) {
        event.reply("Hi").queue()
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
class App : Component<IProps, App.State>(IProps()) {
    class State {
        var selected: String? = null
    }

    init {
        this.state = State()
    }

    private val onSelect = MenuSelect { event ->
        updateState {
            selected = event.selectedOptions.getOrNull(0)?.value
        }

        println(state.selected)
        ui.edit(event)
    }

    override fun onRender(): Children {
        return {
            +Content("My App")

            +RowLayout() - {
                +Menu(onSelect) {
                    placeholder = "Select a Item"

                    options = createOptions(
                        state.selected,
                        "Hello World" to "hello"
                    )
                }
            }
        }
    }
}
```
Invoke `updateState` to update state and render the component again
<br>
Normally it should be synchronous but in some cases it is async.

### Update Message after update

#### You have two ways:
- ##### Auto update (For updating multi messages realtime)

  it will update listened hooks when ui is updated
  <br>
  However, the message will update multi times when replying to an interaction event
  <br>
  which is listened as it won't detect if the message is updated manually
  ```kotlin
  event.reply(ui.build()).queue { hook ->
      ui.listen(InteractionUpdateHook(hook))
  }
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
