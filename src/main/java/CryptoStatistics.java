import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class CryptoStatistics {
    @XmlAttribute
    private final String sourceApi = "CoinMarketCap_API";
    private String nrOfCurrencies;
    private Set<CryptoCurrency> currency = new HashSet<>();

    private CryptoStatistics(String numberOfCurrencies,Set<CryptoCurrency> set) {
        this.nrOfCurrencies = numberOfCurrencies;
        this.currency=set;
    }

    private CryptoStatistics() {

    }
    public static CryptoStatistics valueOf(String nrOfCurrencies,Set<CryptoCurrency> set){
        return new CryptoStatistics(nrOfCurrencies,set);
    }

    @Override
    public String toString() {
        return "CryptoStatistics{" +
                "sourceApi='" + sourceApi + '\'' +
                ", nrOfCurrencies='" + nrOfCurrencies + '\'' +
                ", currency=" + currency +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoStatistics that = (CryptoStatistics) o;
        return sourceApi.equals(that.sourceApi) && nrOfCurrencies.equals(that.nrOfCurrencies) && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceApi, nrOfCurrencies, currency);
    }
}
