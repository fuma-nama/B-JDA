> BJDUI is already outdated, please use [DUI](https://github.com/SonMooSans/discord-ui) instead
> 
<img src="https://i.ibb.co/BLSNNcQ/UI-1-25x-1.png" alt="diagram" style="max-width: 500px" />

# BJDUI
A Discord Message UI Framework inspired by React

## Getting Started
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
                success()
            }
        }
    }
}
```
Create a state by using `val state = useState()`
<br>
and Invoke `state.update` to update state and re-render the component

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