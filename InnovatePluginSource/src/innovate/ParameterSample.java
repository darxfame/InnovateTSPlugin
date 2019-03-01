package innovate;

import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.ControllerException;
import com.efiAnalytics.plugin.ecu.ControllerParameter;
import com.efiAnalytics.plugin.ecu.ControllerParameterChangeListener;
import com.efiAnalytics.plugin.ecu.servers.ControllerParameterServer;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ParameterSample extends JPanel implements ControllerParameterChangeListener{

    public final static String NO_PARAMETER = "";
    ControllerParameterServer parameterServer = null;
    String mainConfigName = null;
    String currentParameterName = NO_PARAMETER;

    JTextField txtValue = new JTextField("", 10);
    JComboBox choiceParameter = new JComboBox();
    JButton btnUpdate = new JButton("Update Parameter");

    public ParameterSample(){
        setBorder(BorderFactory.createTitledBorder("Select innovate PCVariable"));
        setLayout(new BorderLayout(5,5));
		
        add(BorderLayout.WEST, choiceParameter);
        add(BorderLayout.CENTER, btnUpdate);
		
        btnUpdate.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sendValue();
            }
        });

        add(BorderLayout.EAST, txtValue);
		
        choiceParameter.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    changeParameter();
                }
            }
        });
    }

    public void initialize(ControllerAccess controllerAccess){
        this.parameterServer = controllerAccess.getControllerParameterServer();
        this.mainConfigName = controllerAccess.getEcuConfigurationNames()[0];

        String[] allParameters = parameterServer.getParameterNames(mainConfigName);
        // choiceParameter.addItem(NO_PARAMETER);// an empty one at the top
        for(int i=0; i<allParameters.length; i++){
            try {
                ControllerParameter param = parameterServer.getControllerParameter(mainConfigName, allParameters[i]);
                if(param.getParamClass().equals(ControllerParameter.PARAM_CLASS_SCALAR)){
                    // only adding Scalar, array need different UI
					if(allParameters[i].indexOf("innovate") > -1 ){
                    choiceParameter.addItem(allParameters[i]);
					}
                }
            } catch (ControllerException ex) {
                JOptionPane.showMessageDialog(this, "Error retrieving Paramter: "+ allParameters[i]);
                Logger.getLogger(ParameterSample.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void changeParameter(){
        // don't forget the clean up...
        unsubscribe();
        // now onto the new parameter.
        currentParameterName = choiceParameter.getSelectedItem().toString();
        if(currentParameterName.equals(NO_PARAMETER)){
            txtValue.setText("");
        }else{
            try {
                parameterServer.subscribe(mainConfigName, currentParameterName, this);
                parameterValueChanged(currentParameterName);
            } catch (ControllerException ex) {
                Logger.getLogger(ParameterSample.class.getName()).log(Level.SEVERE, null, ex);
                currentParameterName = NO_PARAMETER;
                choiceParameter.setSelectedItem(NO_PARAMETER);
            }
        }

    }

    private void sendValue(){
        String paramName = choiceParameter.getSelectedItem().toString();
        if(!paramName.equals("")){
            try {
                double val = Double.parseDouble(txtValue.getText());
                parameterServer.updateParameter(mainConfigName, paramName, val);
            } catch (ControllerException ex) {
                JOptionPane.showMessageDialog(txtValue, "Failed to update value for "+currentParameterName +"\nError:\n"+ex.getMessage());
                parameterValueChanged(currentParameterName);
            } catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(txtValue, "Invalid Value for "+currentParameterName );
                // reset it back to current value
                parameterValueChanged(currentParameterName);
            }
        }

    }

    private void unsubscribe(){
        // You would likely want to maintain specific listeners, but for
        // simplisity sake in an example
        parameterServer.unsubscribe(this);
    }

    public void parameterValueChanged(String parameter) {
        try {
            ControllerParameter param = parameterServer.getControllerParameter(mainConfigName, parameter);
			String text = Double.toString(param.getScalarValue());
			text = text.substring(0, 5);
            txtValue.setText(text);
        } catch (ControllerException ex) {
            Logger.getLogger(ParameterSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
