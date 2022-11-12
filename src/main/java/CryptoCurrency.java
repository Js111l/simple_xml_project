import org.apache.http.client.BackoffManager;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

@XmlRootElement
final class CryptoCurrency {
    @XmlAttribute
    private String name;
    @XmlElement
    private String symbol;
    @XmlElement
    private String priceUSD;
    @XmlElement
    private String priceEUR;
    @XmlElement
    private String pricePLN;
    @XmlElement
    private String dateAdded;
    @XmlElement
    private Supply supply;

    public CryptoCurrency(){

    }

    private CryptoCurrency(CryptoCurrencyBuilder builder) {
        this.name = builder.name;
        this.symbol = builder.symbol;
        this.priceUSD = builder.priceUSD;
        this.priceEUR = builder.priceEUR;
        this.pricePLN = builder.pricePLN;
        this.dateAdded = builder.dateAdded;
        this.supply = builder.supply;
    }

    public static class CryptoCurrencyBuilder {
        private String name;
        private String symbol;
        private String priceUSD;
        private String priceEUR;
        private String pricePLN;
        private String dateAdded;
        private Supply supply;

        public CryptoCurrencyBuilder(String name, String symbol, String priceUSD, String dateAdded, Supply supply) {
            this.name = name;
            this.symbol = symbol;
            this.priceUSD = priceUSD;
            this.dateAdded = dateAdded;
            this.supply = supply;
        }

        public CryptoCurrencyBuilder setPriceEUR(String priceEUR) {
            this.priceEUR = priceEUR;
            return this;
        }

        public CryptoCurrencyBuilder setPricePLN(String pricePLN) {
            this.pricePLN = pricePLN;
            return this;
        }

        public CryptoCurrency build(){
            return new CryptoCurrency(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoCurrency that = (CryptoCurrency) o;
        return Objects.equals(name, that.name) && Objects.equals(symbol, that.symbol) && Objects.equals(priceUSD, that.priceUSD) && Objects.equals(priceEUR, that.priceEUR) && Objects.equals(pricePLN, that.pricePLN) && Objects.equals(dateAdded, that.dateAdded) && Objects.equals(supply, that.supply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol, priceUSD, priceEUR, pricePLN, dateAdded, supply);
    }
}
