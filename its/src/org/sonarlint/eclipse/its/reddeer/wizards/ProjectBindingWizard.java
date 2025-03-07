/*
 * SonarLint for Eclipse ITs
 * Copyright (C) 2009-2023 SonarSource SA
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
package org.sonarlint.eclipse.its.reddeer.wizards;

import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.DefaultText;

public class ProjectBindingWizard extends WizardDialog {
  public ProjectBindingWizard() {
    super(new DefaultShell());
  }

  public static class BoundProjectsPage extends WizardPage {

    public BoundProjectsPage(ReferencedComposite referencedComposite) {
      super(referencedComposite);
    }

    public void clickAdd() {
      new PushButton("Add...").click();
    }
  }

  public static class ServerProjectSelectionPage extends WizardPage {

    public ServerProjectSelectionPage(ReferencedComposite referencedComposite) {
      super(referencedComposite);
    }

    public void waitForProjectsToBeFetched() {
      new WaitUntil(new ControlIsEnabled(new DefaultText(this)));
    }

    public void setProjectKey(String projectKey) {
      new DefaultText(this).setText(projectKey);
    }
  }

}
