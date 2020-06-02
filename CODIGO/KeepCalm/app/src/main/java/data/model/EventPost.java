package data.model;

public class EventPost {
    private String env;
    private String type_events;
    private String state;
    private String description;
    private Event event;
    private String token;

    public EventPost(String token, String env, String type_events, String state, String description) {
        this.token = token;
        this.env = env;
        this.type_events = type_events;
        this.state = state;
        this.description = description;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "EventPost{" +
                "env='" + env + '\'' +
                ", state='" + state + '\'' +
                ", event=" + event +
                '}';
    }
}
