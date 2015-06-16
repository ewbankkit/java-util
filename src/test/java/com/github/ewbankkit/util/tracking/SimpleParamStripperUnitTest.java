/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.tracking;

import org.junit.Assert;
import org.junit.Test;

import com.netsol.adagent.util.tracking.ParamStripper;
import com.netsol.adagent.util.tracking.SimpleParamStripper;

public class SimpleParamStripperUnitTest {
    @Test
    public void stripParams_simpleParam() {
        final String[] undesiredParams = {
                "pArAM2", "paramX"
        };

        ParamStripper paramStripper = new SimpleParamStripper(undesiredParams, true);

        String strippedUrl = paramStripper.stripParams("https://www.example.com:444/path/page.php?param1=value1&param2=value2&param3=value3");
        Assert.assertEquals("Stripped URL: ", "https://www.example.com:444/path/page.php?param1=value1&param3=value3", strippedUrl);

        strippedUrl = paramStripper.stripParams("https://www.example.com:444/path/page.php?param2=value1&param1=value2&param3=value3");
        Assert.assertEquals("Stripped URL: ", "https://www.example.com:444/path/page.php?param1=value2&param3=value3", strippedUrl);

        strippedUrl = paramStripper.stripParams("https://www.example.com:444/path/page.php?param1=value1&param3=value2&param2=value3");
        Assert.assertEquals("Stripped URL: ", "https://www.example.com:444/path/page.php?param1=value1&param3=value2", strippedUrl);
    }

    @Test
    public void stripParams_multipleParams() {
        final String[] undesiredParams = {
                "param1", "paramX", "param2"
        };

        ParamStripper paramStripper = new SimpleParamStripper(undesiredParams, false);

        String strippedUrl = paramStripper.stripParams("http://domain.com/path/page.php?param1=value1&param2=value2&param3=value3");
        Assert.assertEquals("Stripped URL: ", "http://domain.com/path/page.php?param3=value3", strippedUrl);

        strippedUrl = paramStripper.stripParams("http://domain.com/path/page.php?param1=value1&param2=value2");
        Assert.assertEquals("Stripped URL: ", "http://domain.com/path/page.php", strippedUrl);
    }

    @Test
    public void stripParams_simpleParamUrlEncoded() {
        final String[] undesiredParams = {
                "pArAM2", "paramX"
        };

        ParamStripper paramStripper = new SimpleParamStripper(undesiredParams, true);

        String strippedUrl = paramStripper.stripUrlEncodedParams("http://domain.com/path/page.php?param1=value1&param2=value2&param3=value3", "UTF-8");
        Assert.assertEquals("Stripped URL: ", "http://domain.com/path/page.php?param1=value1&param3=value3", strippedUrl);

        strippedUrl = paramStripper.stripUrlEncodedParams("http://domain.com/path/page.php?param1=value1%26stuff&param2=a%7eb%24d&param3=value3", "UTF-8");
        Assert.assertEquals("Stripped URL: ", "http://domain.com/path/page.php?param1=value1%26stuff&param3=value3", strippedUrl);
    }
}
