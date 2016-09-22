package cz.tomasdvorak.eet.client.security;

import cz.tomasdvorak.eet.client.utils.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SecurityCodesGeneratorTest {

    private List<DemoRequestHolder> testData;

    @Before
    public void setUp() throws Exception {
        testData = Arrays.asList(
                new DemoRequestHolder("/keys/01000003.p12", "eet", "/requests/CZ1212121218.valid.v3.xml"),
                new DemoRequestHolder("/keys/01000004.p12", "eet", "/requests/CZ00000019.valid.v3.xml")
        );
    }

    @Test
    public void toBKP() throws Exception {
        for(final DemoRequestHolder request : testData) {
            final String expected = request.getXPathValue("//KontrolniKody/bkp/text()");
            final String actual = request.getCodesGenerator().getBKP(request.getTrzbaDataType());
            Assert.assertEquals("Computed BKP doesn't match the one from provided demo request in " + request.getDemoRequestPath(), expected, actual);
        }
    }

    @Test
    public void toPKP() throws Exception {
        for(final DemoRequestHolder request : testData) {
            final String expected = request.getXPathValue("//KontrolniKody/pkp/text()");
            final byte[] pkp = request.getCodesGenerator().getPKP(request.getTrzbaDataType());
            final String actual = StringUtils.toBase64(pkp);
            Assert.assertEquals("Computed PKP doesn't match the one from provided demo request in " + request.getDemoRequestPath(), expected, actual);
        }
    }
}
