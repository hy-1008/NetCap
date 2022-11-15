package Controller;

import com.example.netcap.NetworkInterfaceDevice;
import com.example.netcap.NetworkPacket;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

/**
 * @author HY
 *
 */
public class NetCapHomeController implements Initializable {
    @FXML
    public Label title;
    @FXML
    private ComboBox<String> chooseNet;
    @FXML
    private TextField filterInput;
    @FXML
    private ComboBox<String> chooseProtocol;
    @FXML
    private Button run;
    @FXML
    private TextField capNumInput;
    @FXML
    private ComboBox<String> chooseModel;
    @FXML
    private Label statusTitle;
    @FXML
    private Label netPacketInfo;
    @FXML
    private TableView<NetworkPacket> packetTable = new TableView<>();
    @FXML
    private TableColumn<NetworkPacket,Integer> num;
    @FXML
    private TableColumn<NetworkPacket,String> time;
    @FXML
    private TableColumn<NetworkPacket,String> source;
    @FXML
    private TableColumn<NetworkPacket,String> destination;
    @FXML
    private TableColumn<NetworkPacket,String> protocolcol;
    @FXML
    private TableColumn<NetworkPacket,Integer> length;
    @FXML
    private TableColumn<NetworkPacket,String> data;

    public String filterS = null;
    public NetworkInterface[] NIDevices;
    public int chooseNet1 = 0;
    public int chooseModel1 = -1;
    public String chooseProtocol1 = "";
    public int capNums = 40;
    public boolean isSetTableData = false;
    public List<IPPacket> packets = new ArrayList<>();
    public int packetIndex = 0;
    public ObservableList<NetworkPacket> dataColm = FXCollections.observableArrayList();
    public ObservableList<NetworkPacket> dataColm2 = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        capNumInput.setVisible(false);
        num.setCellValueFactory(new PropertyValueFactory<>("num"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        source.setCellValueFactory(new PropertyValueFactory<>("source"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        protocolcol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        data.setCellValueFactory(new PropertyValueFactory<>("data"));
        /*
         *
         */
        NIDevices = new NetworkInterfaceDevice().getNetworkInterfaceDevice();
//        NIDevices = JpcapCaptor.getDeviceList();
        int ds = 0;
        for (NetworkInterface nids :NIDevices){
            String data2 = "网卡" + ds + "：" + nids.description + " | " + nids.name;
            chooseNet.getItems().add(data2);
            System.out.println(data2);
            ds++;
        }
        //设置选择协议选框值
        chooseProtocol.getItems().addAll("TCP","UDP","ICMP","IP");
        System.out.println(chooseNet.getSelectionModel().getSelectedIndex());
        chooseModel.getItems().addAll("单次抓包","循环抓包");

}
    protected int runActions = 0;
    Thread t1 = null;
    public void runAction() {
        runActions++;
        System.out.print(runActions%2==1?"运行中":"已暂停");
        System.out.println(runActions%2);

//        Thread t2;

        if (runActions % 2 == 0){
            String imageStartPath = String.valueOf((this.getClass().getResource("/image/start.png")));
            run.setStyle("-fx-background-image:url("+imageStartPath+")");
            System.out.println(run.getStyle());

        }if (runActions % 2 == 1) {
            String imagePausePath = String.valueOf((this.getClass().getResource("/image/pause.png")));
            run.setStyle("-fx-background-image: url("+imagePausePath+")");
            System.out.println(run.getStyle());
            if (chooseModel1 == 0||chooseModel1 == -1){
                if (!capNumInput.getText().isEmpty()){
                    System.out.println(capNumInput.getText());
                    capNums = Integer.parseInt(capNumInput.getText());
                    capNumInput.setVisible(false);
                }
                t1 = new Thread(()-> {
                    try {
                        runCap();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                t1.start();
                if(runActions % 2 == 0){
                    t1.interrupt();
                }

            }
        }
    }

    public void chooseNetInter() {

        setChooseNet1(chooseNet.getSelectionModel().getSelectedIndex());
        System.out.println("已选择网卡："+getChooseNet1());
        System.out.printf("数据大小:"+ dataColm.size());
        if(dataColm.size() > 0){
            System.out.println("清屏");
            dataColm.removeAll();
        }
    }
    public void runCap() throws IOException {
        if(runActions % 2 == 0){
            t1.interrupt();
        }
        JpcapCaptor jpcap = null;
        int caplen = 1512;
        boolean promiscCheck = true;
        int device = getChooseNet1();
        try{
            jpcap = JpcapCaptor.openDevice(NIDevices[device], caplen, promiscCheck, 500);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        /*----------第二步抓包-----------------*/
        int i = 0;
        while (i < capNums){
            assert jpcap != null;

            if(filterS!=null){
                jpcap.setFilter(filterS,true);
            }

            Packet packet = jpcap.getPacket();
            Platform.runLater(()->setStatusInfo("状态","抓包中..."));
            /*
            *Packet packet = jpcap.processPacket();
            *Packet packet = jpcap.loopPacket(-1,);
            */
            if (packet instanceof IPPacket && ((IPPacket) packet).version != 0) {
                i++;
                //强转
                System.out.println(i);
                IPPacket ip = (IPPacket) packet;
                //将抓取的包放进packets里面里访问详细信息
                packets.add(ip);
                System.out.println(ip);

                System.out.println("版本：IPv4");
                System.out.println("优先权：" + ip.priority);
                System.out.println("区分服务：最大的吞吐量： " + ip.t_flag);
                System.out.println("区分服务：最高的可靠性：" + ip.r_flag);
                System.out.println("长度：" + ip.length);
                System.out.println("标识：" + ip.ident);
                System.out.println("DF:Don't Fragment: " + ip.dont_frag);
                System.out.println("NF:Nore Fragment: " + ip.more_frag);
                System.out.println("片偏移：" + ip.offset);
                System.out.println("生存时间：" + ip.hop_limit);
                System.out.println("协议：" + getProtocol(ip.protocol));
                System.out.println("源IP " + ip.src_ip.getHostAddress());
                System.out.println("目的IP " + ip.dst_ip.getHostAddress());
                System.out.println("源主机名： " + ip.src_ip);
                System.out.println("目的主机名： " + ip.dst_ip);
                System.out.println("----------------------------------------------");
                LocalTime localTime = LocalTime.now();
                System.out.println(localTime+"\t时间"+ip.sec);
                String time2 = String.valueOf(localTime);
                String source2 = ip.src_ip.getHostAddress();
                String destination2 = ip.dst_ip.getHostAddress();
                String protocol2 = getProtocol(ip.protocol);
                int length2 = ip.length;
                String data2 = bytesToHexString(ip.data);

                System.out.println(data2);
                System.out.println("数据包信息："+ ip);
                System.out.println("数据链路层报头："+ip.datalink);
                System.out.println("报头数据："+ Arrays.toString(ip.header));

                setTableData(new NetworkPacket(dataColm.size()+1, time2, source2, destination2, protocol2,length2,data2));
                System.out.println(isSetTableData);
                System.out.println("123");
            }
            System.out.println("1121");
        }
        String imageStartPath = String.valueOf((this.getClass().getResource("/image/start.png")));
        run.setStyle("-fx-background-image:url("+imageStartPath+")");
        Platform.runLater(() -> setStatusInfo("状态","抓包完成，点击任意表格项显示详细信息。"));

        runActions++;
    }

    public String getProtocol(int protocols) {
        String  protocol = "";
        switch (protocols) {
            case 1 -> protocol = "ICMP";
            case 2 -> protocol = "IGMP";
            case 6 -> protocol = "TCP";
            case 8 -> protocol = "EGP";
            case 9 -> protocol = "IGP";
            case 17 -> protocol = "UDP";
            case 41 -> protocol = "IPv6";
            case 89 -> protocol = "OSPF";
            default -> {}
        }return protocol;
    }

    public String bytesToHexString(byte[] b)
    {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte value : b) {
            stmp = (Integer.toHexString(value & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

    public int getChooseNet1() {
        return chooseNet1;
    }

    public void setChooseNet1(int chooseNet1) {
        this.chooseNet1 = chooseNet1;
    }


    public String getChooseProtocol1() {
        return chooseProtocol1;
    }

    public void setChooseProtocol1(String chooseProtocol1) {
        this.chooseProtocol1 = chooseProtocol1;
    }


    public void setTableData (NetworkPacket np){
        System.out.println(np);
//        dataColm = FXCollections.observableArrayList(
//                        new NetworkPacket(0,"Jacob1", "Smith5", "Smith4", "Smith3","Smith2","Smith4"),
//                        new NetworkPacket(1,"Jacob2", "Smith4", "Smith4", "Smith3","Smith2","Smith4"),
//                        new NetworkPacket(2,"Jacob3", "Smith3", "Smith4", "Smith3","Smith2","Smith4"),
//                        new NetworkPacket(3,"Jacob4", "Smith2", "Smith4", "Smith3","Smith2","Smith4"),
//                        new NetworkPacket(4,"Jacob5", "Smith1", "Smith4", "Smith3","Smith2","Smith4")
//                );

        dataColm.add(np);
        System.out.println("数据个数："+dataColm.size());
        packetTable.setItems(dataColm);
//        packetTable.getItems().add(np);
        this.isSetTableData = true;
    }

    public void chooseProtocolActon(ActionEvent actionEvent) {
        setChooseProtocol1(chooseProtocol.getSelectionModel().getSelectedItem());
        System.out.println(getChooseProtocol1());
    }

    public void filterAction(ActionEvent actionEvent) {
        filterS = filterInput.getText();
        System.out.println(filterS);

        dataColm.forEach(packet -> {
            if (packet.getSource().contains(filterS)||packet.getDestination().contains(filterS)) {
                dataColm2.add(packet);
            }
        });
        packetTable.setItems(dataColm2);
    }

    public void setStatusInfo(String title, String strInfo){
        netPacketInfo.setText(strInfo);
        statusTitle.setText(title);
    }
    public void showPacketInfo(MouseEvent mouseEvent) {
        packetIndex = packetTable.getSelectionModel().getSelectedIndex();
        System.out.println(packetIndex);
        if(packetIndex==-1){
         return;
        }
        IPPacket ps =  packets.get(packetIndex);
        String psi = (
            "版本：IPv4"+"\n"+
            "优先权：" + ps.priority+"\n"+
            "区分服务：最大的吞吐量： " + ps.t_flag+"\n"+
            "区分服务：最高的可靠性：" + ps.r_flag+"\n"+
            "长度：" + ps.length+"\n"+
            "标识：" + ps.ident+"\n"+
            "DF:Don't Fragment: " + ps.dont_frag+"\n"+
            "NF:Nore Fragment: " + ps.more_frag+"\n"+
            "片偏移：" + ps.offset+"\n"+
            "生存时间：" + ps.hop_limit+"\n"+
            "协议：" + getProtocol(ps.protocol)+"\n"+
            "源IP " + ps.src_ip.getHostAddress()+"\n"+
            "目的IP " + ps.dst_ip.getHostAddress()+"\n"+
            "源主机名： " + ps.src_ip+"\n"+
            "目的主机名： " + ps.dst_ip+"\n"+
            "数据包信息："+ ps +"\n"+
            "数据链路层报头："+ ps.datalink+"\n"+
            "报头数据："+ Arrays.toString(ps.header)
        );
        netPacketInfo.setText(psi);
    }

    public void chooseModelAction() {
        chooseModel1 = chooseModel.getSelectionModel().getSelectedIndex();
        System.out.println("选择模式："+chooseModel1);
        if(chooseModel1==0){
            capNumInput.setVisible(true);
            setStatusInfo("状态","准备抓包...\n" +
                    "1.已选择模式："+chooseModel.getSelectionModel().getSelectedItem()+"\n"+
                    "2.输入单次抓包数量（默认数量40）后点击开始抓包\n"+
                    "3......");
        }
        if(chooseModel1==1){
            setStatusInfo("状态","准备抓包...\n" +
                    "1.已选择模式："+chooseModel.getSelectionModel().getSelectedItem()+"\n"+
                    "2.点击开始抓包\n"+
                    "3......");

        }
    }

    public void clearAction() {
        if (runActions % 2 == 0){
            dataColm2.setAll();
            packetTable.setItems(dataColm2);
        }
    }
}

