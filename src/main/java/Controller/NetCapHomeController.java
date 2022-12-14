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
            String data2 = "??????" + ds + "???" + nids.description + " | " + nids.name;
            chooseNet.getItems().add(data2);
            System.out.println(data2);
            ds++;
        }
        //???????????????????????????
        chooseProtocol.getItems().addAll("TCP","UDP","ICMP","IP");
        System.out.println(chooseNet.getSelectionModel().getSelectedIndex());
        chooseModel.getItems().addAll("????????????","????????????");

}
    protected int runActions = 0;
    Thread t1 = null;
    public void runAction() {
        runActions++;
        System.out.print(runActions%2==1?"?????????":"?????????");
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
        System.out.println("??????????????????"+getChooseNet1());
        System.out.printf("????????????:"+ dataColm.size());
        if(dataColm.size() > 0){
            System.out.println("??????");
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
        /*----------???????????????-----------------*/
        int i = 0;
        while (i < capNums){
            assert jpcap != null;

            if(filterS!=null){
                jpcap.setFilter(filterS,true);
            }

            Packet packet = jpcap.getPacket();
            Platform.runLater(()->setStatusInfo("??????","?????????..."));
            /*
            *Packet packet = jpcap.processPacket();
            *Packet packet = jpcap.loopPacket(-1,);
            */
            if (packet instanceof IPPacket && ((IPPacket) packet).version != 0) {
                i++;
                //??????
                System.out.println(i);
                IPPacket ip = (IPPacket) packet;
                //?????????????????????packets???????????????????????????
                packets.add(ip);
                System.out.println(ip);

                System.out.println("?????????IPv4");
                System.out.println("????????????" + ip.priority);
                System.out.println("???????????????????????????????????? " + ip.t_flag);
                System.out.println("????????????????????????????????????" + ip.r_flag);
                System.out.println("?????????" + ip.length);
                System.out.println("?????????" + ip.ident);
                System.out.println("DF:Don't Fragment: " + ip.dont_frag);
                System.out.println("NF:Nore Fragment: " + ip.more_frag);
                System.out.println("????????????" + ip.offset);
                System.out.println("???????????????" + ip.hop_limit);
                System.out.println("?????????" + getProtocol(ip.protocol));
                System.out.println("???IP " + ip.src_ip.getHostAddress());
                System.out.println("??????IP " + ip.dst_ip.getHostAddress());
                System.out.println("??????????????? " + ip.src_ip);
                System.out.println("?????????????????? " + ip.dst_ip);
                System.out.println("----------------------------------------------");
                LocalTime localTime = LocalTime.now();
                System.out.println(localTime+"\t??????"+ip.sec);
                String time2 = String.valueOf(localTime);
                String source2 = ip.src_ip.getHostAddress();
                String destination2 = ip.dst_ip.getHostAddress();
                String protocol2 = getProtocol(ip.protocol);
                int length2 = ip.length;
                String data2 = bytesToHexString(ip.data);

                System.out.println(data2);
                System.out.println("??????????????????"+ ip);
                System.out.println("????????????????????????"+ip.datalink);
                System.out.println("???????????????"+ Arrays.toString(ip.header));

                setTableData(new NetworkPacket(dataColm.size()+1, time2, source2, destination2, protocol2,length2,data2));
                System.out.println(isSetTableData);
                System.out.println("123");
            }
            System.out.println("1121");
        }
        String imageStartPath = String.valueOf((this.getClass().getResource("/image/start.png")));
        run.setStyle("-fx-background-image:url("+imageStartPath+")");
        Platform.runLater(() -> setStatusInfo("??????","?????????????????????????????????????????????????????????"));

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
        System.out.println("???????????????"+dataColm.size());
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
            "?????????IPv4"+"\n"+
            "????????????" + ps.priority+"\n"+
            "???????????????????????????????????? " + ps.t_flag+"\n"+
            "????????????????????????????????????" + ps.r_flag+"\n"+
            "?????????" + ps.length+"\n"+
            "?????????" + ps.ident+"\n"+
            "DF:Don't Fragment: " + ps.dont_frag+"\n"+
            "NF:Nore Fragment: " + ps.more_frag+"\n"+
            "????????????" + ps.offset+"\n"+
            "???????????????" + ps.hop_limit+"\n"+
            "?????????" + getProtocol(ps.protocol)+"\n"+
            "???IP " + ps.src_ip.getHostAddress()+"\n"+
            "??????IP " + ps.dst_ip.getHostAddress()+"\n"+
            "??????????????? " + ps.src_ip+"\n"+
            "?????????????????? " + ps.dst_ip+"\n"+
            "??????????????????"+ ps +"\n"+
            "????????????????????????"+ ps.datalink+"\n"+
            "???????????????"+ Arrays.toString(ps.header)
        );
        netPacketInfo.setText(psi);
    }

    public void chooseModelAction() {
        chooseModel1 = chooseModel.getSelectionModel().getSelectedIndex();
        System.out.println("???????????????"+chooseModel1);
        if(chooseModel1==0){
            capNumInput.setVisible(true);
            setStatusInfo("??????","????????????...\n" +
                    "1.??????????????????"+chooseModel.getSelectionModel().getSelectedItem()+"\n"+
                    "2.???????????????????????????????????????40????????????????????????\n"+
                    "3......");
        }
        if(chooseModel1==1){
            setStatusInfo("??????","????????????...\n" +
                    "1.??????????????????"+chooseModel.getSelectionModel().getSelectedItem()+"\n"+
                    "2.??????????????????\n"+
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

