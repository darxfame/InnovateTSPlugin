package innovate;

import com.efiAnalytics.plugin.ApplicationPlugin;
import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.ControllerException;
import com.efiAnalytics.plugin.ecu.MathException;
import com.efiAnalytics.plugin.ecu.OutputChannel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class InnovatePlugin extends JPanel implements ApplicationPlugin{

    JLabel rpmReadout = new JLabel();
    JTextField txtReqFuel = new JTextField();
    JPanel pReadouts = null;
    JPanel pSettings = null;
    // UiDisplaySample uiDisplay = null;
    ExpressionEvalSample evalSample = null;
    ParameterSample paramSample = null;
    
    ControllerAccess controllerAccess = null;

    boolean pluginEnabled = false;

    public InnovatePlugin(){
        buildUi();
        
    }

    private void buildUi() {
        setLayout(new BorderLayout());

        pReadouts = new JPanel();
        pReadouts.setLayout(new GridLayout(0,1,1,1));
        pReadouts.setBorder(BorderFactory.createTitledBorder("INNOVATE LC-2 AFR"));
        add(BorderLayout.CENTER, pReadouts);

        // uiDisplay = new UiDisplaySample();
        // add(BorderLayout.CENTER, uiDisplay);uiDisplay = new UiDisplaySample();
        // add(BorderLayout.CENTER, uiDisplay);

        JPanel pSouth = new JPanel();
        pSouth.setLayout(new GridLayout(0,1,2,2));
        // evalSample = new ExpressionEvalSample();
        // pSouth.add(evalSample);
        paramSample = new ParameterSample();
        pSouth.add(paramSample);
        add(BorderLayout.SOUTH, pSouth);
		

    }

    public String getIdName() {
        return "tsInnovateWBO";
    }

    public int getPluginType() {
        return PERSISTENT_DIALOG_PANEL;
        //return TAB_PANEL; // not supported in TunerStudio 1.3x .
    }

    public String getDisplayName() {
        return "Innovate WBO-COM(connect)";
    }

    public String getDescription() {
        // return "A example TunerStudio Plugin implementation to demonstrate "
                // + "the use of the TunerStudio Plugin API's";
				return "wbo";
    }


    public void initialize(ControllerAccess ca) {
        try {
            this.controllerAccess = ca;
            // add the widgets for this controller.
					pReadouts.removeAll();
							// only looking at the main controller, not the CAN controllers.
							// if there is more than one config name, 0 index is the main controller for the project
					String ecuControllerName = ca.getEcuConfigurationNames()[0];
					String[] outputChannelNames = ca.getOutputChannelServer().getOutputChannels(ecuControllerName);
					
					for (int i = 0; i < outputChannelNames.length && i < 150; i++) {
						// String nam = "InnovateWBOAFR";
						OutputChannel oc = ca.getOutputChannelServer().getOutputChannel(ecuControllerName, outputChannelNames[i]);
						String title = oc.getName();
						// JTextField tit = new JTextField(oc.getName(), 10);
						if(title.indexOf("InnovateWBO") > -1 ){
						OutputChannelLabel label = new OutputChannelLabel(oc);
						ca.getOutputChannelServer().subscribe(ecuControllerName, oc.getName(), label);
						pReadouts.add(label);
						}
					}		
            // initialize the UiDisplay
            // uiDisplay.initialize(controllerAccess);

            // initialize the Math Evaluation Sample
            // evalSample.init(controllerAccess);

            // initialize the Parameter Sample
            paramSample.initialize(controllerAccess);

        } catch (ControllerException ex) {
            Logger.getLogger(InnovatePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public boolean displayPlugin(String serialSignature) {
        // evaluate the serialSignature, determine if this plugin
        // should be displayed for this controller.
        pluginEnabled = serialSignature!=null && !serialSignature.isEmpty();
        return pluginEnabled ; // always show menu
    }

    public String getAuthor() {
        return "Artem Kochegizov & AndreiK & Puff";
    }

    public JComponent getPluginPanel() {
        return this;
    }

    public void close() {
        // do any cleanup here as the dialog this was displayed in has been
        // closed. If this is used again a initialize will be called.
        // Make sure the widgets are cleaned up!!!!
        // uiDisplay.clearWidgetPanel();
        try{
        paramSample.serialPort.closePort();
        paramSample = null;
        } catch (SerialPortException e) {
                    //e.printStackTrace();
        }
        
    }
    /** This main is likely not used, but TunerStudio will check the manifest
     * for the an ApplicationPlugin: assignment, if one is not found it will check
     * for a main class and expect that to be an implementation of
     * <code>ApplicationPlugin</code>. 
     *
     * 
     */
    public static void main(String[] args) {
        
    }

    /** Here is is simply always enabled, and a more complex
     * condition that evaluates an expression using controllerAccess.evaluateExpression
     * @return
     */
    public boolean isMenuEnabled() {
        boolean useExpression = true;
        controllerAccess = ControllerAccess.getInstance();
        if(useExpression){ // use an expression to enable the menu for this plugin only if settings call for it.
            String mainController = controllerAccess.getEcuConfigurationNames()[0];
            try {
                return controllerAccess.evaluateExpression(mainController, "(userlevel > 127) && (spk_mode0 == 4)") != 0;
            } catch (MathException ex) {
                Logger.getLogger(InnovatePlugin.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }
        }else{ // just always have it enabled.
            boolean enabled = Math.random() > 0.5;
            System.out.println("Enabled = " + enabled);
            return enabled;
        }
    }

    /** a the URL to be used on the help menu. if null is returned the help menu
     * will be suppressed.
     * @return
     */
    public String getHelpUrl() {
        // return "http://tunerstudio.com/index.php/manuals/79-creating-tunerstudio-plugins";
		return "";
    }

    /** return a version number for informational purposes.
     *
     * @return
     */
    public String getVersion() {
        return "0.01 Alpha";
    }

    /** return the PluginAPI specification this plug in requires.
     * 
     * @return
     */
    public double getRequiredPluginSpec() {
        return 1.0;
    }

}
