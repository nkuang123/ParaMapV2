package main.java.frontend;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The collateral class contains array of connectors and renders them.
 * The rendering can be called in a different way. E.g. JConnectors cn be just
 * added as usual component. In this case programmer must care about their size,
 * and layout.
 *
 */
public class ConnectorContainer extends JPanel {
//public class ConnectorContainer extends JScrollPane {

    ArrayList<JConnector> connectors;

    public ConnectorContainer() {
    }

    public ConnectorContainer(ArrayList<JConnector> connectors) {
        this.connectors = connectors;
    }

    public void setConnectors(ArrayList<JConnector> connectors) {
        this.connectors = connectors;
    }

    public ArrayList<JConnector> getConnectors() {
        return connectors;
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (connectors != null) {
            for (int i = 0; i < connectors.size(); i++) {
                if (connectors.get(i) != null) {
                    connectors.get(i).paint(g);
                }
            }
        }
    }
}

