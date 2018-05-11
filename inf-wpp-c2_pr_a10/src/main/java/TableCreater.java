import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;


public class TableCreater {
    public void createCities() throws Exception {
        // Verbindung zu HBase aufbauen
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        try {
            Admin admin = connection.getAdmin();
            // Tabelle erstellen
            HTableDescriptor tableName = new HTableDescriptor(TableName.valueOf("cities"));
            // Column Family Name erstellen
            tableName.addFamily(new HColumnDescriptor("content"));
            if (!admin.tableExists(tableName.getTableName())) {
                System.out.println("Tabelle cities wird erstellt");
                admin.createTable(tableName);
                System.out.println("erstellt");
            } else {
                System.out.println("Tabelle cities besteht bereits");
            }
        } finally {
            connection.close();
        }
    }
}
