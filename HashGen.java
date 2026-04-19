import org.mindrot.jbcrypt.BCrypt;

public class HashGen {
    public static void main(String[] args) {
        String pw = "admin123";
        String hashed = BCrypt.hashpw(pw, BCrypt.gensalt());
        System.out.println("NEW HASH FOR admin123: " + hashed);
        
        String oldHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhVu";
        try {
            System.out.println("Old hash matches? " + BCrypt.checkpw(pw, oldHash));
        } catch(Exception e) {
            System.out.println("Old hash error: " + e.getMessage());
        }
    }
}
