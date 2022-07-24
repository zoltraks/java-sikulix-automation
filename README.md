Sikulix Automation
------------------

**Sikulix Automation** is a simple RPA (robotic process automation) program which interacts with UI by clicking at specified elements on a screen and typing texts. 

This program is written in Java and is using [SikuliX](http://www.sikulix.com/) library.

By default program is looking for ``config.json`` in current working directory.

You may specify alternate file as first argument.

```
java -jar sikulix-automation.jar config.json
```

## Usage ##

Create JSON file ``config.json`` and execute program with ``java -jar`` command.

Scenario section defines base path for image files and array of steps to take.

```json
{
  "verbose": "true",
  "scenario": {
    "path": "C:/EXAMPLE/Scenario/",
    "steps": [
      {
        "click": "windows_11_start.png"
      },
      {
        "mouse": "RIGHT"
      },
      {
        "wait": "1.5"
      },
      {
        "click": "windows_11_type_search.png"
      },
      {
        "wait": "1.5"
      },
      {
        "type": "notepad"
      },
      {
        "wait": "1.5"
      },
      {
        "click": "search_notepad.png"
      },
      {
        "wait": "1.5"
      },
      {
        "message": "Hello, World"
      },
      {}
    ]
  }
}
```

Images used to recognize UI elements to click in should be located in current working directory or specified by ``path`` in ``scenario`` section.

![](media/image/images.png)

Every step may contain one or more elements from the list below.

 - click
 - type
 - mouse
 - message
 - wait

### click ###

Click inside UI element specified by image.

```json
{
  "click": "windows_start.png"
}
```

### wait ###

Wait for specified time in seconds.

```json
{
  "wait": "1.5"
}
```

### type ###

Type text.

```json
{
  "text": "notepad"
}
```

### mouse ###

Click down and up mouse button. Possible values are: "LEFT", "RIGHT", "MIDDLE".

```json
{
  "mouse": "RIGHT"
}
```

### message ###

Display message dialog.

```json
{
  "message": "Hello, World."
}
```

## Build ##

Use [Apache Maven](https://maven.apache.org/) to build project.

```
mvn -T 1C package
```