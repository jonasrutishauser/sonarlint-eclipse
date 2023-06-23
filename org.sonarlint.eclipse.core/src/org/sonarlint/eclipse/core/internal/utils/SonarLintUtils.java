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
package org.sonarlint.eclipse.core.internal.utils;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import org.eclipse.core.resources.IResource;
import org.sonarlint.eclipse.core.internal.SonarLintCorePlugin;
import org.sonarlint.eclipse.core.internal.extension.SonarLintExtensionTracker;
import org.sonarsource.sonarlint.core.commons.Language;

public class SonarLintUtils {

  private SonarLintUtils() {
    // utility class, forbidden constructor
  }

  public static boolean isSonarLintFileCandidate(IResource resource) {
    if (!resource.exists() || resource.isDerived(IResource.CHECK_ANCESTORS) || resource.isHidden(IResource.CHECK_ANCESTORS)) {
      return false;
    }
    // Ignore .project, .settings, that are not considered hidden on Windows...
    // Also ignore .class (SLE-65)
    if (resource.getName().startsWith(".") || "class".equals(resource.getFileExtension())) {
      return false;
    }
    return true;
  }

  public static String getPluginVersion() {
    return SonarLintCorePlugin.getInstance().getBundle().getVersion().toString();
  }

  /**
   *  Enabled language should be consistent with https://www.sonarsource.com/products/sonarlint/features/eclipse!
   *  Exceptions are: - C/C++ only available with CDT (see CProjectConfiguratorExtension.whitelistedLanguages)
   *                  - Java/JSP only available with JDT (see JavaProjectConfiguratorExtension.whitelistedLanguages)
   */
  public static Set<Language> getEnabledLanguages() {
    var enabledLanguages = EnumSet.of(Language.ABAP, Language.APEX, Language.CSS, Language.COBOL, Language.HTML,
      Language.JS, Language.KOTLIN, Language.PHP, Language.PLI, Language.PLSQL, Language.PYTHON, Language.RPG,
      Language.RUBY, Language.SCALA, Language.SECRETS, Language.TSQL, Language.TS, Language.XML);
    var configurators = SonarLintExtensionTracker.getInstance().getAnalysisConfigurators();
    for (var configurator : configurators) {
      enabledLanguages.addAll(configurator.whitelistedLanguages());
    }
    return enabledLanguages;
  }

  public static int getPlatformPid() {
    return (int) ProcessHandle.current().pid();
  }

  public static ThreadFactory threadFactory(String name, boolean daemon) {
    return runnable -> {
      var result = new Thread(runnable, name);
      result.setDaemon(daemon);
      return result;
    };
  }
}
