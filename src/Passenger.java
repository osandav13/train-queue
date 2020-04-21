public class Passenger {
    private String firstName;
    private String seatNumber;
    private String nicNumber;
    private String date;
    private String departureStation;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public void setName(String name){
        this.firstName = name;
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
