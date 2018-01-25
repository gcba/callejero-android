/**
 * Created by gcbamobile on 18/01/18.
 */

import com.gcba.callejero.ui.GcbaUtils;


import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Created by leonardopalazzo on 18/01/2018.
 */

public class ParserTest {

    @Test
    public void testConvertFahrenheitToCelsius() {
        String point = "POINT (-58.3695565348919203 -34.6267443756208877)";

        Double xExpected = new Double("-58.3695565348919203") ;
        Double yExpected = new Double("-34.6267443756208877") ;

        assertTrue("el X coincide", xExpected.equals(GcbaUtils.parseX(point)));
        assertTrue("el Y coincide", yExpected.equals(GcbaUtils.parseY(point)));

    }



}
