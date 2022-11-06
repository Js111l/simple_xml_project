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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

class main{
    public static void main(String[] args) {
        Crypto_api.getDataFromApiToXml();
        Crypto_api.validateXmlWithDTD();
        Crypto_api.validateAgainstXsd();
    }
}

public final class Crypto_api {
    private static HttpResponse<String> connection() {
        HttpResponse<String> response = null;
        {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri
                                (URI.create("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"))
                        .header("X-CMC_PRO_API_KEY", "4e17aa40-259d-4214-89a9-b8cd8dad7198").GET().build();
                response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Connection succesfull!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }
    private static Set<CryptoCurrency> getDataFromApiToPojo() throws IOException {
        JSONObject object = new JSONObject(connection().body());
        JSONArray array = new JSONArray(object.get("data").toString());
        Set<CryptoCurrency> cryptoSet = new HashSet<>();
        for (int i = 0; i < array.length(); i++) {
            String name = String.valueOf(((JSONObject) array.get(i)).get("name"));
            String symbol = String.valueOf(((JSONObject) array.get(i)).get("symbol"));
            String date_added = String.valueOf(((JSONObject) array.get(i)).get("date_added")).substring(0, 11);
            String price =
                    String.valueOf(((JSONObject) ((JSONObject) (((JSONObject)
                            array.get(i)).get("quote"))).get("USD")).get("price")).substring(0, 6) + " $";
            String circulatingSupply = String.valueOf(((JSONObject) array.get(i)).get("circulating_supply"));
            String total_supply = String.valueOf(((JSONObject) array.get(i)).get("total_supply"));
            String max_supply = String.valueOf(((JSONObject) array.get(i)).get("max_supply"));

            cryptoSet.add(new CryptoCurrency(name, symbol, price, date_added,
                    new Supply(max_supply, circulatingSupply, total_supply)));
        }
        return cryptoSet;
    }

    private static String javaToXml() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CryptoStatistics.class, CryptoCurrency.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Set<CryptoCurrency> set = getDataFromApiToPojo();
            StringWriter writer = new StringWriter();
            marshaller.marshal(CryptoStatistics.valueOf(String.valueOf(set.size()),set), writer);

            return writer.toString();

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createXmlFile()  {
        try {
            File xmlFile = new File("prices.xml");
            xmlFile.createNewFile();

            String doctype = "<!DOCTYPE cryptoStatistics SYSTEM \"validate.dtd\">" +
                    new StringBuilder(Objects.requireNonNull(javaToXml())).delete(0, 55);

            FileWriter fileWriter = new FileWriter("prices.xml");
            fileWriter.append(doctype);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getDataFromApiToXml() {
        try {
            Crypto_api.connection();
            Crypto_api.getDataFromApiToPojo();
            Crypto_api.createXmlFile();
            Crypto_api.javaToXml();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void validateXmlWithDTD() {
        try {
            validate();
            System.out.println("Validating with DTD file...");
        } catch (IOException | SAXException e) {
            e.printStackTrace();
            System.out.println("XML document is invalid :(");
        }
        System.out.println("XML document is valid!!");
    }

    public static void validateAgainstXsd() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new File("validate_xsd.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource("prices.xml"));
            System.out.println("validating against xsd...");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("XML document is invalid :(");
        }
        System.out.println("XML document is valid!!");
    }

    private static void validate() throws IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(true);
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
            e.printStackTrace();
        }
        System.out.println("validating....");
        assert documentBuilder != null;
        documentBuilder.parse(new FileInputStream("prices.xml"));
    }
}




