
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

import java.util.List;
import java.util.Scanner;


public class TrainStation extends Application{
    private Passenger[] waitingRoom;
    private PassengerQueue trainQueue;


    private void addingToQueue(Stage window){
        MongoClient myclient = MongoClients.create();
        MongoDatabase myDB = myclient.getDatabase("cwIntegration");
        MongoCollection<Document> myCollection = myDB.getCollection("bookingDetails");

        FindIterable<Document> findIterable = myCollection.find(and(eq("details","14"),eq("details","s")));
        for (Document document: findIterable){
            List<String> list = (List<String>) document.get("details");
            System.out.println(list.get(0));
        }

        selector(window);
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
