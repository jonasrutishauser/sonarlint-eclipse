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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeFilter;
import org.sonarlint.eclipse.core.SonarLintLogger;

final class ParagraphNodeFilter implements NodeFilter {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Composite pComposite;

  ParagraphNodeFilter(Composite parentWithGridLayout) {
    this.pComposite = new Composite(parentWithGridLayout, SWT.NONE);
    pComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

    var rowLayout = new RowLayout();
    rowLayout.wrap = true;
    rowLayout.marginTop = 0;
    pComposite.setLayout(rowLayout);

  }

  @Override
  public FilterResult head(Node node, int depth) {
    if (node instanceof Element) {
      var elem = (Element) node;
      switch (elem.normalName()) {
        case "a": {
          var link = new Link(pComposite, SWT.LEFT | SWT.WRAP);
          link.setText("<a>" + elem.wholeText() + "</a>");
          link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              BrowserUtils.openExternalBrowser(elem.attr("href"));
            }
          });
          return FilterResult.SKIP_ENTIRELY;
        }
        case "code": {
          var label = new Text(pComposite, SWT.LEFT | SWT.WRAP | SWT.READ_ONLY);
          label.setText(elem.wholeText());
          // var codeFontDesc = FontDescriptor.createFrom(JFaceResources.getTextFont());
          // var codeFont = codeFontDesc.createFont(pComposite.getDisplay());
          // label.setFont(codeFont);
          // label.setBackground(JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR));
          // label.addDisposeListener(e -> codeFont.dispose());
          return FilterResult.SKIP_ENTIRELY;
        }
        case "bold": {
          JFaceResources.getFontRegistry().getBold(
            JFaceResources.DIALOG_FONT);
          // TODO
        }
      }
    } else if (node instanceof TextNode) {
      if (!((TextNode) node).isBlank()) {
        var label = new Label(pComposite, SWT.LEFT | SWT.WRAP);
        label.setText(((TextNode) node).text());
      }
      return FilterResult.SKIP_ENTIRELY;
    }
    LOG.debug("Unsupported node in a paragraph: " + node);
    return FilterResult.SKIP_ENTIRELY;
  }

}
