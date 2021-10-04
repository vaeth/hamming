# hamming

(C) Martin Väth (martin at mvath.de)

This project is under the Creative Commons CC-BY-4.0 license.
SPDX-License-Identifier: CC-BY-4.0

## Find a minimal covering of a Hamming hypercube with a given Hamming distance

The reason for this project is a challenge from
Rainer Rosenthal <r.rosenthal at web.de> in de.sci.mathematik:

To find a minimal set of binary lists of length 11 such that the Hamming
distance to every other binary list of length 11 is at most 3.

In other words: The challenge is to find a minimal covering of the hypercube
(of dimension 11) with “balls” of Hamming distance 3.

This is a backtracking algorithm for the problem with some heuristics.

The constants 11 and 3 above are hardcoded java constants which can easily be
modified in the source code.

To compile the code just call
```
javac Hamming.java
```
with a java 8 compiler. No dependencies are needed.

After that the usage is
```
java Hamming maxSize overlap stopOverlapLevel showLevel [point ...]
```
Explanations of the parameters:

1. `maxSize` denotes the maximal list sizes to output. After this size the
    backtracking is pruned. For the given problem (of dimension 11 and
    Hamming distance 8) it is known that there are solutions of size 16,
    so any larger results than 16 are probably uninteresting and you should
    therefore use 16 (or 15 if you are coutageous and want to risk to see no
    output).
2. `overlap` is a heuristic used to prune the backtracking further:
   First, only points are considered whose Hamming balls of radius 3 have the
   *minimal* intersection with the Hamming neighborhood of distance 3 of the
   previous points. If `overlap` is n > 0, then as next heuristic pruning steps
   only points are considered whose Hamming balls of radius 3+1 ... 3+n
   (starting with 3+1) have *maximal* intersection with the Hamming
   neighborhood of distance 3 of the previous points. The intuition is that
   these balls are as disjoint of the previous balls but “share maximal sides”
   or “form maximal clusters” with the previous balls. If `overlap` is n = 0,
   then only the first heuristic is used. It does probably not make sense to
   specify any value larger than 3 - 1 = 2.
3. `stopOverlapLevel` is the solution size at which only the first heuristic
   is used.
4. `showLevel` is an indicator how much progress information you want to see:
   After that the solution size that backtracking algorithm works without any
   progress information output. Otherwise, at each step in each level a
   progress indicator is printed (incl. how many steps will be contained in
   the main loop of this level.)
5. `point` can be specified to pre-fill certain solution data. By default, the
    point `000...0` is always auto-included in the list of points to be used,
    but you can add more to force a specific type of solution:
    You can simply add other “solution” points in binary notation.

A good call for trying is
```
java Hamming 16 2 9 9
```
which will soon output a lot of solutions of size 16.

Please inform the author, if you manage to find a smaller solution.
