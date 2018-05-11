import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

public class AddingCF {
    public void addCF() throws Exception{
        //Verbindung
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        try {
            // Admin für DDL Befehle
            Admin admin = connection.getAdmin();
            // Neue CF erstellen
            HColumnDescriptor columnDescriptor = new HColumnDescriptor("Fussball");
            // Neue CF an Table übergeben
            admin.addColumn(TableName.valueOf("cities"), columnDescriptor);
            System.out.println("Column Family hinzugefügt");
        } finally {
            connection.close();
        }
    }
}
