package lld.machinecoding.parkinglot;


interface ParkingStrategy{
    String park(int vehicleType, String vehicleNumber,
                String ticketId);
}


class StratergyOne implements ParkingStrategy{


    @Override
    public  String park(int vehicleType, String vehicleNumber,
                String ticketId){
        return "";
    }
}