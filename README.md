<div align="center">
    <img src="https://i.ibb.co/d6rcGkh/BJDA.jpg" alt="banner"/>
    <br>

# BJDA - Better Java Discord API
![GitHub](https://img.shields.io/github/license/SonMooSans/B-JDA) ![Maven Central](https://img.shields.io/maven-central/v/io.github.sonmoosans/bjda) ![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.sonmoosans/bjda?server=https%3A%2F%2Fs01.oss.sonatype.org%2F) ![GitHub Repo stars](https://img.shields.io/github/stars/SonMooSans/B-JDA?style=social)

A Discord Bot Framework based on JDA written in **Kotlin**
<br>
A UI System inspired by React.js
<br>
With many utilities to speed up you development.
</div>

## Why BJDA

## Commands Utilities

Create **Slash Commands**, **Text Commands** with few lines of code

```kotlin
command(name = "hello", description = "Say Hello") {
  
  val size = long("size", "Size of example") {
    optional { 6 }
  }
  
  execute {
    event.reply("size: ${size()}").queue()
  }
}
```
**Application Command** is also supported
```kotlin
val UserHelloCommand = userCommand(name = "hello") {
  execute { event ->
    event.reply("Hello").queue()
  }
}
```
## React.js in Discord Bot
Create an interactive UI easily in few lines of code
<br>
Updating UI with component `state`

*Beautiful, Readable, Flexible*

<img src="https://i.ibb.co/QrP1s16/example-2.gif" alt="example-2" />

[Full Demo](https://github.com/SonMooSans/bjda-example) of above example

### Built-in Components
Code less, Do more
```kotlin
val app = UI {
    pager {
        embed {
            title = "Page 1"
        }
        embed {
            title = "Page 2"
        }
    }
}

app.reply(event)
```

## Installation
### Maven:
```xml
<dependency>
  <groupId>io.github.sonmoosans</groupId>
  <artifactId>bjda</artifactId>
  <version>5.1.0</version>
</dependency>
```

## Getting Started
You can see the documentation [here](https://github.com/SonMooSans/B-JDA/wiki)

### Demo

* **Full Demo of a Todo App:** [Todo Bot](https://github.com/SonMooSans/bjda-example)
* **Demo for production:** [Dishub](https://github.com/SonMooSans/dishub) is a great example with high performance.

### Creating an App
Example at YouTube: https://youtu.be/ksk890SdLvQ
```kotlin
val Panel = component {
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
Create a state by using `val state = useState()`
<br>
and Invoke `state.update` to update state and re-render the component

### Creating the Slash Command
```kotlin
val MessageHelloCommand = command(name = "hello", description = "Hello World") {
  execute {
      val ui = UI(
          Panel..{}
      )
      
      ui.reply(event)
  }
}
```

<br>

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
