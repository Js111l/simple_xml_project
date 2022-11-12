import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class Input {
    private static String input = "";
    private static boolean includeEur = false;
    private static boolean includePln = false;

    public static boolean euroIncluded() {
        return includeEur;
    }

    public static boolean zlotyIncluded() {

        return includePln;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File dtdFile = new File("validate.dtd");
        dtdFile.createNewFile();
        FileWriter writerDtd = new FileWriter(dtdFile, true);
        String defaultDtdContent = "<!ELEMENT cryptoStatistics (nrOfCurrencies,currency+)>\n" +
                "        <!ATTLIST cryptoStatistics sourceApi CDATA #REQUIRED>\n" +
                "        <!ELEMENT nrOfCurrencies (#PCDATA)>\n" +
                "        <!ELEMENT currency (symbol,priceUSD,dateAdded,supply)>\n" +
                "        <!ATTLIST currency name CDATA #REQUIRED>\n" +
                "        <!ELEMENT symbol (#PCDATA)>\n" +
                "        <!ELEMENT priceUSD (#PCDATA)>\n" +
                "        <!ELEMENT dateAdded (#PCDATA)>\n" +
                "        <!ELEMENT supply (max_supply,circulating_supply,total_supply)>\n" +
                "        <!ELEMENT max_supply (#PCDATA)>\n" +
                "        <!ELEMENT circulating_supply (#PCDATA)>\n" +
                "        <!ELEMENT total_supply (#PCDATA)>";

        writerDtd.append(defaultDtdContent);
        writerDtd.close();


        File xsdFile = new File("validate_xsd.xsd");
        xsdFile.createNewFile();
        FileWriter writerXsd = new FileWriter(xsdFile, true);
        String defaultXsdContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "    <xs:element name=\"cryptoStatistics\">\n" +
                "        <xs:complexType>\n" +
                "            <xs:sequence>\n" +
                "                <xs:element name=\"nrOfCurrencies\" type=\"xs:unsignedByte\" />\n" +
                "                <xs:element maxOccurs=\"unbounded\" name=\"currency\">\n" +
                "                    <xs:complexType>\n" +
                "                        <xs:sequence>\n" +
                "                            <xs:element name=\"symbol\" type=\"xs:string\" />\n" +
                "                            <xs:element name=\"priceUSD\" type=\"xs:string\" />\n" +
                "                            <xs:element name=\"dateAdded\" type=\"xs:string\" />\n" +
                "                            <xs:element name=\"supply\">\n" +
                "                                <xs:complexType>\n" +
                "                                    <xs:sequence>\n" +
                "                                        <xs:element name=\"max_supply\" type=\"xs:string\" />\n" +
                "                                        <xs:element name=\"circulating_supply\" type=\"xs:decimal\" />\n" +
                "                                        <xs:element name=\"total_supply\" type=\"xs:decimal\" />\n" +
                "                                    </xs:sequence>\n" +
                "                                </xs:complexType>\n" +
                "                            </xs:element>\n" +
                "                        </xs:sequence>\n" +
                "                        <xs:attribute name=\"name\" type=\"xs:string\" use=\"required\" />\n" +
                "                    </xs:complexType>\n" +
                "                </xs:element>\n" +
                "            </xs:sequence>\n" +
                "            <xs:attribute name=\"sourceApi\" type=\"xs:string\" use=\"required\" />\n" +
                "        </xs:complexType>\n" +
                "    </xs:element>\n" +
                "</xs:schema>";
        writerXsd.append(defaultXsdContent);
        writerXsd.close();

        while (true) {
            System.out.println(" * type:  \n" +
                    "   *  \"generate\" to generate new xml file with top 100 crypto prices *\n" +
                    "   *  \"open file_name\" to open specific file *\n" +
                    "   *  \"show files\" to list current files *\n" +
                    "   *  \"validate dtd/xsd\" to validate xml file with choosen method *\n" +
                    "   *  \"quit\" to end program *");

            Scanner scanner = new Scanner(System.in);
            input = scanner.nextLine();

            if (input.equals("generate")) {
                System.out.println("generating...");
                try {
                    Scanner scanner_EUR = new Scanner(System.in);
                    System.out.println("Include EUR price? [Y/N]");
                    String eur = scanner_EUR.nextLine();
                    if (eur.toLowerCase(Locale.ROOT).equals("y")) {
                        includeEur = true;
                    }
                    Scanner scanner_PLN = new Scanner(System.in);
                    System.out.println("Include PLN price? [Y/N]");
                    String pln = scanner_PLN.nextLine();
                    if (pln.toLowerCase(Locale.ROOT).equals("y")) {
                        includePln = true;
                    }
                    Crypto_api.getDataFromApiToPojo();
                    Crypto_api.createXmlFile();
                    Crypto_api.javaToXml();
                    System.out.println("file created! ");
                    System.out.println("created:\n" +
                            "prices.xml");
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Error occured");
                }
            }

            if (input.startsWith("open")) {
                String fileName = input.substring(4).trim();
                File xmlFile = new File(fileName);
                if (xmlFile.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            }

            if (input.equals("show files")) {
                DirectoryStream<Path> pathDirectoryStream =
                        Files.newDirectoryStream(Path.of("C:\\simple_XML_project"), x -> x.getFileName()
                                .toString().contains(".") && !x.getFileName().toString().startsWith("."));
                Iterator<Path> iterator = pathDirectoryStream.iterator();
                while (iterator.hasNext()) {
                    Path path = iterator.next();
                    System.out.println(path.getFileName().toString());
                }
            }

            if (input.startsWith("validate")) {
                String fileName = input.substring(8).trim();
                switch (fileName) {
                    case "dtd":
                        Crypto_api.validateXmlWithDTD();
                        break;
                    case "xsd":
                        Crypto_api.validateAgainstXsd();
                        break;
                    default:
                        System.out.println("incorrect file name!");
                        break;
                }
            }
            if (input.equals("quit")) {
                File dtdToDelete = new File("validate.dtd");
                dtdToDelete.delete();
                File xsdToDelete = new File("validate_xsd.xsd");
                xsdToDelete.delete();
                File xmlToDelete = new File("prices.xml");
                xmlToDelete.delete();
                break;
            }
        }
    }


    static void methodInvoker() {
        System.out.println(" * type:  \n" +
                "   *  \"generate\" to generate new xml file with top 100 crypto prices *\n" +
                "   *  \"open file_name\" to open specific file *\n" +
                "   *  \"show files\" to list current files *\n" +
                "   *  \"validate dtd/xsd\" to validate xml file with choosen method *\n" +
                "   *  \"quit\" to end program *");

        Scanner scanner = new Scanner(System.in);
        input = scanner.nextLine();
    }
}


//Simple console app that takes data about cryptocurrencies from Api, saves specific data to Java classes and then creates
//xml file using JAXB library, which enables mapping between xml files nad Java objects.
// After the xml file is created, you can validate xml with dtd file and xsd schema file.
class Connection {
    private Connection() {

    }

    public final HttpResponse<String> response = connection();

    private static HttpResponse<String> connection() { //method responsible for setting up connection
        // if connection is successful it returns the response
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri //sending http request with appriopiate informations in header
                    (URI.create("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"))
                    .header("X-CMC_PRO_API_KEY", "4e17aa40-259d-4214-89a9-b8cd8dad7198").GET().build();
            response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private static Connection connection;

    public static Connection GetInstance() {
        if (connection == null) {
            connection = new Connection();
        }
        return connection;
    }
}


public final class Crypto_api {
    private static String response = Connection.GetInstance().response.body();
    private static Set<CryptoCurrency> cryptoCurrencies;

    protected static void getDataFromApiToPojo () throws IOException {
        //retrieves data from api and saves to Java objects
        JSONObject object = new JSONObject(response); //gets data from connection
        JSONArray array = new JSONArray(object.get("data").toString());
        Set<CryptoCurrency> cryptoSet = new HashSet<>();

        for (int i = 0; i < array.length(); i++) {
            String name = String.valueOf(((JSONObject) array.get(i)).get("name")); //name of currency
            String symbol = String.valueOf(((JSONObject) array.get(i)).get("symbol")); //symbol used on exchanges
            String date_added = String.valueOf(((JSONObject) array.get(i)).get("date_added")).substring(0, 11); //date added on exchanges

            String price = priceHelperMethod(String.valueOf(((JSONObject) ((JSONObject) (((JSONObject)
                    array.get(i)).get("quote"))).get("USD")).get("price")), "$");//current price in USD
            String circulatingSupply = String.valueOf(((JSONObject) array.get(i)).get("circulating_supply"));
            String total_supply = String.valueOf(((JSONObject) array.get(i)).get("total_supply"));
            String max_supply = String.valueOf(((JSONObject) array.get(i)).get("max_supply"));

            Supply supply = new Supply(max_supply, circulatingSupply, total_supply);

            CryptoCurrency currency;
            if (Input.euroIncluded() && !Input.zlotyIncluded()) {
                currency = new CryptoCurrency.CryptoCurrencyBuilder(name, symbol, price, date_added, supply).
                        setPriceEUR(priceHelperMethod(Convert.fromTo("USD", "EUR", price), "€")).build();
                cryptoSet.add(currency);
            } else if (!Input.euroIncluded() && Input.zlotyIncluded()) {
                currency = new CryptoCurrency.CryptoCurrencyBuilder(name, symbol, price, date_added, supply).
                        setPricePLN(priceHelperMethod(Convert.fromTo("USD", "PLN", price), "zł")).build();
                cryptoSet.add(currency);
            } else if (Input.euroIncluded() && Input.zlotyIncluded()) {
                currency = new CryptoCurrency.CryptoCurrencyBuilder(name, symbol, price, date_added, supply).
                        setPriceEUR(priceHelperMethod(Convert.fromTo("USD", "EUR", price), "€")).
                        setPricePLN(priceHelperMethod(Convert.fromTo("USD", "PLN", price), "zł")).build();
                cryptoSet.add(currency);
            } else
                currency = new CryptoCurrency.CryptoCurrencyBuilder(name, symbol, price, date_added, supply).build();
            cryptoSet.add(currency);
        }
        ProcessFile processFile=ProcessFile.GetInstance();
        if (Input.euroIncluded() && Input.zlotyIncluded()) {
            processFile.setEuroFlag(true);
            processFile.setPlnFlag(true);
            cryptoCurrencies = cryptoSet;
        } else if (Input.euroIncluded() && !Input.zlotyIncluded()) {
            processFile.setEuroFlag(true);
            cryptoCurrencies = cryptoSet;
        } else if (!Input.euroIncluded() && Input.zlotyIncluded()) {
            processFile.setPlnFlag(true);
            cryptoCurrencies = cryptoSet;
        } else
            cryptoCurrencies = cryptoSet;
        processFile.process();
    }

    private static String priceHelperMethod(String value, String currencySymbol) {
        if (value.charAt(0) == '0' && !Input.euroIncluded() && !Input.zlotyIncluded()) {
            return new BigDecimal(value).toPlainString() + " " + currencySymbol;
        } else
            return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + currencySymbol;
    }

    protected static String javaToXml() {
        // method is resposible for mapping Java objects to one Xml file
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CryptoStatistics.class, CryptoCurrency.class);
            Marshaller marshaller = jaxbContext.createMarshaller(); //creating marshaller object
            //marshaller is responsible for mapping Java code back to XML
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(CryptoStatistics.valueOf(String.valueOf(cryptoCurrencies.size()), cryptoCurrencies), writer);
            //xml content is written on StringWriter object
            writer.close();
            return writer.toString();
            //returns string object which contains content ready to save to xml format file

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected static void createXmlFile() {
        try {
            //creates the xml file
            File xmlFile = new File("prices.xml");
            xmlFile.createNewFile();

            String doctype = "<!DOCTYPE cryptoStatistics SYSTEM \"validate.dtd\">" +
                    new StringBuilder(Objects.requireNonNull(javaToXml())).delete(0, 55);
            //in order to validate the xml with dtd file we need to have Doctype declaration with
            //reference to dtd file
            //takes a string from javaToXml method,
            // deletes unnecessry information and adds
            //doctype declaration at the beginning

            FileWriter fileWriter = new FileWriter("prices.xml");
            fileWriter.append(doctype);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void validateXmlWithDTD() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(true); //specifies that the parser will validate xml file
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
                documentBuilder.setErrorHandler(new ErrorHandler() {
                    @Override
                    public void warning(SAXParseException exception) throws SAXException {
                        throw exception;
                    }

                    @Override
                    public void error(SAXParseException exception) throws SAXException {
                        throw exception;
                    }

                    @Override
                    public void fatalError(SAXParseException exception) throws SAXException {
                        throw exception;
                    }
                });
            } catch (ParserConfigurationException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("validating....");
            assert documentBuilder != null;
            documentBuilder.parse(new FileInputStream("prices.xml"));
            //validating xml file as the file is parsed

            System.out.println("Validating with DTD file...");
            System.out.println("XML document is valid!!");  //prints success message if file is correct

        } catch (IOException | SAXException e) {  //if any exception is thrown -> it means that xml file is incorrect
            System.out.println(e.getMessage());
            System.out.println("XML document is invalid :(");
        }

    }

    public static void validateAgainstXsd() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        //Factory for creating schema objects
        try {
            Schema schema = schemaFactory.newSchema(new File("validate_xsd.xsd"));
            //set of constrainst to check against xml file, schema is created from our xsd file
            Validator validator = schema.newValidator();
            //as documentation states "A processor that checks an XML document against Schema."
            validator.validate(new StreamSource("" +
                    "prices.xml"));
            //validating xml

            System.out.println("validating against xsd...");
            System.out.println("XML document is valid!!"); //prints success message if file is correct

        } catch (IOException | SAXException e) {  //if any exception is thrown -> it means that xml file is incorrect
            System.out.println(e.getMessage());
            System.out.println("XML document is invalid :(");
        }
    }
}





