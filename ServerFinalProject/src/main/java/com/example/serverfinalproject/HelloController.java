package com.example.serverfinalproject;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import socketfx.Constants;
import socketfx.FxSocketServer;
import socketfx.SocketListener;

public class HelloController implements Initializable {
    boolean areReady = false;
    boolean clientReady = false;

    @FXML
    private Button sendButton,ready;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton;
    @FXML
    private TextField portTextField;
    @FXML
    private Label lblMessages;


    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private boolean isConnected;
    private int counter = 0;
    private String color;

    public enum ConnectionDisplayState {

        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }

    private FxSocketServer socket;

    private void safeSend(String msg) {
        if (socket != null) socket.sendMessage(msg);
    }

    private void connect() {
        try {
            int port = Integer.parseInt(portTextField.getText().trim());
            socket = new FxSocketServer(new FxSocketListener(),
                    port,
                    Constants.instance().DEBUG_NONE);
            socket.connect();
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid port: " + portTextField.getText());
            if (connectButton != null) connectButton.setDisable(false);
        }
    }

    private void displayState(ConnectionDisplayState state) {
//        switch (state) {
//            case DISCONNECTED:
//                connectButton.setDisable(false);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case WAITING:
//            case AUTOWAITING:
//                connectButton.setDisable(true);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case CONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//            case AUTOCONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);
        if (readyAP != null) readyAP.setVisible(true);
        if (ready != null) ready.setDisable(false);

        Runtime.getRuntime().addShutdownHook(new ShutDownThread());

        /*
         * Uncomment to have autoConnect enabled at startup
         */
//        autoConnectCheckBox.setSelected(true);
//        displayState(ConnectionDisplayState.WAITING);
//        connect();
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
    @FXML
    private void handleConnectButton(ActionEvent event) {
        connectButton.setDisable(true);
        displayState(ConnectionDisplayState.WAITING);
        connect();
    }
    //****************************************************************
    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            System.out.println("message received client");
            lblMessages.setText(line);
            if (line.equals("ready") && areReady) {
                if (readyAP != null) readyAP.setVisible(false);
                handleStart();
                disPosClient();
            } else if (line.equals("ready")) {
                clientReady = true;
            }
            if (line.equals("continue") && areReady) {
                resultAP.setVisible(false);
                resultAP.setDisable(false);
                reset();
                disPosClient();
            }else if(line.equals("continue")){
                clientReady=true;
            }else if(line.startsWith("chOP")){
                if(!checkTurn()) {
                    changePositionOP(places.get(Integer.parseInt(line.substring(4))), Integer.parseInt(line.substring(4)));
                }
            }else if(line.equals("update")){
                sendClientInfo();
            }else if(line.equals("controlOP")){
                if(!checkTurn()) {
                    controlC();
                    displayPlayerPosition(playerOP);
                }
            }else if(line.equals("attackOP")){
                if(!checkTurn()){
                    attackC();
                }

            }else if(line.equals("waitOP")) {
                if (!checkTurn()) {
                    waitC();
                }
            }else if(line.equals("quitOP")) {
                resultAP.setVisible(false);
                readyAP.setVisible(true);
                ready.setDisable(false);
                areReady = false;
            }else if(line.equals("locateOP")){
                if(!checkTurn()){
                    locateC();
                }
            }else if(line.equals("planOP")){
                if(!checkTurn()){
                    planC();
                }
            }else if ( line.startsWith("message")){
                receivedLV.getItems().add(line.substring(7));
            }
        }

        @Override
        public void onClosedStatus(boolean isClosed) {

        }
    }
    @FXML
    public void handleSubText(){
        if (sendTF != null && socket != null && !sendTF.getText().isEmpty()){
            safeSend("message" + sendTF.getText());
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
    public void handleStart(){
        disImage("src/main/resources/images/worldMap.jpg");
        playerPins = new ArrayList<>();
        resetPage();
        playerMP = new Player("Player1", "Red");
        playerOP = new Player("player2", "Green");
        places.clear();
        places.add(new Position("New York"));
        places.add(new Position("London"));
        places.add(new Position("Moscow"));
        places.add(new Position("Madrid"));
        places.add(new Position("Berlin"));
        places.add(new Position("Chicago"));
        places.add(new Position("San Francisco"));
        places.add(new Position("Cancun"));
        places.add(new Position("Paris"));
        places.add(new Position("Geneva"));
        places.add(new Position("Nigeria"));
        actions.clear();
        actions.add(new Ability("Move", 0));
        actions.add(new Ability("Control", 0));
        actions.add(new Ability("Wait", 0));
        actions.add(new Ability("Attack", 0));
        actions.add(new Ability("Locate", 20));
        actions.add(new Ability("Plan", 40));
        actions.add(new Ability("Hide", 30));
        MPSelAb = actions.get(0);
        OPSelAb = actions.get(0);
        places.get(0).setPlacesCanGo(places.get(1));
        places.get(0).setPlacesCanGo(places.get(3));
        places.get(1).setPlacesCanGo(places.get(0));
        places.get(1).setPlacesCanGo(places.get(2));
        places.get(1).setPlacesCanGo(places.get(3));
        places.get(1).setPlacesCanGo(places.get(4));
        places.get(2).setPlacesCanGo(places.get(1));
        places.get(2).setPlacesCanGo(places.get(4));
        places.get(3).setPlacesCanGo(places.get(0));
        places.get(3).setPlacesCanGo(places.get(1));
        places.get(3).setPlacesCanGo(places.get(5));
        places.get(3).setPlacesCanGo(places.get(7));
        places.get(3).setPlacesCanGo(places.get(8));
        places.get(4).setPlacesCanGo(places.get(1));
        places.get(4).setPlacesCanGo(places.get(2));
        places.get(5).setPlacesCanGo(places.get(7));
        places.get(5).setPlacesCanGo(places.get(3));
        places.get(6).setPlacesCanGo(places.get(7));
        places.get(6).setPlacesCanGo(places.get(10));
        places.get(7).setPlacesCanGo(places.get(3));
        places.get(7).setPlacesCanGo(places.get(5));
        places.get(7).setPlacesCanGo(places.get(6));
        places.get(7).setPlacesCanGo(places.get(10));
        places.get(7).setPlacesCanGo(places.get(8));
        places.get(8).setPlacesCanGo(places.get(3));
        places.get(8).setPlacesCanGo(places.get(7));
        places.get(8).setPlacesCanGo(places.get(9));
        places.get(9).setPlacesCanGo(places.get(8));
        places.get(10).setPlacesCanGo(places.get(6));
        places.get(10).setPlacesCanGo(places.get(7));
        playerTurn = (int) (Math.random()*2);
        switchTurnTime();
        mapIV.setImage(image);
        initializePlayer();
        sendClientInfo();
        safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
        safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
        displayPlayerPosition(playerMP);
        displayPlayerPosition(playerOP);
        startTime = System.nanoTime();
        start();
    }
    private double startTime;
    public void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
//                System.out.println(now);
                if(startTime>0){
                    if(now - startTime > 1000000000L){
                        if(returnPlayerTurn().getPlayerTime() != 0) {
                            returnPlayerTurn().setPlayerTime(returnPlayerTurn().getPlayerTime() - 1);
                            showTime();
                            startTime = System.nanoTime();
                        }else{
                            returnPlayerTurn().setPlayerTime(20);
                            showTime();
                            switchTurnTime();
                            startTime = System.nanoTime();
                        }
                    }

                }
            }
        }.start();
    }
    public void showTime(){
        if(checkTurn()){
            mPTimeL.setText("Turn time: "+playerMP.getPlayerTime());
            safeSend("showTimeMP" + playerMP.getPlayerTime() );
        }else {
            oPTimeL.setText("Turn time: "+playerOP.getPlayerTime());
            safeSend("showTimeOP" + playerOP.getPlayerTime() );
        }
    }
    public void initializePlayer(){
        int randNum = (int)(Math.random()*11);
        playerMP.setCurrentPosition(places.get(randNum));
        playerMP.setCurrentPosition(randNum);
        playerMP.setEnergy(2);
        playerMP.setEliminated(false);
        playerMP.setIntelPoints(0);
        playerMP.getControlledPositions().clear();
        randNum = (int)(Math.random()*11);
        playerOP.setCurrentPosition(places.get(randNum));
        playerOP.setCurrentPosition(randNum);
        playerOP.setEnergy(2);
        playerOP.setEliminated(false);
        playerOP.setIntelPoints(0);
        playerOP.getControlledPositions().clear();
    }
    public void disPosClient(){
        safeSend("disPOP");
        safeSend("disPMP");
    }
    public void sendClientInfo(){
        safeSend("POPImNam" + playerOP.getPlayerImageN());
        safeSend("POPNam" + playerOP.getName());
        safeSend("PMPImNam" + playerMP.getPlayerImageN());
        safeSend("PMPNam" + playerMP.getName());
        safeSend("PMPCol" + playerMP.getColor());
        safeSend("POPCol" + playerOP.getColor());
        safeSend("POPWins" + playerOP.getWinsNum());
        safeSend("PMPWins" + playerMP.getWinsNum());
        safeSend("POPE" + playerOP.getEnergy());
        safeSend("POPIP" + playerOP.getIntelPoints());
        safeSend("PMPE" + playerMP.getEnergy());
        safeSend("PMPIP" + playerMP.getIntelPoints());
    }
    @FXML
    public void handleToNewYork(){
        if(checkTurn()) {
            changePositionMP(places.get(0), 0);
        }
    }
    @FXML
    public void handleToLondon(){
        if(checkTurn())
            changePositionMP(places.get(1),1);
    }
    @FXML
    public void handleToMoscow(){
        if(checkTurn())
            changePositionMP(places.get(2),2);
    }
    @FXML
    public void handleToMadrid(){
        if(checkTurn())
            changePositionMP(places.get(3),3);
    }
    @FXML
    public void handleToBerlin(){
        if(checkTurn())
            changePositionMP(places.get(4),4);
    }
    @FXML
    public void handleToChicago(){
        if(checkTurn())
            changePositionMP(places.get(5),5);
    }
    @FXML
    public void handleToSanFran(){
        if(checkTurn())
            changePositionMP(places.get(6),6);
    }
    @FXML
    public void handleToCancun(){
        if(checkTurn())
            changePositionMP(places.get(7),7);

    }
    @FXML
    public void handleToParis(){
        if(checkTurn())
            changePositionMP(places.get(8),8);

    }
    @FXML
    public void handleToGeneva(){
        if(checkTurn())
            changePositionMP(places.get(9),9);

    }
    @FXML
    public void handleToNigeria(){
        if(checkTurn())
            changePositionMP(places.get(10), 10);

    }
    @FXML
    public void handleControl(){
        if(checkTurn()){
            controlS();
        }
    }
    @FXML
    public void handleQuit(){
        resultAP.setVisible(false);
        readyAP.setVisible(true);
        ready.setDisable(false);
        areReady = false;
        safeSend("quitMP");
    }
    @FXML
    public void handleContinue(){
        areReady=true;
        safeSend("continue");
        if (clientReady){
            resultAP.setVisible(false);
            resultAP.setDisable(false);
            reset();
            sendClientInfo();
            safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
            safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
            disPosClient();
        }else{
            resultAP.setDisable(true);
        }
    }
    @FXML
    public void handlePlan(){
        if(checkTurn()){
            planS();
        }
    }
    public void planS(){
        if(playerMP.getIntelPoints()>= actions.get(5).getAbilityCost()) {
            MPSelAb = actions.get(5);
            bonusES = 1;
            safeSend("messageAn attack is being planned!");
            managePointS();
            updatePage();
        }else{
            receivedLV.getItems().add("Not enough intelPoints!");
        }
    }
    public void planC(){
        if(playerOP.getIntelPoints()>= actions.get(5).getAbilityCost()) {
            OPSelAb = actions.get(5);
            bonusEC = 1;
            receivedLV.getItems().add("An attack is being planned!");
            managePointC();
            updatePage();
        }else{
            safeSend("messageNot enough intelPoints!");
        }
    }
    @FXML
    public void handleLocate(){
        if(checkTurn()){
            locateS();
        }
    }
    public void locateS(){
        if(playerMP.getIntelPoints()>= actions.get(4).getAbilityCost()) {
            MPSelAb = actions.get(4);
            displayPlayerPosition(playerOP);
            safeSend("messageYou have been located!");
            managePointS();
            updatePage();
        }else{
            receivedLV.getItems().add("Not enough intelPoints!");
        }
    }
    public void locateC(){
        if(playerOP.getIntelPoints()>= actions.get(4).getAbilityCost()) {
            OPSelAb = actions.get(4);
            safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
            safeSend("disPMP");
            receivedLV.getItems().add("You have been discovered!");
            managePointC();
            updatePage();
        }else{
            safeSend("messageNot enough intelPoints!");
        }
    }
    public void controlS(){
        MPSelAb = actions.get(1);
        if(!playerMP.checkIfControlled(playerMP.getCurrentPIMNum())) {
                if (playerOP.checkIfControlled(playerMP.getCurrentPIMNum())) {
                playerOP.getControlledPositions().remove(Integer.valueOf(playerMP.getCurrentPIMNum()));
                playerMP.setControlledPositions(playerMP.getCurrentPIMNum());
                placeBtn.get(playerMP.getCurrentPIMNum()).setStyle("-fx-background-color: red");
                sendClientInfo();
                safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
                safeSend("controlMP");
            }else{
                playerMP.setControlledPositions(playerMP.getCurrentPIMNum());
                placeBtn.get(playerMP.getCurrentPIMNum()).setStyle("-fx-background-color: red");
                sendClientInfo();
                safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
                safeSend("controlMP");
            }
        }
        managePointS();
        updatePage();
    }
    public void controlC(){
        OPSelAb = actions.get(1);
        if(!playerOP.checkIfControlled(playerOP.getCurrentPIMNum())) {
            if (playerMP.checkIfControlled(playerOP.getCurrentPIMNum())) {
                playerMP.getControlledPositions().remove(Integer.valueOf(playerOP.getCurrentPIMNum()));
                playerOP.setControlledPositions(playerOP.getCurrentPIMNum());
                placeBtn.get(playerOP.getCurrentPIMNum()).setStyle("-fx-background-color: green");
                sendClientInfo();
                safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
                safeSend("controlOP");
            }else{
                playerOP.setControlledPositions(playerOP.getCurrentPIMNum());
                placeBtn.get(playerOP.getCurrentPIMNum()).setStyle("-fx-background-color: green");
                sendClientInfo();
                safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
                safeSend("controlOP");
            }
        }
        managePointC();
        updatePage();
    }
    @FXML
    public void handleWait(){
        if (checkTurn()){
            waitS();
        }
    }
    public void waitS(){
        MPSelAb = actions.get(2);
        managePointS();
        updatePage();
    }
    public void waitC(){
        OPSelAb = actions.get(2);
        managePointC();
        sendClientInfo();
        updatePage();
    }
    @FXML
    public void handleAttack(){
        if (checkTurn()){
            MPSelAb = actions.get(3);
            attackS();
        }
    }
    public void attackS(){
        MPSelAb = actions.get(3);
        if(playerMP.getCurrentPIMNum() == playerOP.getCurrentPIMNum()){
            playerOP.setEliminated(true);
            playerMP.setWinsNum(playerMP.getWinsNum()+1);
        }else{
            safeSend("disPMP");
            safeSend("message" + "Attack has been attempted!");
        }
        sendClientInfo();
        managePointS();
        updatePage();
        battleOutcome();
    }
    public void attackC(){
        OPSelAb = actions.get(3);
        if(playerOP.getCurrentPIMNum() == playerMP.getCurrentPIMNum()){
            playerMP.setEliminated(true);
            playerOP.setWinsNum(playerOP.getWinsNum()+1);
            receivedLV.getItems().add("Attack has been attempted!");
        }else{
            safeSend("message" + "Attack failed!");
            displayPlayerPosition(playerOP);
        }
        sendClientInfo();
        managePointC();
        updatePage();
        battleOutcome();
    }
    @FXML
    public void handleOkay(){
        finalResultAP.setVisible(false);
        resultAP.setVisible(false);
        readyAP.setVisible(true);
        ready.setDisable(false);
        areReady = false;
    }
    public void battleOutcome(){
        if(playerOP.getWinsNum() == 3){
            OPbattleWon++;
            finalResultAP.setVisible(true);
            finalResultL.setText("Lost");
            safeSend("battleWonOP" + OPbattleWon);
            safeSend("finalResultWinner");
            battleWonL.setText("Missions Won: "+String.valueOf(MPbattleWon));
            gamesWonL.setText("Games Won: " +String.valueOf(MPbattleWon));
            areReady = false;
            clientReady = false;
            startTime = -1L;
            otherTurnAP.setVisible(false);
        }else if(playerMP.getWinsNum() == 3){
            MPbattleWon++;
            finalResultAP.setVisible(true);
            finalResultL.setText("Winner");
            safeSend("battleWonOP" + OPbattleWon);
            safeSend("finalResultLose");
            battleWonL.setText("Missions Won: "+String.valueOf(MPbattleWon));
            gamesWonL.setText("Games Won: " + String.valueOf(MPbattleWon));
            areReady = false;
            clientReady = false;
            startTime = -1L;
            otherTurnAP.setVisible(false);
        }else if(playerMP.getEliminated()){
            resultAP.setVisible(true);
            resultBtn.setText("Lost");
            MPwinsL.setText("Wins: "+ playerMP.getWinsNum());
            OPwinsL.setText("Wins: " + playerOP.getWinsNum());
            sendClientInfo();
            safeSend("resultWin");
            areReady = false;
            clientReady = false;
            otherTurnAP.setVisible(false);
            startTime = -1L;
            //set labels to the right things
        }else if(playerOP.getEliminated()){
            resultAP.setVisible(true);
            resultBtn.setText("Win");
            MPwinsL.setText("Wins: "+ playerMP.getWinsNum());
            OPwinsL.setText("Wins: " + playerOP.getWinsNum());
            sendClientInfo();
            safeSend("resultLost");
            areReady = false;
            clientReady = false;
            otherTurnAP.setVisible(false);
            startTime = -1L;
        }
    }
    public void reset(){
        resetPage();
        startTime = System.nanoTime();
        switchTurnTime();
        initializePlayer();
        sendClientInfo();
        safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
        safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
        displayPlayerPosition(playerMP);
        displayPlayerPosition(playerOP);
        updatePage();
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
    private void handleSendMessageButton(ActionEvent event) {
        if (sendTextField != null && !sendTextField.getText().isEmpty()) {
            safeSend(sendTextField.getText());
            System.out.println("Message sent client");
        }
    }
    @FXML
    public void handleReady(ActionEvent event) {
        if (readyAP == null || ready == null) return;
        areReady = true;
        safeSend("ready");

        if (clientReady) {
            readyAP.setVisible(false);
            handleStart();
            sendClientInfo();
            safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
            safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
            disPosClient();
        } else {
            ready.setDisable(true);
        }
    }
    public void displayPlayerPosition(Player p){
        System.out.println(p.getPlayerImageN());
        if(playerMP.getCurrentPIMNum() == playerOP.getCurrentPIMNum()) {
            disImage("src/main/resources/images/both.png");
            safeSend("samePlace");
            playerPins.get(p.getPreviousLocation()).setImage(null);
            playerPins.get(p.getCurrentPIMNum()).setImage(image);
        }else{
            disImage(p.getPlayerImageN());
            playerPins.get(p.getPreviousLocation()).setImage(null);
            playerPins.get(p.getCurrentPIMNum()).setImage(image);
        }
    }
    public void changePositionMP(Position clickedP, int PIMNum){
        if (checkIfCanGo(playerMP, clickedP)) {
            playerMP.setCurrentPosition(clickedP);
            playerMP.setCurrentPosition(PIMNum);
            if(playerOP.checkIfControlled(playerMP.getCurrentPIMNum())){
                sendClientInfo();
                safeSend("PMPCLIn" + playerMP.getCurrentPIMNum());
                safeSend("disPMP");
            }
            if((int)(Math.random()*100)+1 <15){
                bonus = 5;
            }
            displayPlayerPosition(playerMP);
            managePointS();
            bonus = 0;
            safeSend("messageplayer1 on the move");
//            safeSend("chMP" +PIMNum);
        }else{
            warningCantDo(playerMP);
        }
        updatePage();
    }
    public void changePositionOP(Position clickedP, int PIMNum){
        if (checkIfCanGo(playerOP, clickedP)) {
            playerOP.setCurrentPosition(clickedP);
            playerOP.setCurrentPosition(PIMNum);
            if(playerMP.checkIfControlled(playerOP.getCurrentPIMNum())){
                displayPlayerPosition(playerOP);
            }
            if((int)(Math.random()*100)+1 <15){
                bonus = 5;
            }
            if(playerMP.getCurrentPIMNum() == playerOP.getCurrentPIMNum()){
                managePointC();
                sendClientInfo();
                safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
                safeSend("samePlace");
            }else {
                managePointC();
                sendClientInfo();
                safeSend("POPCLIn" + playerOP.getCurrentPIMNum());
                safeSend("chOP" + PIMNum);
                receivedLV.getItems().add("player2 on the move");
            }
        }else{
            warningCantDo(playerOP);
        }
        updatePage();
    }
    public void updatePage(){
        energyNumL.setText(String.valueOf(playerMP.getEnergy()));
        aPointL.setText(String.valueOf(playerMP.getIntelPoints()));
        sendClientInfo();
        safeSend("update");
    }
    public void managePointS(){
        playerMP.setEnergy(playerMP.getEnergy()-1);
        if(!MPSelAb.equals(actions.get(0))) {
            playerMP.setIntelPoints(playerMP.getIntelPoints() - MPSelAb.getAbilityCost());
        }else{
            playerMP.setIntelPoints(playerMP.getIntelPoints() + 10 +bonus);
        }
        MPSelAb = actions.get(0);
        switchPlayerTurnS();
    }
    private int bonus;
    public void managePointC(){
        playerOP.setEnergy(playerOP.getEnergy()-1);
        if(!OPSelAb.equals(actions.get(0))) {
            playerOP.setIntelPoints(playerOP.getIntelPoints() - OPSelAb.getAbilityCost());
        }else{
            playerOP.setIntelPoints(playerOP.getIntelPoints() + 10 +bonus);
        }
        OPSelAb = actions.get(0);
        switchPlayerTurnC();
    }
    public void warningCantDo(Player p){
        if(p.equals(playerMP)){
            receivedLV.getItems().add("can't do it");
        }else{
            safeSend("NPC");
        }
    }
    public boolean checkIfCanGo(Player p, Position clickedP){
        ArrayList<Position> currentP = p.getCurrentPosition().getPlacesCanGo();
        for(int i = 0; i < currentP.size(); i++){
            if(currentP.get(i).equals(clickedP)){
                return true;
            }
        }
        return false;
    }

    public HelloController(){
    }
    public boolean checkTurn(){
        if(playerTurn%2 == 0){
            return false;
        }else{
            return true;
        }
    }
    public Player returnPlayerTurn(){
        if(playerTurn%2 == 0){
            return playerOP;
        }else{
            return playerMP;
        }
    }
    public void switchPlayerTurnS(){
        if (playerMP.getEnergy() == 0) {
            playerMP.setPlayerTime(20);
            playerTurn++;
            playerMP.setEnergy(2+ bonusES);
            receivedLV.getItems().add("player2 turn");
            otherTurnAP.setVisible(true);
            safeSend("OPTurn");
        }
        bonusES = 0;
        updatePage();
    }
    public void switchPlayerTurnC(){
        if (playerOP.getEnergy() == 0) {
            playerOP.setPlayerTime(20);
            playerTurn++;
            playerOP.setEnergy(2+bonusEC);
            otherTurnAP.setVisible(false);
            receivedLV.getItems().add("player1 turn");
            safeSend("MPTurn");
        }
        bonusEC = 0;
        updatePage();
    }
    public void switchTurnTime(){
        if(checkTurn()){
            returnPlayerTurn().setEnergy(2+bonusEC);
            playerTurn++;
            receivedLV.getItems().add("player2 turn");
            otherTurnAP.setVisible(true);
            safeSend("OPTurn");
        }else{
            returnPlayerTurn().setEnergy(2+bonusEC);
            playerTurn++;
            otherTurnAP.setVisible(false);
            safeSend("MPTurn");
            receivedLV.getItems().add("player1 turn");
        }
    }
    private int MPbattleWon = 0;
    private int OPbattleWon = 0;
    private int bonusES = 0;
    private int bonusEC = 0;
    private Image image;
    @FXML
    private ImageView mapIV, playerPinPointIV1, playerPinPointIV2,
            playerPinPointIV3, playerPinPointIV4, playerPinPointIV5, playerPinPointIV6,
    playerPinPointIV7, playerPinPointIV8, playerPinPointIV9, playerPinPointIV10, playerPinPointIV11;
    private ArrayList<ImageView> playerPins;
    @FXML
    private Button controlBtn, waitBtn, AttackBtn, sendMessBtn, continueBtn, quitBtn, resultBtn,  placeBtn0, placeBtn1, placeBtn2,
            placeBtn3, placeBtn4, placeBtn5, placeBtn6, placeBtn7, placeBtn8, placeBtn9, placeBtn10,
            placeBtn11;
    private ArrayList<Button> placeBtn = new ArrayList<>();
    @FXML
    private Label gamesWonL, oPTimeL, mPTimeL, energyNumL, aPointL, MPRL, OPRL, MPwinsL, OPwinsL, finalResultL, battleWonL;
    @FXML
    private ListView receivedLV;
    @FXML
    private TextField  sendTF;
    @FXML
    private AnchorPane resultAP, readyAP, finalResultAP, otherTurnAP;
    private int playerTurn;
    private Player playerMP;
    private Player playerOP;
    private Ability MPSelAb;
    private Ability OPSelAb;
    List<Position> places = new ArrayList<>();
    List<Ability> actions = new ArrayList<>();

}
