//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.05.14 at 07:22:24 PM BST 
//


package uk.gov.ons.ctp.response.casesvc.message.feedback;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.gov.ons.ctp.response.casesvc.message.feedback package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CaseReceipt_QNAME = new QName("http://ons.gov.uk/ctp/response/casesvc/message/feedback", "caseReceipt");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.gov.ons.ctp.response.casesvc.message.feedback
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CaseReceipt }
     * 
     */
    public CaseReceipt createCaseReceipt() {
        return new CaseReceipt();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CaseReceipt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ons.gov.uk/ctp/response/casesvc/message/feedback", name = "caseReceipt")
    public JAXBElement<CaseReceipt> createCaseReceipt(CaseReceipt value) {
        return new JAXBElement<CaseReceipt>(_CaseReceipt_QNAME, CaseReceipt.class, null, value);
    }

}
