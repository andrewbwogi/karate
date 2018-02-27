package com.intuit.karate;

import com.intuit.karate.http.HttpConfig;
import org.junit.Test;
import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ScriptContextTest {
    @Test
    public void testConfigure() {
        String featureDir = FileUtils.getDirContaining(getClass()).getPath();
        ScriptEnv env = ScriptEnv.init("baz", new File(featureDir));
        CallContext callContext = new CallContext(null, 0, null, -1, false, true, null);
        ScriptContext ctx = new ScriptContext(env, callContext);
        ScriptValue value = new ScriptValue("test-value");

        ctx.configure("headers", value);
        HttpConfig conf = ctx.getConfig();
        assertEquals("test-value", conf.getHeaders().getValue());

        ctx.configure("responseHeaders", value);
        conf = ctx.getConfig();
        assertEquals("test-value", conf.getResponseHeaders().getValue());

        ctx.configure("afterFeature", value);
        conf = ctx.getConfig();
        assertEquals("test-value", conf.getAfterFeature().getValue());

        value = new ScriptValue(true);
        ctx.configure("logPrettyResponse", value);
        conf = ctx.getConfig();
        assertEquals(true, conf.isLogPrettyResponse());

        value = new ScriptValue(false);
        ctx.configure("logPrettyResponse", value);
        conf = ctx.getConfig();
        assertEquals(false, conf.isLogPrettyResponse());

        HashMap<String,String> hm = new HashMap<String, String>();
        hm.put("keyStore","keyStore-test");
        hm.put("keyStorePassword","keyStorePassword-test");
        hm.put("keyStoreType","keyStoreType-test");
        hm.put("trustStore","trustStore-test");
        hm.put("trustStorePassword","trustStorePassword-test");
        hm.put("trustStoreType","trustStoreType-test");
        hm.put("trustAll","true");
        hm.put("algorithm","algorithm-test");
        value = new ScriptValue(hm);
        ctx.configure("ssl", value);
        conf = ctx.getConfig();
        assertEquals(hm.get("keyStore"), conf.getSslKeyStore());
        assertEquals(hm.get("keyStorePassword"), conf.getSslKeyStorePassword());
        assertEquals(hm.get("keyStoreType"), conf.getSslKeyStoreType());
        assertEquals(hm.get("trustStore"), conf.getSslTrustStore());
        assertEquals(hm.get("trustStorePassword"), conf.getSslTrustStorePassword());
        assertEquals(hm.get("trustStoreType"), conf.getSslTrustStoreType());
        assertEquals(true, conf.isSslTrustAll());
        assertEquals(hm.get("algorithm"), conf.getSslAlgorithm());

        hm.replace("trustAll","false");
        value = new ScriptValue(hm);
        ctx.configure("ssl", value);
        conf = ctx.getConfig();
        assertEquals(false, conf.isSslTrustAll());



    }
}
