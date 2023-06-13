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

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeFilter;
import org.sonarlint.eclipse.core.SonarLintLogger;

final class HtmlNodeFilter implements NodeFilter {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final String languageKey;
  private final Font h2Font;
  private final Composite parentWithGridLayout;
  private final Font h1Font;

  HtmlNodeFilter(String languageKey, Composite parentWithGridLayout) {
    this.languageKey = languageKey;
    this.parentWithGridLayout = parentWithGridLayout;
    var h1Desc = FontDescriptor.createFrom(JFaceResources.getHeaderFont()).increaseHeight(1);
    this.h1Font = h1Desc.createFont(parentWithGridLayout.getDisplay());
    this.h2Font = JFaceResources.getHeaderFont();
    parentWithGridLayout.addDisposeListener(e -> h1Font.dispose());
  }

  @Override
  public FilterResult head(Node node, int depth) {
    if (node instanceof Document) {
      return FilterResult.CONTINUE;
    } else if (node instanceof Element) {
      var elem = (Element) node;
      switch (elem.normalName()) {
        case "html":
        case "body":
          return FilterResult.CONTINUE;
        case "head":
          return FilterResult.SKIP_ENTIRELY;
        case "pre": {
          HTMLUtils.createSourceViewer(elem.wholeText(), parentWithGridLayout, languageKey);
          return FilterResult.SKIP_ENTIRELY;
        }
        case "p": {
          var nodeFilter = new ParagraphNodeFilter(parentWithGridLayout);
          elem.childNodes().forEach(n -> n.filter(nodeFilter));
          return FilterResult.SKIP_ENTIRELY;
        }
        case "h1": {
          var h1 = new Label(parentWithGridLayout, SWT.LEFT | SWT.WRAP);
          var layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
          layoutData.verticalIndent = 10;
          h1.setLayoutData(layoutData);
          h1.setText(elem.text());
          h1.setFont(h1Font);
          return FilterResult.SKIP_ENTIRELY;
        }
        case "h2": {
          var h2 = new Label(parentWithGridLayout, SWT.LEFT | SWT.WRAP);
          var layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
          layoutData.verticalIndent = 5;
          h2.setLayoutData(layoutData);
          h2.setText(elem.text());
          h2.setFont(h2Font);
          return FilterResult.SKIP_ENTIRELY;
        }
        case "ol":
        case "ul": {
          var nodeFilter = new ListNodeFilter(parentWithGridLayout, elem.normalName().equals("ol"));
          elem.childNodes().forEach(n -> n.filter(nodeFilter));
          return FilterResult.SKIP_ENTIRELY;
        }
      }
    } else if (node instanceof TextNode) {
      if (!((TextNode) node).isBlank()) {
        var label = new Label(parentWithGridLayout, SWT.LEFT | SWT.WRAP);
        label.setText(((TextNode) node).text());
      }
      return FilterResult.SKIP_ENTIRELY;
    }

    LOG.debug("Unsupported node in html: " + node);
    return FilterResult.SKIP_ENTIRELY;
  }

}
