public class Passenger {
    private String firstName;
    //private String surName;
    private String seatNumber;
    private String nicNumber;
    private int secondsInQueue;

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
