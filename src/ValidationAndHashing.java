import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ValidationAndHashing {
    private PeopleDB list = new PeopleDB();
    private CourseLessonDB db = new CourseLessonDB();
    public boolean gmailValidation(String gmail){
            if(gmail == null || gmail.trim().isEmpty())
                return false;
        if(!Character.isLetterOrDigit(gmail.charAt(0)))
                return false;

        int i = 1;
        for(;i<gmail.length();i++){
            if(Character.isLetterOrDigit(gmail.charAt(i)))
                continue;
            else if(isAllowed(gmail.charAt(i))&&!isAllowed(gmail.charAt(i-1)))
                continue;
                else if(gmail.charAt(i) == '@')
                    break;
                else return false;
        }
        i++;
        try{
            if(!Character.isLetterOrDigit(gmail.charAt(i)))
                return false;

        }catch (Exception e){
            return false;
        }
        i++;
        for(;i<gmail.length();i++){
            if(Character.isLetterOrDigit(gmail.charAt(i)))
                continue;
            else if((gmail.charAt(i) == '.' && ! isAllowed(gmail.charAt(i-1))))
                continue;
                else if((gmail.charAt(i) == '-' && ! isAllowed(gmail.charAt(i-1))))
                    continue;
            else return false;
        }
        if(isAllowed(gmail.charAt(i-1)))
            return false;
        return true;

    }
    public boolean isAllowed(char s){
        return s == '.' || s == '_' || s == '-'|| s == '+';
    }
    public String gmailExist(String gmail){
       ArrayList<Student> students = list.getStudents();
       ArrayList<Instructor> instructors = list.getInstructors();
        for(int i = 0;i<students.size();i++){
            if(gmail.equalsIgnoreCase(students.get(i).getEmail()))
                return students.get(i).getId();
        }
        for(int i = 0;i<instructors.size();i++){
            if(gmail.equalsIgnoreCase(instructors.get(i).getEmail()))
                return instructors.get(i).getId();
        }
        return null;
    }
    public boolean validID(String id, String type) {
        if (id == null || id.trim().isEmpty())
            return false;

        char first = id.charAt(0);

        if (type.equalsIgnoreCase("Lesson")) {
            if (first != 'L' && first != 'l')
                return false;
        }
        else if (type.equalsIgnoreCase("Course")) {
            if (first != 'C' && first != 'c')
                return false;
        }
        else if (type.equalsIgnoreCase("Student")) {
            if (first != 'S' && first != 's')
                return false;
        }
        else if (type.equalsIgnoreCase("Instructor")) {
            if (first != 'I' && first != 'i')
                return false;
        }

        for (int i = 1; i < id.length(); i++) {
            if (!Character.isDigit(id.charAt(i)))
                return false;
        }

        return true;
    }

    public boolean idExist(String id){
        ArrayList<Student> students = list.getStudents();
        ArrayList<Instructor> instructors = list.getInstructors();
        for(int i = 0;i<students.size();i++){
            if(students.get(i).getId().equalsIgnoreCase(id))
                return true;
        }
        for(int i = 0;i<instructors.size();i++){
            if(instructors.get(i).getId().equalsIgnoreCase(id))
                return true;
        }
        return false;
    }
    public boolean nameValidation(String name){
        if(name == null||name.trim().isEmpty())
            return false;
        for(int i = 0;i<name.length();i++)
            if(!Character.isLetter(name.charAt(i)))
                return false;
        return true;
    }
    public String passwordHashing(String pass) throws NoSuchAlgorithmException {
        pass = pass.trim();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] md1 = md.digest(pass.getBytes());
        BigInteger big = new BigInteger(1,md1);
        return big.toString(16);
    }
    public String passwordCheck(String password,String id) throws NoSuchAlgorithmException {
        ArrayList<Student> students = list.getStudents();
        ArrayList<Instructor> instructors = list.getInstructors();
        password = passwordHashing(password);
        int i;
        if(id.toUpperCase().charAt(0)=='S'){
        for(i = 0;i<students.size();i++)
            if(students.get(i).getId().equalsIgnoreCase(id))
                break;
        if(!students.get(i).getHashPassword().equals(password))
            return null;
        return students.get(i).name;
        }
        else{
            for(i = 0;i<instructors.size();i++)
                if(instructors.get(i).getId().equalsIgnoreCase(id))
                    break;
            if(!instructors.get(i).getHashPassword().equals(password))
                return null;
            return instructors.get(i).name;
        }
    }
    public boolean courseIdExist(String id) {
        ArrayList<Course> courses = db.getCourses();

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equalsIgnoreCase(id))
                return true;
        }
        return false;
    }
    public boolean courseNameExist(String name) {
        ArrayList<Course> courses = db.getCourses();

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }
    public boolean lessonIdExist(String id) {
        ArrayList<Lesson> lessons = db.getLessons();
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getId().equalsIgnoreCase(id))
                return true;
        }
        return false;
    }

}
