import java.util.ArrayList;
import java.util.List;

public class PassengerQueue {
    private Passenger[] queueArray = new Passenger[21];
    private int first;
    private int last;
    private int sizeOfQueue;
    private int maxStayInQueue;
    private int minStayInQueue = 1000000000;
    private int boardedNumOfPassengers;
    private int totalStayInQueue;
    //private float averageStayInQueue;
    private int maxLength;

    public Passenger[] getQueueArray() {
        return queueArray;
    }

    public void add(Passenger next){
        if (isFull()){
            return;
        }else {
            this.queueArray[last] = next;
            System.out.println("first " + first);
            System.out.println("last " + last);
            last = (last + 1) % queueArray.length;
            sizeOfQueue = sizeOfQueue + 1;
            totalStayInQueue += next.getSecondsInQueue();
            if(maxLength < sizeOfQueue){
                maxLength = sizeOfQueue;
            }
        }
    }

    public Passenger remove() throws Exception {
        if (isEmpty()){
            throw new Exception();
        }else {
            Passenger passenger = this.queueArray[first];
            if (passenger.getSecondsInQueue() > maxStayInQueue){
                maxStayInQueue = passenger.getSecondsInQueue();
            }if(passenger.getSecondsInQueue() < minStayInQueue){
                minStayInQueue = passenger.getSecondsInQueue();
            }
            first = (first + 1) % queueArray.length;
            sizeOfQueue = sizeOfQueue - 1;
            boardedNumOfPassengers +=1;
            System.out.println("first is: " + first);
            System.out.println("last is: " + last);
            return passenger;
        }
    }

    public boolean isEmpty(){
        if(sizeOfQueue == 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean isFull(){
        if (sizeOfQueue == queueArray.length){
            return true;
        }else {
            return false;
        }
    }

    public List<Passenger> display(){
        List<Passenger> list = new ArrayList<>();
        for (int i=0;i < sizeOfQueue;i++){
            list.add(queueArray[(first + i )% queueArray.length]);
        }
        return list;
    }

    public int getLength(){
        return this.maxLength;
    }

    public int getMinStay() {
        return minStayInQueue;
    }

    public int getMaxStay(){
        return maxStayInQueue;
    }

    public float getAverageStay() {
        return (float) totalStayInQueue/boardedNumOfPassengers;
    }

}
