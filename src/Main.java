public class Main {
    public static void main(String[] args) {
        PeopleDB peopleDB = new PeopleDB();

        if (peopleDB.getAdmins().isEmpty()) {
            Admin defaultAdmin = new Admin("A001", "admin", "admin123");
            peopleDB.addAdmin(defaultAdmin);
        }

        new WelcomeMenu();
    }
}
