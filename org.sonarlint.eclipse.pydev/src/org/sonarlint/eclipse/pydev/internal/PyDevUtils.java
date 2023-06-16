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
package org.sonarlint.eclipse.pydev.internal;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.python.pydev.editor.PyEditConfiguration;
import org.python.pydev.plugin.PyDevUiPrefs;
import org.python.pydev.ui.ColorAndStyleCache;

public class PyDevUtils {
  public static SourceViewerConfiguration sourceViewerConfiguration() {
    IPreferenceStore chainedPrefStore = PyDevUiPrefs.getChainedPrefStore();

    return new PyEditConfiguration(new ColorAndStyleCache(chainedPrefStore), null, chainedPrefStore);
  }

  public static IDocumentPartitioner documentPartitioner() {
    return null;
  }
}
