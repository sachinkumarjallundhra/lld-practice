package lld.designpattren.command.problem;

class SmartHomeControllerV1 {
    private final Light light;
    private final Thermostat thermostat;

    public SmartHomeControllerV1(Light light, Thermostat thermostat) {
        this.light = light;
        this.thermostat = thermostat;
    }

    public void turnOnLight() {
        light.on();
    }

    public void turnOffLight() {
        light.off();
    }

    public void setThermostatTemperature(int temperature) {
        thermostat.setTemperature(temperature);
    }
}
