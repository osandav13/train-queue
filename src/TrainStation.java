
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

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class TrainStation extends Application{
    private final int NUM_OF_PASSENGERS = 42;
    private List<Passenger> waitingRoom = new ArrayList<>();
    private PassengerQueue trainQueue = new PassengerQueue();
    private List<String[]> passengerList = new ArrayList<>();
    //private List<Integer> seatNumberList = new ArrayList<>();
    //private Passenger[] boardedPassengers = new Passenger[NUM_OF_PASSENGERS];
    private List<Passenger> boardedPassengers = new ArrayList<>();
    private String currentDepartingStation;
    private String currentDate;

    private void bubbleSort(){
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
    }

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

        ComboBox<String> leavingStation = new ComboBox<>();
        leavingStation.getItems().addAll("Colombo","Badulla");
        leavingStation.setValue("Colombo");
        leavingStation.setLayoutX(65);
        leavingStation.setLayoutY(130);
        Label comboboxLable1 = new Label("From");
        comboboxLable1.setLayoutX(20);
        comboboxLable1.setLayoutY(135);
        ComboBox<String> arrivingStation = new ComboBox<>();
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
            System.out.println("Queue " + trainQueue.getPassengersInQueue().size());
            Scanner input = new Scanner(System.in);
            System.out.print("Do you want to Continue the current boarding(y/n):");
            String boardingStatus = input.next().toLowerCase();
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
                for (Passenger passenger :trainQueue.getPassengersInQueue()){
                    queueListView.getItems().add(passenger.getName() + "    " +
                            passenger.getNicNumber()+ "    " + passenger.getSeatNumber());
                }
            }
        }

            root.getChildren().addAll(passengerListView,addToWaitingRoom,waitingListView);
            EventHandler<ActionEvent> loadPassengers = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date = datePicker.getValue();
                    String formattedDate = date.format(formatter);
                    currentDate = formattedDate;
                    currentDepartingStation = leavingStation.getValue();
                    System.out.println(currentDate);
                    System.out.println(currentDepartingStation);

                    MongoClient myclient = MongoClients.create();
                    MongoDatabase myDB = myclient.getDatabase("cwIntegration");
                    MongoCollection<Document> myCollection = myDB.getCollection("bookingDetails");

                    passengerListView.getItems().clear();
                    passengerList.clear();
                    waitingRoom.clear();
                    boardedPassengers.clear();
                    for (int i = 0;i < trainQueue.getPassengersInQueue().size();i++){
                        trainQueue.remove();
                    }
                    if (leavingStation.getValue().equals(arrivingStation.getValue())){
                        addToWaitingRoom.setDisable(true);
                        System.out.println("Departing station and arriving station cannot be same");
                    }else {
                        addToWaitingRoom.setDisable(false);
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
                }
            };

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
                        passenger.setDate(passengerList.get(recordIndex)[0]);
                        passenger.setDepartureStation(passengerList.get(recordIndex)[4]);
                        passengerListView.getItems().remove(recordIndex);
                        waitingRoom.add(passenger);
                        waitingListView.getItems().add(passenger.getName() + "    " + passenger.getNicNumber() + "    " + passenger.getSeatNumber());
                        passengerList.remove(recordIndex);
                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("You have not selected a passenger");
                    }
                }
            });

            addToQueue.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    addToWaitingRoom.setDisable(true);
                    bubbleSort();

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
                    }
                }
            });

            board.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //for (Passenger passenger:trainQueue.getPassengersInQueue()){
                    if (!trainQueue.isEmpty()){
                        Passenger boardedPassenger = trainQueue.remove();
                        boardedPassengers.add(boardedPassenger);
                        queueListView.getItems().remove(boardedPassenger.getName() + "    " +
                                boardedPassenger.getNicNumber()+ "    " + boardedPassenger.getSeatNumber());
                        System.out.println(boardedPassenger.getName() + " in seat number "+
                                boardedPassenger.getSeatNumber() + " has boarded the train" );
                    }else {
                        System.out.println("Queue is empty");
                    }
                    //}
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

        for (Passenger passenger : trainQueue.getPassengersInQueue()){
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
        boolean hasBoarded;
        for (int i=1;i <= NUM_OF_PASSENGERS;i++){
            hasBoarded = false;
            for (Passenger passenger : boardedPassengers){
                int seatNumber = Integer.parseInt(passenger.getSeatNumber());
                if (seatNumber == i){
                    String passengerName = passenger.getName();
                    String passengerNic = passenger.getNicNumber();
                    String passengerSeat = passenger.getSeatNumber();
                    boardedListView.getItems().add(passengerSeat + "      " + passengerName  + "      " + passengerNic);
                    hasBoarded = true;
                    break;
                }
            }if (!hasBoarded){
                boardedListView.getItems().add(i + "    " + "empty");
            }
        }

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

        for (Passenger pasenger : trainQueue.getPassengersInQueue()){
            if (pasenger !=null){
                if (pasenger.getSeatNumber().equals(seatNum)){
                    System.out.println("passenger in the queue");
                    passengerInTheQueue = true;
                }else{
                    tempQueue.add(pasenger);
                }
            }
        }
        if (passengerInTheQueue){
            for (int i =0;i< tempQueue.size() + 1;i++){
                System.out.println("before "+trainQueue.getPassengersInQueue().size());
                trainQueue.remove();
                System.out.println("later "+trainQueue.getPassengersInQueue().size());
                System.out.println("came here");
            }
            for (int i =0;i< tempQueue.size();i++){
                trainQueue.add(tempQueue.get(i));
            }
        }else{
            System.out.println("Passenger is not in the Queue");
        }
        selector(window);
    }

    private void saveToDb(Stage window){
        MongoClient myClient = MongoClients.create();
        MongoDatabase myDb = myClient.getDatabase("cwIntegration");
        MongoCollection<Document> myCollection = myDb.getCollection("queueData");
        if (trainQueue.getPassengersInQueue().size() > 0){
            List<Passenger> currentPassengers = trainQueue.getPassengersInQueue();
            myCollection.deleteMany(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));

            for (Passenger queuePassenger : currentPassengers){
                Document document = new Document("date",queuePassenger.getDate())
                        .append("seat",queuePassenger.getSeatNumber())
                        .append("name",queuePassenger.getName())
                        .append("nicNumber",queuePassenger.getNicNumber())
                        .append("departingStation",queuePassenger.getDepartureStation());

                myCollection.insertOne(document);
            }
            FindIterable<Document> findIterable = myCollection.find(new Document());
            for (Document document: findIterable){
                System.out.println(document.toJson());
            }
        }else {
            myCollection.deleteMany(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));
            System.out.println("There are no passengers in the Queue");
        }

        myCollection = myDb.getCollection("waitingRoomData");
        if (waitingRoom.size() > 0){
            myCollection.deleteMany(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));

            for (Passenger waitingRoomPassenger : waitingRoom){
                Document document = new Document("date", waitingRoomPassenger.getDate())
                        .append("seat", waitingRoomPassenger.getSeatNumber())
                        .append("name", waitingRoomPassenger.getName())
                        .append("nicNumber", waitingRoomPassenger.getNicNumber())
                        .append("departingStation", waitingRoomPassenger.getDepartureStation());

                myCollection.insertOne(document);
            }
            FindIterable<Document> findIterable = myCollection.find(new Document());
            for (Document document: findIterable){
                System.out.println(document.toJson());
            }
        }else {
            myCollection.deleteMany(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));
            System.out.println("There are no passengers in the Waiting room");
        }

        myCollection = myDb.getCollection("boardedPassengerData");
        if (boardedPassengers.size() > 0){
            myCollection.deleteMany(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));

            for (Passenger boardedPassenger : boardedPassengers){
                Document document = new Document("date", boardedPassenger.getDate())
                        .append("seat", boardedPassenger.getSeatNumber())
                        .append("name", boardedPassenger.getName())
                        .append("nicNumber", boardedPassenger.getNicNumber())
                        .append("departingStation", boardedPassenger.getDepartureStation());

                myCollection.insertOne(document);
            }
            FindIterable<Document> findIterable = myCollection.find(new Document());
            for (Document document: findIterable){
                System.out.println(document.toJson());
            }
        }else {
            myCollection.deleteMany(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));
            System.out.println("There are no boarded passengers");
        }
        selector(window);
    }

    private boolean dateValidator(String dateToValidate){
        if(dateToValidate == null){
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            //if not valid, it will throw ParseException
            dateFormat.parse(dateToValidate);
            currentDate = dateToValidate;
            return true;
        } catch (ParseException exception) {
            System.out.println("You have entered a wrong date format");
            return false;
        }
    }

    private void loadFromDb(Stage window){
        boolean isDateCorrect;
        boolean isStationCorrect;
        String inputDate;
        String station;

        Scanner input = new Scanner(System.in);
        do {
            System.out.print("Enter the Date(dd-mm-yyyy):");
            inputDate = input.next();
            isDateCorrect = dateValidator(inputDate);
        }while (!isDateCorrect);
        do {
            System.out.print("Enter the departing station:");
            station = input.next();
            if (station.equals("Colombo") || station.equals("Badulla")){
                currentDepartingStation = station;
                isStationCorrect = true;
            }else {
                System.out.println("Enter the correct station");
                isStationCorrect = false;
            }
        }while (!isStationCorrect);

        if(waitingRoom.size() > 0 || trainQueue.getPassengersInQueue().size() > 0 || boardedPassengers.size() > 0){
            while (!trainQueue.isEmpty()) {
                trainQueue.remove();
            }
            waitingRoom.clear();
            boardedPassengers.clear();
        }
        MongoClient myClient = MongoClients.create();
        MongoDatabase myDb = myClient.getDatabase("cwIntegration");

        MongoCollection<Document> myCollection = myDb.getCollection("queueData");
        FindIterable<Document> queueTable = myCollection.find(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));
        for (Document queuePassenger : queueTable){
            Passenger passenger = new Passenger();
            passenger.setSeatNumber((String) queuePassenger.get("seat"));
            passenger.setName((String) queuePassenger.get("name"));
            passenger.setNicNumber((String) queuePassenger.get("nicNumber"));
            passenger.setDate((String) queuePassenger.get("date"));
            passenger.setDepartureStation((String) queuePassenger.get("departingStation"));

            trainQueue.add(passenger);
        }
        myCollection = myDb.getCollection("waitingRoomData");
        FindIterable<Document> waitingRoomTable = myCollection.find(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));
        for (Document waitingRoomPassenger : waitingRoomTable){
            Passenger passenger = new Passenger();
            passenger.setSeatNumber((String) waitingRoomPassenger.get("seat"));
            passenger.setName((String) waitingRoomPassenger.get("name"));
            passenger.setNicNumber((String) waitingRoomPassenger.get("nicNumber"));
            passenger.setDate((String) waitingRoomPassenger.get("date"));
            passenger.setDepartureStation((String) waitingRoomPassenger.get("departingStation"));

            waitingRoom.add(passenger);
        }
        myCollection = myDb.getCollection("boardedPassengerData");
        FindIterable<Document> boardedPassengerTable = myCollection.find(and(eq("date",currentDate),eq("departingStation",currentDepartingStation)));
        for (Document boardedPassenger : boardedPassengerTable){
            Passenger passenger = new Passenger();
            passenger.setSeatNumber((String) boardedPassenger.get("seat"));
            passenger.setName((String) boardedPassenger.get("name"));
            passenger.setNicNumber((String) boardedPassenger.get("nicNumber"));
            passenger.setDate((String) boardedPassenger.get("date"));
            passenger.setDepartureStation((String) boardedPassenger.get("departingStation"));

            boardedPassengers.add(passenger);
        }

        for (Passenger details : trainQueue.getPassengersInQueue()) {
            System.out.println("following data has been inserted to the booking system " + details.getName() + " " +
                    details.getSeatNumber() + " " + details.getNicNumber() + " " + details.getDate()+ " " + details.getDepartureStation());
        }
        for (Passenger details : waitingRoom) {
            System.out.println("following data has been inserted to the booking system " + details.getName() + " " +
                    details.getSeatNumber() + " " + details.getNicNumber() + " " + details.getDate()+ " " + details.getDepartureStation());
        }
        for (Passenger details : boardedPassengers) {
            System.out.println("following data has been inserted to the booking system " + details.getName() + " " +
                    details.getSeatNumber() + " " + details.getNicNumber() + " " + details.getDate()+ " " + details.getDepartureStation());
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

    private void simulation(Stage window) {
        AnchorPane root = new AnchorPane();
        window.setScene(new Scene(root, 750, 750));
        window.setTitle("Add a passenger to the train queue");

        if (waitingRoom.size() == 0){
            System.out.println("There is no data to start the simulation");
            System.out.println("Add data using option A or L from the menu");
            selector(window);
        }else{
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
        Text maxLengthText = new Text();
        maxLengthText.setLayoutX(440);
        maxLengthText.setLayoutY(515);

        Label maxWaitingTime = new Label("Maximum waiting time :");
        maxWaitingTime.setLayoutX(250);
        maxWaitingTime.setLayoutY(530);
        Text maxWaitingText = new Text();
        maxWaitingText.setLayoutX(380);
        maxWaitingText.setLayoutY(545);

        Label minWaitingTime = new Label("Minimum waiting time :");
        minWaitingTime.setLayoutX(250);
        minWaitingTime.setLayoutY(560);
        Text minWaitingText = new Text();
        minWaitingText.setLayoutX(380);
        minWaitingText.setLayoutY(575);


        Label avgWaitingTime = new Label("Average waiting time :");
        avgWaitingTime.setLayoutX(250);
        avgWaitingTime.setLayoutY(590);
        Text avgWaitingText = new Text();
        avgWaitingText.setLayoutX(375);
        avgWaitingText.setLayoutY(605);


        Button close = new Button("Close");
        close.setLayoutX(650);
        close.setLayoutY(700);

        for (Passenger passenger : waitingRoom) {
            String passengerName = passenger.getName();
            String passengerNic = passenger.getNicNumber();
            String passengerSeat = passenger.getSeatNumber();
            waitingRoomListView.getItems().add(passengerName + "      " + passengerNic + "      " + passengerSeat);
        }

        startSimulation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startSimulation.setDisable(true);
                bubbleSort();
                int inheritedDelay = 0;
                boolean queueEmpty = false;
                while (!queueEmpty) {
                    while (waitingRoom.size() != 0) {
                        int randomNumOfPassengers = (int) (Math.random() * 6 + 1);
                        int numOfPassengersInWaitingRoom = waitingRoom.size();
                        System.out.println("number of passenger " + randomNumOfPassengers);
                        System.out.println("waiting room size " + numOfPassengersInWaitingRoom);
                        if (randomNumOfPassengers <= numOfPassengersInWaitingRoom) {
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
                                boardedPassengers.add(boardedPassenger);
                                System.out.println(boardedPassenger.getName() + " in seat number " + boardedPassenger.getSeatNumber() + " has boarded the train");
                            } catch (Exception e) {
                                System.out.println("this shouldnt print");
                            }
                        } else {
                            System.out.println("random number is too high try again");
                        }
                    }
                    try {
                        Passenger boardedPassenger = trainQueue.remove();
                        boardedPassengers.add(boardedPassenger);
                        System.out.println(boardedPassenger.getName() + " in seat number " + boardedPassenger.getSeatNumber() + " has boarded the train");
                    } catch (Exception e) {
                        System.out.println("queue is empty");
                    }
                    queueEmpty = trainQueue.isEmpty();
                }
                maxLengthText.setText(String.valueOf(trainQueue.getLength()));
                maxWaitingText.setText(String.valueOf(trainQueue.getMaxStay()) + " s");
                minWaitingText.setText(String.valueOf(trainQueue.getMinStay()) + " s");
                avgWaitingText.setText(String.valueOf(trainQueue.getAverageStay()) + " s");

                File file = new File("report test.txt");
                PrintWriter printWriter;
                FileWriter fileWriter;
                try {
                    fileWriter = new FileWriter(file, true);
                    printWriter = new PrintWriter(fileWriter, true);
                    printWriter.println("//////////////////////////////////////////////////////////////////////////");
                    //printWriter.print("passenger name  " + "  waiting time");
                    for (Passenger passenger : boardedPassengers) {
                        if (passenger != null){
                            printWriter.println(passenger.getName() + "   " + passenger.getSecondsInQueue());
                        }
                    }
                    printWriter.println("Maximum length queue attained : " + trainQueue.getLength());
                    printWriter.println("Maximum waiting time : " + trainQueue.getMaxStay() + " s");
                    printWriter.println("Minimum waiting time : " + trainQueue.getMinStay() + " s");
                    printWriter.println("Average waiting time : " + trainQueue.getAverageStay() + " s");

                } catch (FileNotFoundException e) {
                    System.out.println("File cannot be found");
                } catch (IOException e) {
                    System.out.println("Write permission required");
                }
            }
        });

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.close();
                selector(window);
            }
        });

        root.getChildren().addAll(waitingRoomListView, startSimulation, close);
        root.getChildren().addAll(maxLengthOfQueue, maxWaitingTime, minWaitingTime,
                avgWaitingTime, maxLengthText, minWaitingText, maxWaitingText, avgWaitingText);
        window.show();
        }
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
                    saveToDb(primaryStage);
                    isInputCorrect = true;
                    break;
                case "l":
                    loadFromDb(primaryStage);
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
