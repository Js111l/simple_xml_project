import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
final class Supply {
    @XmlElement
    private String max_supply;
    @XmlElement
    private String circulating_supply;
    @XmlElement
    private String total_supply;

    public Supply(String max_supply, String circulating_supply, String total_supply) {
        this.max_supply = max_supply;
        this.circulating_supply = circulating_supply;
        this.total_supply = total_supply;
    }

    Supply() {

    }

    @Override
    public String toString() {
        return "Supply{" +
                "max_supply='" + max_supply + '\'' +
                ", circulating_supply='" + circulating_supply + '\'' +
                ", total_supply='" + total_supply + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supply supply = (Supply) o;
        return max_supply.equals(supply.max_supply) && circulating_supply.equals(supply.circulating_supply) && total_supply.equals(supply.total_supply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(max_supply, circulating_supply, total_supply);
    }
}