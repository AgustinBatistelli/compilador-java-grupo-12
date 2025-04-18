package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.factories.ParserFactory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;
import static lyc.compiler.Constants.EXAMPLES_ROOT_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
public class ParserTest {

    @Test
    public void assignmentWithExpression() throws Exception { // done
        compilationSuccessful("c=d*(e-21)/4");
    }

    @Test
    public void syntaxError() {
        compilationError("1234");
    }  // done

    @Test
    void arithmetic() throws Exception {
        compilationSuccessful(readFromFile("arithmetic.txt"));
    }

    @Test
    void assignments() throws Exception {
        compilationSuccessful(readFromFile("assignments.txt"));
    }

    @Test
    void write() throws Exception {
        compilationSuccessful(readFromFile("write.txt"));
    }

    @Test
    void read() throws Exception { // done
        compilationSuccessful(readFromFile("read.txt")); // done
    }

    @Test
    void comment() throws Exception {
        compilationSuccessful(readFromFile("comment.txt")); // done
    }

    @Test
    void init() throws Exception {
        compilationSuccessful(readFromFile("init.txt")); // done
    }

    @Test
    void and() throws Exception {
        compilationSuccessful(readFromFile("and.txt")); // done
    }

    @Test
    void or() throws Exception {
        compilationSuccessful(readFromFile("or.txt")); // done
    }

    @Test
    void not() throws Exception {
        compilationSuccessful(readFromFile("not.txt")); // done
    }

    @Test
    void ifStatement() throws Exception {
        compilationSuccessful(readFromFile("if.txt")); // done
    }

    @Test
    void whileStatement() throws Exception {
        compilationSuccessful(readFromFile("while.txt")); // done
    }


    private void compilationSuccessful(String input) throws Exception {
        assertThat(scan(input).sym).isEqualTo(ParserSym.EOF);
    }

    private void compilationError(String input){
        assertThrows(Exception.class, () -> scan(input));
    }

    private Symbol scan(String input) throws Exception {
        return ParserFactory.create(input).parse();
    }

    private String readFromFile(String fileName) throws IOException {
        URL url = new URL(EXAMPLES_ROOT_DIRECTORY + "/%s".formatted(fileName));
        assertThat(url).isNotNull();
        return IOUtils.toString(url.openStream(), StandardCharsets.UTF_8);
    }


}
