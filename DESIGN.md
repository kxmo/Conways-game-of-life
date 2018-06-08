# Architecture
The program is divided into five broad parts, each of which will be discussed in turn:
1. Core
2. Data structures
3. Main
4. Parsing
5. UI

The tests for each part, if they exist, are contained within part.sub.tests. For example the core logic tests are in core.logic.tests.

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