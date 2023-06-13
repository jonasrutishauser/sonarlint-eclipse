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

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jsoup.Jsoup;
import org.jsoup.select.NodeTraversor;
import org.sonarlint.eclipse.core.internal.extension.SonarLintExtensionTracker;

/** Utility class used for parsing the HTML rule description into native elements */
public final class HTMLUtils {

  private HTMLUtils() {
    // utility class
  }

  /**
   *  Parse HTML into native elements and only fallback on {@link SonarLintWebView} for non-parseable elements
   *
   *  @param html to be parsed
   *  @param parentWithGridLayout to add the elements to
   *  @param languageKey required for code snippet (for syntax highlighting)
   */
  public static void parseIntoElements(String html, Composite parentWithGridLayout, String languageKey) {
    var doc = Jsoup.parse(html);

    NodeTraversor.filter(new HtmlNodeFilter(languageKey, parentWithGridLayout), doc);
  }

  static void createSourceViewer(String html, Composite parent, String languageKey) {
    // Configure the syntax highlighting based on the rule language key and if a configuration and document partitioner
    // is provided by any plug-in via the extension mechanism.
    // INFO: Configuration must extend of org.eclipse.jface.text.source.SourceViewerConfiguration
    // INFO: Document partitioner must implement org.eclipse.jface.text.IDocumentPartitioner
    var configurationProviders = SonarLintExtensionTracker.getInstance().getSyntaxHighlightingProvider();
    SourceViewerConfiguration sourceViewerConfigurationNullable = null;
    for (var configurationProvider : configurationProviders) {
      var sourceViewerConfigurationOptional = configurationProvider.sourceViewerConfiguration(languageKey);
      if (sourceViewerConfigurationOptional.isPresent()) {
        sourceViewerConfigurationNullable = sourceViewerConfigurationOptional.get();
        break;
      }
    }

    IDocumentPartitioner documentPartitionerNullable = null;
    for (var configurationProvider : configurationProviders) {
      var documentPartitionerOptional = configurationProvider.documentPartitioner(languageKey);
      if (documentPartitionerOptional.isPresent()) {
        documentPartitionerNullable = documentPartitionerOptional.get();
        break;
      }
    }

    var snippetElement = new SourceViewer(parent, null, SWT.BORDER | SWT.H_SCROLL);
    var gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalIndent = 10;
    snippetElement.getTextWidget().setLayoutData(gridData);

    var content = new Document(html);
    if (sourceViewerConfigurationNullable != null && documentPartitionerNullable != null) {
      content.setDocumentPartitioner(
        sourceViewerConfigurationNullable.getConfiguredDocumentPartitioning(snippetElement),
        documentPartitionerNullable);
      content.setDocumentPartitioner(documentPartitionerNullable);
      documentPartitionerNullable.connect(content);
    }

    if (sourceViewerConfigurationNullable != null) {
      snippetElement.configure(sourceViewerConfigurationNullable);
    }

    snippetElement.setDocument(content);
    snippetElement.setEditable(false);
  }
}
