import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;

public class SimpleGet {
    // HBase arbeitet nur mit Bytes, deshalb hier zu Bytes konvertieren
    private static byte[] CONTENT_CF = Bytes.toBytes("content");
    private static byte[] FUSSBALL_CF = Bytes.toBytes("Fussball");

    private static byte[] CITY_C = Bytes.toBytes("city");
    private static byte[] STATE_C = Bytes.toBytes("state");
    private static byte[] VEREIN_C = Bytes.toBytes("verein");

    public List<String> getInfo(String plz)  throws Exception{
        List<String> resultArr = new ArrayList<>();
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf("cities"));
            Get get = new Get(Bytes.toBytes(plz));
            get.addColumn(CONTENT_CF, CITY_C);
            get.addColumn(CONTENT_CF, STATE_C);
            get.addColumn(FUSSBALL_CF, VEREIN_C);

            Result result = table.get(get);
            byte[] cityValue = result.getValue(CONTENT_CF, CITY_C);
            byte[] stateValue = result.getValue(CONTENT_CF, STATE_C);
            byte[] vereinValue = result.getValue(FUSSBALL_CF, VEREIN_C);

            resultArr.add(Bytes.toString(cityValue));
            resultArr.add(Bytes.toString(stateValue));
            // resultArr.add(Bytes.toString(vereinValue));
            //System.out.println(Bytes.toString(cityValue));
            //System.out.println(Bytes.toString(stateValue));
        } finally {
            connection.close();
            if (table != null) {
                table.close();
            }
        }
        return resultArr;
    }
}
