
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class TrainStation extends Application{
    private final int NUM_OF_PASSENGERS = 42;
    private Passenger[] waitingRoom = new Passenger[NUM_OF_PASSENGERS];
    private Button[] buttonsArray = new Button[NUM_OF_PASSENGERS];
    private int passengerCounter;
    private int gapBetweenWindowAndButtonX = 20;
    private int gapBetweenWindowAndButtonY = 180;
    private PassengerQueue trainQueue = new PassengerQueue(21);
    private List<String[]> passengerList = new ArrayList<>();
    private List<Integer> seatNumberList = new ArrayList<>();

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
            passengerListView.setMaxHeight(150);
            passengerListView.setLayoutX(450);
            passengerListView.setLayoutY(80);

            Button addToWaitingRoom = new Button("Add to waiting room");
            addToWaitingRoom.setLayoutX(500);
            addToWaitingRoom.setLayoutY(240);

            Button addToQueue = new Button("Add to queue");
            addToQueue.setLayoutX(200);
            addToQueue.setLayoutY(700);

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
                        waitingRoom[passengerCounter] = passenger;
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
            /*
            addToQueue.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int numOfPassengers = (int)(Math.random() * 6 +1);
                    for (int i =0; i < numOfPassengers;i++){
                        int smallestNumber = Collections.min(seatNumberList);
                        System.out.println("smallest number is " +smallestNumber);
                        System.out.println("number of passenger " + numOfPassengers);
                        for (int j=0;j < waitingRoom.length;j++){
                           if (waitingRoom[j] == null){
                                System.out.println("i am null");
                                continue;
                            }else {
                            System.out.println(waitingRoom[j].getSeatNumber());
                                int seatNumber = Integer.parseInt(waitingRoom[j].getSeatNumber());
                                if(seatNumber == smallestNumber) {
                                    trainQueue.add(waitingRoom[j]);
                                    buttonsArray[seatNumber].setVisible(false);
                                    // System.out.println(i);
                                    seatNumberList.remove(smallestNumber);
                                    //waitingRoom[j] = null;
                                }
                            }
                        }
                    }
                }
            });*/
            datePicker.setOnAction(loadPassengers);
            arrivingStation.setOnAction(loadPassengers);
            leavingStation.setOnAction(loadPassengers);

            root.getChildren().addAll(datePicker,leavingStation,arrivingStation,addToQueue);
            root.getChildren().addAll(datePickerLabel,comboboxLable1,comboboxLable2);
            window.show();

            //selector(window);
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
                    System.out.println("v not yet implemented");
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
                    System.out.println("r not yet implemented");
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
