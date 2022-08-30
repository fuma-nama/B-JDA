<div align="center">
    <img src="https://i.ibb.co/d6rcGkh/BJDA.jpg" alt="banner"/>
    <br>

# BJDA - Better Java Discord API
![GitHub](https://img.shields.io/github/license/SonMooSans/B-JDA) ![Maven Central](https://img.shields.io/maven-central/v/io.github.sonmoosans/bjda-core) ![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.sonmoosans/bjda-core?server=https%3A%2F%2Fs01.oss.sonatype.org%2F) ![GitHub Repo stars](https://img.shields.io/github/stars/SonMooSans/B-JDA?style=social)

A Discord Bot Framework based on JDA written in **Kotlin**
<br>
A UI System inspired by React.js
<br>
With many utilities to speed up you development.
</div>

## Installation
Install the Core
```xml
<dependency>
  <groupId>io.github.sonmoosans</groupId>
  <artifactId>bjda-core</artifactId>
  <version>6.0.1</version>
</dependency>
```

## Why BJDA

## Modularized
Keep it Light, Only Import all Needed Modules for your application 

## Commands Utilities
Create **Slash Commands**, **Text Commands** with few lines of code

```kotlin
command(name = "hello", description = "Say Hello") {
  
  val size = int("size", "Size of example") {
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
## Message Component UI Framework
> We highly recommend using [DUI](https://github.com/SonMooSans/discord-ui) instead of BJDUI
> BJDUI is already outdated, and stopped maintain

Install **BJDUI** for UI Module
```xml
<dependency>
    <groupId>io.github.sonmoosans</groupId>
    <artifactId>bjdui</artifactId>
    <version>BJDA_VERSION</version>
</dependency>
```
Create an interactive UI easily in few lines of code
<br>
And Update UI with component state

*Beautiful, Readable, Flexible*

<img src="https://i.ibb.co/QrP1s16/example-2.gif" alt="example-2" />

[Full Demo](https://github.com/SonMooSans/bjda-example) of above example

### Built-in Components
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

## Getting Started
You can see the documentation [here](https://github.com/SonMooSans/B-JDA/wiki)

### Demo

* **Full Demo of a Todo App:** [Todo Bot](https://github.com/SonMooSans/bjda-example)
* **Demo for production:** [Dishub](https://github.com/SonMooSans/dishub) is a great example with high performance.

### Creating a Slash Command
```kotlin
val TestCommand = command(name = "test", description = "Example Command") {
    val size = int("size", "Size of Text")
        .optional()
        .map({"${it}px"}) {
            choices {
                choice("sm", 1)
                choice("md", 2)
                choice("lg", 5)
            }

            default { 0 }
        }

    execute {
        event.reply(size.value).queue()
    }
}
```

<img src="https://i.ibb.co/BLSNNcQ/UI-1-25x-1.png" alt="diagram" style="max-width: 500px" />

## Coming soon

We will move to Kord which is a discord api written in kotlin
