package hello;

public class Greeting {

    private final long id;
    
    private final String intent;
    private final String text;
    private final String full_name;
    private final String first_name;
    private final String last_name;
    private final String application_name;

    public Greeting(long id, String text, String intent, String full_name,String first_name,String last_name,String application_name) {
        this.id = id;
        this.text = text;
        this.intent = intent;
        this.full_name = full_name;
        this.first_name = first_name;
        this.last_name = last_name;
        this.application_name = application_name;
    }

    public long getId() {
        return id;
    }

    public String getIntent() {
        return intent;
    }
    
    public String getText() {
        return text;
    }
    
    public String getFull_name() {
        return full_name;
    }
    
    public String getFirst_name() {
        return first_name;
    }
    
    public String getLast_name() {
        return last_name;
    }
    
    public String getApplication_name() {
        return application_name;
    }
}
