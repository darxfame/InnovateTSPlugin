package innovate;

import com.efiAnalytics.plugin.ecu.ControllerAccess;
import com.efiAnalytics.plugin.ecu.MathException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ExpressionEvalSample extends JPanel{
    ControllerAccess controllerAccess = null;
    JTextField txtExpression = new JTextField("nCylinders * rpm", 40);
    JLabel lblResult = new JLabel();
    JButton btnEvaluate = new JButton("Evaluate >>");
    DecimalFormat df = null;

    public ExpressionEvalSample(){
        setBorder(BorderFactory.createTitledBorder("Evaluate any Expression using Parameters or OutputChannels"));
        setLayout(new BorderLayout(4,4));
        add(BorderLayout.CENTER, txtExpression);
        JPanel eastPanel = new JPanel();
            eastPanel.setLayout(new BorderLayout());
            btnEvaluate.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    evaluate();
                }
            });
            eastPanel.add(BorderLayout.WEST, btnEvaluate);

            lblResult.setPreferredSize(new Dimension(80, 18));
            eastPanel.add(BorderLayout.CENTER, lblResult);
        add(BorderLayout.EAST, eastPanel);

    }

    private void evaluate(){
        String mainConfig = controllerAccess.getEcuConfigurationNames()[0];
        try {
            double result = controllerAccess.evaluateExpression(mainConfig, txtExpression.getText());
            lblResult.setText(getFormatter().format(result));
        } catch (MathException ex) {
            Logger.getLogger(ExpressionEvalSample.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(btnEvaluate, ex.getMessage());
        }
    }

    public void init(ControllerAccess ca){
        this.controllerAccess = ca;
    }

    private DecimalFormat getFormatter(){
        if(df == null){
            df = (DecimalFormat) DecimalFormat.getInstance();
            df.setMaximumFractionDigits(4);
        }
        return df;
    }


}
