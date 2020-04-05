public class PassengerQueue {
    private Passenger[] queueArray;
    private int first;
    private int last;
    private int maxStayInQueue;
    private int maxLength;

    private void add(Passenger next){

    }

    private Passenger remove(){
        //return the removing Passenger object
        Passenger object = queueArray[0];//its not zero always

        return object;
    }

    public boolean isEmpty(){
        //there will be more code here
        return false;
    }

    public boolean isFull(){
        //there will be more code here
        return false;
    }

    public void display(){
        //there will be more code here
    }

    public int getLength(){
        //not sure whether there will be more code
        return this.maxLength;
    }

    public int getMaxStay(){
        //not sure whether there will be more code
        return this.maxStayInQueue;
    }

}
