<p align="center">
    <img src="https://i.ibb.co/J33rn9j/bjda.png" width="200" alt="banner"/>
    <br>
    The Most Powerful Discord Bot Framework in Kotlin
</p>

# BJDA - Better Java Discord API
![GitHub](https://img.shields.io/github/license/SonMooSans/B-JDA) ![Maven Central](https://img.shields.io/maven-central/v/io.github.sonmoosans/bjda) ![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.sonmoosans/bjda?server=https%3A%2F%2Fs01.oss.sonatype.org%2F) ![GitHub Repo stars](https://img.shields.io/github/stars/SonMooSans/B-JDA?style=social)

A Discord Bot Framework based on JDA for **Kotlin**
<br>
Provides a flexible UI System which is inspired by React.js
<br>
And many utility functions to speed up you development.

## Installation
### Maven:
```xml
<dependency>
  <groupId>io.github.sonmoosans</groupId>
  <artifactId>bjda</artifactId>
  <version>5.0.0</version>
</dependency>
```

## Why BJDA

### Fully Modularized Design

You can manage all modules easily

### Beautiful, Readable, Flexible

Create an interactive UI easily in few lines of code
```kotlin
val app = UI {
    pager {
        text("Hello") {}
        embed {
            title = "Hello World"
        }
    }
}

app.reply(event)
```
Declare slash command 
```kotlin
command(name = "hello", description = "Say Hello") {
  name(DiscordLocale.CHINESE_TAIWAN, "測試命令")
  
  val size = long("size", "Size of example").map({ "${it}xl" }) {
    choices(
      "Small" to 2,
      "Medium" to 4,
      "Large" to 6
    )
    optional { 6 }
  }
  
  execute {
    event.reply("size: ${size()}").queue()
  }
}
```
Application Command is also supported
```kotlin
val UserHelloCommand = userCommand(name = "hello") {
  execute { event ->
    event.reply("Hello").queue()
  }
}
```
The coolest thing is you can even create a text command with Clikt
<br>
Notice that It is better to replace text commands with slash commands 
```kotlin
class Hello : TextCommand(name = "apps") { //TextCommand is based on Clikt
  override fun run() {
    UI(App()).reply(event.message)
  }
}
```

## Getting Started
You can see the documentation [here](https://github.com/SonMooSans/B-JDA/wiki)

<img src="https://i.ibb.co/nfddT3X/example-1.gif" alt="demo-gif" style="max-width: 500px" />

### Demo
Full Demo of a Todo App: https://github.com/SonMooSans/bjda-example
### Demo for production?
If your bot is going to be used by more than a hundred servers, you can take a look at [Dishub](https://github.com/SonMooSans/dishub).
<br>
It is a great example with high performance.
### Creating an App
```kotlin
val Panel = FComponent.component {
    val onConfirm by onClick { event ->
        println("Confirmed")
        ui.edit(event) //You may use defer edit for this example too
    };

    {
        embed {
            title = "Hello World"
        }

        row {
            button("Confirm") {
                id = onConfirm
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

### Creating a Slash Command
```kotlin
val MessageHelloCommand = command(name = "hello", description = "Hello World") {
  execute {
    event.reply("Hello").queue()
  }
}
```

<img src="https://i.ibb.co/BLSNNcQ/UI-1-25x-1.png" alt="diagram" style="max-width: 500px" />

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

## What's New Since 5.0.0

### ModalSubmit Hook
ModalSubmit finally comes with same usage of `ButtonClick` and `MenuSelect` Listeners

It also provides `onSubmit` and `onSubmitStatic` methods.
```kotlin
component {
    val onSubmitTodo by onSubmit { event ->
        event.reply("Hello World").queue()
    }
}
```

### New Components Syntax System
Since 5.0.0, we can finally use "kotlin" style to add children components.

**Before:**
```kotlin
{
    + Content("Message Content")
    + embed(title = "Hello World")
    + Pager()..{
        + Embed()..{
            title = "Title"
            color = Color.RED
        }
    }
}
```
**Now:**
```kotlin
{
    //don't need the operator anymore
    embed {
        title = "Hello World"
    }
    
    pager {
        embed {
            title = "Title"
            color = Color.RED
        }
    }
}
```
### Notes
We can use `ComponentCompanion..{}` or `FComponentConstructor..{}` to create component without adding it to children automatically

For class Components, you can also use the constructor instead.
<br>
Use the `-` operator if you only want to set the children

Example: `Pager()-{ children }`, `fComponentExample..{ name = "Something" }`

## Coming soon

We will move to Kord which is a discord api written in kotlin
