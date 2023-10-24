import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.example.entity.SymbolTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SymbolTableTests {
    private SymbolTable symbolTable;
    private Pair<Integer, Integer> position;

    @BeforeEach
    public void setup() {
        this.symbolTable = new SymbolTable(47);
        this.position = new ImmutablePair<>(-1, -1);
    }

    @Test
    public void addIdentifier() throws Exception {
        String identifier = "testIdentifier";

        this.position = this.symbolTable.addIdentifier(identifier);

        assert(!this.position.equals(new ImmutablePair<>(-1, -1)));
    }

    @Test
    public void getIdentifierPosition() throws Exception {
        String identifier = "testIdentifier";
        this.position = this.symbolTable.addIdentifier(identifier);

        Pair<Integer, Integer> savedPosition = this.symbolTable.getPositionIdentifier(identifier);

        assert(this.position.equals(savedPosition));
    }

    @Test
    public void addIntConstant() throws Exception {
        int intConstant = 100;

        this.position = this.symbolTable.addIntConstant(intConstant);

        assert(!this.position.equals(new ImmutablePair<>(-1, -1)));
    }

    @Test
    public void getIntConstantPosition() throws Exception {
        int intConstant = 100;
        this.position = this.symbolTable.addIntConstant(intConstant);

        Pair<Integer, Integer> savedPosition = this.symbolTable.getPositionIntConstant(intConstant);

        assert(this.position.equals(savedPosition));
    }

    @Test
    public void addStringConstant() throws Exception {
        String stringConstant = "testString";

        this.position = this.symbolTable.addStringConstant(stringConstant);

        assert(!this.position.equals(new ImmutablePair<>(-1, -1)));
    }

    @Test
    public void getStringConstantPosition() throws Exception {
        String stringConstant = "testString";
        this.position = this.symbolTable.addStringConstant(stringConstant);

        Pair<Integer, Integer> savedPosition = this.symbolTable.getPositionStringConstant(stringConstant);

        assert(this.position.equals(savedPosition));
    }
}
