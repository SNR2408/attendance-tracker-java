// Custom exception, thrown when a subject id does not exist.
public class SubjectNotFoundException extends Exception {

    public SubjectNotFoundException(String message) {
        super(message);
    }
}
