## Aufgabe 10:
**HBase Docker Container starten**
```shell
docker run --rm -v ~/git/inf-wpp-c2/hbase/volumes:/data --name hbase -p 9090:9090 -p 2181:2181 -p 60000:60000 -p 60010:60010 -p 60020:60020 -p 60030:60030 -h $(hostname) -it nerdammer/hbase
```
`-h` gibt auch gleichzeitig den Server Namen an.  

**Kontrolle ob HBase läuft**  
Im Browser: http://localhost:60010/

**In HBase Shell gelangen**
```shell
docker exec -i hbase hbase shell
```

**Table erstellen**
```shell
create 'emp', 'personal data', 'professional data'
```
## Aufgabe 10a: Daten importieren
**Tabelle erstellen**
```Java
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
```
**Überprüfen**
```shell
describe 'cities'
```
**Daten importieren**
```Java
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
```
**Überprüfen**
```shell
count 'cities'
```

## Aufgabe 10b: Column Family hinzufügen und Spalteneintrag ergänzen
**Column Family hinzufügen**
```Java
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
```
**Überprüfen**
```shell
describe 'cities'
```
**Spalten hinzufügen**
```Java
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
```
## Aufgabe 10c: Für PLZ City und State ausgeben
```Java
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
```
## Aufgabe 10d: Für Stadt die PLZ ausgeben
```Java
public class FilterOnColumnValues {
    private void printResults(ResultScanner scanResult) {
        System.out.println();
        System.out.println("Result: ");

        for (Result res : scanResult) {
            for (Cell cell : res.listCells()) {
                String row = new String(CellUtil.cloneRow(cell));
                String family = new String(CellUtil.cloneFamily(cell));
                String column = new String(CellUtil.cloneQualifier(cell));
                String value = new String(CellUtil.cloneValue(cell));

                System.out.println(row + " " + family + " " + column + " " + value);
            }
            System.out.println("---");
        }
    }

    public void getUseCity(String city) throws Exception{
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);

        Table table = null;
        ResultScanner scanResult = null;
        try {
            table = connection.getTable(TableName.valueOf("cities"));

            SingleColumnValueFilter filter = new SingleColumnValueFilter(
                    Bytes.toBytes("content"),
                    Bytes.toBytes("city"),
                    CompareFilter.CompareOp.EQUAL,
                    new BinaryComparator(Bytes.toBytes(city)));
            // falls "city nicht vorhanden ist, exclude
            filter.setFilterIfMissing(true);

            Scan userScan = new Scan();
            userScan.setFilter(filter);

            scanResult = table.getScanner(userScan);

            printResults(scanResult);
        } finally {
            connection.close();
            if (table != null) {
                table.close();
            }
            if (scanResult != null) {
                scanResult.close();
            }
        }
    }
}
```
## Aufgabe 10e: Vergleich Der Datenbanken
/ | Redis | MongoDB | HBase
--- | --- | --- | ---
**Query mit Key**  | 0,1 ms  | 1,1 ms  | 72 ms
**Query mit Value**  | 5.213,9 ms  | 0,05 ms  |  237 ms

**LoC:** Redis und Mongo in Python sehr ähnlich und mit einigen Zeilen Code leicht zu realisieren. HBase wurde mit Java realisiert und deshalb nicht vergleichbar. Mit dem Framework Happybase scheint es aber ein ähnlicher Aufwand zu sein.

**Arbeitszeit:** Ähnlicher Zeitaufwand bei Redis und MongoDB. Etwas kürzer bei MongoDB durch gute Dokumentation und viele Beispiele. HBase hat sehr viel Zeit durch nicht vorhanden offiziellesm Docker Image eingenommen. Eigentliches Erstellen des Codes ging dann aber ähnlich schnell wie bei den anderen Datenbanken.

## Aufgabe11: Hadoop & Map-Reduce
**Hadoop Docker Container erstellen**
```shell
docker run -t -i --rm -v ~/Desktop/app:/app sequenceiq/hadoop-docker /etc/bootstrap.sh -bash
```
**Variablen erstellen**
```shell
hdfs=/usr/local/hadoop/bin/hdfs
hadoop=/usr/local/hadoop/bin/hadoop
```
**Testen ob run Befehl erfolgreich war**
```shell
$hdfs dfs -ls /
```
**In Directory wechseln**
```shell
cd /app/AnalysisProject/out/artifacts/AnalysisProject_jar
```
**Weiteres**
```shell
$hdfs dfs -mkdir -p /input/wc
$hdfs dfs -mkdir -p /output/wc
$hdfs dfs -copyFromLocal test /input/wc/test
$hadoop jar AnalysisProject.jar WordCount.Main /input/wc /output/wc/1
$hdfs dfs -cat /output/wc/1/*
```
