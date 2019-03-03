package com.company;

import jssc.SerialPort;
import jssc.SerialPortList;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Main {
    public static SerialPort serialPort = null;
    static PortReader read;

    public static class PortReader {
        private final int[] SLEEP_DURATIONS = {40};

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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //status.setText(status.getText()+" New Thread");
                    try {
                        int l =0;
                        while (serialPort.isOpened()) {
                            // status.setText("Status " + serialPort.isOpened());
                            byte[] data = progressiveSleepRead(serialPort);
                            if (data != null){
                                //listener.onDataArrived(data);
                                //choiceCOM.setText(bytesToHex(data));
                                //choiceCOM.setText(conv(data));
                                System.out.println("data " + conv(data));
                            }else{
                                l++;
                                // status.setText("Empty Data" +l);
                            }
                        }
                        // status.setText("Disconnect" +l);
                    } catch (SerialPortException e) {
                        //status.setText(status.getText()+" Except");
                        e.printStackTrace();
                    }

                }
            }, "Reader_" + serialPort).start();
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
                   // status.setText("Sleep " + sleepDuration);
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

    public void disconnect(){
        try {
            serialPort.closePort();

            System.out.println("Innovate port - Disconnect");
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    public static void connect(String port){
        String portName = port;
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
            //status.setText(status.getText()+"Innovate port - " + serialPort.getPortName().toString());
            read = new PortReader();
        }
        catch (SerialPortException ex) {
            //status.setText("Innovate port - " + ex.getLocalizedMessage().toString());
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {
	connect("COM2");
    }
}
