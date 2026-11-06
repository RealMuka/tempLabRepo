package lab.library.model;

import lab.library.exception.InvalidDataException;
import lab.library.util.Validators;

import java.util.regex.Pattern;

public class Reader implements Identifiable {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s|]+@[^@\\s|]+\\.[^@\\s|]+$");

    private final int id;
    private String name;
    private String email;

    public Reader(int id, String name, String email) {
        this.id = Validators.requirePositive("id", id);
        setName(name);
        setEmail(email);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validators.requireText("Имя читателя", name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String value = Validators.requireText("Email", email);
        if (!EMAIL.matcher(value).matches()) {
            throw new InvalidDataException("Некорректный email: " + value);
        }
        this.email = value;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s <%s>", id, name, email);
    }
}
