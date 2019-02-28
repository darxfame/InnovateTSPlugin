package innovate;

import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.ControllerException;
import com.efiAnalytics.plugin.ecu.UiCurve;
import com.efiAnalytics.plugin.ecu.UiTable;
import com.efiAnalytics.plugin.ecu.servers.UiSettingServer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Philip Tobin
 */
public class UiDisplaySample extends JPanel{
    ControllerAccess controllerAccess = null;
    JPanel pWidget = new JPanel();
    JPanel pSelection = new JPanel();
    JComboBox selUiComponent = new JComboBox();
    UiSettingServer settingServer = null;

    public UiDisplaySample(){
        setBorder(BorderFactory.createTitledBorder("Display and TunerStudio UI Component by name something"));
        setLayout(new BorderLayout());
        pSelection.setMinimumSize(new Dimension(180, 180));
        pSelection.setLayout(new BorderLayout());
        pSelection.add(BorderLayout.WEST, selUiComponent);
        add(BorderLayout.NORTH, pSelection);

        // Provide and Action for the selection list
        selUiComponent.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    ItemWrapper iw = (ItemWrapper) e.getItem();
                    selectUiComponent(iw.getComponentName());
                }
            }
        });

        pWidget.setLayout(new FlowLayout(FlowLayout.CENTER));
        pWidget.setPreferredSize(new Dimension(500, 400));
        add(BorderLayout.CENTER, pWidget);
    }

    public void initialize(ControllerAccess controllerAccess){
        this.controllerAccess = controllerAccess;
        String mainConfig = controllerAccess.getEcuConfigurationNames()[0];
        selUiComponent.removeAllItems();
        pWidget.removeAll();
        try {
            this.settingServer = controllerAccess.getUiComponentServer(mainConfig);
            for(Iterator<UiTable> it = settingServer.getUiTable().iterator(); it.hasNext();){
                UiTable table = it.next();
                ItemWrapper iw = new ItemWrapper(table.getName(), "Table");
                selUiComponent.addItem(iw);
            }
            for(Iterator<UiCurve> it = settingServer.getUiCurves().iterator(); it.hasNext();){
                UiCurve curve = it.next();
                ItemWrapper iw = new ItemWrapper(curve.getName(), "Curve");
                selUiComponent.addItem(iw);
            }
            for(Iterator<String> it = settingServer.getUiPanelNames().iterator(); it.hasNext();){
                String panel = it.next();
                ItemWrapper iw = new ItemWrapper(panel, "Panel");
                selUiComponent.addItem(iw);
            }
        } catch (ControllerException ex) {
            Logger.getLogger(UiDisplaySample.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        // There are no guarantees that a UiComponent Exists for every Parameter, but we can
        // demonstrate the use when they exist...
        String[] allParameters = controllerAccess.getControllerParameterServer().getParameterNames(mainConfig);
        for(int i=0; i<allParameters.length; i++){
            ItemWrapper iw = new ItemWrapper(allParameters[i], "Parameter");
            selUiComponent.addItem(iw);
        }
    }

    private void selectUiComponent(String componentName){
        clearWidgetPanel();

        if(componentName != null && !componentName.isEmpty()){
            // Ask for the component
            Component comp = settingServer.getUiComponent(componentName);
            if(comp != null){
                // I guess I have it, but I'm pretty blind to what "it" is.
                pWidget.add(comp);
                pWidget.repaint();
            }else{
                JOptionPane.showMessageDialog(this, "Could not find: "+ componentName);
            }
        }
    }

    protected void clearWidgetPanel(){
        for(int i=0; i<pWidget.getComponentCount(); i++){
            settingServer.disposeUiComponent(pWidget.getComponent(i));
        }
        pWidget.removeAll();
    }

    class ItemWrapper {
        private String componentName = null;
        private String componentType = null;

        ItemWrapper(String name, String type){
            this.componentName = name;
            this.componentType = type;
        }

        public String toString(){
            return getComponentType() +" - " + getComponentName();
        }

        /**
         * @return the componentName
         */
        public String getComponentName() {
            return componentName;
        }

        /**
         * @param componentName the componentName to set
         */
        public void setComponentName(String componentName) {
            this.componentName = componentName;
        }

        /**
         * @return the componentType
         */
        public String getComponentType() {
            return componentType;
        }

        /**
         * @param componentType the componentType to set
         */
        public void setComponentType(String componentType) {
            this.componentType = componentType;
        }
    }
}
