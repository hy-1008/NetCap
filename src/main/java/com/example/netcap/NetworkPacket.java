package com.example.netcap;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author HY
 *
 * 网络数据包类
 */
public class NetworkPacket {


    private SimpleIntegerProperty num;
    private SimpleStringProperty time;
    private SimpleStringProperty source;
    private SimpleStringProperty destination;
    private SimpleStringProperty protocol;
    private SimpleIntegerProperty length;
    private SimpleStringProperty data;


//    public NetworkPacket(SimpleIntegerProperty num, SimpleStringProperty time, SimpleStringProperty source, SimpleStringProperty destination, SimpleStringProperty protocol, SimpleStringProperty length, SimpleStringProperty data) {
//        this.num = num;
//        this.time = time;
//        this.source = source;
//        this.destination = destination;
//        this.protocol = protocol;
//        this.length = length;
//        this.data = data;
//    }
    public NetworkPacket(int num,String time,String source,String destination,String protocol,int length,String data){
//        setNum(num);
        this.num = new SimpleIntegerProperty(num);
//        setTime(time);
        this.time = new SimpleStringProperty(time);
//        setSource(source);
        this.source = new SimpleStringProperty(source);

//        setDestination(destination);
        this.destination = new SimpleStringProperty(destination);
//        setProtocol(protocol);
        this.protocol = new SimpleStringProperty(protocol);
//        setLength(length);
        this.length = new SimpleIntegerProperty(length);
//        setData(data);
        this.data = new SimpleStringProperty(data);

    }

    @Override
    public String toString() {
        System.out.println("1" +num.get() + "2" + source.get() + "3" + destination.get() + "4" + protocol.get() + "5" + data.get()+"6"+length.get()+"7"+ time.get());
        return "1" +num.get() + "2" + source.get() + "3" + destination.get() + "4" + protocol.get() + "5" + data.get()+"6"+length.get()+"7"+ time.get();

    }

    public int getNum() {
        return num.get();
    }


    public SimpleIntegerProperty numProperty() {
        return num;
    }

    public void setNum(int num) {
        this.num.set(num);
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getSource() {
        return source.get();
    }

    public SimpleStringProperty sourceProperty() {
        return source;
    }

    public void setSource(String source) {
        this.source.set(source);
    }

    public String getDestination() {
        return destination.get();
    }

    public SimpleStringProperty destinationProperty() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination.set(destination);
    }

    public String getProtocol() {
        return protocol.get();
    }

    public SimpleStringProperty protocolProperty() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol.set(protocol);
    }

    public int getLength() {
        return length.get();
    }

    public SimpleIntegerProperty lengthProperty() {
        return length;
    }

    public void setLength(int length) {
        this.length.set(length);
    }

    public String getData() {
        return data.get();
    }

    public SimpleStringProperty dataProperty() {
        return data;
    }

    public void setData(String data) {
        this.data.set(data);
    }
}
