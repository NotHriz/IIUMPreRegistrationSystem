import utils.DBConnection;

public class Main {
    public static void main(String[] args) {
        try {
            DBConnection.getConnection();
            System.out.println("Connected using db.properties!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
