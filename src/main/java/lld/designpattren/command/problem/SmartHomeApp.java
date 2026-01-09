package lld.designpattren.command.problem;

public class SmartHomeApp {
    public static void main(String[] args) {
        Light light = new Light();
        Thermostat thermostat = new Thermostat();
        SmartHomeControllerV1 controller = new SmartHomeControllerV1(light, thermostat);

        controller.turnOnLight();
        controller.setThermostatTemperature(22);
        controller.turnOffLight();
    }
}
