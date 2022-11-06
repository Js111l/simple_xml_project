import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
final class CryptoCurrency {
    @XmlAttribute
    private String name;
    @XmlElement
    private String symbol;
    @XmlElement
    private String price;
    @XmlElement
    private String dateAdded;
    @XmlElement
    private Supply supply;

    CryptoCurrency(String name, String symbol, String price, String dateAdded, Supply supply) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.dateAdded = dateAdded;
        this.supply = supply;
    }

    CryptoCurrency() {

    }

    @Override
    public String toString() {
        return "CryptoCurrency{" +
                "name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", price='" + price + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoCurrency that = (CryptoCurrency) o;
        return name.equals(that.name) && symbol.equals(that.symbol) && price.equals(that.price) && dateAdded.equals(that.dateAdded) && supply.equals(that.supply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol, price, dateAdded, supply);
    }
}
