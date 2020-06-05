package data.model;

public class Event {

    private Integer dni;
    private String type_events;
    private String state;
    private String description;

    public Event(Integer dni, String type_events, String state, String description) {
        this.dni = dni;
        this.type_events = type_events;
        this.state = state;
        this.description = description;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public String getType_events() {
        return type_events;
    }

    public void setType_events(String type_events) {
        this.type_events = type_events;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "dni='" + dni + '\'' +
                ", type_events='" + type_events + '\'' +
                ", state='" + state + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
