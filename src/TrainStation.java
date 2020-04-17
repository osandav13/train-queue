
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.*;
import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class TrainStation extends Application{
    private final int NUM_OF_PASSENGERS = 42;
    private List<Passenger> waitingRoom = new ArrayList<>();
   // private Button[] buttonsArray = new Button[NUM_OF_PASSENGERS];
    //private int gapBetweenWindowAndButtonX = 20;
   // private int gapBetweenWindowAndButtonY = 180;
    private PassengerQueue trainQueue = new PassengerQueue();
    private List<String[]> passengerList = new ArrayList<>();
    //private List<Integer> seatNumberList = new ArrayList<>();
    private Passenger[] boardedPassengers = new Passenger[NUM_OF_PASSENGERS];
    //private List<Passenger> boardedPassengers = new ArrayList<>();

    private void addingToQueue(Stage window){
            AnchorPane root = new AnchorPane();
            window.setScene(new Scene(root, 750, 750));
            window.setTitle("Add a passenger to the train queue");
            DatePicker datePicker = new DatePicker();
            datePicker.setValue(LocalDate.now());
            Label datePickerLabel = new Label("Date");
            datePicker.setLayoutX(65);
            datePicker.setLayoutY(80);
            datePickerLabel.setLayoutX(20);
            datePickerLabel.setLayoutY(85);

            ComboBox<String> leavingStation = new ComboBox<String>();
            leavingStation.getItems().addAll("Colombo","Badulla");
            leavingStation.setValue("Colombo");
            leavingStation.setLayoutX(65);
            leavingStation.setLayoutY(130);
            Label comboboxLable1 = new Label("From");
            comboboxLable1.setLayoutX(20);
            comboboxLable1.setLayoutY(135);
            ComboBox<String> arrivingStation = new ComboBox<String>();
            arrivingStation.getItems().addAll("Colombo","Badulla");
            arrivingStation.setValue("Badulla");
            arrivingStation.setLayoutX(65);
            arrivingStation.setLayoutY(185);
            Label comboboxLable2 = new Label("To");
            comboboxLable2.setLayoutX(20);
            comboboxLable2.setLayoutY(190);

            ListView<String> passengerListView = new ListView<>();
            passengerListView.setMaxHeight(200);
            passengerListView.setLayoutX(450);
            passengerListView.setLayoutY(80);

            Button addToWaitingRoom = new Button("Add to waiting room");
            addToWaitingRoom.setLayoutX(500);
            addToWaitingRoom.setLayoutY(280);

            ListView<String> waitingListView = new ListView<>();
            waitingListView.setMaxHeight(200);
            waitingListView.setLayoutX(60);
            waitingListView.setLayoutY(340);

            Button addToQueue = new Button("Add to queue");
            addToQueue.setLayoutX(200);
            addToQueue.setLayoutY(700);

            ListView<String> queueListView = new ListView<>();
            queueListView.setMaxHeight(200);
            queueListView.setLayoutX(450);
            queueListView.setLayoutY(340);

            Button board = new Button("Board the train");
            board.setLayoutX(550);
            board.setLayoutY(550);

            Button close = new Button("Close");
            close.setLayoutX(650);
            close.setLayoutY(700);

        if (passengerList.size() > 0 || waitingRoom.size() > 0 ){
            if (!trainQueue.isEmpty()){
                addToWaitingRoom.setDisable(true);
            }
            System.out.println("passenger list " + passengerList.size());
            System.out.println("waiting room " + waitingRoom.size());
            System.out.println("Queue " + trainQueue.display().size());
            Scanner input = new Scanner(System.in);
            System.out.println("Do you want to Continue the current boarding");
            String boardingStatus = input.next();
            if (boardingStatus.equals("y")){
                datePicker.setDisable(true);
                arrivingStation.setDisable(true);
                leavingStation.setDisable(true);
                for (String[] passengerDetails : passengerList){
                    passengerListView.getItems().add(passengerDetails[2] + "    " + passengerDetails[3] + "    " + passengerDetails[1]);
                }
                for (Passenger passenger : waitingRoom){
                    waitingListView.getItems().add(passenger.getName() + "    " + passenger.getNicNumber() + "    " + passenger.getSeatNumber());
                }
                for (Passenger passenger :trainQueue.display()){
                    queueListView.getItems().add(passenger.getName() + "    " +
                            passenger.getNicNumber()+ "    " + passenger.getSeatNumber());
                }
            }
        }
/*
            for( int buttonNum = 0; buttonNum < NUM_OF_PASSENGERS; buttonNum++){
                buttonsArray[buttonNum] = new Button("" + (buttonNum + 1));
                buttonsArray[buttonNum].setVisible(false);
                root.getChildren().add(buttonsArray[buttonNum]);
                buttonsArray[buttonNum].setPrefWidth(40);
            }*/

            root.getChildren().addAll(passengerListView,addToWaitingRoom,waitingListView);
            EventHandler<ActionEvent> loadPassengers = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date = datePicker.getValue();
                    String formattedDate = date.format(formatter);

                    MongoClient myclient = MongoClients.create();
                    MongoDatabase myDB = myclient.getDatabase("cwIntegration");
                    MongoCollection<Document> myCollection = myDB.getCollection("bookingDetails");

                    passengerListView.getItems().clear();
                    passengerList.clear();
                    waitingRoom.clear();
                    for (Passenger passenger : trainQueue.display()){
                        try {
                            trainQueue.remove();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i=0;i < boardedPassengers.length;i++){
                        if (boardedPassengers[i] != null){
                            boardedPassengers[i] = null;
                        }
                    }

                    FindIterable<Document> findIterable = myCollection.find(and(eq("date",formattedDate),eq("arrivingStation",arrivingStation.getValue())));
                    for (Document record: findIterable){
                        String bookedDate = (String) record.get("date");
                        String bookedSeat = (String) record.get("seat");
                        String bookedName = (String) record.get("name");
                        String bookedNicNum = (String) record.get("nicNumber");
                        String bookedDepartingStation = (String) record.get("departingStation");
                        String bookedArrivingStation = (String) record.get("arrivingStation");
                        String[] bookingDetailsArray = {bookedDate,bookedSeat,bookedName,bookedNicNum,bookedDepartingStation,bookedArrivingStation};
                        passengerList.add(bookingDetailsArray);

                        passengerListView.getItems().add(bookedName + "    " + bookedNicNum + "    " + bookedSeat);
                    }
                }
            };
            /*
            int passengersInQueue = 0;
            for (Passenger passenger: trainQueue.getQueueArray()){
                if (passenger != null){
                    passengersInQueue +=1;
                }
            }
            if(waitingRoom.size() > 0){
                datePicker.setDisable(true);
                arrivingStation.setDisable(true);
                leavingStation.setDisable(true);
                for(Passenger passenger : waitingRoom){
                    waitingListView.getItems().add(passenger.getName() + "    " + passenger.getNicNumber() + "    " + passenger.getSeatNumber());
                }
            }*/

            addToWaitingRoom.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try{
                        datePicker.setDisable(true);
                        arrivingStation.setDisable(true);
                        leavingStation.setDisable(true);
                        int recordIndex = passengerListView.getSelectionModel().getSelectedIndex();
                        Passenger passenger = new Passenger();
                        passenger.setSeatNumber(passengerList.get(recordIndex)[1]);
                        passenger.setName(passengerList.get(recordIndex)[2]);
                        passenger.setNicNumber(passengerList.get(recordIndex)[3]);
                        passengerListView.getItems().remove(recordIndex);
                        waitingRoom.add(passenger);
                        waitingListView.getItems().add(passenger.getName() + "    " + passenger.getNicNumber() + "    " + passenger.getSeatNumber());
  /*                      seatNumberList.add(Integer.parseInt(passenger.getSeatNumber()));
                        int buttonIndex = Integer.parseInt(passenger.getSeatNumber()) - 1;
                        if (passengerCounter % 10 == 0){
                            gapBetweenWindowAndButtonX+=45;
                            gapBetweenWindowAndButtonY =260;
                        }
                        buttonsArray[buttonIndex].setLayoutX(gapBetweenWindowAndButtonX);
                        buttonsArray[buttonIndex].setLayoutY(gapBetweenWindowAndButtonY);
                        buttonsArray[buttonIndex].setVisible(true);
                        gapBetweenWindowAndButtonY+=40;
                        passengerCounter++;*/
                        //System.out.println(passengerList.get(recordIndex)[1] + " " + passengerList.get(recordIndex)[2]+ " " + passengerList.get(recordIndex)[3]);
                        //System.out.println(passenger.getName() +" "+ passenger.getNicNumber()+" " + passenger.getSeatNumber());
                        passengerList.remove(recordIndex);
                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("You have not selected a passenger");
                    }
                    //System.out.println(Arrays.toString(waitingRoom));
                }
            });

            addToQueue.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    addToWaitingRoom.setDisable(true);
                    Passenger temp;
                    for (int i = 0; i < waitingRoom.size() - 1; i++) {
                        for (int j = 0; j < waitingRoom.size() - 1 - i; j++) {
                                int number1 = Integer.parseInt(waitingRoom.get(j).getSeatNumber()) ;
                                int number2 = Integer.parseInt(waitingRoom.get(j + 1).getSeatNumber()) ;
                                if ( number1 > number2) {
                                    temp = waitingRoom.get(j);
                                    waitingRoom.set(j,waitingRoom.get(j+1));
                                    waitingRoom.set(j+1,temp);
                                }
                            }
                        }

                    int randomNumOfPassengers = 1;//(int)(Math.random() * 6 +1);
                    int numOfPassengersInWaitingRoom = waitingRoom.size();
                    System.out.println("number of passenger " + randomNumOfPassengers);
                    System.out.println("waiting room size " + numOfPassengersInWaitingRoom);
                    if (randomNumOfPassengers <= numOfPassengersInWaitingRoom){
                        while(true){
                            if (randomNumOfPassengers == 0){
                                break;
                            }
                            System.out.println("before " + waitingRoom);
                            Passenger currentPassenger = waitingRoom.get(0);
                            if (!trainQueue.isFull()){
                                trainQueue.add(currentPassenger);
                                waitingListView.getItems().remove(currentPassenger.getName() + "    " + currentPassenger.getNicNumber() + "    " + currentPassenger.getSeatNumber());
                                queueListView.getItems().add(currentPassenger.getName() + "    " +
                                        currentPassenger.getNicNumber()+ "    " + currentPassenger.getSeatNumber());
                                waitingRoom.remove(0);
                                //buttonsArray[Integer.parseInt(currentPassenger.getSeatNumber())-1].setVisible(false);

                            }else {
                                System.out.println("Queue is at max capacity");
                            }
                            System.out.println( "after " + waitingRoom);
                            randomNumOfPassengers-=1;
                        }
                    }else {
                        System.out.println("waiting room is empty");
                    }/*for (Passenger item : trainQueue.queueArray){
                        if (item != null){
                            System.out.println(item.getName() + " " + item.getSeatNumber());
                        }
                    }*/
                }

                    /*
                    for (int i = 0; i < waitingRoom.size() - 1; i++) {
                        for (int j = 0; j < waitingRoom.size() - 1 - i; j++) {
                            // comparing adjacent strings
                            if (waitingRoom[j + 1] != null && waitingRoom[j] != null){
                                int number1 = Integer.parseInt(waitingRoom[j].getSeatNumber()) ;
                                int number2 = Integer.parseInt(waitingRoom[j + 1].getSeatNumber()) ;
                                if ( number1 > number2) {
                                    temp = waitingRoom[j];
                                    waitingRoom[j] = waitingRoom[j + 1];
                                    waitingRoom[j + 1] = temp;
                                }
                            }
                        }
                    }*/
                    /*
                    int numOfPassengers = 2;//(int)(Math.random() * 6 +1);
                    System.out.println("number of passenger " + numOfPassengers);
                    for (int i =0;i < numOfPassengers;i++){
                        System.out.println("before " + Arrays.toString(waitingRoom));
                        Passenger currentPassenger = waitingRoom[i];
                        if (currentPassenger == null){
                            numOfPassengers+=1;
                        }else {
                            trainQueue.add(currentPassenger);
                            buttonsArray[Integer.parseInt(currentPassenger.getSeatNumber())-1].setLayoutY(230);
                            buttonsArray[Integer.parseInt(currentPassenger.getSeatNumber())-1].setLayoutX(350);
                            waitingRoom[i] = null;
                            System.out.println( "after " + Arrays.toString(waitingRoom));
                            //System.out.println(trainQueue.queueArray[]);
                        }

                    }


                    for (Passenger item : trainQueue.queueArray){
                        if (item != null){
                            System.out.println(item.getName() + " " + item.getSeatNumber());
                        }
                    }*/
                    /*
                    int numOfPassengers = (int)(Math.random() * 6 +1);
                    System.out.println("number of passenger " + numOfPassengers);
                    for (int i =0; i < numOfPassengers;i++){
                        int smallestNumber = Collections.min(seatNumberList);
                        System.out.println("smallest number is " +smallestNumber);
                        for (int j=0;j < waitingRoom.length;j++){
                            if (waitingRoom[j] == null){
                                System.out.println("i am null");
                            }else {
                                System.out.println(waitingRoom[j].getSeatNumber());
                                int seatNumber = Integer.parseInt(waitingRoom[j].getSeatNumber());
                                if(seatNumber == smallestNumber) {
                                    trainQueue.add(waitingRoom[j]);
                                    buttonsArray[seatNumber].setVisible(false);
                                    System.out.println("created");
                                    seatNumberList.remove(smallestNumber);
                                    //waitingRoom[j] = null;
                                }
                            }
                        }
                    }*/

            });

            board.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    for (Passenger passenger:trainQueue.display()){
                        if (passenger != null){
                            try {
                                Passenger boardedPassenger = trainQueue.remove();
                                boardedPassengers[Integer.parseInt(boardedPassenger.getSeatNumber())] = boardedPassenger;
                                queueListView.getItems().remove(boardedPassenger.getName() + "    " +
                                        boardedPassenger.getNicNumber()+ "    " + boardedPassenger.getSeatNumber());
                                //System.out.println(PassengerQueue.queueArray.);
                                System.out.println(boardedPassenger.getName() + " in seat number "+ boardedPassenger.getSeatNumber() + " has boarded the train" );
                            } catch (Exception e) {
                                System.out.println("Queue is empty");
                            }
                        }
                    }
                    if (trainQueue.isEmpty()){
                        addToWaitingRoom.setDisable(false);
                    }
                }
            });

            datePicker.setOnAction(loadPassengers);
            arrivingStation.setOnAction(loadPassengers);
            leavingStation.setOnAction(loadPassengers);

            root.getChildren().addAll(datePicker,leavingStation,arrivingStation,addToQueue,queueListView,close,board);
            root.getChildren().addAll(datePickerLabel,comboboxLable1,comboboxLable2);
            window.show();

            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    window.close();
                    selector(window);
                }
            });
    }

    private void viewQueue(Stage window){
        AnchorPane root = new AnchorPane();
        window.setScene(new Scene(root, 750, 750));
        window.setTitle("Queue");

        ListView<String> waitingRoomListView = new ListView<>();
        waitingRoomListView.setMaxHeight(250);
        waitingRoomListView.setLayoutX(50);
        waitingRoomListView.setLayoutY(80);

        for (Passenger passenger: waitingRoom){
            String passengerName =passenger.getName();
            String passengerNic =passenger.getNicNumber();
            waitingRoomListView.getItems().add(passengerName + "    " + passengerNic  + "    " + passenger.getSeatNumber());
        }

        ListView<String> queueListView = new ListView<>();
        queueListView.setMaxHeight(250);
        queueListView.setLayoutX(450);
        queueListView.setLayoutY(80);

        //Passenger[] passengersInQueue = trainQueue.getQueueArray();
        for (Passenger passenger : trainQueue.display()){
            if (passenger != null){
                String passengerName =passenger.getName();
                String passengerNic =passenger.getNicNumber();
                queueListView.getItems().add(passengerName + "    " + passengerNic + "    " + passenger.getSeatNumber());
            }
        }

        ListView<String> boardedListView = new ListView<>();
        boardedListView.setMaxHeight(250);
        boardedListView.setLayoutX(250);
        boardedListView.setLayoutY(400);

        for (int i=0;i<NUM_OF_PASSENGERS;i++){
            if (boardedPassengers[i] != null){
                String passengerName = boardedPassengers[i].getName();
                String passengerNic = boardedPassengers[i].getNicNumber();
                String passengerSeat = boardedPassengers[i].getSeatNumber();
                boardedListView.getItems().add(passengerSeat + "      " + passengerName  + "      " + passengerNic);
            }else {
                boardedListView.getItems().add(i + "    " + "empty");
            }
        }
/*
        for (int i=0;i<NUM_OF_PASSENGERS;i++){
            if (boardedPassengers.get(i).getSeatNumber().equals(String.valueOf(i+1))){
                String passengerName = boardedPassengers.get(i).getName();
                String passengerNic = boardedPassengers.get(i).getNicNumber();
                String passengerSeat = boardedPassengers.get(i).getSeatNumber();
                boardedListView.getItems().add(passengerName + "      " + passengerNic  + "      " + passengerSeat);
            }else {
                boardedListView.getItems().add(String.valueOf(i+1));
            }

        }*/

        Button close = new Button("Close");
        close.setLayoutX(650);
        close.setLayoutY(700);


        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.close();
                selector(window);
            }
        });

        root.getChildren().addAll(waitingRoomListView,queueListView,close,boardedListView);
        window.show();
    }

    private void deleteFromQueue(Stage window){
        List<Passenger> tempQueue = new ArrayList<>();
        boolean isNicCorrect;
        boolean isSeatNumCorrect = false;
        boolean passengerInTheQueue = false;
        String nicNum;
        String seatNum = null;

        Scanner deleteFromQueueInput = new Scanner(System.in);
        do {
            System.out.print("Enter the NIC number: ");
            nicNum = deleteFromQueueInput.next();
            if (nicNum.length() == 10){
                isNicCorrect = true;
            }else {
                isNicCorrect = false;
            }
        }while (!isNicCorrect);

        do {
            try {
                System.out.print("Enter the seat number: ");
                seatNum = deleteFromQueueInput.next();
                int seatNumInt = Integer.parseInt(seatNum);
                if (seatNumInt > NUM_OF_PASSENGERS || seatNumInt <= 0) {
                    isSeatNumCorrect = false;
                    System.out.println("Seat number is not correct");
                } else {
                    isSeatNumCorrect = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("You have not entered a number");
            }
        }while(!isSeatNumCorrect);
        
        
        for (Passenger pasenger : trainQueue.display()){
            if (pasenger !=null){
                if (pasenger.getNicNumber().equals(nicNum) && pasenger.getSeatNumber().equals(seatNum)){
                    System.out.println("passenger in the queue");
                    passengerInTheQueue = true;
                }else{
                    tempQueue.add(pasenger);
                }
            }
        }
        if (passengerInTheQueue){
            for (int i =0;i< trainQueue.getQueueArray().length;i++){
                if (!trainQueue.isEmpty()){
                    try {
                        trainQueue.remove();
                    } catch (Exception e) {
                        System.out.println("Queue is empty");
                    }
                }else {
                    System.out.println("queue is empty");
                }
            }
            for (int i =0;i< tempQueue.size();i++){
                trainQueue.add(tempQueue.get(i));
            }
        }else{
            System.out.println("Passenger is not in the Queue");
        }
        selector(window);
    }

    private int delayGenerator(){
        int delay = 0;
        for (int i=0;i<3;i++){
            delay += (int)(Math.random() * 6 +1);
        }
        return delay;
    }

    private void simulation(Stage window){
        AnchorPane root = new AnchorPane();
        window.setScene(new Scene(root, 750, 750));
        window.setTitle("Add a passenger to the train queue");

        /*if (waitingRoom.size() == 0){
            System.out.println("There is no data to start the simulation");
            System.out.println("Add using option A from the menu");
            selector(window);
        }else{*/
            ListView<String> waitingRoomListView = new ListView<>();
            waitingRoomListView.setMaxHeight(250);
            waitingRoomListView.setLayoutX(50);
            waitingRoomListView.setLayoutY(80);

            Button startSimulation = new Button("Start Simulation");
            startSimulation.setLayoutX(90);
            startSimulation.setLayoutY(350);

            Label maxLengthOfQueue = new Label("Maximum length queue attained :");
            maxLengthOfQueue.setLayoutX(250);
            maxLengthOfQueue.setLayoutY(500);
            Text maxLengthText = new Text("21");
            maxLengthText.setLayoutX(440);
            maxLengthText.setLayoutY(515);

            Label maxWaitingTime = new Label("Maximum waiting time :");
            maxWaitingTime.setLayoutX(250);
            maxWaitingTime.setLayoutY(530);

            Label minWaitingTime = new Label("Minimum waiting time :");
            minWaitingTime.setLayoutX(250);
            minWaitingTime.setLayoutY(560);

            Label avgWaitingTime = new Label("Average waiting time :");
            avgWaitingTime.setLayoutX(250);
            avgWaitingTime.setLayoutY(590);

            Button close = new Button("Close");
            close.setLayoutX(650);
            close.setLayoutY(700);

            for (Passenger passenger: waitingRoom){
                String passengerName = passenger.getName();
                String passengerNic = passenger.getNicNumber();
                String passengerSeat = passenger.getSeatNumber();
                waitingRoomListView.getItems().add(passengerName + "      " + passengerNic + "      " + passengerSeat);
            }

            startSimulation.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    startSimulation.setDisable(true);
                    Passenger temp;
                    for (int i = 0; i < waitingRoom.size() - 1; i++) {
                        for (int j = 0; j < waitingRoom.size() - 1 - i; j++) {
                            int number1 = Integer.parseInt(waitingRoom.get(j).getSeatNumber()) ;
                            int number2 = Integer.parseInt(waitingRoom.get(j + 1).getSeatNumber()) ;
                            if ( number1 > number2) {
                                temp = waitingRoom.get(j);
                                waitingRoom.set(j,waitingRoom.get(j+1));
                                waitingRoom.set(j+1,temp);
                            }
                        }
                    }
                    int inheritedDelay = 0;
                    boolean queueEmpty = false;
                    while (!queueEmpty){
                        while (waitingRoom.size() != 0){
                            int randomNumOfPassengers = (int)(Math.random() * 6 +1);
                            int numOfPassengersInWaitingRoom = waitingRoom.size();
                            System.out.println("number of passenger " + randomNumOfPassengers);
                            System.out.println("waiting room size " + numOfPassengersInWaitingRoom);
                            if (randomNumOfPassengers <= numOfPassengersInWaitingRoom){
                                while (randomNumOfPassengers != 0) {
                                    Passenger passenger = waitingRoom.get(0);
                                    int processingDelay = delayGenerator();
                                    System.out.println("delay is " + processingDelay);
                                    //System.out.println("before " + waitingRoom);
                                    if (!trainQueue.isFull()) {
                                        passenger.setProcessingDelay(processingDelay);
                                        passenger.setSecondsInQueue(inheritedDelay + processingDelay);
                                        inheritedDelay += processingDelay;
                                        trainQueue.add(passenger);
                                        waitingRoom.remove(0);
                                    } else {
                                        System.out.println("Queue is at max capacity");
                                    }
                                    //System.out.println("after " + waitingRoom);
                                    randomNumOfPassengers -= 1;
                                }
                                try {
                                    Passenger boardedPassenger = trainQueue.remove();
                                    inheritedDelay -= boardedPassenger.getProcessingDelay();
                                    boardedPassengers[Integer.parseInt(boardedPassenger.getSeatNumber())] = boardedPassenger;
                                    System.out.println(boardedPassenger.getName() + " in seat number "+ boardedPassenger.getSeatNumber() + " has boarded the train" );
                                } catch (Exception e) {
                                    System.out.println("this shouldnt print");
                                }
                            }else {
                                System.out.println("random number is too high try again");
                            }
                        }
                        try {
                            Passenger boardedPassenger = trainQueue.remove();
                            boardedPassengers[Integer.parseInt(boardedPassenger.getSeatNumber())] = boardedPassenger;
                            System.out.println(boardedPassenger.getName() + " in seat number "+ boardedPassenger.getSeatNumber() + " has boarded the train" );
                        } catch (Exception e) {
                            System.out.println("queue is empty");
                        }
                        queueEmpty =trainQueue.isEmpty();
                    }
                    System.out.println("max from passenger queue " + trainQueue.getMaxStay());
                    System.out.println("min from passenger queue " + trainQueue.getMinStay());
                    System.out.println("avg from passenger queue " + trainQueue.getAverageStay());


                }
            });


            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    window.close();
                    selector(window);
                }
            });

            root.getChildren().addAll(waitingRoomListView,startSimulation,close);
            root.getChildren().addAll(maxLengthOfQueue,maxWaitingTime,minWaitingTime,avgWaitingTime,maxLengthText);
            window.show();

    }

    private String menu(){
        Scanner input = new Scanner(System.in);
        System.out.println("////////////////////////////////////////////////////////////////////");
        System.out.println("Enter A to Add a passenger to the train queue ");
        System.out.println("Enter v to View the train queue");
        System.out.println("Enter D to Delete passenger from the train queue ");
        System.out.println("Enter S to Store train queue data to a file");
        System.out.println("Enter L to Load data back from the file ");
        System.out.println("Enter R to Run the simulation and produce report ");
        System.out.println("Enter Q to quit the programme");
        System.out.print("Enter the correct input: ");
        String userInput = input.next().toLowerCase();
        return userInput;
    }

    private void selector(Stage primaryStage){
        boolean isInputCorrect = false;
        while (!isInputCorrect){
            String userInput = menu();
            switch (userInput){
                case "a":
                    addingToQueue(primaryStage);
                    isInputCorrect = true;
                    break;
                case "v":
                    viewQueue(primaryStage);
                    isInputCorrect = true;
                    break;
                case "d":
                    deleteFromQueue(primaryStage);
                    isInputCorrect = true;
                    break;
                case "s":
                    System.out.println("s not yet implemented");
                    isInputCorrect = true;
                    break;
                case "l":
                    System.out.println("l not yet implemented");
                    isInputCorrect = true;
                    break;
                case "r":
                    simulation(primaryStage);
                    isInputCorrect = true;
                    break;
                default:
                    isInputCorrect = false;
                    System.out.println("you have entered a wrong input");
            }
        }
    }

    public void start(Stage primaryStage) {
        selector(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
