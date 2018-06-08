# Architecture
The program is divided into five broad parts, each of which will be discussed in turn:
1. Core
2. Data structures
3. Main
4. Parsing
5. UI

The tests for each part, if they exist, are contained within part.sub.tests. For example the core logic tests are in core.logic.tests.

The call order will usually look like:

* main.main()
  * ui.X.AssetLoader - where X is the type of interface, and AssetLoader is an implementer of main.interfaces.AssetLoader
    * ui.X.userInterface.run() - called by main, because userInterface is an implementer of ui.interfaces.Displayable
      * ui.GameManager - used by the user interface to run the game, will call update(Board) on the user interface
        * core.logic.Generation
        * core.structures.Board

## Core
The core contains the rules and structures necessary for the execution of the Game of life. As such core is divided into two parts:
1. Core logic
2. Core structures

Core logic contains the Rules which work on individual cells and their neighbours, and Generation which operates on a Board applying the rules as appropriate.

Core structures contains the data structures strictly related to the game of life. The Cell, an enum that lists the valid states of a position within the universe is within core.structures. The Board, a representation of the universe that contains cells, is also contained within core.structures. More general structures, like ImmutableSet which is used to implement Board, are contained in datastructures.

As a general rule the only classes outside of core that use core.structures should be those that interact with the game directly. The user interfaces are within that group, for example, because they need to know about Cell states to output their states.

## Data structures
General purpose data structures that facilitate the execution of the program. Pair<A,B> for example is used in many places throughout the program to return two items at a time.

## Main
Main contains the entry point to the program. main (with a lower case start) refers to the main method, the entry point to the program. main parses commandline arguments to select an interface, passes the remaining arguments to the UI, and calls UI.run().
If the UI could not be loaded for any reason main:
* Prints to stderr the message provided by the UI loader
* Exits with code 1

If main contains a logic error and neither a UI or error have been received then main exits with code 2.

## Parsing
Parsing contains utilities for parsing commandline arguments.
This package may be moved in future.

## UI

The UI portion of the program is the most complex and is divided into five parts:
1. Asset Loaders
2. Displayable
3. Observers and Observable
4. GameManager
5. CellDisplayer

### Asset loaders

Asset loaders implement main.interfaces.AssetLoader and are responsible for loading all required assets for a UI. If an AssetLoader is able to reasonably display a message to the user via the UI it is supposed to be loading assets for then it must do so; if it cannot then the loader must throw an IOException with a message explaining the problem and (if applicable) how to fix it - this message will be output to stderr by main, and the program will then exit.

If assets are to be loaded (or maintained, such as a web socket) at runtime by the UI the UI is under no circumstances to crash the program - appropriate error messages must be displayed to the user in the event of asset loading failure during runtime. 

### Displayable

Displayable is the interface that UIs must implement to be called by main, and is the interface returned by an AssetLoader once the appropriate assets have been loaded.

Two functions are defined, one from GenericObserver which will be discussed in the Observers and Observable section. Displayable defines one new function: void run(). This function is called by main on the Displayable after it has been loaded by the AssetLoader.

The Displayable may then start the game of life, if applicable to the user interface. A GUI, for example, may wait for the user manually start the game by clicking a button. The GUIs responsibility with regards to Displayable is then to not allow the user to start the game before run is called.

Almost all classes implementing Displayable will want a GameManager and to extend CellDisplay, whose sections can be found below. 

### CellDisplayer

Cell displayer is an abstract class that almost all UIs will want to extend. The purpose of this class is to centralise the conversion of cells into arbitrary types based on the cell's value.

On construction you provide two values for Alive and Dead respectively, and during execution you provide a cell to output(Cell) and you receive one of objects you provided on construction based on the state of the provided cell.

An alternative to the switch would be to have an interface or abstract type for Cells, but that would result in potentially numerous Cell objects of various types which could lead to subtle errors. An interface or abstract class also misrepresents the type by suggesting that it's possible to have values other than the currently provided values.

### Observers and Observable

When the game is running the UI needs to be updated when changes occur. This is achieved by the GameManager implementing Observable and all Displayables implementing Observer.

Whenever a change is made to the Board (this is determined in part by the GameManager as described next) the Observer (the UI) is sent the new Board.

The specific interfaces used to implement the Observer pattern are main.interfaces.GenericObservable and main.interfaces.GenericObserver. I did not use java.util.Observer or java.util.Observable because they take Object as their argument and there is no reason in this case to not have the type safety guarantees provided by generics.

### GameManager

The GameManager is not a necessary part of the UI but provides either continuous or timed execution of generations. This allows for UIs to be updated every X milliseconds and not need to manage the timing themselves.

The GameManager also allows the game to be run for a fixed number of generations before stopping so is recommended as the mechanism to update the UI and manage the game state.
