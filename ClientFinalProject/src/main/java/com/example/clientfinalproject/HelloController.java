package com.example.clientfinalproject;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import socketfx.Constants;
import socketfx.FxSocketClient;
import socketfx.SocketListener;

public class HelloController implements Initializable {
    boolean areReady = false;
    boolean serverReady = false;
    @FXML
    private Button sendButton, readyBtn;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField hostTextField;
    @FXML
    private Label lblName1, lblName2, lblName3, lblName4, lblMessages;

    @FXML
    private GridPane gPaneServer, gPaneClient;


    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private boolean isConnected, turn, serverUNO = false, clientUNO = false;
    public enum ConnectionDisplayState {
        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }
    private FxSocketClient socket;

    private void safeSend(String msg) {
        if (socket != null) socket.sendMessage(msg);
    }

    private void connect() {
        try {
            int port = Integer.parseInt(portTextField.getText().trim());
            socket = new FxSocketClient(new FxSocketListener(),
                    hostTextField.getText(),
                    port,
                    Constants.instance().DEBUG_NONE);
            socket.connect();
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid port: " + portTextField.getText());
            if (connectButton != null) connectButton.setDisable(false);
        }
    }
    private void displayState(ConnectionDisplayState state) {
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);
        Runtime.getRuntime().addShutdownHook(new ShutDownThread());
    }

    class ShutDownThread extends Thread {
        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                socket.shutdown();
            }
        }
    }

    public HelloController(){

    }
    @FXML
    public void handleControl(){
        safeSend("controlOP");
    }
    @FXML
    public void handleToNewYork(){
        safeSend("chOP" + 0);
    }
    @FXML
    public void handleToLondon(){
        safeSend("chOP" + 1);
    }
    @FXML
    public void handleToMoscow(){
        safeSend("chOP" + 2);
    }
    @FXML
    public void handleToMadrid(){
        safeSend("chOP" + 3);
    }
    @FXML
    public void handleToBerlin(){
        safeSend("chOP" + 4);
    }
    @FXML
    public void handleToChicago(){
        safeSend("chOP" + 5);
    }
    @FXML
    public void handleToSanFran(){
        safeSend("chOP" + 6);
    }
    @FXML
    public void handleToCancun(){
        safeSend("chOP" + 7);
    }
    @FXML
    public void handleToParis(){
        safeSend("chOP" + 8);
    }
    @FXML
    public void handleToGeneva(){
        safeSend("chOP" + 9);
    }
    @FXML
    public void handleToNigeria(){
        safeSend("chOP" + 10);
    }
    @FXML
    public void handleSubText(){
        if (sendTF != null) safeSend("message" + sendTF.getText());
    }
    @FXML
    public void handleQuit(){
        resultAP.setVisible(false);
        readyAP.setVisible(true);
        readyBtn.setDisable(false);
        areReady = false;
        safeSend("quitOP");
    }
    @FXML
    public void handleContinue(){
        areReady=true;
        safeSend("continue");
        if (serverReady){
            resultAP.setVisible(false);
            resetPage();
        }else{
            resultAP.setDisable(true);
        }
    }
    @FXML
    public void handleOkay(){
        finalResultAP.setVisible(false);
        resultAP.setVisible(false);
        readyAP.setVisible(true);
        readyBtn.setDisable(false);
        areReady = false;
    }
    @FXML
    public void handleLocate(){
        safeSend("locateOP");
    }
    @FXML
    public void handlePlan(){
        safeSend("planOP");
    }
    @FXML
    public void handleWait(){
        safeSend("waitOP");
    }
    @FXML
    public void handleAttack(){
        safeSend("attackOP");
    }
    public void handleStart(){
        disImage("src/main/resources/images/worldMap.jpg");
        if (mapIV != null && image != null) mapIV.setImage(image);
        resetPage();
        playerMP = new Player();
        playerOP = new Player();
    }
    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            System.out.println("message received server");
            if (lblMessages != null) lblMessages.setText(line);
            if (line.equals("ready") && areReady){
                readyAP.setVisible(false);
                handleStart();
            } else if(line.equals("ready")){
                serverReady=true;
            } if (line.equals("continue") && areReady){
                resultAP.setVisible(false);
                resultAP.setDisable(false);
                resetPage();
            } else if(line.equals("continue")){
                serverReady=true;
            }else if(line.startsWith("POPImNam")){
                playerOP.setPlayerImageN(line.substring(8));
                System.out.println(line.substring(8));
            } else if (line.startsWith("POPNam")){
                playerOP.setName(line.substring(6));
                System.out.println(line.substring(6));
            }
            else if(line.startsWith("PMPImNam")){
                playerMP.setPlayerImageN(line.substring(8));
            } else if (line.startsWith("PMPNam")){
                playerMP.setName(line.substring(6));
            }else if(line.startsWith("PMPCol")){
                playerMP.setColor(line.substring(6));
            } else if (line.startsWith("POPCol")){
                playerOP.setColor(line.substring(6));
            }
            else if(line.startsWith("POPCLIn") && line.length() > 7){
                try { playerOP.setCurrentPosition(Integer.parseInt(line.substring(7))); } catch (NumberFormatException ignored) {}
            } else if (line.startsWith("PMPCLIn") && line.length() > 7){
                try { playerMP.setCurrentPosition(Integer.parseInt(line.substring(7))); } catch (NumberFormatException ignored) {}
            }else if(line.startsWith("POPWins") && line.length() > 7){
                try { playerOP.setWinsNum(Integer.parseInt(line.substring(7))); } catch (NumberFormatException ignored) {}
            } else if (line.startsWith("PMPWins") && line.length() > 7){
                try { playerMP.setWinsNum(Integer.parseInt(line.substring(7))); } catch (NumberFormatException ignored) {}
            }else if(line.startsWith("POPE") && line.length() > 4){
                try { playerOP.setEnergy(Integer.parseInt(line.substring(4))); } catch (NumberFormatException ignored) {}
            } else if (line.startsWith("POPIP") && line.length() > 5){
                try { playerOP.setIntelPoints(Integer.parseInt(line.substring(5))); } catch (NumberFormatException ignored) {}
            }else if(line.startsWith("PMPE") && line.length() > 4){
                try { playerMP.setEnergy(Integer.parseInt(line.substring(4))); } catch (NumberFormatException ignored) {}
            } else if (line.startsWith("PMPIP") && line.length() > 5){
                try { playerMP.setIntelPoints(Integer.parseInt(line.substring(5))); } catch (NumberFormatException ignored) {}
            }
            else if(line.startsWith("chOP")){
                if (playerOP != null) displayPlayerPosition(playerOP);
            }else if (line.equals("disPOP")){
                if (playerOP != null) displayPlayerPosition(playerOP);
            }
            else if (line.equals("disPMP")){
                if (playerMP != null) displayPlayerPosition(playerMP);
            }else if(line.equals("OPTurn")){
                receivedLV.getItems().add("Player2 turn");
                otherTurnAP.setVisible(false);

            }else if(line.equals("MPTurn")){
                receivedLV.getItems().add("Player1 turn");
                otherTurnAP.setVisible(true);

            }else if(line.equals("controlMP") && playerMP != null && !placeBtn.isEmpty()){
                int idx = playerMP.getCurrentPIMNum();
                if (idx >= 0 && idx < placeBtn.size()) placeBtn.get(idx).setStyle("-fx-background-color: "+ playerMP.getColor());
                displayPlayerPosition(playerMP);
            }else if(line.equals("controlOP") && playerOP != null && !placeBtn.isEmpty()){
                int idx = playerOP.getCurrentPIMNum();
                if (idx >= 0 && idx < placeBtn.size()) placeBtn.get(idx).setStyle("-fx-background-color: "+ playerOP.getColor());
                displayPlayerPosition(playerOP);
            }else if(line.startsWith("message")){
                receivedLV.getItems().add(line.substring(7));
            }else if(line.startsWith("result") && playerMP != null && playerOP != null){
                resultAP.setVisible(true);
                MPwinsL.setText("Wins: " + playerMP.getWinsNum());
                OPwinsL.setText("Wins: " + playerOP.getWinsNum());
                resultBtn.setText(line.substring(6));
                areReady = false;
                serverReady = false;
                otherTurnAP.setVisible(false);
                //set labels to the right things
            }else if(line.startsWith("finalResult")){
                finalResultAP.setVisible(true);
                otherTurnAP.setVisible(false);
                finalResultL.setText(line.substring(11));
            }else if(line.equals("update") && playerOP != null){
                energyNumL.setText(String.valueOf(playerOP.getEnergy()));
                aPointL.setText(String.valueOf(playerOP.getIntelPoints()));
            }else if(line.startsWith("showTimeMP")){
                mPTimeL.setText("Turn time: "+line.substring(10));
            }else if(line.startsWith("showTimeOP")){
                oPTimeL.setText("Turn time: "+line.substring(10));
            }else if(line.equals("quitMP")){
                resultAP.setVisible(false);
                readyAP.setVisible(true);
                readyBtn.setDisable(false);
                areReady = false;
            }else if(line.startsWith("battleWonOP")){
                gamesWonL.setText("Games Won: " + line.substring(11));
                battleWonL.setText("Missions Won: " + line.substring(11));
            }else if(line.equals("samePlace") && playerOP != null && !playerPins.isEmpty()){
                disImage("src/main/resources/images/both.png");
                int prev = playerOP.getPreviousLocation(), curr = playerOP.getCurrentPIMNum();
                if (prev >= 0 && prev < playerPins.size()) playerPins.get(prev).setImage(null);
                if (curr >= 0 && curr < playerPins.size()) playerPins.get(curr).setImage(image);
            }
        }
        @Override
        public void onClosedStatus(boolean isClosed) {

        }
    }
    public void disImage(String p){
        if (p == null || p.isEmpty()) return;
        try (FileInputStream temp = new FileInputStream(p)) {
            image = new Image(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void displayPlayerPosition(Player p){
        if (p == null || playerPins.isEmpty()) return;
        String imgPath = p.getPlayerImageN();
        if (imgPath != null) disImage(imgPath);
        int prev = p.getPreviousLocation(), curr = p.getCurrentPIMNum();
        if (prev >= 0 && prev < playerPins.size()) playerPins.get(prev).setImage(null);
        if (curr >= 0 && curr < playerPins.size()) playerPins.get(curr).setImage(image);
    }
    public void resetPage(){
        playerPins.clear();
        playerPins.add(playerPinPointIV1);
        playerPins.add(playerPinPointIV2);
        playerPins.add(playerPinPointIV3);
        playerPins.add(playerPinPointIV4);
        playerPins.add(playerPinPointIV5);
        playerPins.add(playerPinPointIV6);
        playerPins.add(playerPinPointIV7);
        playerPins.add(playerPinPointIV8);
        playerPins.add(playerPinPointIV9);
        playerPins.add(playerPinPointIV10);
        playerPins.add(playerPinPointIV11);
        for(int j = 0; j< playerPins.size(); j++){
            playerPins.get(j).setImage(null);
        }
        placeBtn.clear();
        placeBtn.add(placeBtn0);
        placeBtn.add(placeBtn1);
        placeBtn.add(placeBtn2);
        placeBtn.add(placeBtn3);
        placeBtn.add(placeBtn4);
        placeBtn.add(placeBtn5);
        placeBtn.add(placeBtn6);
        placeBtn.add(placeBtn7);
        placeBtn.add(placeBtn8);
        placeBtn.add(placeBtn9);
        placeBtn.add(placeBtn10);
        for(int i= 0; i < placeBtn.size(); i++){
            placeBtn.get(i).setStyle("-fx-background-color: grey");
        }

    }
    @FXML
    private void handleReady(ActionEvent event) {
//        if (!sendTextField.getText().equals("")) {
//            String x = sendTextField.getText();
//            socket.sendMessage(x);
//            System.out.println("sent message client");
//        }
        areReady=true;
        safeSend("ready");
        if (serverReady){
            readyAP.setVisible(false);
            handleStart();
        }else{
            readyBtn.setDisable(true);
        }
    }
    @FXML
    private void handleConnectButton(ActionEvent event) {
        connectButton.setDisable(true);
        displayState(ConnectionDisplayState.WAITING);
        connect();
    }
    private Player playerMP;
    private Player playerOP;
    private Image image;
    @FXML
    private ImageView mapIV, playerPinPointIV1, playerPinPointIV2,
            playerPinPointIV3, playerPinPointIV4, playerPinPointIV5, playerPinPointIV6,
            playerPinPointIV7, playerPinPointIV8, playerPinPointIV9, playerPinPointIV10, playerPinPointIV11;
    private ArrayList<ImageView> playerPins = new ArrayList<>();
    @FXML
    private Button controlBtn, waitBtn, AttackBtn, sendMessBtn, continueBtn, quitBtn, resultBtn, placeBtn0, placeBtn1, placeBtn2,
            placeBtn3, placeBtn4, placeBtn5, placeBtn6, placeBtn7, placeBtn8, placeBtn9, placeBtn10,
            placeBtn11;
    private ArrayList<Button> placeBtn = new ArrayList<>();
    @FXML
    private Label gamesWonL, oPTimeL, mPNameL, mPTimeL, energyNumL, aPointL, MPRL, OPRL, MPwinsL, OPwinsL, finalResultL, battleWonL;
    @FXML
    private TextField sendTF, nameTF;
    @FXML
    private AnchorPane resultAP, readyAP, finalResultAP, otherTurnAP;
    @FXML
    private ListView receivedLV;
    private int playerTurn =1;
}