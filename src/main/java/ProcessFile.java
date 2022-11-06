import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public final class ProcessFile {
    private ProcessFile() {
    }

    private static boolean euroFlag;
    private static boolean plnFlag;

    public static void setEuroFlag(boolean eur) {
        euroFlag = eur;
    }

    public static void setPlnFlag(boolean pln) {
        plnFlag = pln;
    }

    public static void process(File dtd, File xsd) throws IOException {
        if (euroFlag && plnFlag) {
            List<String> listDtd = new LinkedList<>();
            listDtd = Files.readAllLines(Path.of("validate.dtd"));
            dtd.delete();
            listDtd.remove(3);
            listDtd.add(3, "        <!ELEMENT currency (symbol,priceUSD,priceEUR,pricePLN,dateAdded,supply)>");
            listDtd.add(7, "        <!ELEMENT priceEUR (#PCDATA)>");
            listDtd.add(8, "        <!ELEMENT pricePLN (#PCDATA)>");
            File newDtd = new File("validate.dtd");
            Files.write(Path.of("validate.dtd"), listDtd, StandardCharsets.UTF_8);
            newDtd.createNewFile();

            List<String> listXsd = new LinkedList<>();
            listXsd = Files.readAllLines(Path.of("validate_xsd.xsd"));
            xsd.delete();
            listXsd.add(11, "                            <xs:element name=\"priceEUR\" type=\"xs:string\" />");
            listXsd.add(12, "                            <xs:element name=\"pricePLN\" type=\"xs:string\" />");
            File newXsd = new File("validate_xsd.xsd");
            newXsd.createNewFile();
            Files.write(Path.of("validate_xsd.xsd"), listXsd, StandardCharsets.UTF_8);
        }
        if (euroFlag && !plnFlag) {
            List<String> listDtd = new LinkedList<>();
            listDtd = Files.readAllLines(Path.of("validate.dtd"));
            dtd.delete();
            listDtd.remove(3);
            listDtd.add(3, "        <!ELEMENT currency (symbol,priceUSD,priceEUR,dateAdded,supply)>");
            listDtd.add(7, "        <!ELEMENT priceEUR (#PCDATA)>");
            File newDtd = new File("validate.dtd");
            Files.write(Path.of("validate.dtd"), listDtd, StandardCharsets.UTF_8);
            newDtd.createNewFile();
            List<String> listXsd = new LinkedList<>();
            listXsd = Files.readAllLines(Path.of("validate_xsd.xsd"));
            xsd.delete();
            listXsd.add(11, "                            <xs:element name=\"priceEUR\" type=\"xs:string\" />");
            File newXsd = new File("validate_xsd.xsd");
            newXsd.createNewFile();
            Files.write(Path.of("validate_xsd.xsd"), listXsd, StandardCharsets.UTF_8);
        }
        if (!euroFlag && plnFlag) {
            List<String> listDtd = new LinkedList<>();
            listDtd = Files.readAllLines(Path.of("validate.dtd"));
            dtd.delete();
            listDtd.remove(3);
            listDtd.add(3, "        <!ELEMENT currency (symbol,priceUSD,pricePLN,dateAdded,supply)>");
            listDtd.add(7, "        <!ELEMENT priceEUR (#PCDATA)>");
            File newDtd = new File("validate.dtd");
            newDtd.createNewFile();
            Files.write(Path.of("validate.dtd"), listDtd, StandardCharsets.UTF_8);

            List<String> listXsd = new LinkedList<>();
            listXsd = Files.readAllLines(Path.of("validate_xsd.xsd"));
            xsd.delete();
            listXsd.add(11, "                            <xs:element name=\"pricePLN\" type=\"xs:string\" />");
            File newXsd = new File("validate_xsd.xsd");
            newXsd.createNewFile();
            Files.write(Path.of("validate_xsd.xsd"), listXsd, StandardCharsets.UTF_8);
        }
    }
}
