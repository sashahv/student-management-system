package pl.dfjp.students.entity.student;

public enum Gender {
    MALE("Mężczyzna"),
    FEMALE("Kobieta");

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
