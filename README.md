# Conway's game of life

## TL;DR:
### What
This is an implementation of Conway's game of life written in Java 8.

### Why
I have made or contributed to a variety of projects, some of which use databases, GUIs, web interfaces, CLIs, custom data structures, and so on but no project where I have contributed to all of the above.
This project is a (rather artificial) attempt to have as many elements of a "real world application" as I can in one place.

### Where
The project is currently hosted on Github.

### When
Projects like this are never truly finished - there's always one more feature to add.

### How
1. Install a Java runtime that supports Java 8
2. Download the source code (this repository)
3. Compile the program
4. Execute the program
5. Set up a game of life

### Who
Me, and maybe you!
Currently the project has been written in it's entirety by me.
You are welcome to submit patches or suggestions in any way that suits you (as with all of my projects).
If you like I will add your name to this README or a list of contributors, or you can add it yourself in your patch.

## The long version

### What
This repository contains an implementation of Conway's game of life written in Java 8.

#### Conway's game of life
The game rules and description are paraphrased from [Wikipedia](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life).
Conway's game of life consists of a 2D universe on a grid, where every position on this grid contains a cell.
Every cell is in one of two states at any given moment:
1. Alive
2. Dead

Every cell has exactly eight neighbours: the other cells (alive or dead) surrounding it.
A cell's live neighbours are the subset of the eight neighbours that are Alive.

Every cell transitions from one state to another in an instant, and all cells transition at the same time.
Every cell transitions between states according to the following mutually exclusive rules:
* Alive and 2 or 3 live neighbours -> Alive
* Dead and exactly 3 live neighbours -> Alive
* Otherwise -> Dead

#### Design

The gory design details are available in the [design document](DESIGN.md).

### Why, where, and when
The same as their respective sections in the short version

### How

#### Install a Java 8 runtime
We are going to be compiling the program, so will be needing the Java Development Kit (JDK).
For example, the official builds from [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). 
Follow the installation instructions and optionally try run or compile the demos or samples available on the same page to test your installation worked.

#### Download the source code
Select the green `Clone or download` button on the top right of the repository
A small window titled `Clone with HTTPS` will appear, this is the window we use for both of the following instructions. You only need to follow one.

##### Github UI
1. Select `Download ZIP`
2. Save the file
3. Move it somewhere appropriate
4. Extract the files from the zip

##### Git
1. Copy the URL (keeping the .git at the end)
2. Move to an appropriate directory
3. Run `git clone [the url]` where [the url] is the text you copied

#### Compile the program
Coming soon

#### Execute the program
Coming soon

#### Set up a game of life
Coming soon