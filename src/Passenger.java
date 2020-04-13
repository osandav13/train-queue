public class Passenger {
    private String firstName;
    //private String surName;
    private String seatNumber;
    private String nicNumber;
    private int secondsInQueue;
    private int processingDelay;

    public int getProcessingDelay() {
        return processingDelay;
    }

    public void setProcessingDelay(int processingDelay) {
        this.processingDelay = processingDelay;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public void setName(String name){
        this.firstName = name;
        //this.surName = surName;
    }

    public String getName(){
        return firstName ;
    }

    public void setSecondsInQueue(int seconds){
        this.secondsInQueue = seconds;
    }

    public int getSecondsInQueue(){
        return secondsInQueue;
    }


}
