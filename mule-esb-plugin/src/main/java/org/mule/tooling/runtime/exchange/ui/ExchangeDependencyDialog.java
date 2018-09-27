package org.mule.tooling.runtime.exchange.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ListTableModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.tooling.runtime.exchange.ExchangeArtifact;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExchangeDependencyDialog extends DialogWrapper {
    final static Logger logger = Logger.getInstance(ExchangeDependencyDialog.class);

    //private final static String EXCHANGE_QUERY = "{ \"query\" : \"{ assets(query: { type: \\\"extension\\\" }) { runtimeVersion, groupId, assetId, version, classifier, name, description, status, type } }\" }";
    private final static String EXCHANGE_QUERY = "{ \"query\" : \"{ assets(query: { runtimeVersion: \\\"4.x\\\", type: \\\"extension\\\", offset: 0, limit: 100000 }) { runtimeVersion, groupId, assetId, version, classifier, name, description, status, type } }\" }";

    //TODO : we need to make it configurable
    private final static String ANYPOINT_EXCHANGE_URL = "https://anypoint.mulesoft.com/graph/api/v1/graphql";

    private JPanel contentPane;
    private JPanel mainPane;
    private ExchangeArtifactFilter filterComponent;

    private JBTable myInputsTable;

    private ListTableModel<ExchangeArtifact> myTableModel;

    private ExchangeArtifact[] mySelectedArtifacts = new ExchangeArtifact[] {};

    public ExchangeDependencyDialog() {
        super(true);
        super.init();

        setModal(true);
        setTitle("Add Connector Dependency from Anypoint Exchange");

        //createTable();

        filterComponent.setMyTable(myInputsTable);
    }

    public ExchangeArtifact[] getSelectedArtifacts() {
        return mySelectedArtifacts;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }

    public JPanel getMainPane() {
        return mainPane;
    }

    public void setMainPane(JPanel mainPane) {
        this.mainPane = mainPane;
    }

    public JBTable getMyInputsTable() {
        return myInputsTable;
    }

    public void setMyInputsTable(JBTable myInputsTable) {
        this.myInputsTable = myInputsTable;
    }

    private void createTable() {
        myTableModel = new ExchangeArtifactTableModel();

        myInputsTable.setAutoCreateRowSorter(true);
        myInputsTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        updateTable();

        myInputsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                logger.info("Number of selected rows " + myInputsTable.getSelectedRows().length);
                for (int si : myInputsTable.getSelectedRows())
                    logger.info(">>> next row " + si);

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();

                List<ExchangeArtifact> artifacts = new ArrayList<>();

                if (!lsm.isSelectionEmpty()) {
                    // Find out which indexes are selected.
                    int minIndex = lsm.getMinSelectionIndex();
                    int maxIndex = lsm.getMaxSelectionIndex();
                    for (int i = minIndex; i <= maxIndex; i++) {
                        if (lsm.isSelectedIndex(i)) {
                            int modelIndex = myInputsTable.convertRowIndexToModel(i);
                            logger.info(">>> ModelIndex " + modelIndex);
                            logger.info(">>> ViewIndex " + myInputsTable.convertRowIndexToView(i));

                            artifacts.add(myTableModel.getItem(modelIndex));
                        }
                    }
                }

                mySelectedArtifacts = artifacts.toArray(new ExchangeArtifact[] {});
            }
        });

        myInputsTable.getEmptyText().setText("No Connectors Found.");
        myInputsTable.setColumnSelectionAllowed(false);
        myInputsTable.setRowSelectionAllowed(true);
        myInputsTable.setShowGrid(true);
        myInputsTable.setDragEnabled(false);
        myInputsTable.setShowHorizontalLines(true);
        myInputsTable.setShowVerticalLines(true);
        myInputsTable.setIntercellSpacing(new Dimension(0, 0));
        myInputsTable.setExpandableItemsEnabled(true);
        myInputsTable.setStriped(true);
        myInputsTable.setRowHeight(myInputsTable.getRowHeight() + 10);
        myInputsTable.setVisible(true);
    }

    private void updateTable() {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(ANYPOINT_EXCHANGE_URL);
            post.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(EXCHANGE_QUERY);
            post.setEntity(entity);

            StringBuffer jsonResponse = new StringBuffer();

            HttpResponse httpResponse = client.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(httpResponse.getEntity().getContent()));
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    jsonResponse.append(inputLine);
                }
                reader.close();

                logger.debug("JSON Response is " + jsonResponse.toString());
            }
            ((CloseableHttpClient) client).close();

            final List<ExchangeArtifact> artifacts = new ArrayList<>();

            JSONObject responseObject = new JSONObject(jsonResponse.toString());
            JSONArray assets = responseObject.getJSONObject("data").getJSONArray("assets");

            for (int i = 0; i < assets.length(); i++) {
                JSONObject nextAsset = assets.getJSONObject(i);
                ExchangeArtifact nextArtifact = new ExchangeArtifact(
                        nextAsset.isNull("groupId") ? "N/A" : nextAsset.getString("groupId"),
                        nextAsset.isNull("assetId") ? "N/A" : nextAsset.getString("assetId"),
                        nextAsset.isNull("classifier") ? "mule-plugin" : nextAsset.getString("classifier"),
                        nextAsset.isNull("version") ? "N/A" : nextAsset.getString("version"),
                        nextAsset.isNull("runtimeVersion") ? "N/A" : nextAsset.getString("runtimeVersion"),
                        nextAsset.isNull("name") ? "N/A" : nextAsset.getString("name"),
                        nextAsset.isNull("description") ? "N/A" : nextAsset.getString("description")
                );
                artifacts.add(nextArtifact);
            }

            myTableModel.setSortable(true);
            myTableModel.setItems(artifacts);
            myInputsTable.setModel(myTableModel);

//            sortTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
        myInputsTable = new JBTable();
        createTable();
    }
    
    private void sortTable() {
        DefaultRowSorter sorter = (DefaultRowSorter)myInputsTable.getRowSorter();
        sorter.setComparator(0, new Comparator<ExchangeArtifact>() {
            @Override
            public int compare(ExchangeArtifact o1, ExchangeArtifact o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        sorter.setComparator(1, new Comparator<ExchangeArtifact>() {
            @Override
            public int compare(ExchangeArtifact o1, ExchangeArtifact o2) {
                return o1.getVersion().compareTo(o2.getVersion());
            }
        });
        sorter.setComparator(2, new Comparator<ExchangeArtifact>() {
            @Override
            public int compare(ExchangeArtifact o1, ExchangeArtifact o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });
        sorter.setComparator(3, new Comparator<ExchangeArtifact>() {
            @Override
            public int compare(ExchangeArtifact o1, ExchangeArtifact o2) {
                return o1.getGroupId().compareTo(o2.getGroupId());
            }
        });
        sorter.setComparator(4, new Comparator<ExchangeArtifact>() {
            @Override
            public int compare(ExchangeArtifact o1, ExchangeArtifact o2) {
                return o1.getArtifactId().compareTo(o2.getArtifactId());
            }
        });
        sorter.setComparator(5, new Comparator<ExchangeArtifact>() {
            @Override
            public int compare(ExchangeArtifact o1, ExchangeArtifact o2) {
                return o1.getClassifier().compareTo(o2.getClassifier());
            }
        });
        sorter.setComparator(6, new Comparator<ExchangeArtifact>() {
            @Override
            public int compare(ExchangeArtifact o1, ExchangeArtifact o2) {
                return o1.getRuntimeVersion().compareTo(o2.getRuntimeVersion());
            }
        });

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        sorter.toggleSortOrder(0);

        sorter.sort();
    }
}
