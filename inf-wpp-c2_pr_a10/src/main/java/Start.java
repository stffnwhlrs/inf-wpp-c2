import java.util.List;
import java.util.Scanner;

public class Start {
    public static void main(String[] args) throws Exception {
        TableCreater tableCreater = new TableCreater();
        FileInserter fileInserter = new FileInserter();
        AddingCF addingCF = new AddingCF();
        AddingValues addingValues = new AddingValues();
        SimpleGet simpleGet = new SimpleGet();
        FilterOnColumnValues filterOnColumnValues = new FilterOnColumnValues();
        List<String> result;
        while(true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("10a für Tabelle 'cities' erstellen und Daten einfügen");
            System.out.println("10b für Column Family hinzufügen");
            System.out.println("10c für Suche über PLZ");
            System.out.println("10d für City");
            System.out.println("exit für exit");
            System.out.println("Welche Option?");

            String option = scanner.next();

            if (option.equals("10a")) {
                long startTime = System.currentTimeMillis();
                tableCreater.createCities();
                fileInserter.insertData();
                System.out.println("--- 10a abgeschlossen ---");
                System.out.println("Dauer:" + (System.currentTimeMillis() - startTime) + " ms");
            }
            if (option.equals("10b")) {
                long startTime = System.currentTimeMillis();
                addingCF.addCF();
                addingValues.addValues();
                System.out.println("--- 10b abgeschlossen ---");
                System.out.println("Dauer:" + (System.currentTimeMillis() - startTime) + " ms");
            }
            if (option.equals("10c")) {
                System.out.println("Welche PLZ?");
                String plz = scanner.next();
                long startTime = System.currentTimeMillis();
                result = simpleGet.getInfo(plz);
                for (String s : result) {
                    System.out.println(s);
                }
                System.out.println("--- 10c abgeschlossen ---");
                System.out.println("Dauer:" + (System.currentTimeMillis() - startTime) + " ms");
            }
            if (option.equals("10d")) {
                System.out.println("Welche Stadt?");
                String city = scanner.next();
                city = city.toUpperCase();
                long startTime = System.currentTimeMillis();
                filterOnColumnValues.getUseCity(city);
                System.out.println("--- 10d abgeschlossen ---");
                System.out.println("Dauer:" + (System.currentTimeMillis() - startTime) + " ms");
            }
            if(option.equals("exit")) {
                break;
            }
        }
    }
}
