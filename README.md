# cs61b
Contains projects from UC Berkeley's Data Structures and Algorithms course.
Project 0: Blocks
The Blocks puzzle in this project has numerous variations available on the web and in mobile apps (for example, you can play an online Blocks Puzzle).
You are presented with a grid of square cells (initially empty) and a set of some number of pieces formed from configurations of square, cell-sized blocks
(we'll call these pieces the hand), which you try to arrange in order to cover empty cells of the grid. After each piece is placed, any rows and columns of
the grid that have become completely filled are cleared. After you place all the pieces in the hand, it is refilled with the same number of pieces as before.
Play continues until none of the pieces in the hand can fit anywhere on the board.

Project 1: Enigma
Designed Enigma machine from world war II.

Project 2: Ataxx
Ataxx is a two-person game played with red and blue pieces on a 7-by-7 board. As illustrated below, there are two possible kinds of moves:

Extending - you can extend from a piece of your own color by laying down a new piece of your color in an empty square next to that existing piece
(horizontally, vertically, or diagonally).
Jumping - you can jump by moving a piece of your own color to an empty, non-adjacent square that is no more than two rows and no more than two columns distant.

In either case, all opposing pieces that are next to the previously empty destination square are replaced by pieces of your color.

The red player goes first, and the two players alternate until there are no more moves possible or until there have been 25 consecutive jumps between both players.
You are allowed to skip a move (we will call this a pass) only if you have at least one piece on the board, but no legal move.
The winner is the player who has the most pieces at the end of the game; thus, you don't automatically lose just because you can't move.
The one exception to this condition is if you have no pieces left on the board. In this case, you have no way of winning, so you will automatically lose.
It is possible also to tie, when both sides end up with the same number of pieces on the board and: neither player can make a move, or
the maximum number of jumps (25 consecutive across both players) has been reached.

Project 3: Gitlet
