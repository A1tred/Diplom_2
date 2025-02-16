package models;

public class User {
    private String email;
    private String password;
    private String name;

    public User() {}

    private User(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.name = builder.name;
    }

    public static class Builder {
        private String email;
        private String password;
        private String name;

        public Builder() {
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
