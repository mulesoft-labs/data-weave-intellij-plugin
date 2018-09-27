package org.mule.tooling.runtime.exchange.ui;

import com.intellij.ui.FilterComponent;
import com.intellij.ui.table.JBTable;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

public class ExchangeArtifactFilter extends FilterComponent {
    private JBTable myTable;

    public ExchangeArtifactFilter() {
        super("EXCHANGE_ARTIFACT_FILTER", 5);
        //this.myTable = myTable;
    }

    @Override
    public void filter() {
        String filterText = getFilter();
        DefaultRowSorter rowSorter = (DefaultRowSorter)myTable.getRowSorter();
        if (StringUtils.isEmpty(filterText))
            rowSorter.setRowFilter(null);
        else
            rowSorter.setRowFilter(RowFilter.regexFilter(filterText));
    }

    public JBTable getMyTable() {
        return myTable;
    }

    public void setMyTable(JBTable myTable) {
        this.myTable = myTable;
    }
}