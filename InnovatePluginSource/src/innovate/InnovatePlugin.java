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


        JPanel pSouth = new JPanel();
        pSouth.setLayout(new GridLayout(0,1,2,2));
        paramSample = new ParameterSample();
        pSouth.add(paramSample);
        add(BorderLayout.SOUTH, pSouth);
		

    }

    public String getIdName() {
        return "tsInnovateAFR";
    }

    public int getPluginType() {
        return PERSISTENT_DIALOG_PANEL;
    }

    public String getDisplayName() {
        return "Innovate WBO-COM(connect)";
    }

    public String getDescription() {
				return "wbo";
    }


    public void initialize(ControllerAccess ca) {
        try {
            this.controllerAccess = ca;
					pReadouts.removeAll();
					String ecuControllerName = ca.getEcuConfigurationNames()[0];
					String[] outputChannelNames = ca.getOutputChannelServer().getOutputChannels(ecuControllerName);
					
					for (int i = 0; i < outputChannelNames.length && i < 150; i++) {
						OutputChannel oc = ca.getOutputChannelServer().getOutputChannel(ecuControllerName, outputChannelNames[i]);
						String title = oc.getName();
						if(title.indexOf("InnovateWBO") > -1 ){
						OutputChannelLabel label = new OutputChannelLabel(oc);
						ca.getOutputChannelServer().subscribe(ecuControllerName, oc.getName(), label);
						pReadouts.add(label);
						}
					}		
            paramSample.initialize(controllerAccess);

        } catch (ControllerException ex) {
            Logger.getLogger(InnovatePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public boolean displayPlugin(String serialSignature) {
        pluginEnabled = serialSignature!=null && !serialSignature.isEmpty();
        return pluginEnabled ;
    }

    public String getAuthor() {
        return "Artem Kochegizov & AndreiK & Puff";
    }

    public JComponent getPluginPanel() {
        return this;
    }

    public void close() {
        if(paramSample.btnDisConnect.isEnabled()){
        try{
        paramSample.serialPort.closePort();
        paramSample = null;
        } catch (SerialPortException e) {
                    e.printStackTrace();
        }}
        
    }

    public static void main(String[] args) {
        
    }

    public boolean isMenuEnabled() {
        boolean useExpression = true;
        controllerAccess = ControllerAccess.getInstance();
        if(useExpression){ 
            String mainController = controllerAccess.getEcuConfigurationNames()[0];
            try {
                return controllerAccess.evaluateExpression(mainController, "(userlevel > 127) && (spk_mode0 == 4)") != 0;
            } catch (MathException ex) {
                Logger.getLogger(InnovatePlugin.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }
        }else{ 
            boolean enabled = Math.random() > 0.5;
            System.out.println("Enabled = " + enabled);
            return enabled;
        }
    }

    public String getHelpUrl() {
        return "http://rusefi.com";
    }

    public String getVersion() {
        return "0.05 Alpha";
    }

    public double getRequiredPluginSpec() {
        return 1.0;
    }

}
