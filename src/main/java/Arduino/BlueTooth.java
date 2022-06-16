package Arduino;


import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.Arrays;

public class BlueTooth {

    private SerialPort serialPort;

    private int dataInputSize;

   // private Lock data = new ReentrantLock();
   // public Condition info = data.newCondition();

    public boolean readyToWrite = false;

    public int[] buffer;

    private final String name;

    private class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            System.out.println("Event!");
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    //int[] data = serialPort.readIntArray(event.getEventValue());
                   // data.lock();
                 //   try {
                        int[] data = serialPort.readIntArray(dataInputSize);
                        buffer = data;
                        System.out.println(name + " " + Arrays.toString(data));
                        readyToWrite = true;
                    //    info.signal();
                    //} finally {
                     //   data.unlock();
                  //  }

                } catch (SerialPortException ex) {
                    try {
                        serialPort.closePort();
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                    System.out.println(ex);
                }
            }
        }
    }

    public BlueTooth(String port, String name, int dataInputSize) {
        this.dataInputSize = dataInputSize;
        this.name = name;
        serialPort = new SerialPort(port);
        try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //Включаем аппаратное управление потоком
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            //Устанавливаем ивент лисенер и маску
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            //Отправляем запрос устройству
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    public void write(int[] buf) {
       // data.lock();
       // try{
        try {
            serialPort.writeIntArray(buf);
            readyToWrite = false;
        } catch (SerialPortException e) {
            e.printStackTrace();
            try {
                serialPort.closePort();
            } catch (SerialPortException serialPortException) {
                serialPortException.printStackTrace();
            }
       }
        //} finally {
        //    data.unlock();
       // }
    }

    public void write(String data)  {
        try{
        serialPort.writeString(data); }
        catch (SerialPortException e) {

        }
    }
}

