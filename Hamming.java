
// (C) Martin V\"ath (martin at mvath.de)
// SPDX-License-Identifier: CC-BY-4.0

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Hamming {

  private static final int LENGTH = 11;
  private static final int DISTANCE = 3;

  private static final int TOTAL = (1 << LENGTH);
  private static final List<Integer> worst;
  static {
    worst = new ArrayList<Integer>();
    for (int i = 0; i < TOTAL; ++i) {
      worst.add(i);
    }
  }

  private static final int ARGS = 4;
  private static int maxSize = 16;
  private static int overlap = 2;
  private static int stopOverlapLevel = 2;
  private static int showLevel = maxSize;

  public static void main(String[] args) {
    if (args.length < ARGS) {
      System.err.printf("Usage: java Hamming maxSize overlap stopOverlapLevel showLevel [point ...]\n"
          + "maxSize [1...%d]: Maximal size to check for. Try 16.\n"
          + "overlap [0...%d]: Look for that much overlap as heuristics. Try 2.\n"
          + "stopOverlapLevel [1...maxSize]: Stop heuristics after that level. Try 9.\n"
          + "showLevel [1...maxSize]: Show level if at most that large. Try 9.\n",
        TOTAL,
        (DISTANCE + 1) / 2);
      return;
    }
    maxSize = Integer.parseInt(args[0]);
    overlap = Integer.parseInt(args[1]);
    stopOverlapLevel = Integer.parseInt(args[2]);
    showLevel = Integer.parseInt(args[3]);
    List<Integer> first = new ArrayList<>();
    first.add(0);
    for (int i = ARGS; i < args.length; ++i) {
      first.add(Integer.parseInt(args[i], 2));
    }
    Set<Integer> covered = covered(first);
    System.out.printf("Startsize %d covers %d|%d\n", first.size(), covered.size(), TOTAL);
    print(recurse(first, covered));
  }

  public static List<Integer> recurse(List<Integer> previous, Set<Integer> covered) {
    int size = previous.size();
    if (size > maxSize) {
      return worst;
    }
    // assert(covered.equals(covered(previous)));
    if (covered.size() == TOTAL) {
      print(previous);
      return previous;
    }
    List<Integer> result = new ArrayList<>(previous);
    List<Integer> candidates = almostDisjoint(covered, DISTANCE);
    if (size <= stopOverlapLevel) {
      for (int i = 1; i <= overlap; ++i) {
        candidates = maximalTouching(candidates, covered, DISTANCE + i);
      }
    }

    int bestSize = TOTAL;
    int bestAttempt = 0;
    int count = 0;
    for (int attempt : candidates) {
      if (size <= showLevel) {
        System.out.printf("Level %d: %d|%d\n", size, ++count, candidates.size());
      }
      Set<Integer> currentCovered = ball(attempt, DISTANCE);
      currentCovered.addAll(covered);
      result.add(attempt);
      List<Integer> currentResult = recurse(result, currentCovered);
      int attemptSize = currentResult.size();
      if (attemptSize < bestSize) {
        bestSize = attemptSize;
        bestAttempt = attempt;
      }
      result.remove(result.size() - 1);
    }
    result.add(bestAttempt);
    return result;
  }

  public static List<Integer> almostDisjoint(Set<Integer> covered, int radius) {
    return kIntersect(covered, minIntersect(covered, radius), radius);
  }

  public static List<Integer> maximalTouching(List<Integer> candidates, Set<Integer> covered, int radius) {
    if (candidates.size() == 1) {
      return candidates;
    }
    return kIntersect(candidates, covered, maxIntersect(candidates, covered, radius), radius);
  }

  public static int minIntersect(Set<Integer> covered, int radius) {
    int minimum = TOTAL;
    for (int i = 0; i < TOTAL; ++i) {
      Set<Integer> intersection = ball(i, radius);
      intersection.retainAll(covered);
      minimum = Math.min(intersection.size(), minimum);
    }
    return minimum;
  }

  public static int maxIntersect(Collection<Integer> candidates, Set<Integer> covered, int radius) {
    int maximum = -1;
    for (int i : candidates) {
      Set<Integer> intersection = ball(i, radius);
      intersection.retainAll(covered);
      maximum = Math.max(intersection.size(), maximum);
    }
    return maximum;
  }

  public static List<Integer> kIntersect(Set<Integer> covered, int k, int radius) {
    List<Integer> kIntersect = new ArrayList<>();
    for (int i = 0; i < TOTAL; ++i) {
      Set<Integer> intersection = ball(i, radius);
      intersection.retainAll(covered);
      if (intersection.size() == k) {
        kIntersect.add(i);
      }
    }
    return kIntersect;
  }

  public static List<Integer> kIntersect(Collection<Integer> candidates, Set<Integer> covered, int k, int radius) {
    List<Integer> kIntersect = new ArrayList<>();
    for (int i : candidates) {
      Set<Integer> intersection = ball(i, radius);
      intersection.retainAll(covered);
      if (intersection.size() == k) {
        kIntersect.add(i);
      }
    }
    return kIntersect;
  }

  public static Set<Integer> ball(int center, int radius) {
    Set<Integer> ball = new HashSet<>();
    addToBall(ball, center, radius);
    return ball;
  }

  public static void addToBall(Set<Integer> ball, int center, int radius) {
    ball.add(center);
    if (radius > 0) {
      for (int k = 0; k < LENGTH; ++k) {
        int bit = (1 << k);
        addToBall(ball, center ^ bit, radius - 1);
      }
    }
  }

  public static Set<Integer> covered(Collection<Integer> s) {
    Set<Integer> union = new HashSet<>();
    for (int i : s) {
      union.addAll(ball(i, DISTANCE));
    }
    return union;
  }

  public static void print(Collection<Integer> s) {
    System.out.printf("%s: [", s.size());
    boolean first = true;
    for (int i : s) {
      if (!first) {
        System.out.print(" ");
      }
      first = false;
      print(i);
    }
    System.out.println("]");
  }

  public static void print(int i) {
    for (int j = LENGTH; j > 0; --j) {
      System.out.print(((i & (1 << (j - 1))) != 0) ? "1" : "0");
    }
  }
}
