import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileInserter {
    // HBase arbeitet nur mit Bytes, deshalb hier zu Bytes konvertieren
    private static byte[] CONTENT_CF = Bytes.toBytes("content");

    private static byte[] CITY_C = Bytes.toBytes("city");
    private static byte[] STATE_C = Bytes.toBytes("state");

    public void insertData() throws IOException {
        // Hilsklasse um Daten in JSON Objekte zu konvertieren
        FileReader fileReader = new FileReader();
        // Die konvertierten Daten
        ArrayList<JSONObject> data = fileReader.read("plz.data.txt");
        // Verbindung
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = null;
        try {
            // Erhalte die Tabelle
            table = connection.getTable(TableName.valueOf("cities"));
            //Die einzufügenden Put Objekte (Batch Import)
            List<Put> putList = new ArrayList<>();
            for (JSONObject o : data) {
                String plz = o.getString("_id");
                String city = o.getString("city");
                String state = o.getString("state");

                System.out.println(plz + " " + city + " " + state);

                // Die wichtigen Daten an ein Put Objekt übergeben
                //Angeben welche Row
                Put put = new Put(Bytes.toBytes(plz));
                //Angeben für welche Column Family und Column
                put.addColumn(CONTENT_CF, CITY_C, Bytes.toBytes(city));
                put.addColumn(CONTENT_CF, STATE_C, Bytes.toBytes(state));
                //In Liste einfügen
                putList.add(put);
            }
            // Alle Objekte an Tabelle übergeben und so importieren
            table.put(putList);
            System.out.println("Inserted rows");
        } finally {
            connection.close();
            if (table != null) {
                table.close();
            }
        }
    }

}
