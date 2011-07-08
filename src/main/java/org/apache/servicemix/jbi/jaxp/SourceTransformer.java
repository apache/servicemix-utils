/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.jbi.jaxp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.crypto.dsig.Transform;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jaxen.function.ext.EndsWithFunction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * A helper class to transform from one type of {@link Source} to another
 * 
 * @version $Revision: 670459 $
 */
public class SourceTransformer {

    public static final String DEFAULT_CHARSET_PROPERTY = "org.apache.servicemix.default.charset";
    public static final String DEFAULT_VALIDATING_DTD_PROPERTY = "org.apache.servicemix.default.validating-dtd";
    private static ThreadLocal<WeakReference<DocumentBuilder>> docBuilderCache = new ThreadLocal<WeakReference<DocumentBuilder>>();
    private static ThreadLocal<WeakReference<TransformerFactory>> transformerFactoryCache = new ThreadLocal<WeakReference<TransformerFactory>>();
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;

    /*
     * When converting a DOM tree to a SAXSource, we try to use Xalan internal
     * DOM parser if available. Else, transform the DOM tree to a String and
     * build a SAXSource on top of it.
     */
    private static final Class DOM_2_SAX_CLASS;
    static {
        Class cl = null;
        try {
            cl = Class.forName("org.apache.xalan.xsltc.trax.DOM2SAX");
        } catch (Throwable t) {
            // Ignore
        }
        DOM_2_SAX_CLASS = cl;
    }

    private static String defaultCharset = System.getProperty(DEFAULT_CHARSET_PROPERTY, "UTF-8");
    private static boolean defaultValidatingDtd = (new Boolean(System.getProperty(DEFAULT_VALIDATING_DTD_PROPERTY, "false"))).booleanValue();

    private DocumentBuilderFactory documentBuilderFactory;

    	
    public SourceTransformer() {
    }

    public SourceTransformer(DocumentBuilderFactory documentBuilderFactory) {
        this.documentBuilderFactory = documentBuilderFactory;
    }

    public static String getDefaultCharset() {
        return defaultCharset;
    }

    public static void setDefaultCharset(String defaultCharset) {
        SourceTransformer.defaultCharset = defaultCharset;
    }
    
    public static boolean getDefaultValidatingDtd() {
        return defaultValidatingDtd;
    }
    
    public static void setDefaultValidatingDtd(boolean defaultValidatingDtd) {
        SourceTransformer.defaultValidatingDtd = defaultValidatingDtd;
    }

    /**
     * Converts the given input Source into the required result, using the default charset
     */
    public void toResult(Source source, Result result) throws TransformerException {
        toResult(source, result, defaultCharset);
    }

    /**
     * Converts the given input Source into the required result, using the specified encoding
     * @param source the input Source
     * @param result the output Result
     * @param charset the required charset, if you specify <code>null</code> the default charset will be used
     */
    public void toResult(Source source, Result result, String charset)
        throws TransformerConfigurationException, TransformerException {
        if (source == null) {
            return;
        }
        if (charset == null) {
            charset = defaultCharset;
        }
        Transformer transformer = createTransfomer();
        if (transformer == null) {
            throw new TransformerException("Could not create a transformer - JAXP is misconfigured!");
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, charset);
        transformer.transform(source, result);
    }

    /**
     * Converts the given input Source into text
     */
    public String toString(Source source) throws TransformerException {
        if (source == null) {
            return null;
        } else if (source instanceof StringSource) {
            return ((StringSource) source).getText();
        } else if (source instanceof BytesSource) {
            return new String(((BytesSource) source).getData());
        } else {
            StringWriter buffer = new StringWriter();
            toResult(source, new StreamResult(buffer));
            return buffer.toString();
        }
    }

    /**
     * Converts the given input Node into text
     */
    public String toString(Node node) throws TransformerException {
        return toString(new DOMSource(node));
    }

    /**
     * Converts the content of the given message to a String
     * 
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public String contentToString(NormalizedMessage message) throws MessagingException, TransformerException, ParserConfigurationException,
                    IOException, SAXException {
        return toString(message.getContent());
    }

    /**
     * Converts the source instance to a {@link DOMSource} or returns null if
     * the conversion is not supported (making it easy to derive from this class
     * to add new kinds of conversion).
     */
    public DOMSource toDOMSource(Source source) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        if (source instanceof DOMSource) {
            return (DOMSource) source;
        } else if (source instanceof SAXSource) {
            return toDOMSourceFromSAX((SAXSource) source);
        } else if (source instanceof StreamSource) {
            return toDOMSourceFromStream((StreamSource) source);
        } else if (source instanceof StaxSource) {
            return toDOMSourceFromStax((StaxSource) source);
        } else {
            return null;
        }
    }

    public Source toDOMSource(NormalizedMessage message) throws MessagingException, TransformerException, ParserConfigurationException,
                    IOException, SAXException {
        Node node = toDOMNode(message);
        return new DOMSource(node);
    }

    public Source toDOMSource(StaxSource source) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Transformer transformer = createTransfomer();
        DOMResult result = new DOMResult();
        transformer.transform(source, result);
        return new DOMSource(result.getNode(), result.getSystemId());
    }

    /**
     * Converts the source instance to a {@link SAXSource} or returns null if
     * the conversion is not supported (making it easy to derive from this class
     * to add new kinds of conversion).
     */
    public SAXSource toSAXSource(Source source) throws IOException, SAXException, TransformerException {
        if (source instanceof SAXSource) {
            return (SAXSource) source;
        } else if (source instanceof DOMSource) {
            return toSAXSourceFromDOM((DOMSource) source);
        } else if (source instanceof StreamSource) {
            return toSAXSourceFromStream((StreamSource) source);
        } else if (source instanceof StaxSource) {
            return toSAXSourceFromStax((StaxSource) source);
        } else {
            return null;
        }
    }

    public StreamSource toStreamSource(Source source) throws TransformerException {
        if (source instanceof StreamSource) {
            return (StreamSource) source;
        } else if (source instanceof DOMSource) {
            return toStreamSourceFromDOM((DOMSource) source);
        } else if (source instanceof SAXSource) {
            return toStreamSourceFromSAX((SAXSource) source);
        } else {
            return null;
        }
    }

    public StreamSource toStreamSourceFromSAX(SAXSource source) throws TransformerException {
        InputSource inputSource = source.getInputSource();
        if (inputSource != null) {
            if (inputSource.getCharacterStream() != null) {
                return new StreamSource(inputSource.getCharacterStream());
            }
            if (inputSource.getByteStream() != null) {
                return new StreamSource(inputSource.getByteStream());
            }
        }
        String result = toString(source);
        return new StringSource(result);
    }

    public StreamSource toStreamSourceFromDOM(DOMSource source) throws TransformerException {
        String result = toString(source);
        return new StringSource(result);
    }

    public SAXSource toSAXSourceFromStream(StreamSource source) {
        InputSource inputSource;
        if (source.getReader() != null) {
            inputSource = new InputSource(source.getReader());
        } else {
            inputSource = new InputSource(source.getInputStream());
        }
        inputSource.setSystemId(source.getSystemId());
        inputSource.setPublicId(source.getPublicId());
        return new SAXSource(inputSource);
    }

    public SAXSource toSAXSourceFromStax(StaxSource source) {
        return (SAXSource) source;
    }

    public Reader toReaderFromSource(Source src) throws TransformerException {
        StreamSource stSrc = toStreamSource(src);
        Reader r = stSrc.getReader();
        if (r == null) {
            r = new InputStreamReader(stSrc.getInputStream());
        }
        return r;
    }

    public DOMSource toDOMSourceFromStream(StreamSource source) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = createDocumentBuilder();
        String systemId = source.getSystemId();
        Document document = null;
        Reader reader = source.getReader();
        if (reader != null) {
            document = builder.parse(new InputSource(reader));
        } else {
            InputStream inputStream = source.getInputStream();
            if (inputStream != null) {
                InputSource inputsource = new InputSource(inputStream);
                inputsource.setSystemId(systemId);
                document = builder.parse(inputsource);
            } else {
                throw new IOException("No input stream or reader available");
            }
        }
        return new DOMSource(document, systemId);
    }

    public DOMSource toDOMSourceFromStax(StaxSource source) throws TransformerException {
        Transformer transformer = createTransfomer();
        DOMResult result = new DOMResult();
        transformer.transform(source, result);
        return new DOMSource(result.getNode(), result.getSystemId());
    }

    public SAXSource toSAXSourceFromDOM(DOMSource source) throws TransformerException {
        if (DOM_2_SAX_CLASS != null) {
            try {
                Constructor cns = DOM_2_SAX_CLASS.getConstructor(new Class[] {Node.class });
                XMLReader converter = (XMLReader) cns.newInstance(new Object[] {source.getNode() });
                converter.setFeature("http://xml.org/sax/features/validation", defaultValidatingDtd);
                converter.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", defaultValidatingDtd);
                return new SAXSource(converter, new InputSource());
            } catch (Exception e) {
                throw new TransformerException(e);
            }
        } else {
            String str = toString(source);
            StringReader reader = new StringReader(str);
            return new SAXSource(new InputSource(reader));
        }
    }

    public DOMSource toDOMSourceFromSAX(SAXSource source) throws IOException, SAXException, ParserConfigurationException,
                    TransformerException {
        return new DOMSource(toDOMNodeFromSAX(source));
    }

    public Node toDOMNodeFromSAX(SAXSource source) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DOMResult result = new DOMResult();
        toResult(source, result);
        return result.getNode();
    }

    /**
     * Converts the given TRaX Source into a W3C DOM node
     * 
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public Node toDOMNode(Source source) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        DOMSource domSrc = toDOMSource(source);
        return domSrc != null ? domSrc.getNode() : null;
    }

    /**
     * Avoids multple parsing to DOM by caching the DOM representation in the
     * message as a property so future calls will avoid the reparse - and avoid
     * issues with stream based Source instances.
     * 
     * @param message
     *            the normalized message
     * @return the W3C DOM node for this message
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public Node toDOMNode(NormalizedMessage message) throws MessagingException, TransformerException, ParserConfigurationException,
                    IOException, SAXException {
        Source content = message.getContent();
        return toDOMNode(content);
    }

    /**
     * Create a DOM element from the normalized message.
     * 
     * @param message
     * @return
     * @throws MessagingException
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Element toDOMElement(NormalizedMessage message) throws MessagingException, TransformerException, ParserConfigurationException,
                    IOException, SAXException {
        Node node = toDOMNode(message);
        return toDOMElement(node);
    }

    /**
     * Create a DOM element from the given source.
     * 
     * @param source
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Element toDOMElement(Source source) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        Node node = toDOMNode(source);
        return toDOMElement(node);
    }

    /**
     * Create a DOM element from the DOM node. Simply cast if the node is an
     * Element, or return the root element if it is a Document.
     * 
     * @param node
     * @return
     * @throws TransformerException
     */
    public Element toDOMElement(Node node) throws TransformerException {
        // If the node is an document, return the root element
        if (node instanceof Document) {
            return ((Document) node).getDocumentElement();
            // If the node is an element, just cast it
        } else if (node instanceof Element) {
            return (Element) node;
            // Other node types are not handled
        } else {
            throw new TransformerException("Unable to convert DOM node to an Element");
        }
    }

    /**
     * Create a DOM document from the given normalized message
     * 
     * @param message
     * @return
     * @throws MessagingException
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Document toDOMDocument(NormalizedMessage message) throws MessagingException, TransformerException, ParserConfigurationException,
                    IOException, SAXException {
        Node node = toDOMNode(message);
        return toDOMDocument(node);
    }

    /**
     * Create a DOM document from the given source.
     * 
     * @param source
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Document toDOMDocument(Source source) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        Node node = toDOMNode(source);
        return toDOMDocument(node);
    }

    /**
     * Create a DOM document from the given Node. If the node is an document,
     * just cast it, if the node is an root element, retrieve its owner element
     * or create a new document and import the node.
     * 
     * @param node
     * @return
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public Document toDOMDocument(Node node) throws ParserConfigurationException, TransformerException {
        // If the node is the document, just cast it
        if (node instanceof Document) {
            return (Document) node;
            // If the node is an element
        } else if (node instanceof Element) {
            Element elem = (Element) node;
            // If this is the root element, return its owner document
            if (elem.getOwnerDocument().getDocumentElement() == elem) {
                return elem.getOwnerDocument();
                // else, create a new doc and copy the element inside it
            } else {
                Document doc = createDocument();
                doc.appendChild(doc.importNode(node, true));
                return doc;
            }
            // other element types are not handled
        } else {
            throw new TransformerException("Unable to convert DOM node to a Document");
        }
    }

    /**
     * Converts the source instance to a StaxSource or returns null if the
     * conversion is not supported (making it easy to derive from this class
     * to add new kinds of conversion).
     *
     * @param source the source
     * @return the converted StaxSource
     * @throws XMLStreamException
     */
    public StaxSource toStaxSource(Source source) throws XMLStreamException {
        if (source instanceof StaxSource) {
            return (StaxSource) source;
        } else {
            XMLInputFactory factory = getInputFactory();
            XMLStreamReader reader = factory.createXMLStreamReader(source);
            return new StaxSource(reader);
        }
    }

    public XMLStreamReader toXMLStreamReader(Source source) throws XMLStreamException, TransformerException {
        if (source instanceof StaxSource) {
            return ((StaxSource) source).getXMLStreamReader();
        }
        // It seems that woodstox 2.9.3 throws some NPE in the servicemix-soap
        // when using DOM, so use our own dom / stax parser
        if (source instanceof DOMSource) {
            Node n = ((DOMSource) source).getNode();
            Element el = n instanceof Document ? ((Document) n).getDocumentElement() : n instanceof Element ? (Element) n : null;
            if (el != null) {
                return new W3CDOMStreamReader(el);
            }
        }
        XMLInputFactory factory = getInputFactory();
        try {
            return factory.createXMLStreamReader(source);
        } catch (XMLStreamException e) {
            return factory.createXMLStreamReader(toReaderFromSource(source));
        }
    }

    // Properties
    // -------------------------------------------------------------------------
    public DocumentBuilderFactory getDocumentBuilderFactory() {
        if (documentBuilderFactory == null) {
            documentBuilderFactory = createDocumentBuilderFactory();
        }
        return documentBuilderFactory;
    }

    public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
        this.documentBuilderFactory = documentBuilderFactory;
    }

    // Helper methods
    // -------------------------------------------------------------------------
    public DocumentBuilderFactory createDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        factory.setValidating(defaultValidatingDtd);
        return factory;
    }

    public DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        WeakReference<DocumentBuilder> cachedDocBuilder = docBuilderCache.get();

        DocumentBuilder docBuilder = null;
        if (cachedDocBuilder != null) {
            docBuilder = cachedDocBuilder.get();
        }

        if (docBuilder == null) {
            docBuilder = getDocumentBuilderFactory().newDocumentBuilder();            
            docBuilderCache.set(new WeakReference<DocumentBuilder>(docBuilder));
        }

        return docBuilder;
    }

    public Document createDocument() throws ParserConfigurationException {
        DocumentBuilder builder = createDocumentBuilder();
        return builder.newDocument();
    }
    
    public Transformer createTransfomer() throws TransformerConfigurationException {
        return createTransformerFactory().newTransformer();
    }
    
    public TransformerFactory createTransformerFactory() {        
        WeakReference<TransformerFactory> cachedFactory = transformerFactoryCache.get();

        TransformerFactory factory = null;
        if (cachedFactory != null) {
            factory = cachedFactory.get();
        }

        if (factory == null) {
            factory = TransformerFactory.newInstance();
            transformerFactoryCache.set(new WeakReference<TransformerFactory>(factory));
        }

        return factory;        
    }
    
    public TransformerFactory getTransformerFactory() {
    	return createTransformerFactory();
    }
    	 
    public void setTransformerFactory(TransformerFactory transformerFactory) {
    	transformerFactoryCache.set(new WeakReference<TransformerFactory>(transformerFactory));
    }

    public XMLInputFactory getInputFactory() {
        if (inputFactory == null) {
            inputFactory = createInputFactory();
        }
        return inputFactory;
    }

    public void setInputFactory(XMLInputFactory inputFactory) {
        this.inputFactory = inputFactory;
    }

    public XMLOutputFactory getOutputFactory() {
        if (outputFactory == null) {
            outputFactory = createOutputFactory();
        }
        return outputFactory;
    }

    public void setOutputFactory(XMLOutputFactory outputFactory) {
        this.outputFactory = outputFactory;
    }

    protected XMLInputFactory createInputFactory() {
        return XMLInputFactory.newInstance();
    }

    protected XMLOutputFactory createOutputFactory() {
        return XMLOutputFactory.newInstance();
    }

}
