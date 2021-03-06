package pt.isel.ls.model.entities;

public class User implements Entity {
    private int uid;
    private String name;
    private String email;

    public User(int uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public User(int uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public User(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
    }
}
