package cz.tomasdvorak.eet.client.binding;

import java.time.ZonedDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XmlDateAdapterTest {

    private XmlDateAdapter xmlDateAdapter;

    @Before
    public void setUp() throws Exception {
        xmlDateAdapter = new XmlDateAdapter();
    }

    @Test
    public void verify() throws Exception {
        final String inputDate = "2016-07-07T12:36:23+02:00";
        final ZonedDateTime date = xmlDateAdapter.unmarshal(inputDate);
        Assert.assertEquals(inputDate, xmlDateAdapter.marshal(date));
    }

}
