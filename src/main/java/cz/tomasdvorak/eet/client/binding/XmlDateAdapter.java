package cz.tomasdvorak.eet.client.binding;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * dateTime attribute is in EET defined by pattern \d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(Z|[+\-]\d\d:\d\d) according to ISO 8601.
 * The default CXF conversion includes also millis and timezone in format HHMM. We need to get rid of millis and timezone
 * has to be in format HH:MM. Thus we implement our own converter.
 *
 * TODO: is it needed? Exists any other or default solution?
 *
 * Valid date example: 2016-12-09T16:45:36+01:00
 *
 * @see XMLGregorianCalendar#toXMLFormat()
 */
public class XmlDateAdapter extends XmlAdapter<String, Date> {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    @Override
    public Date unmarshal( String inputDate) throws Exception {
        inputDate = inputDate.replaceAll(":(\\d\\d)$", "$1");
        return new SimpleDateFormat(DATE_PATTERN).parse(inputDate);
    }

    @Override
    public String marshal(final Date inputDate) throws Exception {
        final String result = new SimpleDateFormat(DATE_PATTERN).format(inputDate);
        return result.substring(0, result.length() - 2) + ":" + result.substring(result.length() - 2);
    }

}
