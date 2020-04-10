public class PassengerQueue {
    private Passenger[] queueArray;
    private int first;
    private int last;
    private int maxStayInQueue;
    private int maxLength;

    public PassengerQueue(int maxLength){
        super();
        this.maxLength = maxLength;
        this.queueArray = new Passenger[maxLength];
    }

    public void add(Passenger next){
        if (isFull()){
            System.out.println("Queue is at max capacity");
        }else {
            last = (last + 1) % maxLength;
            this.queueArray[last] = next;
        }
    }

    public Passenger remove() throws Exception {
        if (isEmpty()){
            throw new Exception("Queue is empty");
        }else {
            Passenger passenger = this.queueArray[first];
            first = (first + 1) % maxLength;
            return passenger;
        }
    }

    public boolean isEmpty(){
        if(first == 0 && last == 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean isFull(){
        if (first == ((last + 1) % maxLength)){
            return true;
        }else {
            return false;
        }
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
