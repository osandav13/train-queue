public class Passenger {
    private String firstName;
    private String surName;
    private int secondsInQueue;

    public void setName(String name,String surName){
        this.firstName = name;
        this.surName = surName;
    }

    public String getName(){
        return firstName + " " + surName;
    }

    public void setSecondsInQueue(int seconds){
        this.secondsInQueue = seconds;
    }

    public int getSecondsInQueue(){
        return secondsInQueue;
    }


}
