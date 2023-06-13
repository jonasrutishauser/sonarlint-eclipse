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
package org.sonarlint.eclipse.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeFilter;
import org.sonarlint.eclipse.core.SonarLintLogger;

final class ListNodeFilter implements NodeFilter {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final boolean ordered;

  private final Composite listComposite;

  ListNodeFilter(Composite parent, boolean ordered) {
    this.listComposite = new Composite(parent, SWT.NONE);
    listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    var gridLayout = new GridLayout();
    gridLayout.verticalSpacing = 0;
    gridLayout.marginHeight = 0;
    listComposite.setLayout(gridLayout);
    this.ordered = ordered;
  }

  @Override
  public FilterResult head(Node node, int depth) {
    if (node instanceof Element) {
      var elem = (Element) node;
      switch (elem.normalName()) {
        case "li": {
          var liComposite = new Composite(listComposite, SWT.NONE);
          var layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
          liComposite.setLayoutData(layoutData);
          var gridLayout = new GridLayout(2, false);
          gridLayout.verticalSpacing = 0;
          gridLayout.marginHeight = 0;
          liComposite.setLayout(gridLayout);
          var bullet = new Label(liComposite, SWT.LEFT | SWT.WRAP);
          bullet.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
          bullet.setText("\u2022");
          var nodeFilter = new ParagraphNodeFilter(liComposite);
          elem.childNodes().forEach(n -> n.filter(nodeFilter));
          return FilterResult.SKIP_ENTIRELY;
        }
      }
    } else if (node instanceof TextNode && !((TextNode) node).isBlank()) {
      var label = new Label(listComposite, SWT.LEFT | SWT.WRAP);
      label.setText(((TextNode) node).getWholeText());
      return FilterResult.SKIP_ENTIRELY;
    }
    LOG.debug("Unsupported node in a list: " + node);
    return FilterResult.SKIP_ENTIRELY;
  }

}
