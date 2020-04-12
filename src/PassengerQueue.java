public class PassengerQueue {
    private Passenger[] queueArray = new Passenger[21];
    private int first;
    private int last;
    private int sizeOfQueue;
    private int maxStayInQueue;
    private int maxLength;

    public Passenger[] getQueueArray() {
        return queueArray;
    }

    public void add(Passenger next){
        if (isFull()){
            return;
        }else {
            this.queueArray[last] = next;
            last = (last + 1) % queueArray.length;
            sizeOfQueue = sizeOfQueue + 1;
        }
    }

    public Passenger remove() throws Exception {
        if (isEmpty()){
            throw new Exception();
        }else {
            Passenger passenger = this.queueArray[first];
            first = (first + 1) % queueArray.length;
            sizeOfQueue = sizeOfQueue - 1;
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
