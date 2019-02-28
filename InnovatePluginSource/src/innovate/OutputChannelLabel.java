package innovate;

import com.efiAnalytics.plugin.ecu.OutputChannel;
import com.efiAnalytics.plugin.ecu.OutputChannelClient;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class OutputChannelLabel extends JPanel implements OutputChannelClient{
    OutputChannel outputChannel = null;
    JLabel lblValue = null;
    DecimalFormat df = null;

    public OutputChannelLabel(OutputChannel outputChannel){
        this.outputChannel = outputChannel;
        setLayout(new BorderLayout(2,4));
        String title = outputChannel.getName();
        if(outputChannel.getUnits()!=null && !outputChannel.getUnits().isEmpty()){
            title += "("+outputChannel.getUnits()+") :";
        }else{
            title += " :";
        }
        JLabel lblTitle = new JLabel(title, JLabel.RIGHT);
        add(BorderLayout.WEST, lblTitle);
        lblValue = new JLabel("#####", JLabel.LEFT);
        lblValue.setMinimumSize(new Dimension(45, 18));
        lblValue.setPreferredSize(new Dimension(45, 18));
        add(BorderLayout.CENTER, lblValue);
    }

    public void setCurrentOutputChannelValue(String channelName, double value) {
        lblValue.setText(getFormatter().format(value));
    }

    private DecimalFormat getFormatter(){
        if(df == null){
            df = (DecimalFormat) DecimalFormat.getInstance();
            df.setMaximumFractionDigits(2);
        }
        return df;
    }

}
