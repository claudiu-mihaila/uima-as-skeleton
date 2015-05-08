package eu.claudiumihaila.nlp.uima;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;

public class Pipeline {
    private UimaAsynchronousEngine uimaAsEngine = null;
	private Object CAS_POOL_SIZE = 5;
    
    
    public Pipeline(UimaAsBaseCallbackListener callback) throws Exception 
    {
        System.out.println("Sample UIMA application - asynchronous - Claudiu Mihăilă");
        System.out.println("==============================================");

        // creating UIMA analysis engine
        uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();

        // preparing map for use in deploying services
        Map<String,Object> deployCtx = new HashMap<String,Object>();
        deployCtx.put(UimaAsynchronousEngine.DD2SpringXsltFilePath, "dd2spring.xsl");
        deployCtx.put(UimaAsynchronousEngine.SaxonClasspath, "file:" + "saxon8.jar");
        
        System.out.println("Deploying UIMA services");
        uimaAsEngine.deploy("./desc/eu/claudiumihaila/nlp/uima/TypeADeployment.xml", deployCtx);
        uimaAsEngine.deploy("./desc/eu/claudiumihaila/nlp/uima/TypeBDeployment.xml", deployCtx);
        uimaAsEngine.deploy("./desc/eu/claudiumihaila/nlp/uima/TypeCDeployment.xml", deployCtx);
        uimaAsEngine.deploy("./desc/eu/claudiumihaila/nlp/uima/TypeDDeployment.xml", deployCtx);
        uimaAsEngine.deploy("./desc/eu/claudiumihaila/nlp/uima/TypeEDeployment.xml", deployCtx);

        // creating aggregate analysis engine
        System.out.println("Deploying analysis engine");
        uimaAsEngine.deploy("./desc/AAEDeployment.xml", deployCtx);

        // add callback listener that will be informed when processing completes
        uimaAsEngine.addStatusCallbackListener(callback);

        // preparing map for use in a UIMA client for submitting text to process
        System.out.println("Initialising UIMA client");
        deployCtx.put(UimaAsynchronousEngine.ServerUri, "tcp://localhost:61616");
        deployCtx.put(UimaAsynchronousEngine.Endpoint,  "AAEQueue");
        deployCtx.put(UimaAsynchronousEngine.CasPoolSize, CAS_POOL_SIZE );
        uimaAsEngine.initialize(deployCtx);
    }
    
    /**
     * Uses the UIMA analysis engine to process the provided document text.
     */
    public void process(String text) throws CASException, Exception{
        CAS cas = uimaAsEngine.getCAS();
        cas.setDocumentText(text);
        System.out.println(System.currentTimeMillis());
        uimaAsEngine.sendCAS(cas);
        System.out.println(System.currentTimeMillis());
    }

}
