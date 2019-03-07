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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jssc.SerialPort;
import jssc.SerialPortList;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ParameterSample extends JPanel implements ControllerParameterChangeListener{

    public final static String NO_PARAMETER = "";
    ControllerParameterServer parameterServer = null;
    String mainConfigName = null;
    String currentParameterName = NO_PARAMETER;
    PortReader read;
    String comport = "";
    public  SerialPort serialPort = null;
    public String paramName;

    
    JTextField txtValue = new JTextField("", 10);
    JComboBox choiceParameter = new JComboBox();
    JComboBox comnum = new JComboBox();
    JButton btnConnect = new JButton("Connect");
    JButton btnDisConnect = new JButton("Disconnect");
    JLabel choiceCOM = null;
    JLabel status;
    JLabel warn = null;
    JButton btnUpdate = new JButton("Update Parameter");
    
   public class PortReader {
    private final int[] SLEEP_DURATIONS = {1,20,50,100};
            LinkedList<Byte> byteList = new LinkedList();
            

                public Thread t;
                
 
        public String conv(byte[] in) {
                            short afr = 0;
                            short  lambda = 0;
                            float ratio = 0;
                            
                            if ((in[0]==(byte)0xb2) && (in[1]==(byte)0x82)){  //определяем заголовок 
                            if (((in[2] & 254)==66)) //определяем выполнение условия 
                                    {afr=in[3];
                                     afr |= ((in[2] & 1)<<7); //формируем значение aft для формулы, но надо бы проверить, правильно ли собрал 
                                            lambda=in[5];	
                                    lambda |= ((in[4] & 63)<<7);
                                    ratio = (float)(lambda+500)*(float)(afr)/(float)10000.0;
                                    //System.out.println(ratio);
                                    ratio = (float)new BigDecimal(ratio).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                    return Float.toString(ratio);
                                    }}else{
        /*final StringBuilder builder = new StringBuilder();
            builder.append(String.format("%02x ", in[0]));
            builder.append(String.format("%02x", in[1]));
            return builder.toString();*/
                    return "";
                            }
            return Float.toString(ratio);
       }
    
        public PortReader() {
            //status.setText("Connect ");
           t = new Thread(new Runnable() {
            @Override
            public void run() {
                //status.setText(status.getText()+" New Thread");
                paramName = choiceParameter.getSelectedItem().toString();
                try {
                   // int l =0;
           while (serialPort.isOpened()) {
               // status.setText("Status " + serialPort.isOpened());
                        byte[] data = progressiveSleepRead(serialPort);
                        if (data != null){
                                for(byte k : data)
                                    byteList.add(k);
                                while(byteList.size()>=6){
                                   byte[] packet = new byte[6];
                                   for(int k=0; k<6; k++)
                                       packet[k] = byteList.pollFirst();
                                   //System.out.println("data " + conv(packet));
                                   choiceCOM.setText(conv(packet));
                                   txtValue.setText(conv(packet));
                                   try {
                                    double val = Double.parseDouble(txtValue.getText());
                                    parameterServer.updateParameter(mainConfigName, paramName, val);
                                        } catch (ControllerException ex) {}
                                }
                            //listener.onDataArrived(data);
                            //choiceCOM.setText(bytesToHex(data));
                           
                        }else{
                            //l++;
                       // status.setText("Empty Data" +l);
                        }  
                    }
          // status.setText("Disconnect" +l);
           } catch (SerialPortException e) {
               //status.setText(status.getText()+" Except");
                    e.printStackTrace();
                }

            }
           }, "Reader_k"); t.start();
        }

   public byte[] progressiveSleepRead(SerialPort serialPort) throws SerialPortException {
        for (int sleepDuration : SLEEP_DURATIONS) {
            byte[] data;
            synchronized (serialPort) {
                data = serialPort.readBytes();
            }
            if (data != null)
                return data;
            try {
                Thread.sleep(sleepDuration);
                //status.setText("Sleep " + sleepDuration);
                } 
            catch (InterruptedException e) 
                {
                    //status.setText(e.toString());
                    throw new IllegalStateException(e);
                }
        }

        return null;
    }
   }
   
   
      private void SetPortNames() {
    comnum.removeAllItems();
    String[] portNames = SerialPortList.getPortNames();
    for (String portName : portNames) {
        comnum.addItem(portName);
        System.out.println(portName);
    }
}     

    public void disconnect(){ 
        btnConnect.setEnabled(true);
    try {
                    serialPort.closePort();
                    //status.setText("Port - " + comnum.getSelectedItem().toString() + " Disconnect");
                    choiceCOM.setText("#####");
                    btnDisConnect.setEnabled(false);
                }
                 catch (SerialPortException ex) {
                        //System.out.println(ex);
                    }
                }  
    
    public void connect(String port){
        String portName = port;
        if(serialPort != null && serialPort.isOpened()){
            
           try {
                    serialPort.closePort();
                    //status.setText("Innovate port - " + comnum.getSelectedItem().toString() + " Disconnect");
                }
                 catch (SerialPortException ex) {
                        //System.out.println(ex);
                    }
                }  
        serialPort = new SerialPort(portName);
                try {
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_19200,
                                         SerialPort.DATABITS_8,
                                         SerialPort.STOPBITS_1,
                                         SerialPort.PARITY_NONE);
                    serialPort.setRTS(false);
                    serialPort.setDTR(false);
                    serialPort.setFlowControlMode(0);
                    //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
                    //choiceCOM.setText("com");
                    //status.setText("Port - " + serialPort.getPortName().toString() + " Connect");
                    btnConnect.setEnabled(false);
                    btnDisConnect.setEnabled(true);
                    read = new PortReader();
                }
                catch (SerialPortException ex) {
                    //status.setText("Port - " + ex.getLocalizedMessage().toString());
                    btnConnect.setEnabled(true);
                    //System.out.println(ex);
                }  
    }
   

    public ParameterSample(){
        setBorder(BorderFactory.createTitledBorder("Select innovate Connect & PCVariable"));
        setLayout(new BorderLayout(5,5));
        
        JPanel pSouth = new JPanel();
        JPanel pNorth = new JPanel();
        JPanel pCenter = new JPanel();
        add(BorderLayout.SOUTH, pSouth);
        add(BorderLayout.CENTER, pCenter);
        add(BorderLayout.NORTH, pNorth);
        
        choiceCOM = new JLabel("#####", JLabel.LEFT);
        //status = new JLabel("Not Connected", JLabel.RIGHT);
        btnDisConnect.setEnabled(false);
        warn = new JLabel("", JLabel.LEFT);
        warn.setFont(new java.awt.Font("Tahoma", 0, 15));
        warn.setForeground(new java.awt.Color(255, 0, 51));
        warn.setText("ВНИМАНИЕ! ПРИ ОБРЫВЕ КОННЕКТА С INNOVATE ЗАКРЫВАТЬ ФОРМУ ЧЕРЕЗ КНОПКУ CLOSE");
        		
         pSouth.add(BorderLayout.WEST, choiceParameter);
         pSouth.add(BorderLayout.CENTER, btnUpdate);
         pSouth.add(BorderLayout.EAST, txtValue);

         pCenter.add(BorderLayout.WEST, comnum);
         pCenter.add(BorderLayout.SOUTH, btnDisConnect);
         pCenter.add(BorderLayout.CENTER, btnConnect);
         pCenter.add(BorderLayout.EAST, choiceCOM);
         pNorth.add(BorderLayout.WEST, warn);
         //pNorth.add(BorderLayout.SOUTH, status);

                
		
        btnUpdate.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                sendValue();
            }
        });

        btnConnect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                connect(comnum.getSelectedItem().toString());
            }
        });

        btnDisConnect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }
        });


		
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

        SetPortNames();
        
        String[] allParameters = parameterServer.getParameterNames(mainConfigName);
         //choiceParameter.addItem(NO_PARAMETER);// an empty one at the top
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
        paramName = choiceParameter.getSelectedItem().toString();
        //String paramName = choiceParameter.getSelectedItem().toString();
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
