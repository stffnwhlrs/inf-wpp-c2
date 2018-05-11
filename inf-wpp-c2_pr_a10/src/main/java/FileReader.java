import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class FileReader{

    public FileReader() {
    }

    public ArrayList<JSONObject> read(String fileName) {
        ArrayList<JSONObject> result = new ArrayList<>();
        String line = null;

        try {
            java.io.FileReader fileReader = new java.io.FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                result.add(obj);
            }

            bufferedReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Datei nicht gefunden: " + fileName + "'");
        } catch(IOException ex) {
            System.out.println("Datei konnte nicht gelesen werden: " + fileName + "'");
        }
        return result;
    }
//    public static void main(String[] args) {
//        String fileName = "plz.data.txt";
//        String line = null;
//
//
//        try {
//            java.io.FileReader fileReader = new java.io.FileReader(fileName);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            while ((line = bufferedReader.readLine()) != null) {
//                JSONObject obj = new JSONObject(line);
//                System.out.println(obj.get("city"));
//                // System.out.println(obj);
//            }
//
//            bufferedReader.close();
//
//        } catch (FileNotFoundException ex) {
//            System.out.println("Unable to open file '" + fileName + "'");
//        } catch(IOException ex) {
//            System.out.println("Error reading file '" + fileName + "'");
//            // Or we could just do this:
//            // ex.printStackTrace();
//        }
//    }
}
