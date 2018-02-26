/*
 * The MIT License
 *
 * Copyright 2017 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate;

import com.intuit.karate.cucumber.ScenarioInfo;
import com.intuit.karate.exception.KarateFileNotFoundException;
import com.intuit.karate.http.Cookie;
import com.intuit.karate.http.HttpClient;
import com.intuit.karate.http.HttpConfig;
import com.intuit.karate.http.HttpRequest;
import com.intuit.karate.validator.Validator;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 *
 * @author pthomas3
 */
public class ScriptContext {

    public final Logger logger;

    protected final ScriptBindings bindings;

    protected final int callDepth;
    protected final List<String> tags;
    protected final Map<String, List<String>> tagValues;
    protected final ScriptValueMap vars;
    protected final Map<String, Validator> validators;
    protected final ScriptEnv env;

    protected final ScenarioInfo scenarioInfo;

    // these can get re-built or swapped, so cannot be final
    protected HttpClient client;
    protected HttpConfig config;

    // the actual http request last sent on the wire
    protected HttpRequest prevRequest;

    public void setScenarioError(Throwable error) {
        scenarioInfo.setErrorMessage(error.getMessage());
    }

    public void setPrevRequest(HttpRequest prevRequest) {
        this.prevRequest = prevRequest;
    }

    public ScriptEnv getEnv() {
        return env;
    }

    public ScriptValueMap getVars() {
        return vars;
    }

    public HttpConfig getConfig() {
        return config;
    }

    public void updateConfigCookies(Map<String, Cookie> cookies) {
        if (cookies == null) {
            return;
        }
        if (config.getCookies().isNull()) {
            config.setCookies(new ScriptValue(cookies));
        } else {
            Map<String, Object> map = config.getCookies().evalAsMap(this);
            map.putAll(cookies);
            config.setCookies(new ScriptValue(map));
        }
    }

    public boolean isLogPrettyRequest() {
        return config.isLogPrettyRequest();
    }

    public boolean isLogPrettyResponse() {
        return config.isLogPrettyResponse();
    }

    public boolean isPrintEnabled() {
        return config.isPrintEnabled();
    }

    public ScriptContext(ScriptEnv env, CallContext call) {
        this.env = env.refresh(null);
        logger = env.logger;
        callDepth = call.callDepth;
        tags = call.getTags();
        tagValues = call.getTagValues();
        scenarioInfo = call.getScenarioInfo();
        if (call.reuseParentContext) {
            CoverageStructure.addBranch(2,3);
            vars = call.parentContext.vars; // shared context !
            validators = call.parentContext.validators;
            config = call.parentContext.config;
        } else if (call.parentContext != null) {
            CoverageStructure.addBranch(3,3);
            vars = call.parentContext.vars.copy();
            validators = call.parentContext.validators;
            config = new HttpConfig(call.parentContext.config);
        } else {
            CoverageStructure.addBranch(4,3);
            vars = new ScriptValueMap();
            validators = Validator.getDefaults();
            config = new HttpConfig();
            config.setClientClass(call.httpClientClass);
        }
        client = HttpClient.construct(config, this);
        bindings = new ScriptBindings(this);
        if (call.parentContext == null && call.evalKarateConfig) {
            CoverageStructure.addBranch(5,3);
            try {
                String configScript;
                String configPath = System.getProperty(ScriptBindings.KARATE_CONFIG);
                if (configPath != null) { // over-ridden by user or command-line / stand-alone jar
                    CoverageStructure.addBranch(6,3);
                    File configFile = new File(configPath);
                    configScript = String.format("%s('%s')", ScriptBindings.READ, FileUtils.FILE_COLON + configFile.getPath());
                } else {
                    CoverageStructure.addBranch(7,3);
                    configScript = ScriptBindings.READ_KARATE_CONFIG;
                }
                Script.callAndUpdateConfigAndAlsoVarsIfMapReturned(false, configScript, null, this);
            } catch (Exception e) {
                if (e instanceof KarateFileNotFoundException) {
                    CoverageStructure.addBranch(8,3);
                    logger.warn("skipping bootstrap configuration: {}", e.getMessage());
                } else {
                    CoverageStructure.addBranch(9,3);
                    throw new RuntimeException("evaluation of " + ScriptBindings.KARATE_CONFIG_JS + " failed:", e);
                }
            }
        }
        if (call.callArg != null) { // if call.reuseParentContext is true, arg will clobber parent context
            CoverageStructure.addBranch(10,3);
            for (Map.Entry<String, Object> entry : call.callArg.entrySet()) {
                vars.put(entry.getKey(), entry.getValue());
            }
            vars.put(Script.VAR_ARG, call.callArg);
            vars.put(Script.VAR_LOOP, call.loopIndex);
        } else if (call.parentContext != null) {
            CoverageStructure.addBranch(1,3);
            vars.put(Script.VAR_ARG, ScriptValue.NULL);
            vars.put(Script.VAR_LOOP, -1);
        }
        logger.trace("karate context init - initial properties: {}", vars);
    }

    public void configure(HttpConfig config) {
        this.config = config;
        client = HttpClient.construct(config, this);
    }

    public void configure(String key, String exp) {
        configure(key, Script.evalKarateExpression(exp, this));
    }

    public void configure(String key, ScriptValue value) { // TODO use enum
        key = StringUtils.trimToEmpty(key);
        if (key.equals("headers")) {
            CoverageStructure.addBranch(1,2);
            config.setHeaders(value);
            return;
        }
        if (key.equals("cookies")) {
            CoverageStructure.addBranch(2,2);
            config.setCookies(value);
            return;
        }
        if (key.equals("responseHeaders")) {
            CoverageStructure.addBranch(3,2);
            config.setResponseHeaders(value);
            return;
        }
        if (key.equals("cors")) {
            CoverageStructure.addBranch(4,2);
            config.setCorsEnabled(value.isBooleanTrue());
            return;
        }
        if (key.equals("logPrettyResponse")) {
            CoverageStructure.addBranch(5,2);
            config.setLogPrettyResponse(value.isBooleanTrue());
            return;
        }
        if (key.equals("logPrettyRequest")) {
            CoverageStructure.addBranch(6,2);
            config.setLogPrettyRequest(value.isBooleanTrue());
            return;
        }
        if (key.equals("printEnabled")) {
            CoverageStructure.addBranch(7,2);
            config.setPrintEnabled(value.isBooleanTrue());
            return;
        }
        if (key.equals("afterScenario")) {
            CoverageStructure.addBranch(8,2);
            config.setAfterScenario(value);
            return;
        }
        if (key.equals("afterFeature")) {
            CoverageStructure.addBranch(9,2);
            config.setAfterFeature(value);
            return;
        }
        if (key.equals("httpClientClass")) {
            CoverageStructure.addBranch(10,2);
            config.setClientClass(value.getAsString());
            // re-construct all the things ! and we exit early
            client = HttpClient.construct(config, this);
            return;
        }
        if (key.equals("httpClientInstance")) {
            CoverageStructure.addBranch(11,2);
            config.setClientInstance(value.getValue(HttpClient.class));
            // here too, re-construct client - and exit early
            client = HttpClient.construct(config, this);
            return;
        }
        if (key.equals("charset")) {
            if (value.isNull()) {
                CoverageStructure.addBranch(12,2);
                config.setCharset(null);
            } else {
                CoverageStructure.addBranch(13,2);
                config.setCharset(Charset.forName(value.getAsString()));
            }
            // here again, re-construct client - and exit early
            client = HttpClient.construct(config, this);            
            return;
        }
        // beyond this point, we don't exit early and we have to re-configure the http client
        if (key.equals("ssl")) {
            if (value.isString()) {
                CoverageStructure.addBranch(14,2);
                config.setSslEnabled(true);
                config.setSslAlgorithm(value.getAsString());
            } else if (value.isMapLike()) {
                CoverageStructure.addBranch(15,2);
                config.setSslEnabled(true);
                Map<String, Object> map = value.getAsMap();
                config.setSslKeyStore((String) map.get("keyStore"));
                config.setSslKeyStorePassword((String) map.get("keyStorePassword"));
                config.setSslKeyStoreType((String) map.get("keyStoreType"));
                config.setSslTrustStore((String) map.get("trustStore"));
                config.setSslTrustStorePassword((String) map.get("trustStorePassword"));
                config.setSslTrustStoreType((String) map.get("trustStoreType"));
                String trustAll = (String) map.get("trustAll");
                if (trustAll != null) {
                    CoverageStructure.addBranch(16,2);
                    config.setSslTrustAll(Boolean.valueOf(trustAll));
                }
                config.setSslAlgorithm((String) map.get("algorithm"));
            } else {
                CoverageStructure.addBranch(17,2);
                config.setSslEnabled(value.isBooleanTrue());
            }
        } else if (key.equals("followRedirects")) {
            CoverageStructure.addBranch(18,2);
            config.setFollowRedirects(value.isBooleanTrue());
        } else if (key.equals("connectTimeout")) {
            CoverageStructure.addBranch(19,2);
            config.setConnectTimeout(Integer.valueOf(value.getAsString()));
        } else if (key.equals("readTimeout")) {
            CoverageStructure.addBranch(20,2);
            config.setReadTimeout(Integer.valueOf(value.getAsString()));
        } else if (key.equals("proxy")) {
            if (value.isString()) {
                CoverageStructure.addBranch(21,2);
                config.setProxyUri(value.getAsString());
            } else {
                CoverageStructure.addBranch(22,2);
                Map<String, Object> map = value.getAsMap();
                config.setProxyUri((String) map.get("uri"));
                config.setProxyUsername((String) map.get("username"));
                config.setProxyPassword((String) map.get("password"));
            }
        } else if (key.equals("userDefined")) {
            CoverageStructure.addBranch(23,2);
            config.setUserDefined(value.getAsMap());
        } else {
            CoverageStructure.addBranch(24,2);
            throw new RuntimeException("unexpected 'configure' key: '" + key + "'");
        }
        client.configure(config, this);
    }

}
