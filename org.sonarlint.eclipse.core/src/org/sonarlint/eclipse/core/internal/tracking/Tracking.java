/*
 * SonarLint for Eclipse
 * Copyright (C) 2015-2023 SonarSource SA
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.eclipse.core.internal.tracking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public class Tracking<RAW extends Trackable, BASE extends Trackable> {

  /**
   * Matched issues -> a raw issue is associated to a base issue
   */
  private final IdentityHashMap<RAW, BASE> rawToBase = new IdentityHashMap<>();
  private final IdentityHashMap<BASE, RAW> baseToRaw = new IdentityHashMap<>();

  private final Collection<RAW> raws;
  private final Collection<BASE> bases;

  public Tracking(Input<RAW> rawInput, Input<BASE> baseInput) {
    this.raws = rawInput.get();
    this.bases = baseInput.get();
  }

  /**
   * Returns an Iterable to be traversed when matching issues. That means
   * that the traversal does not fail if method {@link #match(Trackable, Trackable)}
   * is called.
   */
  public Iterable<RAW> getUnmatchedRaws() {
    var result = new ArrayList<RAW>();
    for (var r : raws) {
      if (!rawToBase.containsKey(r)) {
        result.add(r);
      }
    }
    return result;
  }

  public Map<RAW, BASE> getMatchedRaws() {
    return rawToBase;
  }

  public BASE baseFor(RAW raw) {
    return rawToBase.get(raw);
  }

  /**
   * The base issues that are not matched by a raw issue and that need to be closed.
   */
  public Iterable<BASE> getUnmatchedBases() {
    var result = new ArrayList<BASE>();
    for (var b : bases) {
      if (!baseToRaw.containsKey(b)) {
        result.add(b);
      }
    }
    return result;
  }

  boolean containsUnmatchedBase(BASE base) {
    return !baseToRaw.containsKey(base);
  }

  void match(RAW raw, BASE base) {
    rawToBase.put(raw, base);
    baseToRaw.put(base, raw);
  }

  boolean isComplete() {
    return rawToBase.size() == raws.size();
  }

}
