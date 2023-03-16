import com.nike.artemis.RateRule;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TestForPojo {
    @Test
    public void equalsHashCodeContracts() {
        EqualsVerifier.forClass(RateRule.class).verify();
    }
}
