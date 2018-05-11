import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class AddingValues {
    private static byte[] FUSSBALL_CF = Bytes.toBytes("Fussball");
    private static byte[] VEREIN_C = Bytes.toBytes("verein");

    public void addValues() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = null;
        ResultScanner scanResult = null;
        try {
            table = connection.getTable(TableName.valueOf("cities"));
            Scan scan = new Scan();
            scanResult = table.getScanner(scan);
            for (Result res : scanResult) {
                for (Cell cell : res.listCells()) {
                    String row = new String(CellUtil.cloneRow(cell));
                    String  value = new String(CellUtil.cloneValue(cell));
                    if(value.equals("HAMBURG") || value.equals("BREMEN")) {
                        Put put = new Put(Bytes.toBytes(row));
                        put.addColumn(FUSSBALL_CF, VEREIN_C, Bytes.toBytes("ja"));
                        table.put(put);
                    }
                }
            }
        } finally {
            connection.close();
            if(table != null) {
                table.close();
            }
            if(scanResult != null) {
                scanResult.close();
            }
        }
    }
}
