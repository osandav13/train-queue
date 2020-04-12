
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.mongodb.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import com.mongodb.client.model.Sorts;
import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class TrainStation extends Application{
    private final int NUM_OF_PASSENGERS = 42;
    private List<Passenger> waitingRoom = new ArrayList<>();
    private Button[] buttonsArray = new Button[NUM_OF_PASSENGERS];
    private int passengerCounter;
    private int gapBetweenWindowAndButtonX = 20;
    private int gapBetweenWindowAndButtonY = 180;
    private PassengerQueue trainQueue = new PassengerQueue();
    private List<String[]> passengerList = new ArrayList<>();
    private List<Integer> seatNumberList = new ArrayList<>();
    private List<Passenger> boardedPassengers = new ArrayList<>();

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

            for( int buttonNum = 0; buttonNum < NUM_OF_PASSENGERS; buttonNum++){
                buttonsArray[buttonNum] = new Button("" + (buttonNum + 1));
                buttonsArray[buttonNum].setVisible(false);
                root.getChildren().add(buttonsArray[buttonNum]);
                buttonsArray[buttonNum].setPrefWidth(40);
            }

            root.getChildren().addAll(passengerListView,addToWaitingRoom);
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

                    FindIterable<Document> findIterable = myCollection.find(and(eq("date",formattedDate),eq("arrivingStation",arrivingStation.getValue())));
                    int i =0;
                    for (Document record: findIterable){
                        String bookedDate = (String) record.get("date");
                        String bookedSeat = (String) record.get("seat");
                        String bookedName = (String) record.get("name");
                        String bookedNicNum = (String) record.get("nicNumber");
                        String bookedDepartingStation = (String) record.get("departingStation");
                        String bookedArrivingStation = (String) record.get("arrivingStation");
                        String[] bookingDetailsArray = {bookedDate,bookedSeat,bookedName,bookedNicNum,bookedDepartingStation,bookedArrivingStation};
                        passengerList.add(bookingDetailsArray);

                        passengerListView.getItems().add(bookedName + "  " + bookedNicNum + "  " + bookedSeat);
                       /* System.out.println(passengerList.get(i)[0]);
                        System.out.println(passengerList.get(i)[1]);
                        System.out.println(passengerList.get(i)[2]);
                        System.out.println(passengerList.get(i)[3]);
                        System.out.println(passengerList.get(i)[4]);
                        System.out.println(passengerList.get(i)[5]);*/
                        i++;
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
                        passengerListView.getItems().remove(recordIndex);
                        waitingRoom.add(passenger);
                        seatNumberList.add(Integer.parseInt(passenger.getSeatNumber()));
                        int buttonIndex = Integer.parseInt(passenger.getSeatNumber()) - 1;
                        if (passengerCounter % 10 == 0){
                            gapBetweenWindowAndButtonX+=45;
                            gapBetweenWindowAndButtonY =260;
                        }
                        buttonsArray[buttonIndex].setLayoutX(gapBetweenWindowAndButtonX);
                        buttonsArray[buttonIndex].setLayoutY(gapBetweenWindowAndButtonY);
                        buttonsArray[buttonIndex].setVisible(true);
                        gapBetweenWindowAndButtonY+=40;
                        passengerCounter++;
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
                    /*for(Passenger passenger: waitingRoom){
                        System.out.println(passenger.getName() + " " + passenger.getSeatNumber());
                    }*/
                    int randomNumOfPassengers = (int)(Math.random() * 6 +1);
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
                                waitingRoom.remove(0);
                                buttonsArray[Integer.parseInt(currentPassenger.getSeatNumber())-1].setVisible(false);
                                queueListView.getItems().add(currentPassenger.getName() + "  " +
                                        currentPassenger.getNicNumber()+ "  " + currentPassenger.getSeatNumber());
                            }else {
                                System.out.println("Queue is at max capacity");
                            }
                            System.out.println( "after " + waitingRoom);
                            randomNumOfPassengers-=1;
                        }
                    }else {
                        System.out.println("random number is too high try again");
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
                    for (Passenger passenger:trainQueue.getQueueArray()){
                        if (passenger != null){
                            try {
                                Passenger boardedPassenger = trainQueue.remove();
                                queueListView.getItems().remove(0);
                                //System.out.println(PassengerQueue.queueArray.);
                                System.out.println(boardedPassenger.getName() + " in seat number "+ boardedPassenger.getSeatNumber() + "has boarded the train" );
                            } catch (Exception e) {
                                System.out.println("Queue is empty");
                                return;
                            }
                        }
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

        ListView<String> passengerListView = new ListView<>();
        passengerListView.setMaxHeight(250);
        passengerListView.setLayoutX(50);
        passengerListView.setLayoutY(80);

        for (Passenger passenger: waitingRoom){
            String passengerName =passenger.getName();
            String passengerNic =passenger.getNicNumber();
            passengerListView.getItems().add(passengerName + "  " + passengerNic  + " " + passenger.getSeatNumber());
        }

        ListView<String> queueListView = new ListView<>();
        queueListView.setMaxHeight(250);
        queueListView.setLayoutX(450);
        queueListView.setLayoutY(80);

        Button close = new Button("Close");
        close.setLayoutX(650);
        close.setLayoutY(700);

        Passenger[] passengersInQueue = trainQueue.getQueueArray();
        for (Passenger passenger : passengersInQueue){
            if (passenger != null){
                String passengerName =passenger.getName();
                String passengerNic =passenger.getNicNumber();
                queueListView.getItems().add(passengerName + "  " + passengerNic + " " + passenger.getSeatNumber());
            }
        }

        ListView<String> boardedListView = new ListView<>();
        boardedListView.setMaxHeight(250);
        boardedListView.setLayoutX(250);
        boardedListView.setLayoutY(400);

        for (Passenger passenger: boardedPassengers){
            String passengerName = passenger.getName();
            String passengerNic = passenger.getNicNumber();
            String passengerSeat = passenger.getSeatNumber();
            passengerListView.getItems().add(passengerName + "      " + passengerNic  + "      " + passengerSeat);
        }


        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.close();
                selector(window);
            }
        });

        root.getChildren().addAll(passengerListView,queueListView,close,boardedListView);
        window.show();
    }

    private void deleteFromQueue(){

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

        if (waitingRoom.size() == 0){
            System.out.println("There is no data to start the simulation");
            System.out.println("Add using option A from the menu");
            selector(window);
        }else{
            ListView<String> waitingRoomListView = new ListView<>();
            waitingRoomListView.setMaxHeight(250);
            waitingRoomListView.setLayoutX(50);
            waitingRoomListView.setLayoutY(80);

            Button startSimulation = new Button("Start Simulation");
            startSimulation.setLayoutX(90);
            startSimulation.setLayoutY(350);

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
                    for (Passenger passenger: waitingRoom){
                        int randomNumOfPassengers = (int)(Math.random() * 6 +1);
                        int numOfPassengersInWaitingRoom = waitingRoom.size();
                        System.out.println("number of passenger " + randomNumOfPassengers);
                        System.out.println("waiting room size " + numOfPassengersInWaitingRoom);
                        if (randomNumOfPassengers <= numOfPassengersInWaitingRoom){
                            while (randomNumOfPassengers != 0) {
                                int processingDelay = delayGenerator();
                                System.out.println("before " + waitingRoom);
                                if (!trainQueue.isFull()) {
                                    passenger.setSecondsInQueue(processingDelay);
                                    trainQueue.add(passenger);
                                    waitingRoom.remove(0);
                                } else {
                                    System.out.println("Queue is at max capacity");
                                }
                                System.out.println("after " + waitingRoom);
                                randomNumOfPassengers -= 1;
                            }
                        }else {
                            System.out.println("random number is too high try again");
                        }

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

            root.getChildren().addAll(waitingRoomListView,startSimulation,close);
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
        System.out.println(userInput);
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
                    System.out.println("d not yet implemented");
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
